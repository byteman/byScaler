package com.example.bluetooth.le;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.example.worker.WorkService;


public class BleApplication extends Application {

	private BleService mService;
	private WorkService mWorkService;
	private IBle mBle;

	private final ServiceConnection mServiceConnection2 = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			
			mWorkService = ((WorkService.LocalBinder) rawBinder).getService();
			mWorkService.get();
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mWorkService = null;	
		}
	};
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((BleService.LocalBinder) rawBinder).getService();
			mBle = mService.getBle();
			
			Intent bindIntent = new Intent(getApplicationContext(), WorkService.class);
			bindService(bindIntent, mServiceConnection2, Context.BIND_AUTO_CREATE);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
	};
	


	@Override
	public void onCreate() {
		super.onCreate();

		Intent bindIntent = new Intent(this, BleService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		
	}
	
	public IBle getIBle() {
		return mBle;
	}
}
