package com.blescaler.ui;

import java.lang.ref.WeakReference;


import com.blescaler.ui.R;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;
import com.tencent.bugly.crashreport.CrashReport;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SysParamActivity extends Activity implements OnClickListener {

	private EditText ed_unit;
	private Button btn_read, btn_save,btn_quit;

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	
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
		ed_unit = (EditText)findViewById(R.id.ed_unit);
		btn_quit.setOnClickListener(this);
		//btn_read.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		
		ed_unit.setText(WorkService.getUnit());
		CrashReport.testJavaCrash();
	
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
		
		case R.id.btn_save:
			String unit = "";
			unit = ed_unit.getText().toString();
			if(false == checkunit(unit)) return;
			WorkService.setUnit(unit);
			Utils.Msgbox(this, "保存成功");
			break;
		
		case R.id.btn_quit:
			finish();
			break;
		default:
			break;
		}
	}
	
}
