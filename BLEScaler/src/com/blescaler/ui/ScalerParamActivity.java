package com.blescaler.ui;

import java.lang.ref.WeakReference;





import com.blescaler.ui.R;
import com.blescaler.ui.ble.MainActivity;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ScalerParamActivity extends Activity implements OnClickListener {

	
	
	private EditText edit_zerotrack;
	private EditText edit_zeroinit;
	private EditText edit_handzero;
	private EditText edit_mtd;
	private EditText edit_filter;
	private EditText edt_nov;// edt_unit;
	private EditText edit_sleep,edit_srs_num;
	//private EditText edit_dignum;
	private Spinner sp_dignum;
	private Spinner sp_div;
	private Spinner sp_unit;
	private Button btn_read, btn_write,btn_eeprom;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
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
		popConnectProcessBar(this);
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
		edit_zerotrack = (EditText) findViewById(R.id.edit_zerotrack);
		edit_zeroinit = (EditText) findViewById(R.id.edit_zeroinit);
		edit_handzero = (EditText) findViewById(R.id.edit_handzero);
		edit_mtd = (EditText) findViewById(R.id.edit_mtd);
		edit_filter= (EditText) findViewById(R.id.edit_fiter);
		edit_sleep= (EditText) findViewById(R.id.edit_sleep);
		edit_srs_num= (EditText) findViewById(R.id.edit_snr_num);
		//edit_dignum = (EditText) findViewById(R.id.edit_dot);
		sp_dignum = (Spinner) findViewById(R.id.sp_dot);
		sp_div = (Spinner) findViewById(R.id.sp_div);
		sp_unit = (Spinner) findViewById(R.id.sp_unit);
		btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);
		btn_write = (Button) findViewById(R.id.btn_save);
		btn_write.setOnClickListener(this);
		btn_eeprom = (Button) findViewById(R.id.btn_eeprom);
		btn_eeprom.setOnClickListener(this);
		
		
		edt_nov = (EditText) findViewById(R.id.ed_nov);
		//edt_unit = (EditText) findViewById(R.id.ed_unit);
		//address = getIntent().getStringExtra("address");
		address = WorkService.getDeviceAddress(this, 0);
		mHandler = new MHandler(this);
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
	private boolean checknov(String nov, ScalerParam sp) {

		if (nov.equals("")) {
			Utils.Msgbox(this, "请输入额定重量");
			return false;
		}
		

		try {
			sp.setNov(Integer.parseInt(nov));
		} catch (java.lang.NumberFormatException e) {
			Utils.Msgbox(this, "额定重量请输入整数");
			// TODO: handle exception
			return false;
		}
		
		return true;
	}
	private void clear()
	{
	edit_zerotrack.setText("");
	edit_zeroinit.setText("");
	edit_handzero.setText("");
	edit_mtd.setText("");
	edit_filter.setText("");
	edt_nov.setText("");
	edit_sleep.setText("");
	edit_srs_num.setText("");
	
	
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
						
						WorkService.requestReadPar(address);
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
					//if (!checkunit(edt_unit.getText().toString(), sp))
					//	return;
					ScalerParam sp = new ScalerParam();
					int nov = Integer.parseInt(edt_nov.getText().toString());
					int mtd = Integer.parseInt(edit_mtd.getText().toString());
					int zeroinit = Integer.parseInt(edit_zeroinit.getText().toString());
					int zerotrack = Integer.parseInt(edit_zerotrack.getText().toString());
					int filter = Integer.parseInt(edit_filter.getText().toString());
					int handzero = Integer.parseInt(edit_handzero.getText().toString());
					int sleep = Integer.parseInt(edit_sleep.getText().toString());
					int srs_num = Integer.parseInt(edit_srs_num.getText().toString());
					
					//int dot = Integer.parseInt(sp_dignum.getText().toString());
					sp.setNov(nov);
					sp.setMtd((byte) mtd);
					sp.setPwr_zerotrack((byte) zeroinit);
					sp.setZerotrack((byte) zerotrack);
					sp.setFilter((byte)filter);
					sp.setHand_zerotrack((byte) handzero);
					sp.setSleep((short) sleep);
					sp.setSnr_num((short) srs_num);
					int div_id = (int) sp_div.getSelectedItemId();
				
					sp.setResultionIndex((byte) div_id);
					
					sp.setDignum((byte) sp_dignum.getSelectedItemId());
					sp.setUnit((byte) sp_unit.getSelectedItemId());

					WorkService.requestWriteParamValue(address,sp);
		
				}
				
			}).start();
			
			ScalerParam sp = new ScalerParam();


			break;
		case R.id.btn_eeprom:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		default:
			break;
		}
	}
	
	private void showParam(ScalerParam sp)
	{
		//edit_dignum.setText(""+sp.getDignum());
		sp_dignum.setSelection(sp.getDignum());
		sp_div.setSelection(sp.getResultionIndex());
		sp_unit.setSelection(sp.getUnit());
		edit_mtd.setText(""+sp.getMtd());
		edit_handzero.setText(""+sp.getHand_zerotrack());
		edit_zerotrack.setText(""+sp.getZerotrack());
		edit_zeroinit.setText(""+sp.getPwr_zerotrack());	
		edt_nov.setText(String.valueOf(sp.getNov()));
		edit_filter.setText(String.valueOf(sp.getFilter()));
		edit_sleep.setText(String.valueOf(sp.getSleep()));
		edit_srs_num.setText(String.valueOf(sp.getSnr_num()));
		
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

				case Global.MSG_SCALER_PAR_GET_RESULT:
				{
					Scaler scaler = (Scaler) msg.obj;
					if(scaler==null)return;
					theActivity.showParam(scaler.para);
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
					
					//String reason = WorkService.getFailReason(msg.arg1);
					//Utils.Msgbox(theActivity, "请求失败: " + reason);
					break;
				}
				case Global.MSG_SCALER_CONNECT_OK:
				{
				
					if(progressDialog!=null && progressDialog.isShowing())
						progressDialog.dismiss(); //关闭进度条
						//Toast.makeText(theActivity.getActivity(),"all connect",Toast.LENGTH_SHORT).show();
					
					break;
				}
				case MSG_TIMEOUT:
				{
				
					if(progressDialog!=null && progressDialog.isShowing())
					{
						progressDialog.dismiss(); //关闭进度条
						theActivity.showFailBox("连接超时，点击重量显示可重新连接！");
						//Toast.makeText(theActivity.getActivity(),"timeout",Toast.LENGTH_SHORT).show();
					}
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
