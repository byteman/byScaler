package com.example.worker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.bluetooth.le.BleApplication;
import com.example.bluetooth.le.Config;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.example.bluetooth.le.Utils;
import com.example.db.WeightRecord;
import com.example.worker.Global;
import com.lvrenyang.utils.DataUtils;






import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 观察者模式
 * 
 * @author Administrator
 * 
 */
public class WorkService extends Service {

	// Service和workThread通信用mHandler
	public static WorkThread workThread = null;
	private static Handler mHandler = null;
	private static List<Handler> targetsHandler = new ArrayList<Handler>(5);
	public static Map<String,Scaler> scalers;
	private static Map<Integer,Scaler> scalers2;
	private static IBle mBle;
	private static final int REQUEST_ENABLE_BT = 1;
	private String TAG = "WorkSrv";
	private static String strUnit = "kg";
	private static int max_count = 1;
	private static String mPrinterAddress;
	
	
	//private Thread _threadRead;
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			//Log.e(TAG, action);
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				Log.e( TAG, "server connect");

				Message msg = mHandler.obtainMessage(Global.MSG_BLE_CONNECTRESULT);
				final BluetoothDevice device = intent.getExtras()
						.getParcelable(BleService.EXTRA_DEVICE);
			
				
				msg.obj = device;
				mHandler.sendMessage(msg);
				
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				Log.e( TAG, "server disconnect");
				Bundle extras = intent.getExtras();
				if(extras == null) return;
								
				String addr = extras.getString(BleService.EXTRA_ADDR);

						
				Scaler s = scalers.get(addr);
				if(s != null)
				{
					s.setConnected(false,null);
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_DISCONNECTRESULT);
					msg.obj = addr;
					mHandler.sendMessage(msg);	
				}
							
				
			} 

			else if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_NOT_SUPPORT);
				mHandler.sendMessage(msg);	
			} 
			else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
					// device found
				Log.e( TAG, "server BLE_DEVICE_FOUND");
					Bundle extras = intent.getExtras();
					final BluetoothDevice device = extras
							.getParcelable(BleService.EXTRA_DEVICE);
				
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_SCANRESULT);
					msg.obj = device;
					mHandler.sendMessage(msg);
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
					
			}else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				// displayGattServices(mBle.getServices(mDeviceAddress));
				Log.e( TAG, "server discovered");
				String address = intent.getExtras().getString(BleService.EXTRA_ADDR);
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_SERVICEDISRESULT);
				msg.obj = address;
				mHandler.sendMessage(msg);
				BleGattCharacteristic chars = mBle.getService(address,
						UUID.fromString(Utils.UUID_SRV)).getCharacteristic(
						UUID.fromString(Utils.UUID_DATA));
				if(chars != null)
				{
					
					Scaler s = scalers.get(address);
					if(s != null)
					{
						s.setConnected(true,chars);
					}
					mBle.requestCharacteristicNotification(address, chars);
				}
				
				
			} else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				
				Bundle extras = intent.getExtras();
				boolean mNotifyStarted = extras.getBoolean(BleService.EXTRA_VALUE);
				if(mNotifyStarted)
				{
					Toast.makeText(getApplicationContext(), "notify"+extras.getString(BleService.EXTRA_ADDR), Toast.LENGTH_SHORT).show();
				}
			}  else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action))
			{
				Bundle extras = intent.getExtras();
				String addr = extras.getString(BleService.EXTRA_ADDR);
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);
				if(val.length < 4)
				{
					return;
				}
				if((val[0] == 'A') && (val[1] == 'D') && (val[2] == 'V'))
				{
					
					if(val[3] == ':')
					{
						if(val.length < 8) return;
						byte w[] = {0,0,0,0};
						System.arraycopy(val,4,w,0, 4);
						
						int weight = Utils.bytesToWeight(w);
						Scaler d = WorkService.scalers.get(addr);
						if(d==null) return;
						d.setWeight(weight);
						weight = 0;
						for(int i = 0 ; i < max_count; i++)
						{
							
							 if(scalers2.containsKey(i)) //不包含这个地址才创建新的称台设备.
							 {
								Scaler dev = scalers2.get(i);
								
								if(dev!=null && dev.isConnected())
								{
									weight += dev.getWeight();
								}
							 }
												
						
						}
						final BluetoothDevice device = extras
								.getParcelable(BleService.EXTRA_DEVICE);
					
						Message msg = mHandler.obtainMessage(Global.MSG_BLE_WGTRESULT);
						msg.arg1 = weight;
						msg.obj  = device;
						mHandler.sendMessage(msg);
					}
					
				}
				else if((val[0] == 'P') && (val[1] == 'A') && (val[2] == 'R'))
				{
					
						
					if(val[3] == '?') //参数读取的返回值.
					{
						
						
						Scaler d = WorkService.scalers.get(addr);
						if(d==null) return;
						int ret = d.para.parseParaBuffer(val)?1:0;
						final BluetoothDevice device = extras
								.getParcelable(BleService.EXTRA_DEVICE);
					
						Message msg = mHandler.obtainMessage(Global.MSG_SCALER_PAR_GET_RESULT);
						msg.arg1 = ret;
						msg.obj  = d;
						mHandler.sendMessage(msg);
					}
					else if(val[3] == ':') //参数设置的返回值.
					{
						
						final BluetoothDevice device = extras
								.getParcelable(BleService.EXTRA_DEVICE);
					
						Message msg = mHandler.obtainMessage(Global.MSG_SCALER_PAR_SET_RESULT);
						msg.arg1 = val[4];
						msg.obj  = device;
						mHandler.sendMessage(msg);
					}
				}
				

				
				
				
			/*	for(Scaler s : WorkService.scalers.values())
				{
					weight+= s.getWeight();
				}*/
				
				

			}else if (BleService.BLE_REQUEST_FAILED.equals(action)) {
				
				Log.e( TAG, "ble request failed");
				String address = intent.getExtras().getString(BleService.EXTRA_ADDR);
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_FAILERESULT);
				msg.obj = address;
				mHandler.sendMessage(msg);
				
			}
		}
		
	};
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public class LocalBinder extends Binder {
		public WorkService getService() {
			return WorkService.this;
		}
	}
	
	@Override
	public void onCreate() {
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle != null) {
			Log.e("WorkService", "getBLE failed");
			
		} 
		BluetoothAdapter adpter=BluetoothAdapter.getDefaultAdapter();
		adpter.enable();
		scalers = new HashMap<String, Scaler>();
		scalers2 = new HashMap<Integer, Scaler>();
		mPrinterAddress = WorkService.getPrinterAddress(this);
		if(mPrinterAddress == null || mPrinterAddress=="")
		{
			mPrinterAddress = "00:02:0A:03:C3:BC";
			WorkService.setPrinterAddress(this, mPrinterAddress);
		}
		//WorkService.setDeviceAddress(this, 1,"C4:BE:84:22:8F:B0");
		WorkService.setDeviceAddress(this, 0,"C4:BE:84:22:91:E2");
		//WorkService.setDeviceAddress(this, 2,"C4:BE:84:22:8F:C8");
		for(int i = 0 ; i < max_count; i++)
		{
			String addr = WorkService.getDeviceAddress(this, i);
			
			if(addr != null && addr != "")
			{
				 if(!scalers.containsKey(addr)) //不包含这个地址才创建新的称台设备.
				 {
					 Scaler scaler =  new Scaler(addr);
					 scalers.put(addr,scaler);
					 scalers2.put(i, scaler);
				 }
								
			}
		}
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		mHandler = new MHandler(this);
		workThread = new WorkThread(mHandler);
		workThread.start();
		//_threadRead = new Thread(new ReadThread());
		//_threadRead.start();
		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		
		Log.v("DrawerService", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("DrawerService", "onStartCommand");
		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {

		workThread.quit();
		workThread = null;
		Log.v("DrawerService", "onDestroy");
	}
	public void get()
	{
		
	}
	static class MHandler extends Handler {

		WeakReference<WorkService> mService;

		MHandler(WorkService service) {
			mService = new WeakReference<WorkService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			notifyHandlers(msg);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void addHandler(Handler handler) {
		if (!targetsHandler.contains(handler)) {
			targetsHandler.add(handler);
		}
	}

	/**
	 * 
	 * @param handler
	 */
	public static void delHandler(Handler handler) {
		if (targetsHandler.contains(handler)) {
			targetsHandler.remove(handler);
		}
	}

	/**
	 * 
	 * @param msg
	 */
	public static void notifyHandlers(Message msg) {
		for (int i = 0; i < targetsHandler.size(); i++) {
			Message message = Message.obtain(msg);
			targetsHandler.get(i).sendMessage(message);
		}
	}
	public static void startScan()
	{
		
		if(mBle != null)
		mBle.startScan();
	}
	public static  void stopScan()
	{
		if(mBle != null)
			mBle.stopScan();
	}
	public static String formatUnit(String kg)
	{
		return kg + WorkService.strUnit;
	}
	public static  boolean requestConnect(String address)
	{
		if(mBle == null) return false;
		return mBle.requestConnect(address);
	}
	public static  void requestDisConnect(String address)
	{
		if(mBle == null) return ;
		mBle.disconnect(address);
	}
	public static  void requestDisConnectAll()
	{
		if(mBle == null) return ;
	
		 mBle.disconnectAll();
	}
	public static boolean adapterEnabled()
	{
		if(mBle == null) return false;
		return mBle.adapterEnabled();
	}
	public static boolean hasConnected(String address)
	{
		if(mBle == null) return false;
		return mBle.hasConnected(address);
	}
	public static boolean requestValue(String address,String cmd)
	{
		if(mBle == null) return false;

		BleGattCharacteristic chars = scalers.get(address).GetBleChar();
		//BleGattCharacteristic chars = mChars.get(address);
		if(chars == null) return false;
		
		chars.setValue(cmd);
		
		return mBle.requestWriteCharacteristic(address, chars, "false");

	}
	public static boolean requestWriteParamValue(String address,ScalerParam s)
	{
		if(mBle == null) return false;

		BleGattCharacteristic chars = scalers.get(address).GetBleChar();
		//BleGattCharacteristic chars = mChars.get(address);
		if(chars == null) return false;
		if(s == null) return false;
		chars.setValue(s.getSetCmdBuffer());
		
		return mBle.requestWriteCharacteristic(address, chars, "false");

	}
	public static boolean requestCalibZero(String address)
	{
		return requestValue(address, "MSV?;");
	}
	public static boolean requestReadWgt(String address)
	{
		
		return requestValue(address, "ADV?;");

	}
	public static boolean requestReadNov(String address)
	{
		
		return requestValue(address, "NOV?;");

	}
	public static boolean requestReadStillMonitor(String address)
	{
		return requestValue(address, "MTD?;");
	}
	public static boolean requestReadZTE(String address)
	{
		return requestValue(address, "ZTE?;");
	}
	public static boolean requestReadZSE(String address)
	{
		return requestValue(address, "ZSE?;");
	}
	public static boolean requestReadENU(String address)
	{
		return requestValue(address, "ENU?;");
	}
	public static boolean requestReadDPT(String address)
	{
		return requestValue(address, "DTP?;");
	}
	public static boolean requestReadRSN(String address)
	{
		return requestValue(address, "RSN?;");
	}
	public static boolean requestReadPar(String address)
	{
		return requestValue(address, "PAR?;");
	}
	public static boolean requestWritePar(String address)
	{
		//return requestValue(address, "PAR?;");
		return true;
	}
	public static boolean hasConnectPrinter()
	{
		return WorkService.workThread.isConnected();
	}
	public static boolean requestPrint(WeightRecord data)
	{
		byte[] header =  { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,0x01 };
		byte[] setHT = {0x1b,0x44,0x10,0x00};
		byte[] HT = {0x09};
		byte[] LF = {0x0d,0x0a};
		if(!hasConnectPrinter()) return false;
		byte[][] allbuf = new byte[][]{header,
				setHT,"流水号".getBytes(),HT,data.getID().getBytes(),LF,LF,
				
				setHT,"日期".getBytes(),HT,data.getFormatDate().getBytes(),LF,
				setHT,"时间".getBytes(),HT,data.getFormatTime().getBytes(),LF,
				setHT,"毛重".getBytes(),HT,WorkService.formatUnit(data.getGross()).getBytes(),LF,
				setHT,"皮重".getBytes(),HT,WorkService.formatUnit(data.getTare()).getBytes(),LF,
				setHT,"净重".getBytes(),HT,WorkService.formatUnit(data.getNet()).getBytes(),LF,LF,
				};
		byte[] buf = DataUtils.byteArraysToBytes(allbuf);
		if (WorkService.workThread.isConnected()) {
			Bundle d = new Bundle();
			d.putByteArray(Global.BYTESPARA1, buf);
			d.putInt(Global.INTPARA1, 0);
			d.putInt(Global.INTPARA2, buf.length);
			WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, d);
		} else {
			//打印机未连接
			return false;
		}
		return true;
		
	}
	public static String getPrinterAddress(Context pCtx)
	{
		
		return Config.getInstance(pCtx).getPrinterAddress();
	}
	public static void setPrinterAddress(Context pCtx,String address)
	{	
		 Config.getInstance(pCtx).setPrinterAddress(address);
	}
	public static String getDeviceAddress(Context pCtx,int index)
	{
		
		return Config.getInstance(pCtx).getDevAddress(index);
	}
	public static void setDeviceAddress(Context pCtx, int index,String address)
	{
		 Config.getInstance(pCtx).setDevAddress(index,address);
		 if(!scalers.containsKey(address)) //不包含这个地址才创建新的称台设备.
		 {
			 Scaler scaler = new Scaler(address);
			 scalers.put(address, scaler);
			 scalers2.put(index, scaler);
		 }
	}
	/*
	 * for(Scaler dev : WorkService.scalers.values())
		{
			if(dev.isConnected())
			{
				WorkService.requestReadWgt(dev.getAddress());
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	 * */
	public static boolean connectAll()
	{
		boolean need_connect = false;
		if(WorkService.hasConnectAll()) return true;
		if(WorkService.scalers.size() < max_count)
		{
			return false;
		}
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //不包含这个地址才创建新的称台设备.
			 {
				 Scaler dev = scalers2.get(i);
				 if(dev!=null)
				 {
					 WorkService.requestConnect(dev.getAddress());
					 need_connect = true;
				 }
				
			 }
								
		
		}
		
	/*	for(Scaler dev : WorkService.scalers.values())
		{
			if(!dev.isConnected())
			{
				WorkService.requestConnect(dev.getAddress());
				need_connect = true;
			}
		}*/
				
		return !need_connect;
	}
	public static boolean hasConnectAll()
	{
		boolean need_connect = false;
		if(WorkService.scalers.size() < max_count)
		{
			return false;
		}
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //包含这个地址才获取称台设备.
			 {
				 Scaler dev = scalers2.get(i);
				 
				if(dev!=null && !dev.isConnected())
				{
					need_connect = true;
					break;
				}
			 }
								
		
		}
		/*for(Scaler dev : WorkService.scalers.values())
		{
			if(!dev.isConnected())
			{
				need_connect = true;
				break;
			}
		}*/
		return !need_connect;
	}
	public static boolean readAllWgt()
	{
		if(!hasConnectAll()) return false;
		for(Scaler dev : WorkService.scalers.values())
		{
			WorkService.requestReadWgt(dev.getAddress());
		}
		return true;
	}
	public static void connectPrinter(String address)
	{
		if(WorkService.workThread.isConnected()) return;
		if(address == null)
		{
			WorkService.workThread.connectBt(mPrinterAddress);
			return;
		}
		WorkService.workThread.connectBt(address);
	}
	public static String getPrinterAddress()
	{
		return mPrinterAddress;
	}
	public static int getScalerCount()
	{
		return scalers.size();
	}
	public static String getScalerAddress(int index)
	{
		if(index >= getScalerCount()) return null;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return null;
		
		return s.getAddress();
	}
	public static boolean getScalerConnectState(int index)
	{
		if(index >= getScalerCount()) return false;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return false;
		
		return s.isConnected(); 
		
	}
}

