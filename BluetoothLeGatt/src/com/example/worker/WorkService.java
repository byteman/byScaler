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
	private static IBle mBle;
	private static final int REQUEST_ENABLE_BT = 1;
	private String TAG = "WorkSrv";
	private static String strUnit = "kg";
	
	private static Map<String, BleGattCharacteristic> mChars;
	class ReadThread implements Runnable{
		private boolean _quit = false;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!_quit)
			{
				boolean need_connect = false;
				if(WorkService.scalers.size() < 4)
				{
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				for(Scaler dev : WorkService.scalers.values())
				{
					if(!dev.isConnected())
					{
						WorkService.requestConnect(dev.getAddress());
						need_connect = true;
					}
				}
				if(need_connect) 
				{
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				for(Scaler dev : WorkService.scalers.values())
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
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	private Thread _threadRead;
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
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_DISCONNECTRESULT);
				final BluetoothDevice device = intent.getExtras()
						.getParcelable(BleService.EXTRA_DEVICE);
				String addr = device.getAddress();
				mChars.remove(addr);
				msg.obj = device;
				Scaler s = scalers.get(addr);
				if(s != null)
				{
					s.setConnected(false);
				}
				
				mHandler.sendMessage(msg);	
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
					mChars.remove(address);
					mChars.put(address, chars);
					Scaler s = scalers.get(address);
					if(s != null)
					{
						s.setConnected(true);
					}
					mBle.requestCharacteristicNotification(address, chars);
				}
				
				
			} else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				
				Bundle extras = intent.getExtras();
				boolean mNotifyStarted = extras.getBoolean(BleService.EXTRA_VALUE);
				if(mNotifyStarted)
				{
					Toast.makeText(getApplicationContext(), "notify", Toast.LENGTH_SHORT).show();
				}
			}  else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action))
			{
				Bundle extras = intent.getExtras();
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);
				
				int weight = Utils.bytesToInt(val);

			
				final BluetoothDevice device = extras
						.getParcelable(BleService.EXTRA_DEVICE);
			
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_WGTRESULT);
				msg.arg1 = weight;
				msg.obj  = device;
				mHandler.sendMessage(msg);

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
		mChars  = new HashMap<String, BleGattCharacteristic>();
		scalers = new HashMap<String, Scaler>();
		
		for(int i = 0 ; i < 4; i++)
		{
			String addr = WorkService.getDeviceAddress(this, i);
			if(addr != null)
			{
				scalers.put(addr, new Scaler(addr));
				
			}
		}
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		mHandler = new MHandler(this);
		workThread = new WorkThread(mHandler);
		workThread.start();
		_threadRead = new Thread(new ReadThread());
		_threadRead.start();
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

		BleGattCharacteristic chars = mChars.get(address);
		if(chars == null) return false;
		
		chars.setValue(cmd);
		
		return mBle.requestWriteCharacteristic(address, chars, "false");

	}
	public static boolean requestReadWgt(String address)
	{
		
		return requestValue(address, "MSV?;");

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
	public static boolean requestPrint(WeightRecord data)
	{
		byte[] setHT = {0x1b,0x44,0x18,0x00};
		byte[] HT = {0x09};
		byte[] LF = {0x0d,0x0a};
		byte[][] allbuf = new byte[][]{
				setHT,"流水号".getBytes(),HT,WorkService.formatUnit(data.getID()).getBytes(),LF,LF,
				setHT,"毛重".getBytes(),HT,WorkService.formatUnit(data.getGross()).getBytes(),LF,
				setHT,"皮重".getBytes(),HT,WorkService.formatUnit(data.getTare()).getBytes(),LF,
				setHT,"净重".getBytes(),HT,WorkService.formatUnit(data.getNet()).getBytes(),LF,
				setHT,"时间".getBytes(),HT,data.getFormatTime().getBytes(),LF,LF,			
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
	public static String getDeviceAddress(Context pCtx,int index)
	{
		
		return Config.getInstance(pCtx).getDevAddress(index);
	}
	public static void setDeviceAddress(Context pCtx, int index,String address)
	{
		 Config.getInstance(pCtx).setDevAddress(index,address);
		 if(!scalers.containsKey(address)) //不包含这个地址才创建新的称台设备.
			 scalers.put(address, new Scaler(address));
	}
}

