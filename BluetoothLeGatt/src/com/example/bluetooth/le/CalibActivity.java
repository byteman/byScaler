package com.example.bluetooth.le;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.le.WeightActivity.MHandler;
import com.example.worker.Global;
import com.example.worker.WorkService;

public class CalibActivity extends Activity {

	private String mDeviceAddress;
	
	
	private final String TAG = "CalibActivity";
	private TextView m_tvAD,m_tvK;
	private Button m_btCalibZero, m_btCalibWgt;
	private EditText m_etZero, m_etWgt;
	private static Handler mHandler = null;
	private final class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.btCalbZero) {
				WorkService.requestCalibZero(mDeviceAddress);
			
			} else if (v.getId() == R.id.btCalbWgt) {
				if (m_etWgt.getText().length() <= 0) {
					Toast.makeText(CalibActivity.this, "请先输入内容",
							Toast.LENGTH_LONG).show();
					return;
				}
			
				float wgt   = Float.valueOf((String) m_etWgt.getText().toString());
				
			}

		}
	}


	private ActionBar mActionBar;
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
		
		//String characteristic = getIntent().getStringExtra("characteristic");
	
		mHandler = new MHandler(this);
		pTimer = new Timer();
		pTimer.schedule(new TimerTask() {
			public void run() {
				//Log.e(TAG,"calib timer");	
				
			}
		}, 1000, 1000);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		Log.e(TAG, "OnResume");
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop");
		WorkService.delHandler(mHandler);
		pTimer.cancel();
	}
	static class MHandler extends Handler {

		WeakReference<CalibActivity> mActivity;

		MHandler(CalibActivity activity) {
			mActivity = new WeakReference<CalibActivity>(activity);
			
		}

		@Override
		public void handleMessage(Message msg) {
			CalibActivity theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_BLE_WGTRESULT:
				{
					//BluetoothDevice device = (BluetoothDevice) msg.obj;
					int weight = msg.arg1;
					theActivity.m_tvAD.setText(String.valueOf(weight));
					break;
				}
				
			}
			
		}
	}

}
