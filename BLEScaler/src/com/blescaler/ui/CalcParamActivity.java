package com.blescaler.ui;

import java.lang.ref.WeakReference;








import com.blescaler.db.Channel;
import com.blescaler.db.Config;
import com.blescaler.ui.R;
import com.blescaler.ui.ble.MainActivity;
import com.blescaler.utils.FloatValue;
import com.blescaler.utils.NumberValues;
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
import android.widget.TextView;
import android.widget.Toast;

public class CalcParamActivity extends Activity implements OnClickListener {

	
	
	private EditText edit_R0,edit_T0,edit_K,edit_G,edit_C;
	private int  channel;
	private TextView lbl_chan;
	private Channel chan = null;
	private Button  btn_write,btn_back;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//WorkService.requestReadPar(address);
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
		setContentView(R.layout.test3);
				
		channel = getIntent().getIntExtra("channel", 1);
		
		chan = Config.getInstance(this).getChannel(channel);
		
		edit_G = (EditText) findViewById(R.id.edit_G);
		edit_K = (EditText) findViewById(R.id.edit_K);	
		edit_R0 = (EditText) findViewById(R.id.edit_R0);	
		edit_T0 = (EditText) findViewById(R.id.edit_T0);	
		edit_C = (EditText) findViewById(R.id.edit_C);	
		lbl_chan = (TextView) findViewById(R.id.lblchan);
		lbl_chan.setText("通道" + channel + "配置") ;
		if(chan != null)
		{
			edit_G.setText("" + chan.getG());
			edit_K.setText("" + chan.getK());
			edit_R0.setText("" + chan.getR0());
			edit_T0.setText("" + chan.getT0());
			edit_C.setText("" + chan.getC());
		}
		
		btn_write = (Button) findViewById(R.id.btn_save);
		btn_write.setOnClickListener(this);
	
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
	
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btn_save:
			{
				Channel c = new Channel();
				FloatValue fv = NumberValues.GetFloatValue(edit_G.getText().toString());
				if(fv.ok){
					c.setG(fv.value);
				}
				fv = NumberValues.GetFloatValue(edit_K.getText().toString());
				if(fv.ok){
					c.setK(fv.value);
				}
				fv = NumberValues.GetFloatValue(edit_R0.getText().toString());
				if(fv.ok){
					c.setR0(fv.value);
				}
				fv = NumberValues.GetFloatValue(edit_T0.getText().toString());
				if(fv.ok){
					c.setT0(fv.value);
				}
				fv = NumberValues.GetFloatValue(edit_C.getText().toString());
				if(fv.ok){
					c.setC(fv.value);
				}
				Config.getInstance(this).setChannel(channel, c);
				Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();
			}
		
			break;
		case R.id.btn_back:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		
		default:
			break;
		}
	}


}
