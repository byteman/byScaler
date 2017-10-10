package com.blescaler.worker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Formatter.BigDecimalLayoutForm;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

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



import com.blescaler.utils.BleApplication;
import com.blescaler.utils.Register;
import com.blescaler.utils.Utils;
import com.blescaler.utils.Config;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleRequest.FailReason;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

/**
 * 瑙傚療鑰呮ā寮�
 * 
 * @author Administrator
 * 
 */
public class WorkService extends Service {

	// Service鍜寃orkThread閫氫俊鐢╩Handler
	
	
	private static Handler mHandler = null;
	private static Context  myCtx = null;
	private static List<Handler> targetsHandler = new ArrayList<Handler>(5); 
	public static Map<String,Scaler> scalers;
	private static Map<Integer,Scaler> scalers2;
	//private static SparseArray<Scaler> scalers2;
	private static IBle mBle;
	private static String TAG = "WorkSrv";
	
	private static int max_count = 1;	//钃濈墮绉よ澶囦釜鏁�.
	private static String mPrinterAddress; //鎵撳嵃鏈鸿摑鐗欏湴鍧�
	private static BlockingQueue<byte[]> m_resend_queue = new ArrayBlockingQueue<byte[]>(10);
	//private static ConcurrentHashMap<Integer,CmdObject> m_cmd_queue = new ConcurrentHashMap<Integer,CmdObject>();
	private static HashMap<Integer,CmdObject> m_cmd_queue = new HashMap<Integer,CmdObject>();
	
	
	////////////////////绉伴噸鍙橀噺////////////////////////////////
	private static int zero = 0; //闆剁偣閲嶉噺
	private static int next = 0;
	private static int tare = 0; //鐨噸
	private static int tmp_tare = 0; //涓存椂鐨噸
	private static int gross= 0; //姣涢噸,浠庣Г涓婄洿鎺ヨ鍙栫殑閲嶉噺
	private static int net  = 0; //鍑�閲� = 姣涢噸-鐨噸-闆剁偣閲嶉噺.
	private static boolean is_net_state = false; // 鏄惁鏄噣閲嶇姸鎬�,榛樿鏄瘺閲嶇姸鎬�
	private Object lock = new Object();
	private static Object cmd_lock = new Object();
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
								write_buffer(o.value);
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
	private Thread reSendThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				try {
					byte[] cmd = m_resend_queue.take();
					if(cmd !=null)
					{
						write_buffer(cmd);
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	public void recv_object(byte[] cmd)
	{
		int reg_addr = cmd[3];
		if(reg_addr ==0 || reg_addr==47)
		   return;
		synchronized (cmd_lock) {
			if(m_cmd_queue.containsKey(reg_addr))
			{
				m_cmd_queue.remove(reg_addr);
				
			}
		}
		
	}
	public void notifyReconnect()
	{
		synchronized (lock) {
			lock.notify();
		}
	}
	static public boolean addCmd(byte[] cmd)
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
			o = new CmdObject(cmd);
			m_cmd_queue.put(reg_addr, o);
		}
		return true;
		
	}
	//钃濈墮绉ゆ秷鎭帴鏀跺櫒.
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				//钃濈墮杩炴帴鎴愬姛.
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_CONNECTRESULT);
				final BluetoothDevice device = intent.getExtras()
						.getParcelable(BleService.EXTRA_DEVICE);
			
				
				msg.obj = device;	//杩炴帴鎴愬姛鐨勮摑鐗欒澶�
				mHandler.sendMessage(msg);
				
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				//钃濈墮鏂紑.
				
				Bundle extras = intent.getExtras();
				if(extras == null) return;
								
				String addr = extras.getString(BleService.EXTRA_ADDR);
				Toast.makeText(getApplicationContext(), addr+"鏀跺埌鏂紑...",Toast.LENGTH_SHORT).show();	
						
				Scaler s = scalers.get(addr);
				if(s != null)
				{
					
					s.setConnected(false,null);
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_DISCONNECTRESULT);
					msg.obj = addr;
					mHandler.sendMessage(msg);	
				}
				notifyReconnect();			
				
			} 

			else if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				//鎵嬫満涓嶆敮鎸佽摑鐗�4.0
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_NOT_SUPPORT);
				mHandler.sendMessage(msg);	
			} 
			else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				//鎵弿鍒颁竴涓猙le璁惧.
					Bundle extras = intent.getExtras();
					final BluetoothDevice device = extras
							.getParcelable(BleService.EXTRA_DEVICE);
				
					Message msg = mHandler.obtainMessage(Global.MSG_BLE_SCANRESULT);
					msg.obj = device;
					mHandler.sendMessage(msg);
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
				//鎵嬫満涓嶆敮鎸佽摑鐗�
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_NO_BT_ADAPTER);
				mHandler.sendMessage(msg);	
			}else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				//ble璁惧鐨勬湇鍔℃灇涓惧畬姣�.
				String address = intent.getExtras().getString(BleService.EXTRA_ADDR);
				Message msg = mHandler.obtainMessage(Global.MSG_BLE_SERVICEDISRESULT);
				msg.obj = address;
				mHandler.sendMessage(msg);
				//鑾峰彇瀵规柟钃濈墮妯″潡[鏁版嵁閫氳]鐗瑰緛鎻忚堪绗�
				Log.e("Scaler", "discover_service notify");  
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
					//鍚姩鏁版嵁鎺ユ敹閫氱煡.
					if(!mBle.requestCharacteristicNotification(address, chars))
					{
						Toast.makeText(getApplicationContext(), "鏈嶅姟鍙戠幇鎴愬姛锛岃姹傚惎鐢ㄩ�氱煡澶辫触!",Toast.LENGTH_SHORT).show();
					}
					else
					{
						//Toast.makeText(getApplicationContext(), "鏈嶅姟鍙戠幇鎴愬姛",Toast.LENGTH_SHORT).show();
					}
				}
				
				
			}else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				//鍚敤閫氱煡鎴愬姛.
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
				//鏈夊鏂归�氱煡鏁版嵁杩斿洖.
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
				//鍛戒护璇锋眰澶辫触,鍒嗘瀽鏄偅涓懡浠わ紝鍐冲畾鏄惁閲嶆柊鍙戦��.
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
	public static String getFailType(int type)
	{
		String err = "unkown";
		if(type == FailReason.RESULT_FAILED.ordinal())
		{
			err = "缁撴灉澶辫触";
		}
		else if(type == FailReason.TIMEOUT.ordinal())
		{
			err= "璇锋眰瓒呮椂";
		}
		else if(type == FailReason.START_FAILED.ordinal())
		{
			err ="璇锋眰澶辫触";
		}
		return err;
	}
	public static String getFailReason(int reason)
	{
		String type = "鏈煡鍘熷洜";
		if(reason ==  RequestType.CHARACTERISTIC_NOTIFICATION.ordinal())
		{
			type = "鍚姩閫氱煡璇锋眰澶辫触";
		}
		else if(reason ==  RequestType.CONNECT_GATT.ordinal())
		{
			type = "杩炴帴鏈嶅姟澶辫触";
		}
		else if(reason ==  RequestType.DISCOVER_SERVICE.ordinal())
		{
			type = "鏋氫妇鏈嶅姟澶辫触";
		}
		else if(reason ==  RequestType.CONNECT_GATT.ordinal())
		{
			type = "杩炴帴鏈嶅姟澶辫触";
		}
		else if(reason ==  RequestType.CHARACTERISTIC_INDICATION.ordinal())
		{
			type = "鐗瑰緛鎸囩ず澶辫触";
		}
		else if(reason ==  RequestType.READ_CHARACTERISTIC.ordinal())
		{
			type = "璇诲彇鐗瑰緛澶辫触";
		}
		else if(reason ==  RequestType.READ_DESCRIPTOR.ordinal())
		{
			type = "璇诲彇鎻忚堪绗﹀け璐�";
		}
		else if(reason ==  RequestType.READ_RSSI.ordinal())
		{
			type = "璇诲彇RSSI澶辫触";
		}
		else if(reason ==  RequestType.WRITE_CHARACTERISTIC.ordinal())
		{
			type = "鍐欏叆鐗瑰緛澶辫触";
		}
		else if(reason ==  RequestType.CHARACTERISTIC_STOP_NOTIFICATION.ordinal())
		{
			type = "鍋滄閫氱煡澶辫触";
		}
		else if(reason ==  RequestType.WRITE_DESCRIPTOR.ordinal())
		{
			type = "鍐欐弿杩扮澶辫触";
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
		max_count = 1;//Config.getInstance(ctx).getScalerCount();
		
		for(int i = 0 ; i < max_count; i++)
		{
			String addr = WorkService.getDeviceAddress(ctx, i);
			String name = WorkService.getDeviceName(ctx, i);
			if(addr != null && addr != "")
			{
				 if(!scalers.containsKey(addr)) //涓嶅寘鍚繖涓湴鍧�鎵嶅垱寤烘柊鐨勭О鍙拌澶�.
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
		if(adpter!=null)
		{
			adpter.enable();
		}else{
			Log.e("WorkService", "can not open ble");
		}
		
		scalers = new HashMap<String, Scaler>();
		scalers2 = new HashMap<Integer, Scaler>();
		
		loadScalerConfig(this);
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		mHandler = new MHandler(this);
	
		//_threadRead = new Thread(new ReadThread());
		//_threadRead.start();
		Message msg = Message.obtain();
		msg.what = Global.MSG_ALLTHREAD_READY;
		notifyHandlers(msg);
		
		Log.v("DrawerService", "onCreate");
		myCtx = this;
		reConnThread.start();
		reSendThread.start();
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
	//鍚姩鎵弿ble钃濈墮璁惧
	public static void startScan()
	{
		
		if(mBle != null)
		mBle.startScan();
	}
	//鍋滄鎵弿ble钃濈墮璁惧
	public static  void stopScan()
	{
		if(mBle != null)
			mBle.stopScan();
	}
	
	//璇锋眰杩炴帴鏌愪釜绉ょ殑钃濈墮鍦板潃
	public static  boolean requestConnect(String address)
	{
		if(mBle == null) return false;
		return mBle.requestConnect(address);
	}
	//璇锋眰鏂紑鏌愪釜绉ょ殑钃濈墮鍦板潃
	public static  void requestDisConnect(String address)
	{
		if(mBle == null) return ;
		mBle.disconnect(address);
	}
	//璇锋眰鏂紑鎵�鏈夌Г鐨勮摑鐗欏湴鍧�
	public static  void requestDisConnectAll()
	{
		if(mBle == null) return ;
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //鍖呭惈杩欎釜鍦板潃鎵嶈幏鍙栫О鍙拌澶�.
			 {
				Scaler dev = scalers2.get(i);
				 
				if(dev!=null)
				{
					dev.setConnected(false, null);
					mBle.disconnect(dev.getAddress());
				}
			 }
								
		
		}
	}
	//鍒ゆ柇鎵嬫満钃濈墮鏄惁鍚敤
	public static boolean adapterEnabled()
	{
		if(mBle == null) return false;
		return mBle.adapterEnabled();
	}
	//鍒ゆ柇鏌愪釜钃濈墮鍦板潃鏄惁宸茬粡杩炴帴
	public static boolean hasConnected(String address)
	{
		if(mBle == null) return false;
		return mBle.hasConnected(address);
	}
	private static boolean  read_registers(int reg_addr,int num)
	{
		//璁惧鍦板潃 1byte
		//鍛戒护绫诲瀷 0x3
		//璧峰瀵勫瓨鍣ㄥ湴鍧� reg_addr
		//瀵勫瓨鍣ㄦ暟閲� 2bytes(闇�瑕佽鍙栫殑瀵勫瓨鍣ㄦ暟閲�)
		//鏁版嵁瀛楄妭鏁� 1byte (2*N)
		//瀵勫瓨鍣ㄥ�� (2*N)瀛楄妭.
		//crc16
		short u_reg_addr = (short)reg_addr;
		short u_reg_num  = (short)num;
		byte buffer[]={0x20,0x3,(byte)((u_reg_addr>>8)&0xff),(byte)(u_reg_addr&0xFF),(byte)((u_reg_num>>8)&0xff),(byte)(u_reg_num&0xFF),0,0};
		//byte buffer[]={0x20,0x3,0,0x20,0,1,(byte) 0x83,0x71};
		short crc16 = (short)CRC16.calcCrc16(buffer,0,buffer.length-2);
		buffer[6] = (byte)(crc16&0xFF);
		buffer[7] = (byte)((crc16>>8)&0xff);
		
		return write_buffer(buffer);
		
	}
	//鍚戞煇涓瘎瀛樺櫒鍐欏叆鍊�.
	private static boolean  read_register(short reg_addr)
	{
		//璁惧鍦板潃 1byte
		//鍛戒护绫诲瀷 0x3
		//璧峰瀵勫瓨鍣ㄥ湴鍧� reg_addr
		//瀵勫瓨鍣ㄦ暟閲� 2bytes(闇�瑕佽鍙栫殑瀵勫瓨鍣ㄦ暟閲�)
		//鏁版嵁瀛楄妭鏁� 1byte (2*N)
		//瀵勫瓨鍣ㄥ�� (2*N)瀛楄妭.
		//crc16

		return read_registers(reg_addr,(short) 1);
		
	
		
	}

	public static boolean  read_all_ks()
	{

		try{
			Register reg = new Register();
			//1st
			
			write_buffer(reg.BeginRead(Global.REG_SENSOR_DIFF_K1,4));
			Thread.sleep(200);
			//3rd
			
			write_buffer(reg.BeginRead(Global.REG_SENSOR_DIFF_K3,4));
			Thread.sleep(100);

		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}

		return true;
	}
	public static boolean  common_msg(int reg_addr,int value )
	{
		Register reg = new Register();
		reg.BeginWrite(reg_addr);
		reg.putShort((short) value);
				
		return write_buffer(reg.getResult());
	}
	public static boolean  auto_k(int index)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_AUTO_DIFF_CALIB_INDEX);
		reg.putShort((short) index);
				
		return write_buffer(reg.getResult());
	}
	public static boolean  hand_k(int index, int value)
	{
		
		Register reg = new Register();
		reg.BeginWrite(Global.REG_SENSOR_DIFF_K1+index*2);
		//reg.putShort((short) index);
		reg.putInt(value);
		
		return write_buffer(reg.getResult());

	}
	private static boolean  write_buffer(byte[] value)
	{
		if(value[3] != 0 && value[3]!=47)
		{
			//addCmd(value);
			
			addCmd(value);
		}
		return write_buffer2(value);
	}
//鍙戦�佹暟鎹粰杩炴帴浜嗙殑璁惧.
	private static boolean  write_buffer2(byte[] value)
	{
		if(mBle == null) return false;

		if(!hasConnectAll()) return false;
		
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //鍖呭惈杩欎釜鍦板潃鎵嶈幏鍙栫О鍙拌澶�.
			 {
				 Scaler dev = scalers2.get(i);
				 
				 if(dev!=null && dev.isConnected())
				 {

						BleGattCharacteristic chars = dev.GetBleChar();
						if(chars == null) return false;
						
						
						chars.setValue(value);
						
						return mBle.requestWriteCharacteristic(dev.getAddress(), chars, "false");
				 }
			 }
								
		
		}
		return true;
		
		
		
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
	//鏍囧畾闆剁偣
	//address 璁惧鍦板潃  
	public static boolean requestCalibZero(String address) 
	{
		return requestValue(address, "CLZ;");
	}
	public static boolean requestReadAds()
	{
		try{
			Register reg = new Register();
			//1st
			
			write_buffer(reg.BeginRead(Global.REG_AD_CHAN1,4));
			Thread.sleep(100);
			//3rd
			
			write_buffer(reg.BeginRead(Global.REG_AD_CHAN3,4));
			Thread.sleep(100);

		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}

		return true;
	}
	public static boolean CalibZero() 
	{
		Register reg = new Register();
		//1st
		reg.BeginWrite(Global.REG_CALIB_INDEX);
		reg.putShorts((short) 0,(short)1);
	
		return write_buffer(reg.getResult());
	}
	//鏍囧畾閲嶉噺.calibWet 鏍囧畾閲嶉噺鍊� nov 婊￠噺绋�
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
	public static boolean CalibK(int point,int calibWet) 
	{
		Register reg = new Register();
		//1st
		reg.BeginWrite(Global.REG_CALIB_INDEX);
		reg.putShorts((short) point,(short)1);
		if(point > 0)
			reg.putInt(calibWet);
		return write_buffer(reg.getResult());
	}
	//璇锋眰璇诲彇鍙傛暟
	public static boolean requestReadPar(String address) throws InterruptedException
	{
		read_registers(Global.REG_DOTNUM,1); //灏忔暟鐐逛綅鏁�
		Thread.sleep(50);
		read_registers(Global.REG_DIV1,5); //鍒嗗害鍊�->閲忕▼
		Thread.sleep(50);
		read_registers(Global.REG_UNIT,6);//鍗曚綅->婊ゆ尝绛夌骇
		Thread.sleep(50);
		read_registers(Global.REG_SLEEP_S,2);//鍗曚綅->婊ゆ尝绛夌骇
		return true;
	}
	//璇锋眰淇敼鍙傛暟,淇敼鍚庣殑鍙傛暟鏈繚瀛�
	public static boolean requestWriteParamValue(String address,ScalerParam s)
	{
		if(mBle == null) return false;

		Scaler scaler = scalers2.get(0);
		if(scaler==null) return false;
		BleGattCharacteristic chars = scaler.GetBleChar();
		//BleGattCharacteristic chars = mChars.get(address);
		if(chars == null) return false;
		if(s == null) return false;
		ScalerParam sp = scaler.para;
		try{
			Register reg = new Register();
			//1st
			reg.BeginWrite(Global.REG_DIV1);
			
			reg.putShorts((short)s.getResultionx(),(short)s.getResultionx());	
			reg.putInts(s.getNov());	
			
			write_buffer(reg.getResult());
			Thread.sleep(200);
			
			//2nd
			reg.BeginWrite(Global.REG_UNIT);
			reg.putShorts(s.getUnit(),s.getPwr_zerotrack(),s.getHand_zerotrack());
			
			write_buffer(reg.getResult());
			Thread.sleep(200);
			
			reg.BeginWrite(Global.REG_ZERO_TRACK_SPAN);
			reg.putShorts(s.getZerotrack(),s.getMtd(),s.getFilter());
			write_buffer(reg.getResult());
			Thread.sleep(200);
			
			//3rd
			reg.BeginWrite(Global.REG_DOTNUM); //dot
			reg.putShort(s.getDignum());
			write_buffer(reg.getResult());
			Thread.sleep(200);
			
			reg.BeginWrite(Global.REG_SLEEP_S); //dot
			reg.putShort(s.getSleep());
			reg.putShort(s.getSnr_num());
			write_buffer(reg.getResult());
			Thread.sleep(200);
		
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}

		
		//chars.setValue(s.getSetCmdBuffer());
		
		return mBle.requestWriteCharacteristic(address, chars, "false");

	}
	//閫氱煡绉ゅ皢鍙傛暟鍐欏叆鍐呴儴eeprom
	public static boolean requestSaveParam(String address)
	{
		return requestValue(address, "SAV1;");
	}
	//璇诲彇鏌愪釜绉ょ殑閲嶉噺鍊�
	public static boolean requestReadWgt(String address)
	{
		int size = getQueSize() ;
		if(size > 10) 
		{
			Log.e(TAG, "queue underflow " + size);
			return false;
		}
		Log.e(TAG, "send packet");
		return read_registers((short)Global.REG_WEIGHT, (short)4);

	}
	

	public static String getPrinterAddress()
	{
		return mPrinterAddress;
	}
	//鑾峰彇鎸囧畾鍦板潃鐨勭О鍙拌澶�.
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
	//鑾峰彇鍦板潃搴忓彿鐨勭О鐨勮摑鐗欏湴鍧�
	public static String getDeviceAddress(Context pCtx,int index)
	{
		
		return Config.getInstance(pCtx).getDevAddress(index);
	}
	//淇敼鍦板潃搴忓彿鐨勭О鐨勮摑鐗欏湴鍧�
	public static void setDeviceAddress(Context pCtx, int index,String address)
	{
		 Config.getInstance(pCtx).setDevAddress(index,address);
		
		 
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
		//淇敼鍦板潃鍚庯紝閲嶆柊鍔犺浇鍦板潃鍒楄〃.
		loadScalerConfig(pCtx);
	}
	//杩炴帴鎵�鏈夎摑鐗欑Г,鏃犺鏄惁杩炴帴鎴愬姛
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
			
			 if(scalers2.containsKey(i)) //涓嶅寘鍚繖涓湴鍧�鎵嶅垱寤烘柊鐨勭О鍙拌澶�.
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
		
		if(WorkService.hasConnectAll()) return true;
		if(WorkService.scalers.size() < max_count)
		{
			return false;
		}
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //涓嶅寘鍚繖涓湴鍧�鎵嶅垱寤烘柊鐨勭О鍙拌澶�.
			 {
				 Scaler dev = scalers2.get(i);
				 if(dev!=null && dev.isConnected()!=true)
				 {
					 WorkService.requestConnect(dev.getAddress());
					 return true;
				 }
				
			 }
								
		
		}
		return false;
	}
	//鎵�鏈夌О閮藉凡缁忚繛鎺ュ惁
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
			
			 if(scalers2.containsKey(i)) //鍖呭惈杩欎釜鍦板潃鎵嶈幏鍙栫О鍙拌澶�.
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
	//璇诲彇鎵�鏈夌О鐨勯噸閲�.
	public static boolean readAllWgt()
	{
		if(!hasConnectAll()) return false;
		
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //鍖呭惈杩欎釜鍦板潃鎵嶈幏鍙栫О鍙拌澶�.
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
	public static boolean readPower()
	{
		return read_registers((short)Global.REG_BATTERY, (short)1);
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
	
	//鑾峰彇绉ょ殑涓暟
	public static int getScalerCount()
	{
		if(scalers==null) return 0;
		return scalers.size();
	}
	//鑾峰彇鎸囧畾搴忓彿绉ょ殑钃濈墮鍦板潃
	public static String getScalerAddress(int index)
	{
		if(index >= getScalerCount()) return null;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return null;
		
		return s.getAddress();
	}
	
	//鑾峰彇鎸囧畾搴忓彿绉ょ殑杩炴帴鐘舵��.
	public static boolean getScalerConnectState(int index)
	{
		if(index >= getScalerCount()) return false;
		
		
		Scaler s = scalers2.get(index);
		if(s == null) return false;
		
		return s.isConnected(); 
		
	}
	//鑾峰彇鎵�鏈夌Г鍔犺捣鏉ョ殑閲嶉噺.
	public static int getTotalWeight()
	{
		int totalweight = 0;
		for(int i = 0 ; i < max_count; i++)
		{
			
			 if(scalers2.containsKey(i)) //涓嶅寘鍚繖涓湴鍧�鎵嶅垱寤烘柊鐨勭О鍙拌澶�.
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
	//鑾峰彇棰勭疆鍜屽幓鐨椂淇濆瓨鐨勭毊閲�
	public static int getSavedTareWeight()
	{
		return tmp_tare;
	}
	//鑾峰彇褰撳墠鐨勭毊閲�.鍑�閲嶇姸鎬佷笅鎵嶆湁鐨噸锛屾瘺閲嶇姸鎬佷笅鐨噸涓�0
	public static int getTareWeight()
	{
		if(is_net_state) return tare;
		return 0;
	}
	//鑾峰彇姣涢噸
	public static int getGrossWeight()
	{
		return getTotalWeight() - zero;
	}
	//鑾峰彇鍑�閲�,姣涢噸鐘舵�佷笅鐨噸涓�0锛屽噣閲�=姣涢噸  鍑�閲嶇姸鎬佷笅鍑�閲嶆墠鏄笂娆¤缃殑鍊硷紝鍑�閲�=姣涢噸-鍑�閲� .  瀹炴椂鏄剧ず鐨勪篃鏄繖涓噸閲�.
	public static int getNetWeight()
	{
		return getGrossWeight() - getTareWeight();
	}
	
	//缃浂褰撳墠閲嶉噺
	public static boolean setZero()
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_OPERATION);
		reg.putShort((short) 1);
		return write_buffer(reg.getResult());
	}
	//棰勭疆鐨噸,鎵嬪伐璁剧疆鐨噸,棰勭疆鐨噸鍚庯紝鐘舵�佹洿鏀逛负鍑�閲嶇姸鎬�.
	public static boolean setPreTare(int preTare)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_TARE);
		reg.putInt(preTare);
		return write_buffer(reg.getResult());
		
	}
	public static boolean isNetState()
	{
		return is_net_state;
	}
	//鍘荤毊锛屽彇褰撳墠鐨勯噸閲忎负鐨噸,鍘荤毊鍚庢洿鏀逛负鍑�閲嶇姸鎬�.鍑�閲嶇姸鎬佷笉鑳藉幓鐨�
	public static boolean discardTare()
	{
		
		//if(is_net_state) return false;
		int gross = getGrossWeight();
		
		if(gross > 0)
		{
			tare = gross;
			tmp_tare = tare;
			is_net_state = true;
			
		}
		else
		{
			tare = 0;
			tmp_tare = 0;
			is_net_state = false;
		}
		return true;
	}
	//姣涢噸鍜屽噣閲嶇姸鎬佸垏鎹�.
	public static boolean switchNetGross()
	{
		is_net_state=!is_net_state;
		if(is_net_state)
		{
			//濡傛灉鎵嬪伐鍒囨崲鍒板噣閲嶇姸鎬�.
			tare = tmp_tare; //鎭㈠涓婃淇濆瓨鐨勭毊閲�.
		}
		else
		{
			//濡傛灉鎵嬪伐鍒囨崲鍒颁簡姣涢噸鐘舵��.
			tare = 0; //灏嗙毊閲嶈缃负0锛�
		}
		return is_net_state;
	}
	public static boolean CtrlLight(int index)
	{
		Register reg = new Register();
		reg.BeginWrite(Global.REG_LAMP_CTRL);
		reg.putShort((short) (index));
		return write_buffer(reg.getResult());
		
	}

	private class CommandReceiver extends BroadcastReceiver{

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   int cmd=intent.getIntExtra("cmd", -1);
		   if(cmd==1){//濡傛灉绛変簬0
		 
		     stopSelf();//鍋滄鏈嶅姟
		    
		   }
		  }
		  
	}

}

