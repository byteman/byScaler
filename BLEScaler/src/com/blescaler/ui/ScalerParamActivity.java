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
import android.widget.Toast;

public class ScalerParamActivity extends Activity implements OnClickListener {

	
	
	private EditText edit_ver;
	private EditText edit_time;
	private EditText edit_hostip;
	private EditText edit_port;
	private EditText edit_id;
	private EditText edit_heart;
	private EditText edit_channel,edit_send_time_s,edit_acquire_s;

	private Button btn_read, btn_write,btn_next,btn_back;
	
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
		setContentView(R.layout.test);
				
		edit_ver = (EditText) findViewById(R.id.edit_ver);
		//edit_time = (EditText) findViewById(R.id.edit_time);
		
		edit_hostip = (EditText) findViewById(R.id.edit_hostip);
		edit_port = (EditText) findViewById(R.id.edit_port);
		edit_id= (EditText) findViewById(R.id.edit_id);
		edit_heart= (EditText) findViewById(R.id.edit_heart);
		edit_channel= (EditText) findViewById(R.id.edit_channel);
		//edit_dignum = (EditText) findViewById(R.id.edit_dot);
		edit_send_time_s = (EditText) findViewById(R.id.edit_send_time_s);
		edit_acquire_s = (EditText) findViewById(R.id.edit_acquire_s);
	
		btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);
		btn_write = (Button) findViewById(R.id.btn_save);
		btn_write.setOnClickListener(this);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
	
		address = getIntent().getStringExtra("address");
		mHandler = new MHandler(this);
	}
	private void clear()
	{
		edit_ver.setText("");
		edit_hostip.setText("");
		edit_port.setText("");
		edit_id.setText("");
		edit_heart.setText("");
		edit_channel.setText("");
		edit_send_time_s.setText("");
		edit_acquire_s.setText("");
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_read:
			clear();
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						
						WorkService.requestReadPar1(address);
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
					try{
						
						sp.SetHostString(edit_hostip.getText().toString());
						
						sp.hostport = Integer.parseInt(edit_port.getText().toString());
						sp.dev_id = (short) Integer.parseInt(edit_id.getText().toString());
						sp.heart = (short) Integer.parseInt(edit_heart.getText().toString());
						sp.channel = (short) Integer.parseInt(edit_channel.getText().toString());
						sp.send_time_s = (short) Integer.parseInt(edit_send_time_s.getText().toString());
						sp.acquire_s = (short) Integer.parseInt(edit_acquire_s.getText().toString());
					} catch (Exception e) {
						Toast.makeText(ScalerParamActivity.this, "输入格式有误", Toast.LENGTH_LONG).show();
					}	
					WorkService.requestWriteParamValue1(address,sp);
		
				}
				
			}).start();
			
		
			break;
		case R.id.btn_back:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		case R.id.btn_next:
			
			  Intent intent = new Intent(ScalerParamActivity.this, ScalerParamActivity2.class);
			  intent.putExtra("address","00");
			  startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private void showVerParam(ScalerParam sp)
	{

		edit_ver.setText(""+sp.version);	
		edit_id.setText("" + sp.dev_id);
	}
	private void showHostParam(ScalerParam sp)
	{
		edit_hostip.setText(sp.GetHostString());
		edit_port.setText(""+sp.hostport);
	}
	private void showTimeParam(ScalerParam sp)
	{	
		edit_send_time_s.setText(""+sp.send_time_s);
		edit_heart.setText("" + sp.heart);
		edit_channel.setText("" + sp.channel);
		edit_acquire_s.setText("" + sp.acquire_s);
	}
	static class MHandler extends Handler {

		WeakReference<ScalerParamActivity> mActivity;

		MHandler(ScalerParamActivity activity) {
			mActivity = new WeakReference<ScalerParamActivity>(activity);
			
		}

		@Override
		public void handleMessage(Message msg) {
			ScalerParamActivity theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_GET_PARAM1_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.showVerParam(scaler.para);
					//Utils.Msgbox(theActivity, "读取成功");
					break;
				}
				case Global.MSG_GET_PARAM2_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.showHostParam(scaler.para);
					//Utils.Msgbox(theActivity, "读取成功");
					break;
				}
				case Global.MSG_GET_PARAM3_RESULT:
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
					//Utils.Msgbox(theActivity, "请求失败: " + reason);
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
