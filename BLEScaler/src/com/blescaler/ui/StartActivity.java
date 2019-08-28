package com.blescaler.ui;

import java.lang.ref.WeakReference;

import com.blescaler.utils.Utils;
import com.blescaler.ui.R;
import com.blescaler.ui.ble.MainActivity;
import com.blescaler.worker.WorkService;
import com.blescaler.worker.Global;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class StartActivity extends Activity {

	

	private static Handler mHandler = null;
	private static final int REQUEST_ENABLE_BT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.start);
		
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
		Utils.setDiscoverableTimeout(10);
		
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

				case Global.MSG_ALLTHREAD_READY: 
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
}