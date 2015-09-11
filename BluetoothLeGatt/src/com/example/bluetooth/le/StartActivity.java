package com.example.bluetooth.le;

import java.lang.ref.WeakReference;

import com.example.worker.Global;
import com.example.worker.WorkService;




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
/*	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	protected void onResume() {
		super.onResume();
	

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		
		
		
		if (!WorkService.adapterEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}*/
	
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
