package com.blescaler.worker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.R.bool;
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
import android.util.SparseArray;
import android.widget.Toast;

import com.blescaler.db.Config;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.BleApplication;
import com.blescaler.utils.Utils;
import com.lvrenyang.utils.DataUtils;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleRequest.FailReason;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

/**
 * 观察者模式
 * 
 * @author Administrator
 * 
 */
public class WorkService extends Service {

	// Service和workThread通信用mHandler
	
	public static WorkThread workThread = null; //打印服务工作线程
	private static Handler mHandler = null;
	private static Context  myCtx = null;
	private static List<Handler> targetsHandler = new ArrayList<Handler>(5); 
	public static Map<String,Scaler> scalers;
	private static Map<Integer,Scaler> scalers2;
	//private static SparseArray<Scaler> scalers2;
	private static IBle mBle;
	private static String TAG = "WorkSrv";
	private static String strUnit = "kg";
	private static int max_count = 1;	//蓝牙秤设备个数.
	private static String mPrinterAddress; //打印机蓝牙地址
	////////////////////称重变量////////////////////////////////
	private static int zero = 0; //零点重量
	private static int next = 0;
	private static int tare = 0; //皮重
	private static int tmp_tare = 0; //临时皮重
	private static int gross= 0; //毛重,从秤上直接读取的重量
	private static int net  = 0; //净重 = 毛重-皮重-零点重量.
	private static boolean is_net_state = false; // 是否是净重状态,默认是毛重状态
	/////////////////////////////////////////////////
	
	
	//蓝牙秤消息接收器.
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				//蓝牙连接成功.
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_CONNECTRESULT);
				final BluetoothDevice device = intent.getExtras()
						.getParcelable(BleService.EXTRA_DEVICE);
			
				
				msg.obj = device;	//连接成功的蓝牙设备
				mHandler.sendMessage(msg);
				
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				//蓝牙断开.
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
				//手机不支持蓝牙4.0
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_NOT_SUPPORT);
				mHandler.sendMessage(msg);	
			} 
			else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				//扫描到一个ble设备.
					Bundle extras = intent.getExtras();
					final BluetoothDevice device = extras
							.getParcelable(BleService.EXTRA_DEVICE);
				
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_SCANRESULT);
					msg.obj = device;
					mHandler.sendMessage(msg);
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
				//手机不支持蓝牙
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_NO_BT_ADAPTER);
				mHandler.sendMessage(msg);	
			}else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				//ble设备的服务枚举完毕.
				String address = intent.getExtras().getString(BleService.EXTRA_ADDR);
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_SERVICEDISRESULT);
				msg.obj = address;
				mHandler.sendMessage(msg);
				//获取对方蓝牙模块[数据通讯]特征描述符
				
				BleGattService bgs = mBle.getService(address,UUID.fromString(Utils.UUID_SRV));
				if(bgs == null)
				{
					Log.e("service",address+"lost");
					return;
				}
				
				BleGattCharacteristic chars = bgs.getCharacteristic(
						UUID.fromString(Utils.UUID_DATA));
				
				if(chars != null)
				{
					
					Scaler s = scalers.get(address);
					if(s != null)
					{
						s.setConnected(true,chars);
					}
					//启动数据接收通知.
					if(!mBle.requestCharacteristicNotification(address, chars))
					{
						Toast.makeText(getApplicationContext(), "通知失败!",Toast.LENGTH_SHORT).show();
					}
					
				}
				
				
			}else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				//启用通知成功.
				Bundle extras = intent.getExtras();
				boolean mNotifyStarted = extras.getBoolean(BleService.EXTRA_VALUE);
				if(mNotifyStarted)
				{
					Toast.makeText(getApplicationContext(), "enable notify"+extras.getString(BleService.EXTRA_ADDR), Toast.LENGTH_SHORT).show();
				}
			}else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action))
			{
				//有对方通知数据返回.
				Bundle extras = intent.getExtras();
				String addr = extras.getString(BleService.EXTRA_ADDR);
				Scaler d = WorkService.scalers.get(addr);
				if(d==null) 
				{
					Log.e(TAG,"get data but can not find address"+addr+"");
					return;
				}
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);
				if(val.length < 4)
				{
					return;
				}
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_FAILERESULT);
				
				
				int code = d.parseData(val, msg);
				if(code == 0)
				{
					return;
				}
				msg.what = code;
				//msg.arg1 = d.getWeight();
				mHandler.sendMessage(msg);
				
		}		
		else if (BleService.BLE_REQUEST_FAILED.equals(action)) {
				//命令请求失败,分析是那个命令，决定是否重新发送.
				Bundle b = intent.getExtras();
				if(b == null) return;
				
				String address 	 = b.getString(BleService.EXTRA_ADDR);
				RequestType type = (RequestType) b.getSerializable(BleService.EXTRA_REQUEST);
				//FailReason  reason =  (FailReason) b.getSerializable(BleService.EXTRA_REASON);
			
				if(type == RequestType.CHARACTERISTIC_NOTIFICATION)
				{
					Scaler s = scalers.get(address);
					if(s == null)
					{
						return;
					}
					BleGattService bgs = mBle.getService(address,UUID.fromString(Utils.UUID_SRV));
					if(bgs == null)
					{
						Log.e("service",address+"lost");
						return;
					}
					
					BleGattCharacteristic chars = bgs.getCharacteristic(
							UUID.fromString(Utils.UUID_DATA));
					if(chars==null) return;
					//启动数据接收通知.
					if(!mBle.requestCharacteristicNotification(address,chars))
					{
						Toast.makeText(getApplicationContext(), "通知失败!",Toast.LENGTH_SHORT).show();
					}
					
				}
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_FAILERESULT);
				msg.obj = address;
				msg.arg1 = type.ordinal();
				//msg.arg2 = reason.ordinal();
				mHandler.sendMessage(msg);
				
			}
		}
		
	};
	public static String getFailReason(int reason)
	{
		String type = "未知原因";
		if(reason ==  RequestType.CHARACTERISTIC_NOTIFICATION.ordinal())
		{
			type = "启动通知请求失败";
		}
		else if(reason ==  RequestType.CONNECT_GATT.ordinal())
		{
			type = "连接服务失败";
		}
		else if(reason ==  RequestType.DISCOVER_SERVICE.ordinal())
		{
			type = "枚举服务失败";
		}
		else if(reason ==  RequestType.CONNECT_GATT.ordinal())
		{
			type = "连接服务失败";
		}
		else if(reason ==  RequestType.CHARACTERISTIC_INDICATION.ordinal())
		{
			type = "特征指示失败";
		}
		else if(reason ==  RequestType.READ_CHARACTERISTIC.ordinal())
		{
			type = "读取特征失败";
		}
		else if(reason ==  RequestType.READ_DESCRIPTOR.ordinal())
		{
			type = "读取特征失败";
		}
		else if(reason ==  RequestType.READ_RSSI.ordinal())
		{
			type = "读取特征失败";
		}
		else if(reason ==  RequestType.WRITE_CHARACTERISTIC.ordinal())
		{
			type = "读取特征失败";
		}
		else if(reason ==  RequestType.CHARACTERISTIC_STOP_NOTIFICATION.ordinal())
		{
			type = "停止通知失败";
		}
		else if(reason ==  RequestType.WRITE_DESCRIPTOR.ordinal())
		{
			type = "写描述符失败";
		}
		return type;
		 
	}
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
	
	private static void loadScalerConfig(Context ctx)
	{
		//WorkService.setDeviceAddress(this, 1,"C4:BE:84:22:8F:B0");
		scalers.clear();
		scalers2.clear();
		//WorkService.setDeviceAddress(this, 0,"C4:BE:84:22:91:E2");
		//WorkService.setDeviceAddress(this, 2,"C4:BE:84:22:8F:C8");
		max_count = Config.getInstance(ctx).getScalerCount();
		strUnit   = Config.getInstance(ctx).getUnit();
		for(int i = 0 ; i < max_count; i++)
		{
			String addr = WorkService.getDeviceAddress(ctx, i);
			String name = WorkService.getDeviceName(ctx, i);
			if(addr != null && addr != "")
			{
				 if(!scalers.containsKey(addr)) //不包含这个地址才创建新的称台设备.
				 {
					 Scaler scaler =  new Scaler(addr);
					 scaler.setName(name);
					 scalers.put(addr,scaler);
					 scalers2.put(i, scaler);
				 }
								
			}
		}	
	}
	@Override
	public void onCreate() {
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle == null) {
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
		loadScalerConfig(this);
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
		myCtx = this;
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
	//启动扫描ble蓝牙设备
	public static void startScan()
	{
		
		if(mBle != null)
		mBle.startScan();
	}
	//停止扫描ble蓝牙设备
	public static  void stopScan()
	{
		if(mBle != null)
			mBle.stopScan();
	}
	public static String formatUnit(String kg)
	{
		return kg + WorkService.strUnit;
	}
	//请求连接某个秤的蓝牙地址
	public static  boolean requestConnect(String address)
	{
		if(mBle == null) return false;
		return mBle.requestConnect(address);
	}
	//请求断开某个秤的蓝牙地址
	public static  void requestDisConnect(String address)
	{
		if(mBle == null) return ;
		mBle.disconnect(address);
	}
	//请求断开所有秤的蓝牙地址
	public static  void requestDisConnectAll()
	{
		if(mBle == null) return ;
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //包含这个地址才获取称台设备.
			 {
				Scaler dev = scalers2.get(i);
				 
				if(dev!=null)
				{
					dev.setConnected(false, null);
					mBle.disconnect(dev.getAddress());
				}
			 }
								
		
		}
		
		// mBle.disconnectAll();
	}
	//判断手机蓝牙是否启用
	public static boolean adapterEnabled()
	{
		if(mBle == null) return false;
		return mBle.adapterEnabled();
	}
	//判断某个蓝牙地址是否已经连接
	public static boolean hasConnected(String address)
	{
		if(mBle == null) return false;
		return mBle.hasConnected(address);
	}
	
	private static boolean requestValue(String address,String cmd)
	{
		if(mBle == null) return false;

		Scaler s = scalers.get(address);
		if(s==null) return false;
		BleGattCharacteristic chars = s.GetBleChar();
		//BleGattCharacteristic chars = mChars.get(address);
		if(chars == null) return false;
		
		chars.setValue(cmd);
		
		return mBle.requestWriteCharacteristic(address, chars, "false");

	}
	//标定零点
	//address 设备地址  
	public static boolean requestCalibZero(String address) 
	{
		return requestValue(address, "CLZ;");
	}
	//标定重量.calibWet 标定重量值 nov 满量程
	public static boolean requestCalibK(String address,int calibWet,int nov) 
	{
		Scaler s = scalers.get(address);
		if(s==null) return false;
		if(nov==0)nov = 1000000;
		double tmp = (double)calibWet;
		double nov_full = 1000000;
		double nov_float = nov;
		double out = tmp * (nov_full/nov_float);
		int w = (int)out;
		
		String cmd = "CLK:" +w + ";";
		
		return requestValue(address, cmd);
	}
	//请求读取参数
	public static boolean requestReadPar(String address)
	{
		return requestValue(address, "PAR?;");
	}
	//请求修改参数,修改后的参数未保存
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
	//通知秤将参数写入内部eeprom
	public static boolean requestSaveParam(String address)
	{
		return requestValue(address, "SAV1;");
	}
	//读取某个秤的重量值
	public static boolean requestReadWgt(String address)
	{
		int size = getQueSize() ;
		if(size > 10) 
		{
			Log.e(TAG, "queue underflow " + size);
			return false;
		}
		return requestValue(address, "ADV?;");

	}
	//判断打印机是否已经连接
	public static boolean hasConnectPrinter()
	{
		return WorkService.workThread.isConnected();
	}
	//获取打印机的蓝牙地址
	public static String getPrinterAddress(Context pCtx)
	{
		
		return Config.getInstance(pCtx).getPrinterAddress();
	}
	//修改打印机的蓝牙地址.
	public static void setPrinterAddress(Context pCtx,String address)
	{	
		 Config.getInstance(pCtx).setPrinterAddress(address);
	}
	//打印榜单
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
	//连接指定地址的打印机
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
	//获取打印机地址
	public static String getPrinterAddress()
	{
		return mPrinterAddress;
	}
	//获取指定地址的称台设备.
	public static Scaler getScaler(String addr)
	{
		return scalers.get(addr);
	}
	public static Scaler getScaler(int id)
	{
		return scalers2.get(id);
	}
	public static int getQueSize()
	{
		if(mBle==null) return 0;
		return mBle.getQueueSize();
	}
	//获取地址序号的称的蓝牙地址
	public static String getDeviceAddress(Context pCtx,int index)
	{
		
		return Config.getInstance(pCtx).getDevAddress(index);
	}
	//修改地址序号的称的蓝牙地址
	public static void setDeviceAddress(Context pCtx, int index,String address)
	{
		 Config.getInstance(pCtx).setDevAddress(index,address);
		
		/* if(!scalers.containsKey(address)) //不包含这个地址才创建新的称台设备.
		 {
			 Scaler scaler = new Scaler(address);
			 scalers.put(address, scaler);
			 scalers2.put(index, scaler);
		 }*/
		 
	}
	public static void setDeviceName(Context pCtx, int index,String name)
	{
		 Config.getInstance(pCtx).setDevName(index,name);
	}
	public static String getDeviceName(Context pCtx, int index)
	{
		return Config.getInstance(pCtx).getDevName(index);
	}
	public static void saveDevicesAddress(Context pCtx, List<String> devs)
	{
		if(devs.size() == 0) 
		{
			return;
		}
		for(int i = 0 ; i   < devs.size(); i++)
		{
			setDeviceAddress(pCtx, i , devs.get(i));
		}
		
		max_count = devs.size();
		Config.getInstance(pCtx).setScalerCount(max_count);
		//修改地址后，重新加载地址列表.
		loadScalerConfig(pCtx);
	}
	//连接所有蓝牙秤,无论是否连接成功
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
				 if(dev!=null && dev.isConnected()!=true)
				 {
					 WorkService.requestConnect(dev.getAddress());
					 need_connect = true;
				 }
				
			 }
								
		
		}
		return !need_connect;
	}
	public static boolean connectNext()
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
		return !need_connect;
	}
	//所有称都已经连接否
	public static boolean hasConnectAll()
	{
		boolean need_connect = false;
		if(WorkService.scalers==null) return false;
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
		
		return !need_connect;
	}
	//读取所有称的重量.
	public static boolean readAllWgt()
	{
		if(!hasConnectAll()) return false;
		
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //包含这个地址才获取称台设备.
			 {
				 Scaler dev = scalers2.get(i);
				 
				 if(dev!=null && dev.isConnected())
				 {
					WorkService.requestReadWgt(dev.getAddress());
				 }
			 }
								
		
		}
		
		return true;
	}
	public static boolean readNextWgt(boolean needAllconnect)
	{
		if(needAllconnect) if(!hasConnectAll()) return false;
	
		if(next >= scalers2.size()) next = 0;
		Scaler dev = scalers2.get(next);
		 
		if(dev!=null && dev.isConnected())
		{
			WorkService.requestReadWgt(dev.getAddress());
		}
		next++;
		
		
		return true;
	}
	
	//获取秤的个数
	public static int getScalerCount()
	{
		if(scalers==null) return 0;
		return scalers.size();
	}
	//获取指定序号秤的蓝牙地址
	public static String getScalerAddress(int index)
	{
		if(index >= getScalerCount()) return null;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return null;
		
		return s.getAddress();
	}
	
	//获取指定序号秤的连接状态.
	public static boolean getScalerConnectState(int index)
	{
		if(index >= getScalerCount()) return false;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return false;
		
		return s.isConnected(); 
		
	}
	//获取所有秤加起来的重量.
	public static int getTotalWeight()
	{
		int totalweight = 0;
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //不包含这个地址才创建新的称台设备.
			 {
				Scaler dev = scalers2.get(i);
				
				if(dev!=null && dev.isConnected())
				{
					totalweight += dev.getWeight();
				}
			 }							
		}
		return totalweight;
	}
	//获取预置和去皮时保存的皮重
	public static int getSavedTareWeight()
	{
		return tmp_tare;
	}
	//获取当前的皮重.净重状态下才有皮重，毛重状态下皮重为0
	public static int getTareWeight()
	{
		if(is_net_state) return tare;
		return 0;
	}
	//获取毛重
	public static int getGrossWeight()
	{
		return getTotalWeight() - zero;
	}
	//获取净重,毛重状态下皮重为0，净重=毛重  净重状态下净重才是上次设置的值，净重=毛重-净重 .  实时显示的也是这个重量.
	public static int getNetWeight()
	{
		return getGrossWeight() - getTareWeight();
	}
	
	//置零当前重量
	public static boolean setZero()
	{
		if(is_net_state) return false; //净重状态不允许置零
		zero = getTotalWeight(); //毛重状态下，取所有称台总重为零点重量.
		return true;
	}
	//预置皮重,手工设置皮重,预置皮重后，状态更改为净重状态.
	public static boolean setPreTare(int preTare)
	{
		if(is_net_state) return false; //净重状态不允许置零
		tare = preTare;
		tmp_tare = tare;
		is_net_state = true;
		return true;
	}
	public static boolean isNetState()
	{
		return is_net_state;
	}
	//去皮，取当前的重量为皮重,去皮后更改为净重状态.净重状态不能去皮
	public static boolean discardTare()
	{
		if(is_net_state) return false;
		tare = getGrossWeight();
		tmp_tare = tare;
		is_net_state = true;
		return true;
	}
	//毛重和净重状态切换.
	public static boolean switchNetGross()
	{
		is_net_state=!is_net_state;
		if(is_net_state)
		{
			//如果手工切换到净重状态.
			tare = tmp_tare; //恢复上次保存的皮重.
		}
		else
		{
			//如果手工切换到了毛重状态.
			tare = 0; //将皮重设置为0，
		}
		return is_net_state;
	}
	public static String getUnit()
	{
		return strUnit;
	}
	public static boolean setUnit(String unit)
	{
		if(myCtx == null) return false;
		Config.getInstance(myCtx).setUnit(unit);
		strUnit = unit;
		return true;
	}
	private class CommandReceiver extends BroadcastReceiver{

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   int cmd=intent.getIntExtra("cmd", -1);
		   if(cmd==1){//如果等于0
		 
		     stopSelf();//停止服务
		    
		   }
		  }
		  
	}

}

