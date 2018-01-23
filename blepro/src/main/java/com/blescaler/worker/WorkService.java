package com.blescaler.worker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.blescaler.util.Register;
import com.blescaler.util.Utils;
import com.blescaler.util.Config;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleRequest.FailReason;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

/** @defgroup SDKInterface SDK接口 
 *  SDK对外的接口函数
 *  @{
 */

/**
   # SDK主服务,提供了蓝牙称管理的所有接口
   
  	 - 提供蓝牙4.0设备的扫描和连接接口
   	 - 蓝牙断线重连和信号检测
   	 - 蓝牙BLE可靠性重发机制
   	 - 基于蓝牙的数据分包处理机制
 	 - 提供称重协议发送和接收
 	 	+ 读取重量
 		+ 标定和角差
 	 	+ 称重参数设置
 		+ 称重状态显示[毛皮状态,稳定，零点]
 		+ 设备控制[led，去皮，清零等]
 		
   	
 */


public class WorkService extends Service {

	// Service和workThread通信用mHandler
	private BleService mService = null;
	
	private static Handler mHandler = null;
	private static Context  myCtx = null;
	private static List<Handler> targetsHandler = new ArrayList<Handler>(5); 
	public static  Map<String,Scaler> scalers;
	
	private static IBle mBle;
	private static String TAG = "WorkSrv";
	
	private static int max_count = 0;	//蓝牙秤设备个数.
	
	private static HashMap<Integer,CmdObject> m_cmd_queue = new HashMap<Integer,CmdObject>();
	private static Object cmd_lock = new Object();
	private static Object scaler_lock = new Object();
	
	
	 //! A constructor.
    /*!
      A more elaborate description of the constructor.
    */

	/////////////////////////////////////////////////
	private Thread reConnThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while(true)
			{
				
				synchronized (cmd_lock) {
					Iterator<Map.Entry<Integer,CmdObject>> it = m_cmd_queue.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry<Integer,CmdObject> entry = it.next();
						CmdObject o = entry.getValue();
						if(o!=null)
						{
							if(o.isTimeout())
							{
								write_buffer(o.address,o.value);
							}
							if(o.needRemove())
							{
								it.remove();
							}
							
						}
					}
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	
	private static void recv_object(byte[] cmd)
	{
		int reg_addr = cmd[3];
		if(reg_addr == Global.REG_WEIGHT || reg_addr==Global.REG_BATTERY)
		   return;
		synchronized (cmd_lock) {
			if(m_cmd_queue.containsKey(reg_addr))
			{
				m_cmd_queue.remove(reg_addr);
				
			}
		}
		
	}
	
	private  static boolean addCmd(String addr,byte[] cmd)
	{
		int reg_addr = cmd[3]; 
		CmdObject o = null;
		synchronized (cmd_lock) {
			if(m_cmd_queue.containsKey(reg_addr))
			{
				o = m_cmd_queue.get(reg_addr);
				o.reset();
				return true;
			}
			o = new CmdObject(addr,cmd);
			m_cmd_queue.put(reg_addr, o);
		}
		return true;
		
	}
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
				Toast.makeText(getApplicationContext(), addr+"收到断开...",Toast.LENGTH_SHORT).show();	
						
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
				
					//intent.putExtra(BleService.EXTRA_RSSI, rssi);
					int rssi = intent.getExtras().getInt(BleService.EXTRA_RSSI);
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_SCANRESULT);
					
					msg.obj = device;
					msg.arg1 = rssi;
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
					Log.e("service",address+"can not find service");
					return;
				}
				
				BleGattCharacteristic chars = bgs.getCharacteristic(
						UUID.fromString(Utils.UUID_DATA));
				
				if(chars != null)
				{
					
					Scaler s = scalers.get(address);
					if(s != null)
					{
						Log.e("Scaler", address + "service discory ok");
						
					}
					//启动数据接收通知.
					if(!mBle.requestCharacteristicNotification(address, chars))
					{
						Toast.makeText(getApplicationContext(), "服务发现成功，请求启用通知失败!",Toast.LENGTH_SHORT).show();
					}
					else
					{
						//Toast.makeText(getApplicationContext(), "服务发现成功",Toast.LENGTH_SHORT).show();
					}
				}
				
				
			}else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				//启用通知成功.
				Bundle extras = intent.getExtras();
				String address = intent.getExtras().getString(BleService.EXTRA_ADDR);
				boolean mNotifyStarted = extras.getBoolean(BleService.EXTRA_VALUE);
				if(mNotifyStarted)
				{
					
	
					BleGattService bgs = mBle.getService(address,UUID.fromString(Utils.UUID_SRV));
					if(bgs == null)
					{
						Log.e("service",address+"can not find service");
						return;
					}
					
					BleGattCharacteristic chars = bgs.getCharacteristic(
							UUID.fromString(Utils.UUID_DATA));
					
					if(chars != null)
					{
						
						Scaler s = scalers.get(address);
						if(s != null)
						{
							Log.e("Scaler", address + "connect ok");
							s.setConnected(true, chars);
							Message msg = mHandler.obtainMessage(Global.MSG_SCALER_CONNECT_OK);
							msg.obj = address;
						
							mHandler.sendMessage(msg);
						}
						
					}
					else
					{
						Log.e("Scaler", address + "can not find chars");
					}
					
					
					//Toast.makeText(getApplicationContext(), "enable notify"+extras.getString(BleService.EXTRA_ADDR), Toast.LENGTH_SHORT).show();
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
				
				recv_object(val);
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
				int  reason =  b.getInt(BleService.EXTRA_REASON);
			
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_FAILERESULT);
				msg.obj = address;
				msg.arg1 = type.ordinal();
				msg.arg2 = reason;
				mHandler.sendMessage(msg);
				
			}
		}
		
	};
	//读取某个称的寄存器.

	private static boolean  write_buffer(String address,byte[] value)
	{
		if(value[3] != Global.REG_WEIGHT && value[3] != Global.REG_BATTERY)
		{
			addCmd(address,value);
		}
		return write_buffer2(address,value);
	}
	//发送数据给连接了的设备.
	private static boolean  write_buffer2(String address,byte[] value)
	{
		if(mBle == null) return false;
	
	
		 Scaler dev = scalers.get(address);
		 
		 if(dev !=null && dev.isConnected())
		 {
			BleGattCharacteristic chars = dev.GetBleChar();
			if(chars == null) return false;
				
			chars.setValue(value);
			
			return mBle.requestWriteCharacteristic(dev.getAddress(), chars, "false");
		 }
	
		return true;
		
		
		
	}
	public static String getFailType(int type)
	{
		String err = "unkown";
		if(type == FailReason.RESULT_FAILED.ordinal())
		{
			err = "结果失败";
		}
		else if(type == FailReason.TIMEOUT.ordinal())
		{
			err= "请求超时";
		}
		else if(type == FailReason.START_FAILED.ordinal())
		{
			err ="请求失败";
		}
		return err;
	}
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
			type = "读取描述符失败";
		}
		else if(reason ==  RequestType.READ_RSSI.ordinal())
		{
			type = "读取RSSI失败";
		}
		else if(reason ==  RequestType.WRITE_CHARACTERISTIC.ordinal())
		{
			type = "写入特征失败";
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
	private boolean init()
	{
	
		BluetoothAdapter adpter=BluetoothAdapter.getDefaultAdapter();
		if(adpter!=null)
		{
			adpter.enable();
		}else{
			Log.e("WorkService", "can not open ble");
		}
		
		scalers = new HashMap<String, Scaler>();

		registerReceiver(mBleReceiver, BleService.getIntentFilter());
	
		mHandler = new MHandler(this);
	
		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		
		Log.v("DrawerService", "onCreate");
		myCtx = this;
		reConnThread.start();
		
		return false;
		
	}
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService  = ((BleService.LocalBinder) rawBinder).getService();
			mBle = mService.getBle();
			init();
		}
	
		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
		
		
	};
	
	@Override
	public void onCreate() {
		
		Intent bindIntent = new Intent(this, BleService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
			
		
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
	
	
		Log.v("DrawerService", "onDestroy");
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
	 * 注册观察对象，sdk有任何数据返回的时候会通知注册者
	 * @param handler 注册的观察者
	 * @return 没有返回

	 */
	public static void addHandler(Handler handler) {
		if (!targetsHandler.contains(handler)) {
			targetsHandler.add(handler);
		}
	}
	
	/**
	 * 删除观察对象
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
	/**
	 * 启动扫描ble蓝牙设备
	 */
	public static void startScan()
	{
		
		if(mBle != null)
		mBle.startScan();
	}
	/**
	 * 启动扫描ble蓝牙设备
	 */
	//停止扫描ble蓝牙设备
	public static  void stopScan()
	{
		if(mBle != null)
			mBle.stopScan();
	}
/**
 * 请求连接某个秤的蓝牙地址
 * @param address 蓝牙称的地址
 * @return 请求是否成功，这里返回true 不表示立即成功,而需要调用hasConnected来判断是否真正的成功
 */
	
	public static  boolean requestConnect(String address)
	{
		synchronized (cmd_lock) {
			if(scalers == null)
			{
				Log.e("WorkService", "scalers==null");
				return false;
			}
			if(!scalers.containsKey(address))
			{
				scalers.put(address, new Scaler(address));
			}
		}
		if(mBle == null) 
		{
			Log.e("WorkService", "mBle==null");
			return false;
		}
		return mBle.requestConnect(address);
	}
	/**
	 * 请求断开某个秤的蓝牙地址
	 * @param address 蓝牙称的地址
	 * @return 请求是否成功，这里返回true 不表示立即成功,而需要调用hasConnected来判断是否真正的成功
	 */	
	public static  void requestDisConnect(String address)
	{
		synchronized (cmd_lock) {
			if(scalers.containsKey(address))
			{
				scalers.remove(address);
			}
		}
		if(mBle == null) return ;
		mBle.disconnect(address);
	}
	/**
	 * 判断手机蓝牙BLE是否已经开启
	 * @return true 开启 false 未开启
	 */		
	
	public static boolean adapterEnabled()
	{
		if(mBle == null) return false;
		return mBle.adapterEnabled();
	}
	/**
	 * 判断某个蓝牙地址是否已经连接
	 * @param address
	 * @return
	 */
	public static boolean hasConnected(String address)
	{
		if(mBle == null) return false;
		return mBle.hasConnected(address);
	}
	
	
	/**
	 * 读取某个蓝牙称所有的角差系数
	 * @param address
	 * @return
	 */
	public static boolean  read_all_ks(String address)
	{
	
		try{
			Register reg = new Register();
			//1st
			
			write_buffer(address,reg.BeginRead(Global.REG_SENSOR_DIFF_K1,4));
			Thread.sleep(200);
			//3rd
			
			write_buffer(address,reg.BeginRead(Global.REG_SENSOR_DIFF_K3,4));
			Thread.sleep(100);
	
		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	
		return true;
	}
	/**
	 * 向某个称的某个寄存器地址发送一个数据
	 * @param address 称地址
	 * @param reg_addr 寄存器地址
	 * @param value  写入的值
	 * @return
	 */
	public static boolean  common_msg(String address,int reg_addr,int value )
	{
		Register reg = new Register();
		reg.BeginWrite(reg_addr);
		reg.putShort((short) value);
				
		return write_buffer(address,reg.getResult());
	}
	
	/**
	 * 自动角差标定
	 * @param address 称地址
	 * @param index 标定的是第几个传感器
	 * @return
	 */
	public static boolean  auto_k(String address,int index)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_AUTO_DIFF_CALIB_INDEX);
		reg.putShort((short) index);
				
		return write_buffer(address,reg.getResult());
	}
	/**
	 * 手动角差标定
	 * @param address 称地址
	 * @param index 标定的是第几个传感器
	 * @param value 修改的值 该系数是放大了100倍
	 * @return
	 */
	public static boolean  hand_k(String address,int index, int value)
	{
		
		Register reg = new Register();
		reg.BeginWrite(Global.REG_SENSOR_DIFF_K1+index*2);
		//reg.putShort((short) index);
		reg.putInt(value);
		
		return write_buffer(address,reg.getResult());
	
	}

	/**
	 * 读取某个称每一路的AD值
	 * @param address 称地址
	 * @return
	 */
	public static boolean requestReadAds(String address)
	{
		try{
			Register reg = new Register();
			//1st
			
			write_buffer(address,reg.BeginRead(Global.REG_AD_CHAN1,4));
			Thread.sleep(100);
			//3rd
			
			write_buffer(address,reg.BeginRead(Global.REG_AD_CHAN3,4));
			Thread.sleep(100);
	
		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	
		return true;
	}
	/**
	 * 标定零点
	 * @param address 称地址
	 * @return
	 */
	public static boolean CalibZero(String address) 
	{
		Register reg = new Register();
		//1st
		reg.BeginWrite(Global.REG_CALIB_INDEX);
		reg.putShorts((short) 0,(short)1);
	
		return write_buffer(address,reg.getResult());
	}
	/**
	 * 标定某个称的重量
	 * @param address 称地址
	 * @param point 标定点
	 * @param calibWet 标定的砝码重量
	 * @return
	 */
	public static boolean CalibK(String address,int point,int calibWet) 
	{
		Register reg = new Register();
		//1st
		reg.BeginWrite(Global.REG_CALIB_INDEX);
		reg.putShorts((short) point,(short)1);
		if(point > 0)
			reg.putInt(calibWet);
		return write_buffer(address,reg.getResult());
	
	}
	/**
	 * 请求读取计量参数
	 * @param address 称地址
	 * @return
	 * @throws InterruptedException
	 */
	
	public static boolean requestReadPar(String address) throws InterruptedException
	{
		Register reg = new Register();
		
		write_buffer(address,reg.BeginRead(Global.REG_DOTNUM,1));
		
		
		Thread.sleep(50);
		write_buffer(address,reg.BeginRead(Global.REG_DIV1,5));//分度数
		
		//read_registers(address,Global.REG_DIV1,5); //分度数
		Thread.sleep(50);
		write_buffer(address,reg.BeginRead(Global.REG_UNIT,6));//单位
		//read_registers(address,Global.REG_UNIT,6);//单位
		Thread.sleep(50);
		write_buffer(address,reg.BeginRead(Global.REG_SLEEP_S,2));//休眠时间
		//read_registers(address,Global.REG_SLEEP_S,2);//休眠时间
		return true;
	}
	/**
	 * 请求写入计量参数
	 * @param address 称地址
	 * @param s 称参数
	 * @return
	 * @throws InterruptedException
	 */
	public static boolean requestWriteParamValue(String address,ScalerParam s)
	{
		if(mBle == null) return false;
		if(s == null) return false;
		Scaler scaler = scalers.get(address);
		if(scaler==null) return false;
		BleGattCharacteristic chars = scaler.GetBleChar();
	
		if(chars == null) return false;
		
		
		try{
			Register reg = new Register();
			//1st
			reg.BeginWrite(Global.REG_DIV1);
			
			reg.putShorts((short)s.getResultionx(),(short)s.getResultionx());	
			reg.putInts(s.getNov());	
			
			write_buffer(address,reg.getResult());
			Thread.sleep(200);
			
			//2nd
			reg.BeginWrite(Global.REG_UNIT);
			reg.putShorts(s.getUnit(),s.getPwr_zerotrack(),s.getHand_zerotrack());
			
			write_buffer(address,reg.getResult());
			Thread.sleep(200);
			
			reg.BeginWrite(Global.REG_ZERO_TRACK_SPAN);
			reg.putShorts(s.getZerotrack(),s.getMtd(),s.getFilter());
			write_buffer(address,reg.getResult());
			Thread.sleep(200);
			
			//3rd
			reg.BeginWrite(Global.REG_DOTNUM); //dot
			reg.putShort(s.getDignum());
			write_buffer(address,reg.getResult());
			Thread.sleep(200);
			
			reg.BeginWrite(Global.REG_SLEEP_S); //dot
			reg.putShort(s.getSleep());
			reg.putShort(s.getSnr_num());
			write_buffer(address,reg.getResult());
			Thread.sleep(200);
		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	
		
		return mBle.requestWriteCharacteristic(address, chars, "false");
	
	}
	/**
	 * 读取剩余电量
	 * @param address
	 * @return
	 */
	public static boolean readPower(String address)
	{
		Register reg = new Register();
		
		return write_buffer(address,reg.BeginRead(Global.REG_BATTERY,1));
		
		
		//return read_registers(address,(short)Global.REG_BATTERY, (short)1);
	}
	/**
	 * 读取某个秤的重量值
	 * @param address
	 * @return
	 */
	
	public static boolean requestReadWgt(String address)
	{
		Register reg = new Register();
		
		return write_buffer(address,reg.BeginRead(Global.REG_WEIGHT, 1));
		
		//return read_registers(address,(short)Global.REG_WEIGHT, (short)7);
	}
	public static boolean requestReadWgtV2(String address)
	{
		Register reg = new Register();
		
		return write_buffer(address,reg.BeginRead(Global.REG_WEIGHT_V2, 7));
		
		//return read_registers(address,(short)Global.REG_WEIGHT, (short)7);
	}
	
	
	//获取指定地址的称台设备.
	public static Scaler getScaler(String addr)
	{
		return scalers.get(addr);
	}
	
	public static int getQueSize()
	{
		if(mBle==null) return 0;
		return mBle.getQueueSize();
	}
	
	public static String getDeviceAddress(Context pCtx,int index)
	{
		
		return Config.getInstance(pCtx).getDevAddress(index);
	}

	public static void setDeviceAddress(Context pCtx, int index,String address)
	{
		 Config.getInstance(pCtx).setDevAddress(index,address);
		
		 
	}
	public static int getDeviceCount(Context pCtx)
	{
		return Config.getInstance(pCtx).getScalerCount();	
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

		
	}
	public static boolean discardTare(String address)
	{
		return common_msg(address,Global.REG_OPERATION,2);
	}
	/**
	 * 清零
	 * @param address
	 * @return
	 */
	public static boolean setZero(String address)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_OPERATION);
		reg.putShort((short) 1);
		return write_buffer(address, reg.getResult());
	}
	public static int getScalerCount()
	{
		if(scalers==null) return 0;
		return scalers.size();
	}
	/**
	 * 预置皮重,手工设置皮重,预置皮重后，状态更改为净重状态.
	 * @param address
	 * @param preTare 
	 * @return
	 */
	public static boolean setPreTare(String address,int preTare)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_TARE);
		reg.putInt(preTare);
		return write_buffer(address,reg.getResult());
		
	}
	/**
	 * 断开所有称的连接
	 */
	public static  void requestDisConnectAll()
	{
		if(mBle == null) return ;
		
		Iterator<Map.Entry<String,Scaler>> it = scalers.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String,Scaler> entry = it.next();
			Scaler s = entry.getValue();
			if(s!=null)
			{
				s.setConnected(false, null);
				mBle.disconnect(s.getAddress());
			}
		}
	}
	/**
	 * 控制灯
	 * @param address 
	 * @param index 
	 * @return
	 */
	
	public static boolean CtrlLight(String address,int index)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_LAMP_CTRL);
		reg.putShort((short) (index));
		return write_buffer(address,reg.getResult());
		
	}
	

}
/** @} */ // end of group1

