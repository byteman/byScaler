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
	private BleGattCharacteristic mCharacteristic;
	private final String TAG = "CalibActivity";
	private TextView m_tvAD;
	private Button m_btCalibZero, m_btCalibWgt;
	private EditText m_etZero, m_etWgt;

	private final class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.btCalbZero) {
				m_etZero.setText(m_tvAD.getText());
			} else if (v.getId() == R.id.btCalbWgt) {
				if (m_etWgt.getText().length() <= 0) {
					Toast.makeText(CalibActivity.this, "请先输入内容",
							Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();

			String action = intent.getAction();
			Log.e(TAG, action);
			if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {

				finish();
			} else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

				ad = Utils.byte2Int(val);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						m_tvAD.setText(String.valueOf(ad));
					}
				});

			} else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
				Toast.makeText(CalibActivity.this, "Write success!",
						Toast.LENGTH_SHORT).show();
			}

		}
	};
	private ActionBar mActionBar;

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
		m_tvAD = (TextView) findViewById(R.id.tvAD);
		m_btCalibZero = (Button) findViewById(R.id.btCalbZero);
		m_btCalibWgt = (Button) findViewById(R.id.btCalbWgt);
		m_etZero = (EditText) findViewById(R.id.etZero);
		m_etWgt = (EditText) findViewById(R.id.etWgt);
		final View.OnClickListener pClickListener = new ButtonListener();

		m_btCalibZero.setOnClickListener(pClickListener);
		m_btCalibWgt.setOnClickListener(pClickListener);

		mDeviceAddress = getIntent().getStringExtra("address");
		String service = getIntent().getStringExtra("service");
		String characteristic = getIntent().getStringExtra("characteristic");
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle == null) {
			Toast.makeText(this, "BLE not find", Toast.LENGTH_LONG).show();
			return;
		}
		mCharacteristic = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(characteristic));

		if (mCharacteristic == null) {
			Toast.makeText(this, "Characteristic not find", Toast.LENGTH_LONG)
					.show();
			return;
		}
		new Timer().schedule(new TimerTask() {
			public void run() {

				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristic);
			}
		}, 0, 100);

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
	}

}
