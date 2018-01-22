package com.blescaler.ui;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.Locale;

import com.blescaler.utils.Utils;
import com.blescaler.ui.ble.MainActivity;
import com.blescaler.worker.WorkService;
import com.blescaler.worker.Global;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartActivity extends Activity {

	

	private static Handler mHandler = null;
	private static final int REQUEST_ENABLE_BT = 1;
	protected void switchLanguage(String language) {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		switch (language) {
			case "zh":
				config.locale = Locale.CHINESE;
				resources.updateConfiguration(config, dm);
				break;
			case "en":
				config.locale = Locale.ENGLISH;
				resources.updateConfiguration(config, dm);
				break;
			default:
				config.locale = Locale.US;
				resources.updateConfiguration(config, dm);
				break;
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题
		setContentView(R.layout.activity_start);
		initView();

		switchLanguage("zh");
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
		Utils.setDiscoverableTimeout(10);
		
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		//当工具栏发生变化的时候
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
	}

	static class MHandler extends Handler {

		WeakReference<StartActivity> mActivity;

		MHandler(StartActivity activity) {
			mActivity = new WeakReference<StartActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			StartActivity theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_ALLTHREAD_READY: //跳转
				{
					Intent intent ;

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					intent = new Intent(theActivity,
								MainActivity.class);
				
					theActivity.startActivity(intent);
	
					theActivity.finish();
				
					break;
				}

			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		}
	}

}
