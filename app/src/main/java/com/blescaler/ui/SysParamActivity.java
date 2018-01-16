package com.blescaler.ui;

import java.lang.ref.WeakReference;










import com.blescaler.ui.R;
import com.blescaler.ui.ScalerParamActivity.MHandler;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;
import com.tencent.bugly.crashreport.CrashReport;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SysParamActivity extends Activity implements OnClickListener {

	private EditText ed_ad1,ed_ad2,ed_ad3,ed_ad4=null;
	private Button btn_read, btn_save,btn_quit;
	private String address = "C4:BE:84:22:91:E2";
	private static Handler mHandler = null;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private Runnable runnable = null;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		popConnectProcessBar(this);
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacks(runnable);
		
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
	private void showFailBox(String msg)
	{
		 new AlertDialog.Builder(this).setTitle("prompt")//设置对话框标题  
		  
	     .setMessage(msg)//设置显示的内容  
	  
	     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
	  
	          
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
	  
	             // TODO Auto-generated method stub  
	  
	            dialog.dismiss();
	  
	         }  
	  
	     }).show();//在按键响应事件中显示此对话框  
	  
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
		setContentView(R.layout.activity_param);
		
		btn_quit = (Button) findViewById(R.id.btn_quit);
		//btn_read = (Button) findViewById(R.id.btn_read);
		btn_save = (Button) findViewById(R.id.btn_save);
		ed_ad1= (EditText)findViewById(R.id.ed_ad1);
		ed_ad2= (EditText)findViewById(R.id.ed_ad2);
		ed_ad3= (EditText)findViewById(R.id.ed_ad3);
		ed_ad4= (EditText)findViewById(R.id.ed_ad4);
		
		btn_quit.setOnClickListener(this);
		//btn_read.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		
		address = WorkService.getDeviceAddress(this, 0);
	
		mHandler = new MHandler(this);
		runnable = new Runnable(){  
			   @Override  
			   public void run() {  
			    // TODO Auto-generated method stub  
			    //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作  
				   WorkService.requestReadAds(address);
				   
				   
				   mHandler.postDelayed(this, 1000);  
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
	private boolean checkunit(String unit) {
		if (unit.equals("")) {
			Utils.Msgbox(this, "请输入单位");
			return false;
		}
		if (unit.length() > 3) {
			Utils.Msgbox(this, "单位的长度不要超过3个字符");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_read:
			
			break;
		case R.id.btn_quit:
			finish();
			break;
		default:
			break;
		}
	}
	static class MHandler extends Handler {

		WeakReference<SysParamActivity> mActivity;

		MHandler(SysParamActivity activity) {
			mActivity = new WeakReference<SysParamActivity>(activity);
			
		}

		@Override
		public void handleMessage(Message msg) {
			SysParamActivity theActivity = mActivity.get();
			switch (msg.what) {

				
				case Global.MSG_SCALER_AD_CHAN1_RESULT:
				{				
					theActivity.ed_ad1.setText(""+msg.arg1);
					theActivity.ed_ad2.setText(""+msg.arg2);
					break;
				
				}
				case Global.MSG_SCALER_AD_CHAN2_RESULT:
				{				
					theActivity.ed_ad3.setText(""+msg.arg1);
					theActivity.ed_ad4.setText(""+msg.arg2);
					break;
				
				}
			}
		}
	}

}
