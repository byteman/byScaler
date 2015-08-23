package com.example.worker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.bluetooth.le.BleApplication;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.example.bluetooth.le.Utils;
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
	private static IBle mBle;
	private static final int REQUEST_ENABLE_BT = 1;
	private String TAG = "WorkSrv";
	
	
	private static Map<String, BleGattCharacteristic> mChars;
	
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
			
				
				msg.obj = device;
				mHandler.sendMessage(msg);	
			} 

			else if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				
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
		mChars = new HashMap<String, BleGattCharacteristic>();
		
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		mHandler = new MHandler(this);
		workThread = new WorkThread(mHandler);
		workThread.start();
		
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
	public static boolean requestPrint()
	{
		byte[] setHT = {0x1b,0x44,0x18,0x00};
		byte[] HT = {0x09};
		byte[] LF = {0x0d,0x0a};
		byte[][] allbuf = new byte[][]{
				setHT,"FOOD".getBytes(),HT,"PRICE".getBytes(),LF,LF,
				setHT,"DECAF16".getBytes(),HT,"30".getBytes(),LF,
				setHT,"ISLAND BLEND".getBytes(),HT,"180".getBytes(),LF,
				setHT,"FLAVOR SMALL".getBytes(),HT,"30".getBytes(),LF,
				setHT,"Kenya AA".getBytes(),HT,"90".getBytes(),LF,
				setHT,"CHAI".getBytes(),HT,"15.5".getBytes(),LF,
				setHT,"MOCHA".getBytes(),HT,"20".getBytes(),LF,
				setHT,"BREVE".getBytes(),HT,"1000".getBytes(),LF,LF,LF
				};
		byte[] buf = DataUtils.byteArraysToBytes(allbuf);
		if (WorkService.workThread.isConnected()) {
			Bundle data = new Bundle();
			data.putByteArray(Global.BYTESPARA1, buf);
			data.putInt(Global.INTPARA1, 0);
			data.putInt(Global.INTPARA2, buf.length);
			WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
		} else {
			//打印机未连接
			return false;
		}
		return true;
		
	}
	
}

