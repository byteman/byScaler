package com.blescaler.ui;

import java.lang.ref.WeakReference;


import com.blescaler.ui.R;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ScalerParamActivity extends Activity implements OnClickListener {

	
	
	private Spinner sp_zerotrack;
	private Spinner sp_zeroinit;
	private Spinner sp_mtd;
	private Spinner sp_dignum;
	private Spinner sp_div;
	private Button btn_read, btn_write,btn_eeprom;
	private EditText edt_nov;// edt_unit;
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
		sp_zerotrack = (Spinner) findViewById(R.id.sp_zerotrack);
		sp_zeroinit = (Spinner) findViewById(R.id.sp_zeroinit);
		sp_mtd = (Spinner) findViewById(R.id.sp_mtd);
		sp_dignum = (Spinner) findViewById(R.id.sp_dignum);
		sp_div = (Spinner) findViewById(R.id.sp_div);
		btn_read = (Button) findViewById(R.id.btn_read);
		btn_read.setOnClickListener(this);
		btn_write = (Button) findViewById(R.id.btn_save);
		btn_write.setOnClickListener(this);
		btn_eeprom = (Button) findViewById(R.id.btn_eeprom);
		btn_eeprom.setOnClickListener(this);
		
		edt_nov = (EditText) findViewById(R.id.ed_nov);
		//edt_unit = (EditText) findViewById(R.id.ed_unit);
		address = getIntent().getStringExtra("address");
		mHandler = new MHandler(this);
	}

	private boolean checkunit(String unit, ScalerParam sp) {
		if (unit.equals("")) {
			Utils.Msgbox(this, "请输入单位");
			return false;
		}
		if (unit.length() > 3) {
			Utils.Msgbox(this, "单位的长度不要超过3个字符");
			return false;
		}
		sp.setUnit(unit);
		return true;
	}

	private boolean checknov(String nov, ScalerParam sp) {

		if (nov.equals("")) {
			Utils.Msgbox(this, "请输入额定重量");
			return false;
		}
		if (nov.length() > 7) {
			Utils.Msgbox(this, "额定重量的长度不要超过7个字符");
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_read:
			WorkService.requestReadPar(address);
			break;
		case R.id.btn_save:
			ScalerParam sp = new ScalerParam();

			//if (!checkunit(edt_unit.getText().toString(), sp))
			//	return;
			if (!checknov(edt_nov.getText().toString(), sp))
				return;
			sp.setMtd((byte) sp_mtd.getSelectedItemId());
			sp.setZerotrack((byte) sp_zerotrack.getSelectedItemId());
			sp.setPwr_zerotrack((byte) sp_zeroinit.getSelectedItemId());
			sp.setDignum((byte) sp_dignum.getSelectedItemId());
			sp.setResultion((byte)sp_div.getSelectedItemId());
			edt_nov.setText(sp.getNov()+"");
			//edt_unit.setText(sp.getUnit()+"");
			WorkService.requestWriteParamValue(address, sp);
			break;
		case R.id.btn_eeprom:
			WorkService.requestSaveParam(address);
			break;
		default:
			break;
		}
	}
	private void showParam(ScalerParam sp)
	{
		sp_dignum.setSelection(sp.getDignum());
		sp_div.setSelection(sp.getResultion());
		sp_mtd.setSelection(sp.getMtd());
		sp_zerotrack.setSelection(sp.getZerotrack());
		sp_zeroinit.setSelection(sp.getPwr_zerotrack());
		
		edt_nov.setText(String.valueOf(sp.getNov()));
		//edt_unit.setText(String.valueOf(sp.getUnit()));
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
					Utils.Msgbox(theActivity, "读取成功");
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
