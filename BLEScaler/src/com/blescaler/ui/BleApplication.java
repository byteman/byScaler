package com.blescaler.ui;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tencent.bugly.crashreport.CrashReport;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.blescaler.utils.CrashHandler;
import com.blescaler.worker.WorkService;


public class BleApplication extends Application {

	
	private WorkService mWorkService;
	

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			
			mWorkService = ((WorkService.LocalBinder) rawBinder).getService();
			
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mWorkService = null;	
		}
	};
	


	@Override
	public void onCreate() {
		super.onCreate();

		
        CrashReport.initCrashReport(this, "900009251", false);

		Intent bindIntent = new Intent(getApplicationContext(), WorkService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		
		
	}
	public void quit()
	{
		mWorkService.unbindService(mServiceConnection);
	}

}
