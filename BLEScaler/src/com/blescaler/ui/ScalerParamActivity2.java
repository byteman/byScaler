package com.blescaler.ui;

import java.lang.ref.WeakReference;



import com.blescaler.ui.ble.MainActivity;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ScalerParamActivity2 extends Activity implements OnClickListener {

	
	
	private EditText edit_ver;
	private EditText edit_time;
	private EditText edit_write_index,edit_read_index;
	private Button 	 btn_gprs_test;
	

	private Button btn_read, btn_write,btn_back,btn_sync_time;
	
	private String address = "C4:BE:84:22:91:E2";
	private static Handler mHandler = null;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		//WorkService.requestReadPar(address);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		WorkService.delHandler(mHandler);
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
		setContentView(R.layout.test2);
				
		edit_ver = (EditText) findViewById(R.id.edit_ver);
		//edit_time = (EditText) findViewById(R.id.edit_time);
		
		edit_time = (EditText) findViewById(R.id.edit_time);
		edit_write_index = (EditText) findViewById(R.id.edit_write_index);
		edit_read_index= (EditText) findViewById(R.id.edit_read_index);
	
	
		btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);
		btn_write = (Button) findViewById(R.id.btn_save);
		btn_write.setOnClickListener(this);
		btn_sync_time = (Button) findViewById(R.id.btn_sync_time);
		btn_sync_time.setOnClickListener(this);
		
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
	
		address = getIntent().getStringExtra("address");
		mHandler = new MHandler(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_read:
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						WorkService.requestReadPar2(address);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).start();
			
			break;
		case R.id.btn_save:
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub

					ScalerParam sp = new ScalerParam();
					sp.write_index = (short) Integer.parseInt(edit_write_index.getText().toString());
					sp.read_index = (short) Integer.parseInt(edit_read_index.getText().toString());
					sp.SetNowTime();
									
					WorkService.requestWriteParamValue2(address,sp);
		
				}
				
			}).start();
			
		
			break;
		case R.id.btn_back:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		case R.id.btn_sync_time:
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub

					ScalerParam sp = new ScalerParam();
				
					sp.SetNowTime();
									
					WorkService.SyncTime(address,sp);
		
				}
				
			}).start();
			break;
		default:
			break;
		}
	}
	
	private void showAddrParam(ScalerParam sp)
	{	
		edit_write_index.setText(""+sp.write_index);
		edit_read_index.setText("" + sp.read_index);
	
	}
	private void showTimeParam(ScalerParam sp)
	{
		
		edit_time.setText(sp.GetTimeString());	

	
	}
	static class MHandler extends Handler {

		WeakReference<ScalerParamActivity2> mActivity;

		MHandler(ScalerParamActivity2 activity) {
			mActivity = new WeakReference<ScalerParamActivity2>(activity);
			
		}

		@Override
		public void handleMessage(Message msg) {
			ScalerParamActivity2 theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_GET_PARAM4_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.showAddrParam(scaler.para);
					//Utils.Msgbox(theActivity, "读取成功");
					break;
				}
				case Global.MSG_GET_PARAM5_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.showTimeParam(scaler.para);
					//Utils.Msgbox(theActivity, "读取成功");
					break;
				}
				case Global.MSG_SCALER_PAR_SET_RESULT:
				{
					//设置参数的返回
					if(msg.arg1 == 0)
					{
						Utils.Msgbox(theActivity, "写入成功");
					}
					else
					{
						Utils.Msgbox(theActivity, "写入失败");
					}
					break;
				}
				case Global.MSG_BLE_FAILERESULT:
				{
					
					String reason = WorkService.getFailReason(msg.arg1);
					Utils.Msgbox(theActivity, "请求失败: " + reason);
					break;
				}
				case Global.MSG_SCALER_SAVE_EEPROM:
				{
					//设置参数的返回
					if(msg.arg1 == 0)
					{
						Utils.Msgbox(theActivity, "保存成功");
					}
					else
					{
						Utils.Msgbox(theActivity, "保存失败");
					}
					break;
				}
			}
		}
	}

}
