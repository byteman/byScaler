package com.blescaler.ui;

import java.lang.ref.WeakReference;
import java.util.Timer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.blescaler.utils.FloatValue;
import com.blescaler.utils.IntValue;
import com.blescaler.utils.NumberValues;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class CalibActivity extends Activity {

	private String address;
	
	
	private final String TAG = "CalibActivity";
	private TextView m_tvLoad,m_tvK;
	private Button btnScaler1,btnScaler2,btnScaler3,btnScaler4,btnStart=null;
	private Button m_btCalibZero, m_btCalibWgt,btn_read,m_btQuit=null;
	private EditText editText1,editText2,editText3,editText4=null;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private EditText m_etWgt;
	private boolean isStarted = false;
	private static Handler mHandler = null;

	private TextView m_tvWgt=null;

	private Runnable runnable;


	private final class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.btCalbZero) {
				WorkService.CalibZero(address);
			
			} else if (v.getId() == R.id.btCalbWgt) {
				if (m_etWgt.getText().length() <= 0) {
					Toast.makeText(CalibActivity.this, "input calc value",
							Toast.LENGTH_LONG).show();
					return;
				}
				IntValue wgt   =NumberValues.GetIntValue(m_etWgt.getText().toString());
				
				WorkService.CalibK(address,1, wgt.value);
			}else if(v.getId() == R.id.btn_save)
			{
				finish();
			}else if(v.getId() == R.id.Button01)
			{
				//scaler1
				if(isStarted){
					//auto calib
					WorkService.auto_k(address,1);
				}else{
					//hand calib
					FloatValue vf =NumberValues.GetFloatValue(editText1.getText().toString());
					if(vf.ok)
						WorkService.hand_k(address,0, (int) (vf.value*1000));
				}
			}
			else if(v.getId() == R.id.Button03)
			{
				if(isStarted){
					//auto calib
					WorkService.auto_k(address,2);
				}else{
					//hand calib
					FloatValue vf =NumberValues.GetFloatValue(editText2.getText().toString());
					if(vf.ok)
						WorkService.hand_k(address,1, (int) (vf.value*1000));
				}
				//scaler2
			}else if(v.getId() == R.id.Button02)
			{
				if(isStarted){
					//auto calib
					WorkService.auto_k(address,3);
				}else{
					//hand calib
					FloatValue vf =NumberValues.GetFloatValue(editText3.getText().toString());
					if(vf.ok)
						WorkService.hand_k(address,2, (int) (vf.value*1000));
				}
				//scaler3
			}else if(v.getId() == R.id.Button04)
			{
				if(isStarted){
					//auto calib
					WorkService.auto_k(address,4);
				}else{
					//hand calib
					FloatValue vf =NumberValues.GetFloatValue(editText4.getText().toString());
					if(vf.ok)
						WorkService.hand_k(address,3, (int) (vf.value*1000));
				}
				//scaler4
			}else if(v.getId() == R.id.Button05)
			{
				//start.
				if(isStarted){
					btnStart.setText(CalibActivity.this.getResources().getString(R.string.start));
					WorkService.auto_k(address,5);
				}else{
					btnStart.setText(CalibActivity.this.getResources().getString(R.string.stop));
					WorkService.auto_k(address,0);
				}
				isStarted=!isStarted;
				
			}
			else if(v.getId() == R.id.btn_read)
			{
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						WorkService.read_all_ks(address);
					}
					
				}).start();
				
				
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
		
		m_tvLoad = (TextView) findViewById(R.id.tvLoad);
		
		btnScaler1=(Button) findViewById(R.id.Button01);
		btnScaler2=(Button) findViewById(R.id.Button03);
		btnScaler3=(Button) findViewById(R.id.Button02);
		btnScaler4=(Button) findViewById(R.id.Button04);
		btnStart=(Button) findViewById(R.id.Button05);
		editText1=(EditText)findViewById(R.id.editText1);
		editText2=(EditText)findViewById(R.id.EditText03);
		editText3=(EditText)findViewById(R.id.EditText04);
		editText4=(EditText)findViewById(R.id.EditText05);
		btn_read = (Button) findViewById(R.id.btn_read);
		
		final View.OnClickListener pClickListener = new ButtonListener();

		m_btCalibZero.setOnClickListener(pClickListener);
		m_btCalibWgt.setOnClickListener(pClickListener);
		m_btQuit.setOnClickListener(pClickListener);
		btnScaler1.setOnClickListener(pClickListener);
		btnScaler2.setOnClickListener(pClickListener);
		btnScaler3.setOnClickListener(pClickListener);
		btnScaler4.setOnClickListener(pClickListener);
		btn_read.setOnClickListener(pClickListener);
		btnStart.setOnClickListener(pClickListener);
		address = WorkService.getDeviceAddress(this, 0);
		
		//String characteristic = getIntent().getStringExtra("characteristic");
	
		mHandler = new MHandler(this);
		
		runnable = new Runnable(){  
			   @Override  
			   public void run() {  
			    // TODO Auto-generated method stub  
			    //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作  
				   WorkService.requestReadWgt(address);
				   
				   
				   mHandler.postDelayed(this, 2000);  
			   }   
		};  
		mHandler.postDelayed(runnable, 2000);

	}
	private void popConnectProcessBar(Context ctx)
	{
		address = WorkService.getDeviceAddress(this, 0);
		if(address == "")
		{
			showFailBox("没有连接的蓝牙秤，请先扫描！");
			return;
		}
		if(WorkService.hasConnected(address)) return;
		
		if(progressDialog!=null && progressDialog.isShowing())
		{
			return;
		}
	    progressDialog =ProgressDialog.show(ctx, "bleScaler", "connecting scaler");     
        
	    //reloadScaler();
	    WorkService.requestConnect(address);
	  
        
        Message msg = mHandler.obtainMessage(MSG_TIMEOUT);
        
	    mHandler.sendMessageDelayed(msg, 5000);
	}
	private void showFailBox(String msg)
	{
		 new AlertDialog.Builder(this).setTitle(this.getResources().getString(R.string.prompt))//设置对话框标题  
		  
	     .setMessage(msg)//设置显示的内容  
	  
	     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
	  
	          
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
	  
	             // TODO Auto-generated method stub  
	  
	            dialog.dismiss();
	  
	         }  
	  
	     }).show();//在按键响应事件中显示此对话框  
	  
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
		popConnectProcessBar(this);
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
				case Global.MSG_SCALER_K_QUERY_RESULT:
				{
					Scaler d = (Scaler) msg.obj;
					
					if(d != null)
					{
						if(msg.arg1==1)
						{
							theActivity.editText1.setText(""+d.allks[0]);
							theActivity.editText2.setText(""+d.allks[1]);
						}
						else if(msg.arg1==2)
						{
							theActivity.editText3.setText(""+d.allks[2]);
							theActivity.editText4.setText(""+d.allks[3]);
						}
					}
					break;
				}
				case Global.MSG_BLE_WGTRESULT:
				{
					//BluetoothDevice device = (BluetoothDevice) msg.obj;
					
					Scaler d = (Scaler) msg.obj;
					
					if(d != null && theActivity.m_tvWgt!=null)
					{
						
						String w = d.getWeight()+"";
						theActivity.m_tvWgt.setText(w);
					
							
					}
					break;
				}
				case Global.MSG_SCALER_PAR_GET_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					
					Toast.makeText(theActivity, "success!", Toast.LENGTH_SHORT).show();
					break;
				}
				case Global.MSG_SCALER_ZERO_CALIB_RESULT:
				{
				
					Scaler s = (Scaler)msg.obj;
					if(msg.arg1 != 0)
					{
						Toast.makeText(theActivity, "calibrate failed!", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						
						Toast.makeText(theActivity, "calibrate ok", Toast.LENGTH_SHORT).show();
					}
					break;
				}
				case Global.MSG_SCALER_K_CALIB_RESULT:
				{
					Scaler s = (Scaler)msg.obj;
					if(msg.arg1 != 0)
					{
						Toast.makeText(theActivity, "calibrate failed!", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						theActivity.m_tvLoad.setText(String.valueOf(s.getLoadValue()));
						Toast.makeText(theActivity, "calibrate ok", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
			
		}
	}

}
