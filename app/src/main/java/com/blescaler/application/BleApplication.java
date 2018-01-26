package com.blescaler.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import android.support.annotation.NonNull;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
//import com.blescaler.utils.CrashHandler;
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

	@Override public void onCreate() {
		super.onCreate();

		CrashReport.initCrashReport(this, "900009251", false);

		Intent bindIntent = new Intent(getApplicationContext(), WorkService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

		//设置默认的smart 上拉下拉刷新
		SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
			@NonNull
			@Override
			public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
				return new ClassicsFooter(context);
			}
		});
		SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
			@NonNull
			@Override
			public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
				return new ClassicsHeader(context);
			}
		});

	}
	public void quit()
	{
		mWorkService.unbindService(mServiceConnection);
	}

}
