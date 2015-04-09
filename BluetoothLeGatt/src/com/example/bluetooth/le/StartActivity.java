package com.example.bluetooth.le;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class StartActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.start);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				//BluetoothAdapter adpter=BluetoothAdapter.getDefaultAdapter();
				//adpter.enable();
				
				Intent intent ;
				if(Config.getInstance(StartActivity.this).getDevAddress() == "")
				{
					intent = new Intent(getApplication(),
							DeviceScanActivity.class);

				}
				else 
				{
					
					intent = new Intent(getApplication(),
							WeightActivity.class);
				}
				
				startActivity(intent);

				StartActivity.this.finish();
			}

		}, 1000);

	}
}
