package com.example.bluetooth.le;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

public class CalibActivity extends Activity {

	private String mDeviceAddress;
	private int ad = 0;
	private IBle mBle;
	private BleGattCharacteristic mCharacteristicAD;
	private BleGattCharacteristic mCharacteristicK;
	private BleGattCharacteristic mCharacteristicZero;
	private final String TAG = "CalibActivity";
	private TextView m_tvAD,m_tvK;
	private Button m_btCalibZero, m_btCalibWgt;
	private EditText m_etZero, m_etWgt;

	private final class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.btCalbZero) {
				int ad 	  = Integer.valueOf((String) m_tvAD.getText());
				m_etZero.setText(m_tvAD.getText());
				mCharacteristicZero.setValue(Utils.intToByte(ad));
				mBle.requestWriteCharacteristic(mDeviceAddress, mCharacteristicZero,"");
				
			} else if (v.getId() == R.id.btCalbWgt) {
				if (m_etWgt.getText().length() <= 0) {
					Toast.makeText(CalibActivity.this, "请先输入内容",
							Toast.LENGTH_LONG).show();
					return;
				}
				float ad 	 = Float.valueOf((String) m_tvAD.getText());
				float zero  = Float.valueOf((String) m_etZero.getText().toString());
				float wgt   = Float.valueOf((String) m_etWgt.getText().toString());
				float k = (float)( wgt / (ad-zero) );
				
				mCharacteristicK.setValue(Utils.float2byte(k));
				mBle.requestWriteCharacteristic(mDeviceAddress, mCharacteristicK,"");
				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicK);
				
			}

		}
	}

	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		private float k;

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();

			String action = intent.getAction();
			//Log.e(TAG, action);
			if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {

				finish();
			} else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				
				String uuid = extras.getString(BleService.EXTRA_UUID).toUpperCase();
				if(uuid.equals(Utils.UUID_AD))
				{
					//if(BleService.EXTRA_VALUE)
					byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

					ad = Utils.bytesToInt(val);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							m_tvAD.setText(String.valueOf(ad));
						}
					});
				}
				else if(uuid.equals(Utils.UUID_K))
				{
					byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

					k = Utils.byte2float(val,0);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							m_tvK.setText(String.valueOf(k));
						}
					});
				}
				
					
				
				else if(uuid.equals(Utils.UUID_ZERO))
				{
					byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

					ad = Utils.bytesToInt(val);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							m_etZero.setText(String.valueOf(ad));
						}
					});
				}
				else if(uuid.equals(Utils.UUID_WGT))
				{
					byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

					ad = Utils.bytesToInt(val);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							m_tvWgt.setText(String.valueOf(ad));
						}
					});
				}
				
		
			}	
			 else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
				Toast.makeText(CalibActivity.this, "Write success!",
						Toast.LENGTH_SHORT).show();
			}

		}
	};
	private ActionBar mActionBar;
	private BleGattCharacteristic mCharacteristicWgt;
	private TextView m_tvWgt;
	private Timer pTimer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calib);
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		mActionBar = getActionBar();
		// 设置是否显示应用程序的图标
		mActionBar.setDisplayShowHomeEnabled(true);
		// 将应用程序图标设置为可点击的按钮
		mActionBar.setHomeButtonEnabled(true);
		// 将应用程序图标设置为可点击的按钮,并且在图标上添加向左的箭头
		// 该句代码起到了决定性作用
		mActionBar.setDisplayHomeAsUpEnabled(true);
		m_tvAD  = (TextView) findViewById(R.id.tvAD);
		m_tvWgt = (TextView) findViewById(R.id.tvWgt);
		m_btCalibZero = (Button) findViewById(R.id.btCalbZero);
		m_btCalibWgt = (Button) findViewById(R.id.btCalbWgt);
		m_etZero = (EditText) findViewById(R.id.etZero);
		m_etWgt = (EditText) findViewById(R.id.etWgt);
		m_tvK = (TextView) findViewById(R.id.tvCalibKLabel);
		final View.OnClickListener pClickListener = new ButtonListener();

		m_btCalibZero.setOnClickListener(pClickListener);
		m_btCalibWgt.setOnClickListener(pClickListener);

		mDeviceAddress = getIntent().getStringExtra("address");
		String service = getIntent().getStringExtra("service");
		//String characteristic = getIntent().getStringExtra("characteristic");
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle == null) {
			Toast.makeText(this, "BLE not find", Toast.LENGTH_LONG).show();
			return;
		}
		mCharacteristicAD = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(Utils.UUID_AD));

		mCharacteristicZero = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(Utils.UUID_ZERO));
		mCharacteristicK = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(Utils.UUID_K));
		mCharacteristicWgt = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(Utils.UUID_WGT));
		
		mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicZero);
		mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicK);
		
		if (mCharacteristicAD == null) {
			Toast.makeText(this, "Characteristic not find", Toast.LENGTH_LONG)
					.show();
			return;
		}
		pTimer = new Timer();
		pTimer.schedule(new TimerTask() {
			public void run() {

				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicAD);
				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicWgt);
				
			}
		}, 1000, 1000);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mBleReceiver, BleService.getIntentFilter());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mBleReceiver);
		pTimer.cancel();
	}

}
