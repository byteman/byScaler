package com.example.bluetooth.le;

import java.util.Map;
import java.util.Set;

import com.xtremeprog.sdk.ble.IBle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class StartActivity extends Activity {

	private IBle mBle;
	private static final int REQUEST_ENABLE_BT = 1;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		

	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 不显示程序的标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 不显示系统的标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.start);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				BleApplication app = (BleApplication) getApplication();
				mBle = app.getIBle();
				if(mBle == null)
				{
					Toast.makeText(StartActivity.this, "ble not start", Toast.LENGTH_LONG).show();
					StartActivity.this.finish();
					return;
				}
				if (mBle != null && !mBle.adapterEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
				
				SharedPreferences sp = getSharedPreferences("device",
						Activity.MODE_PRIVATE);

				String address = sp.getString("address", "");
				String name    = sp.getString("name", "");
				if (address.length() == 0) {
					startActivity(new Intent(getApplication(),
							DeviceScanActivity.class));  
					
				}
				else {
					connectDevice(name,address);
					Intent intent =  new Intent(getApplication(),
							DeviceScanActivity.class);
					
					//intent.putExtra("address", value)
					startActivity(intent);  
					
				}
				StartActivity.this.finish();
			}

			
		}, 1000);

	}

	protected void connectDevice(String name, String address) {
		// TODO Auto-generated method stub
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
	}

}
