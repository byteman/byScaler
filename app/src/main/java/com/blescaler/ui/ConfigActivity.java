package com.blescaler.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.blescaler.db.Config;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class ConfigActivity extends Activity implements OnClickListener {



	private RadioGroup radioGroup;
	private RadioButton enRadio;
	private RadioButton zhRadio;
	private Button btn_back;
	private static Handler mHandler = null;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//WorkService.requestReadPar(address);
	}
	protected void switchLanguage(String language) {
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		switch (language) {
			case "zh":
				config.locale = Locale.CHINESE;
				resources.updateConfiguration(config, dm);
				break;
			case "en":
				config.locale = Locale.ENGLISH;
				resources.updateConfiguration(config, dm);
				break;
			default:
				config.locale = Locale.US;
				resources.updateConfiguration(config, dm);
				break;
		}
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
		setContentView(R.layout.activity_config);
		radioGroup=findViewById(R.id.radioGroupID);
		enRadio=findViewById(R.id.enGroupID);
		zhRadio=findViewById(R.id.zhGroupID);
		String lang = Config.getInstance(this).getLanguage();
		if(lang == "zh")
		{
			zhRadio.setChecked(true);

		}
		else
		{
			enRadio.setChecked(true);
		}
		btn_back = findViewById(R.id.btn_save);
		btn_back.setOnClickListener(this);
		//设置监听
		radioGroup.setOnCheckedChangeListener(new RadioGroupListener());

	}
	class RadioGroupListener implements RadioGroup.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId==enRadio.getId()){
				Config.getInstance(ConfigActivity.this).setLanguage("en");
				switchLanguage("en");
			}else if (checkedId==zhRadio.getId()){
				Config.getInstance(ConfigActivity.this).setLanguage("zh");
				switchLanguage("zh");
			}
		}
	}

	private boolean checknov(String nov, ScalerParam sp) {

		if (nov.equals("")) {
			Utils.Msgbox(this, "请输入额定重量");
			return false;
		}


		try {
			sp.setNov(Integer.parseInt(nov));
		} catch (NumberFormatException e) {
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
		case R.id.btn_save:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		default:
			break;
		}
	}
	


}
