/**
 * This XPG software is supplied to you by Xtreme Programming Group, Inc.
 * ("XPG") in consideration of your agreement to the following terms, and your
 * use, installation, modification or redistribution of this XPG software
 * constitutes acceptance of these terms.锟� If you do not agree with these terms,
 * please do not use, install, modify or redistribute this XPG software.
 * 
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, XPG grants you a non-exclusive license, under XPG's
 * copyrights in this original XPG software (the "XPG Software"), to use and
 * redistribute the XPG Software, in source and/or binary forms; provided that
 * if you redistribute the XPG Software, with or without modifications, you must
 * retain this notice and the following text and disclaimers in all such
 * redistributions of the XPG Software. Neither the name, trademarks, service
 * marks or logos of XPG Inc. may be used to endorse or promote products derived
 * from the XPG Software without specific prior written permission from XPG.锟�
 * Except as expressly stated in this notice, no other rights or licenses,
 * express or implied, are granted by XPG herein, including but not limited to
 * any patent rights that may be infringed by your derivative works or by other
 * works in which the XPG Software may be incorporated.
 * 
 * The XPG Software is provided by XPG on an "AS IS" basis.锟� XPG MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE XPG SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 * 
 * IN NO EVENT SHALL XPG BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE XPG SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER
 * THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR
 * OTHERWISE, EVEN IF XPG HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ABOUT XPG: Established since June 2005, Xtreme Programming Group, Inc. (XPG)
 * is a digital solutions company based in the United States and China. XPG
 * integrates cutting-edge hardware designs, mobile applications, and cloud
 * computing technologies to bring innovative products to the marketplace. XPG's
 * partners and customers include global leading corporations in semiconductor,
 * home appliances, health/wellness electronics, toys and games, and automotive
 * industries. Visit www.xtremeprog.com for more information.
 * 
 * Copyright (C) 2013 Xtreme Programming Group, Inc. All Rights Reserved.
 */

package com.xtremeprog.sdk.ble;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xtremeprog.sdk.ble.BleRequest.FailReason;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;

public class BleService extends Service {
	private static final String TAG = "Scaler";

	/** Intent for broadcast */
	public static final String BLE_NOT_SUPPORTED = "com.xtremeprog.sdk.ble.not_supported";
	public static final String BLE_NO_BT_ADAPTER = "com.xtremeprog.sdk.ble.no_bt_adapter";
	public static final String BLE_STATUS_ABNORMAL = "com.xtremeprog.sdk.ble.status_abnormal";
	/**
	 * @see BleService#bleRequestFailed
	 */
	public static final String BLE_REQUEST_FAILED = "com.xtremeprog.sdk.ble.request_failed";
	/**
	 * @see BleService#bleDeviceFound
	 */
	public static final String BLE_DEVICE_FOUND = "com.xtremeprog.sdk.ble.device_found";
	/**
	 * @see BleService#bleGattConnected
	 */
	public static final String BLE_GATT_CONNECTED = "com.xtremeprog.sdk.ble.gatt_connected";
	/**
	 * @see BleService#bleGattDisConnected
	 */
	public static final String BLE_GATT_DISCONNECTED = "com.xtremeprog.sdk.ble.gatt_disconnected";
	/**
	 * @see BleService#bleServiceDiscovered
	 */
	public static final String BLE_SERVICE_DISCOVERED = "com.xtremeprog.sdk.ble.service_discovered";
	/**
	 * @see BleService#bleCharacteristicRead
	 */
	public static final String BLE_CHARACTERISTIC_READ = "com.xtremeprog.sdk.ble.characteristic_read";
	/**
	 * @see BleService#bleCharacteristicNotification
	 */
	public static final String BLE_CHARACTERISTIC_NOTIFICATION = "com.xtremeprog.sdk.ble.characteristic_notification";
	/**
	 * @see BleService#bleCharacteristicIndication
	 */
	public static final String BLE_CHARACTERISTIC_INDICATION = "com.xtremeprog.sdk.ble.characteristic_indication";
	/**
	 * @see BleService#bleCharacteristicWrite
	 */
	public static final String BLE_CHARACTERISTIC_WRITE = "com.xtremeprog.sdk.ble.characteristic_write";
	/**
	 * @see BleService#bleCharacteristicChanged
	 */
	public static final String BLE_CHARACTERISTIC_CHANGED = "com.xtremeprog.sdk.ble.characteristic_changed";

	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_SCAN_RECORD = "SCAN_RECORD";
	public static final String EXTRA_SOURCE = "SOURCE";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String EXTRA_REQUEST = "REQUEST";
	public static final String EXTRA_REASON = "REASON";

	/** Source of device entries in the device list */
	public static final int DEVICE_SOURCE_SCAN = 0;
	public static final int DEVICE_SOURCE_BONDED = 1;
	public static final int DEVICE_SOURCE_CONNECTED = 2;

	public static final UUID DESC_CCC = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public enum BLESDK {
		NOT_SUPPORTED, ANDROID, SAMSUNG, BROADCOM
	}

	private final IBinder mBinder = new LocalBinder();
	private BLESDK mBleSDK;
	private IBle mBle;
	private BlockingQueue<BleRequest> mRequestQueue = new ArrayBlockingQueue<BleRequest>(10);
	private BlockingQueue<Integer> mTimeoutQueue = new ArrayBlockingQueue<Integer>(1);
	
	private BleRequest mCurrentRequest = null;

	
	private String mNotificationAddress;

	private Thread workThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			mRequestQueue.clear();
			
			
			// TODO Auto-generated method stub
			while(true)
			{
				try {
					processNextRequest();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	
	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BLE_NOT_SUPPORTED);
		intentFilter.addAction(BLE_NO_BT_ADAPTER);
		intentFilter.addAction(BLE_STATUS_ABNORMAL);
		intentFilter.addAction(BLE_REQUEST_FAILED);
		intentFilter.addAction(BLE_DEVICE_FOUND);
		intentFilter.addAction(BLE_GATT_CONNECTED);
		intentFilter.addAction(BLE_GATT_DISCONNECTED);
		intentFilter.addAction(BLE_SERVICE_DISCOVERED);
		intentFilter.addAction(BLE_CHARACTERISTIC_READ);
		intentFilter.addAction(BLE_CHARACTERISTIC_NOTIFICATION);
		intentFilter.addAction(BLE_CHARACTERISTIC_WRITE);
		intentFilter.addAction(BLE_CHARACTERISTIC_CHANGED);
		return intentFilter;
	}
	public  int getqueueSize()
	{
		return mRequestQueue.size();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public BleService getService() {
			return BleService.this;
		}
	}

	@Override
	public void onCreate() {
		mBleSDK = getBleSDK();
		if (mBleSDK == BLESDK.NOT_SUPPORTED) {
			return;
		}

		Log.d(TAG, " " + mBleSDK);
		if (mBleSDK == BLESDK.ANDROID) {
			mBle = new AndroidBle(this);
		} 
		workThread.start();
			
	}

	protected void bleNotSupported() {
		Intent intent = new Intent(BleService.BLE_NOT_SUPPORTED);
		sendBroadcast(intent);
	}

	protected void bleNoBtAdapter() {
		Intent intent = new Intent(BleService.BLE_NO_BT_ADAPTER);
		sendBroadcast(intent);
	}

	private BLESDK getBleSDK() {
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			// android 4.3
			return BLESDK.ANDROID;
		}

		ArrayList<String> libraries = new ArrayList<String>();
		for (String i : getPackageManager().getSystemSharedLibraryNames()) {
			libraries.add(i);
		}

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			// android 4.2.2
			if (libraries.contains("com.samsung.android.sdk.bt")) {
				return BLESDK.SAMSUNG;
			} else if (libraries.contains("com.broadcom.bt")) {
				return BLESDK.BROADCOM;
			}
		}

		bleNotSupported();
		return BLESDK.NOT_SUPPORTED;
	}

	public IBle getBle() {
		return mBle;
	}

	/**
	 * Send {@link BleService#BLE_DEVICE_FOUND} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_DEVICE} device {@link BluetoothDevice} <br>
	 * {@link BleService#EXTRA_RSSI} rssi int<br>
	 * {@link BleService#EXTRA_SCAN_RECORD} scan record byte[] <br>
	 * {@link BleService#EXTRA_SOURCE} source int, not used now <br>
	 */
	protected void bleDeviceFound(BluetoothDevice device, int rssi,
			byte[] scanRecord, int source) {
		Log.d("blelib", "[" + new Date().toLocaleString() + "] device found "
				+ device.getAddress());
		Intent intent = new Intent(BleService.BLE_DEVICE_FOUND);
		intent.putExtra(BleService.EXTRA_DEVICE, device);
		intent.putExtra(BleService.EXTRA_RSSI, rssi);
		intent.putExtra(BleService.EXTRA_SCAN_RECORD, scanRecord);
		intent.putExtra(BleService.EXTRA_SOURCE, source);
		sendBroadcast(intent);
	}

	/**
	 * Send {@link BleService#BLE_GATT_CONNECTED} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_DEVICE} device {@link BluetoothDevice} <br>
	 */
	protected void bleGattConnected(BluetoothDevice device) {
		Intent intent = new Intent(BLE_GATT_CONNECTED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		sendBroadcast(intent);
		requestProcessed(device.getAddress(), RequestType.CONNECT_GATT, true);
	}

	/**
	 * Send {@link BleService#BLE_GATT_DISCONNECTED} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * 
	 * @param address
	 */
	protected void bleGattDisConnected(String address) {
		Intent intent = new Intent(BLE_GATT_DISCONNECTED);
		intent.putExtra(EXTRA_ADDR, address);
		sendBroadcast(intent);
		requestProcessed(address, RequestType.CONNECT_GATT, false);
	}

	/**
	 * Send {@link BleService#BLE_SERVICE_DISCOVERED} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * 
	 * @param address
	 */
	protected void bleServiceDiscovered(String address) {
		Intent intent = new Intent(BLE_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_ADDR, address);
		sendBroadcast(intent);
		requestProcessed(address, RequestType.DISCOVER_SERVICE, true);
	}
	
	protected void requestProcessed(String address, RequestType requestType,
			boolean success) {
		if (mCurrentRequest != null && mCurrentRequest.type == requestType) {
			
			
			if (!success) {
				bleRequestFailed(mCurrentRequest.address, mCurrentRequest.type,
						FailReason.RESULT_FAILED);
				Log.e(TAG, "-processrequest type " + requestType + " address "
						+ address + " [success: " + success + "]");
			}
			mTimeoutQueue.offer(success?0:1); //提供数据并且唤醒.
			
		}

	}



	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_READ} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_UUID} characteristic uuid {@link String}<br>
	 * {@link BleService#EXTRA_STATUS} read status {@link Integer} Not used now <br>
	 * {@link BleService#EXTRA_VALUE} data byte[] <br>
	 * 
	 * @param address
	 * @param uuid
	 * @param status
	 * @param value
	 */
	protected void bleCharacteristicRead(String address, String uuid,
			int status, byte[] value) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_READ);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);
		requestProcessed(address, RequestType.READ_CHARACTERISTIC, true);
	}

	protected void addBleRequest(BleRequest request) {

		if(!mRequestQueue.offer(request))
		{
			Log.e(TAG, "addBleRequest full");
		}
		
		
	}
	
	private void processNextRequest() throws InterruptedException 
	{
	
		BleRequest tmpRequest = mRequestQueue.take();
	 //如果mRequestQueue没有数据就会阻塞.直到有数据会被唤醒.

		synchronized (this) {
			mCurrentRequest = tmpRequest;
		}
	
		//Log.d(TAG, "+processrequest type " + mCurrentRequest.type + " address "
		//		+ mCurrentRequest.address + " remark " + mCurrentRequest.remark);
		
		boolean ret = false;
		switch (mCurrentRequest.type) {
		case CONNECT_GATT:
			ret = ((IBleRequestHandler) mBle).connect(mCurrentRequest.address);
			break;
		case DISCOVER_SERVICE:
			Log.e(TAG, "begin discover_service");
			ret = mBle.discoverServices(mCurrentRequest.address);
			break;
		case CHARACTERISTIC_NOTIFICATION:
		case CHARACTERISTIC_INDICATION:
		case CHARACTERISTIC_STOP_NOTIFICATION:
			ret = ((IBleRequestHandler) mBle).characteristicNotification(
					mCurrentRequest.address, mCurrentRequest.characteristic,mCurrentRequest.type);
			break;
		case READ_CHARACTERISTIC:
			ret = ((IBleRequestHandler) mBle).readCharacteristic(
					mCurrentRequest.address, mCurrentRequest.characteristic);
			break;
		case WRITE_CHARACTERISTIC:
			ret = ((IBleRequestHandler) mBle).writeCharacteristic(
					mCurrentRequest.address, mCurrentRequest.characteristic);
			break;
		case READ_DESCRIPTOR:
			break;
		default:
			break;
		}

		if (!ret) {
		
			Log.e(TAG, "-processrequest type " + mCurrentRequest.type
					+ " address " + mCurrentRequest.address + " [fail start]");
			bleRequestFailed(mCurrentRequest.address, mCurrentRequest.type,
					FailReason.START_FAILED);
			
		}
		else {
			Log.e(TAG,"wait -processrequest type " + mCurrentRequest.type
					+ " address " + mCurrentRequest.address);
			mTimeoutQueue.clear();
			Integer v = mTimeoutQueue.poll(10,TimeUnit.SECONDS);
			if(v == null)
			{
				//timeout
				Log.e(TAG,"timeout stop -processrequest type " + mCurrentRequest.type
						+ " address " + mCurrentRequest.address);
				mRequestQueue.clear();
			}
			else
			{
				Log.e(TAG,"stop -processrequest type " + mCurrentRequest.type
						+ " address " + mCurrentRequest.address + " "+v);
			}
		
			
		}
	}



	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_NOTIFICATION} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_UUID} characteristic uuid {@link String}<br>
	 * {@link BleService#EXTRA_STATUS} read status {@link Integer} Not used now <br>
	 * 
	 * @param address
	 * @param uuid
	 * @param status
	 */
	protected void bleCharacteristicNotification(String address, String uuid,
			boolean isEnabled, int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_NOTIFICATION);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_VALUE, isEnabled);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
		if (isEnabled) {
			requestProcessed(address, RequestType.CHARACTERISTIC_NOTIFICATION,
					true);
		} else {
			requestProcessed(address,
					RequestType.CHARACTERISTIC_STOP_NOTIFICATION, true);
		}
		setNotificationAddress(address);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_INDICATION} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_UUID} characteristic uuid {@link String}<br>
	 * {@link BleService#EXTRA_STATUS} read status {@link Integer} Not used now <br>
	 * 
	 * @param address
	 * @param uuid
	 * @param status
	 */
	protected void bleCharacteristicIndication(String address, String uuid,
			int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_INDICATION);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
		requestProcessed(address, RequestType.CHARACTERISTIC_INDICATION, true);
		setNotificationAddress(address);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_WRITE} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_UUID} characteristic uuid {@link String}<br>
	 * {@link BleService#EXTRA_STATUS} read status {@link Integer} Not used now <br>
	 * 
	 * @param address
	 * @param uuid
	 * @param status
	 */
	protected void bleCharacteristicWrite(String address, String uuid,
			int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_WRITE);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
		requestProcessed(address, RequestType.WRITE_CHARACTERISTIC, true);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_CHANGED} broadcast. <br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_UUID} characteristic uuid {@link String}<br>
	 * {@link BleService#EXTRA_VALUE} data byte[] <br>
	 * 
	 * @param address
	 * @param uuid
	 * @param value
	 */
	protected void bleCharacteristicChanged(String address, String uuid,
			byte[] value) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_CHANGED);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);
	}

	/**
	 * @param reason
	 */
	protected void bleStatusAbnormal(String reason) {
		Intent intent = new Intent(BLE_STATUS_ABNORMAL);
		intent.putExtra(EXTRA_VALUE, reason);
		sendBroadcast(intent);
	}

	/**
	 * Sent when BLE request failed.<br>
	 * <br>
	 * Data in the broadcast intent: <br>
	 * {@link BleService#EXTRA_ADDR} device address {@link String} <br>
	 * {@link BleService#EXTRA_REQUEST} request type
	 * {@link BleRequest.RequestType} <br>
	 * {@link BleService#EXTRA_REASON} fail reason {@link BleRequest.FailReason} <br>
	 */
	protected void bleRequestFailed(String address, RequestType type,
			FailReason reason) {
		Intent intent = new Intent(BLE_REQUEST_FAILED);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_REQUEST, type);
		intent.putExtra(EXTRA_REASON, reason.ordinal());
		sendBroadcast(intent);
	}

	protected String getNotificationAddress() {
		return mNotificationAddress;
	}

	protected void setNotificationAddress(String mNotificationAddress) {
		this.mNotificationAddress = mNotificationAddress;
	}
	protected RequestType getCurrentRequestType()
	{
		synchronized (this) {
			if(mCurrentRequest == null) 
				return RequestType.ERROR_TYPE;
			return mCurrentRequest.type;
		}
		
	}
}