package com.example.bluetooth.le;

import java.lang.ref.WeakReference;

import com.example.worker.WorkService;
import com.example.worker.Global;


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

						
					intent = new Intent(theActivity,
								WeightActivity.class);
				
					theActivity.startActivity(intent);
	
					theActivity.finish();
				
					break;
				}

			}
		}
	}
}
