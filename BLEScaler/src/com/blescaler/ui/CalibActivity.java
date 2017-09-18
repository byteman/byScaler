package com.blescaler.ui;

import java.lang.ref.WeakReference;
import java.util.Timer;

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

import com.blescaler.ui.R;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class CalibActivity extends Activity {

	private static String mDeviceAddress;
	
	
	private final String TAG = "CalibActivity";
	private TextView m_tvLoad,m_tvZero,m_tvK;
	private Button m_btCalibZero, m_btCalibWgt,m_btQuit=null;
	private EditText m_etWgt;
	private static Handler mHandler = null;

	private boolean m_readpara = false;
	private ActionBar mActionBar;
	private TextView m_tvWgt=null;
	private Timer pTimer;
	private Runnable runnable;
	
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
				
				int wgt   = Integer.valueOf((String) m_etWgt.getText().toString());
				Scaler s = WorkService.getScaler(mDeviceAddress);
				if(s == null) return;
				int nov   = s.para.getNov();
				WorkService.requestCalibK(mDeviceAddress, wgt, nov);
			}else if(v.getId() == R.id.btn_save)
			{
				finish();
			}

		}
	}

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
		//mActionBar = getActionBar();
		// 设置是否显示应用程序的图标
		//mActionBar.setDisplayShowHomeEnabled(true);
		// 将应用程序图标设置为可点击的按钮
		//mActionBar.setHomeButtonEnabled(true);
		// 将应用程序图标设置为可点击的按钮,并且在图标上添加向左的箭头
		// 该句代码起到了决定性作用
		//mActionBar.setDisplayHomeAsUpEnabled(true);
		m_tvWgt = (TextView) findViewById(R.id.tvWgt);
		m_btCalibZero = (Button) findViewById(R.id.btCalbZero);
		m_btCalibWgt = (Button) findViewById(R.id.btCalbWgt);
		m_btQuit= (Button) findViewById(R.id.btn_save);
		m_etWgt = (EditText) findViewById(R.id.etWgt);
		m_tvZero = (TextView) findViewById(R.id.tvZeros);
		m_tvLoad = (TextView) findViewById(R.id.tvLoad);
		m_tvK = (TextView) findViewById(R.id.tvCalibKLabel);
		m_tvK = (TextView) findViewById(R.id.tvCalibKLabel);
		
		final View.OnClickListener pClickListener = new ButtonListener();

		m_btCalibZero.setOnClickListener(pClickListener);
		m_btCalibWgt.setOnClickListener(pClickListener);
		m_btQuit.setOnClickListener(pClickListener);
		mDeviceAddress = getIntent().getStringExtra("address");
		m_readpara = false;
		//String characteristic = getIntent().getStringExtra("characteristic");
	
		mHandler = new MHandler(this);
		
		runnable = new Runnable(){  
			   @Override  
			   public void run() {  
			    // TODO Auto-generated method stub  
			    //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作  
				   WorkService.requestReadWgt(mDeviceAddress);
				   if(!m_readpara)
				   {
					   try {
						WorkService.requestReadPar(mDeviceAddress);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   }
				   mHandler.postDelayed(this, 1000);  
			   }   
		};  
		mHandler.postDelayed(runnable, 200);
		//pTimer = new Timer();
		//pTimer.schedule(new TimerTask() {
			//public void run() {
				//Log.e(TAG,"calib timer");	
				
			//}
		///}, 1000, 1000);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacks(runnable);
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
		
		//pTimer.cancel();
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
					
					Scaler d = (Scaler) msg.obj;
					
					if(d != null && theActivity.m_tvWgt!=null)
					{
						if(d.getAddress().equals(mDeviceAddress))
						{
							String w = d.getWeight()+"";
							theActivity.m_tvWgt.setText(w);
						}
							
					}
					break;
				}
				case Global.MSG_SCALER_PAR_GET_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.m_readpara = true;	
					Toast.makeText(theActivity, "读取参数成功...", Toast.LENGTH_SHORT).show();
					break;
				}
				case Global.MSG_SCALER_ZERO_CALIB_RESULT:
				{
					int result = msg.arg1;
					Scaler s = (Scaler)msg.obj;
					if(msg.arg1 != 0)
					{
						Toast.makeText(theActivity, "标定失败", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						theActivity.m_tvZero.setText(String.valueOf(s.getZeroValue()));
						Toast.makeText(theActivity, "标定成功", Toast.LENGTH_SHORT).show();
					}
					break;
				}
				case Global.MSG_SCALER_K_CALIB_RESULT:
				{
					Scaler s = (Scaler)msg.obj;
					if(msg.arg1 != 0)
					{
						Toast.makeText(theActivity, "标定失败", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						theActivity.m_tvLoad.setText(String.valueOf(s.getLoadValue()));
						Toast.makeText(theActivity, "标定成功", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
			
		}
	}

}
