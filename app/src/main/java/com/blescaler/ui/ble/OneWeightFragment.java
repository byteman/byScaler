package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.HistoryWeightActivity;
import com.blescaler.ui.R;
import com.blescaler.util.IntValue;
import com.blescaler.util.NumberValues;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class OneWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	Button btn_save = null;
	ImageView btn_print = null;
	Button btn_tare = null;
	Button btn_zero = null;
	Button btn_swtich = null;
	Button btn_history = null;
	ImageView img_zero = null;
	ImageView img_still = null;
	ImageView img_tare = null;
	ImageView img_conn=null;
	Button btn_unit,btn_still = null;
	BatteryState btn_power = null;
	TextView tv_weight = null,tv_unit=null;
	TextView txtTare=null;
	AutoBgButton btn_ng = null;
	AutoBgButton btn_preset = null;
	
	//Scaler scaler = null;
	public int cont=0,cout_2s,cout_3s=0;
	private static String address;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";
	private static String unit="g";
	private boolean isGross = true;
	WeightDao dao = null;
	private void updateState()
	{

		   if(!WorkService.hasConnected(address))
		   {
               img_conn.getDrawable().setLevel(0);
			   tv_weight.setTextColor(Color.rgb(0x80, 0x80, 0x80));
		   }
		   else
		   {
               img_conn.getDrawable().setLevel(1);
			   //87CEEB
			   tv_weight.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
		   }
		
	}
	private Runnable watchdog = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub


//			   if(cont++ >= 1)
//			   {
//
//				   WorkService.requestReadWgtV2(address);
//				   cont = 0;
//			   }
//			   if(cout_2s++ > 10)
//			   {
//				   updateState();
//				   WorkService.readPower(address);
//				   cout_2s = 0;
//			   }
//			   if(cout_3s > 0)
//			   {
//				   cout_3s--;
//			   }
            updateState();
			   mHandler.postDelayed(this, 1000);
		}

	};

	private void popConnectProcessBar(Context ctx)
	{
		address =WorkService.getDeviceAddress(this.getActivity(), 0);
		if(address == "")
		{
			showFailBox(ctx.getString(R.string.prompt_scan));
			return;
		}
		if(WorkService.hasConnected(address)) return;
		
		if(progressDialog!=null && progressDialog.isShowing())
		{
			return;
		}
	    progressDialog =ProgressDialog.show(ctx, ctx.getString(R.string.prompt_title), ctx.getString(R.string.connect_ble));

	    if(!WorkService.requestConnect(address))
	    {

			if(progressDialog!=null && progressDialog.isShowing())
				progressDialog.dismiss(); //关闭进度条
			//Toast.makeText(this.getActivity(),"连接错误",Toast.LENGTH_SHORT).show();
			return;
	    }
	  
        
        Message msg = mHandler.obtainMessage(MSG_TIMEOUT);
        
	    mHandler.sendMessageDelayed(msg, 5000);
	}
    

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//super.onPause();
		mHandler.removeCallbacks(watchdog);

		WorkService.delHandler(mHandler);
		Log.e(TAG, "onStop");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		mHandler.postDelayed(watchdog, 1000);

		updateState();
		tv_unit.setText(unit);
		
		
		popConnectProcessBar(this.getActivity());
	}
	private void initUI()
	{
		btn_save  = (Button) root.findViewById(R.id.btn_save);
		btn_print = (ImageView) root.findViewById(R.id.btn_print);
		btn_tare  = (Button) root.findViewById(R.id.btn_tare);
		btn_swtich = (Button) root.findViewById(R.id.btn_switch);
		tv_weight = (TextView) root.findViewById(R.id.tv_weight);
		btn_zero = (Button) root.findViewById(R.id.btn_zero);
		btn_ng = (AutoBgButton) root.findViewById(R.id.btn_ng);
		btn_preset = (AutoBgButton) root.findViewById(R.id.btn_preset);
		tv_unit = (TextView) root.findViewById(R.id.textView2);
		txtTare = (TextView)root.findViewById(R.id.txtTare);
		img_zero = (ImageView) root.findViewById(R.id.img_zero);
		img_still = (ImageView) root.findViewById(R.id.img_still);
		img_tare = (ImageView) root.findViewById(R.id.img_tare);
		img_conn =  root.findViewById(R.id.img_conn_state);
        img_conn.getDrawable().setLevel(0);
		btn_unit = (Button) root.findViewById(R.id.btn_unit);
		btn_history = root.findViewById(R.id.btn_history);
		btn_unit.setOnClickListener(this);
		

		btn_save.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		btn_tare.setOnClickListener(this);
		tv_weight.setOnClickListener(this);
		btn_zero.setOnClickListener(this);
		btn_swtich.setOnClickListener(this);
		btn_history.setOnClickListener(this);
		
	}
	private void initRes()
	{
		mHandler = new MHandler(this);
		
		
		//mHandler.postDelayed(watchdog, 200);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_oneweight_table, container, false);
		
		initUI();
		initRes();
		dao = new WeightDao(this.getActivity());
		return root;
	}

	 private void inputTitleDialog() {

	        final EditText inputServer = new EditText(this.getActivity());
	        inputServer.setFocusable(true);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
	        builder.setTitle("请输入").setIcon(
	        		android.R.drawable.ic_dialog_info).setView(inputServer).setNegativeButton(
	                "取消", null);
	        builder.setPositiveButton("确定",
	                new DialogInterface.OnClickListener() {

	                    public void onClick(DialogInterface dialog, int which) {
	                        String inputValue = inputServer.getText().toString();
	                        
	                        IntValue wgt   =NumberValues.GetIntValue(inputValue);
	        				
	                        if(wgt.ok)
	                        {
		                        if(WorkService.setPreTare(address,wgt.value))
	                        	{
	 	                        	
	 	                        }
	                        }
                        
	                        
	                       
	                    }
	                });
	        builder.show();
	    }
	@Override
	public void onClick(View arg0) {
		cout_3s = 5;
		switch(arg0.getId())
		{
		case R.id.btn_still:
			
			break;
		case R.id.btn_history:
			Intent intent = new Intent(this.getActivity(), HistoryWeightActivity.class);

			startActivity(intent);

			break;
		case R.id.btn_save:
			if(saveWeight())
			{
				Utils.Msgbox(this.getActivity(), getString(R.string.saveok));
			}
			else{
				Utils.Msgbox(this.getActivity(), getString(R.string.savefail));
			}

			break;
		case R.id.btn_print:
			WorkService.common_msg(address,Global.REG_OPERATION,99);
			break;
		case R.id.btn_tare:
			WorkService.discardTare(address);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!WorkService.setZero(address))
			{
				//Utils.Msgbox(this.getActivity(), "清零失败，净重状态不允许清零");
			}
			break;
		case R.id.tv_weight:
			popConnectProcessBar(this.getActivity());
			
			break;
		case R.id.btn_zero:
			//清零
			img_still.getDrawable().setLevel(0);
            img_conn.getDrawable().setLevel(0);
			//img_still.setImageDrawable(getResources().getDrawable(R.drawable.ico_a));
			if(!WorkService.setZero(address))
			{
				Utils.Msgbox(this.getActivity(), "清零失败，净重状态不允许清零");
			}
			break;
		case R.id.btn_switch:
			//净重和毛重切换
			img_still.getDrawable().setLevel(1);
			img_conn.getDrawable().setLevel(1);
			//img_still.setImageDrawable(getResources().getDrawable(R.drawable.ico_a_click));
			WorkService.common_msg(address,Global.REG_OPERATION,5);
			break;
		case R.id.btn_preset:
			inputTitleDialog();
			break;
		case R.id.btn_sleep:
			WorkService.common_msg(address,Global.REG_OPERATION,12);
			break;
		case R.id.btn_wake:
			WorkService.common_msg(address,Global.REG_OPERATION,13);
			break;
		case R.id.btn_unit:
			WorkService.common_msg(address,Global.REG_OPERATION,14);
			break;
		
		}
		
	}
	private boolean saveWeight() {
		// TODO Auto-generated method stub
		//净重的时候 毛重=显示重量+皮重
		//毛重的时候 毛重=显示重量

		WeightRecord rec = new WeightRecord();


		if(isGross)
		{
			rec.setGross(tv_weight.getText().toString());
			rec.setNet(tv_weight.getText().toString());
			rec.setTare("0");
		}
		else
		{
			rec.setGross(tv_weight.getText().toString());
			rec.setNet(tv_weight.getText().toString());
			rec.setTare(txtTare.getText().toString());
		}

		if(dao == null) return false;

		return dao.saveWeight(rec);
	}


	private void showFailBox(String msg)
	{
		 new AlertDialog.Builder(this.getActivity()).setTitle(this.getString(R.string.prompt_title))//设置对话框标题
		  
	     .setMessage(msg)//设置显示的内容  
	  
	     .setPositiveButton(this.getString(R.string.ok),new DialogInterface.OnClickListener() {//添加确定按钮
	  
	          
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
	  
	             // TODO Auto-generated method stub  
	  
	            dialog.dismiss();
	  
	         }  
	  
	     }).show();//在按键响应事件中显示此对话框  
	  
	}
	public void set_zero_state(boolean bZero)
	{
		img_zero.getDrawable().setLevel(bZero?1:0);
	}
	public void set_still_state(boolean bStill)
	{
		img_still.getDrawable().setLevel(bStill?1:0);
	}
	public void set_tare_state(boolean bTare)
	{
		img_tare.getDrawable().setLevel(bTare?1:0);
	}

	public static Fragment newFragment() {
		OneWeightFragment f = new OneWeightFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
	}

	static class MHandler extends Handler {

		WeakReference<OneWeightFragment> mActivity;
		

		MHandler(OneWeightFragment activity) {
			mActivity = new WeakReference<OneWeightFragment>(activity);
			
		}
		
		@Override
		public void handleMessage(Message msg) {
			OneWeightFragment theActivity = mActivity.get();
			switch (msg.what) {


				case Global.MSG_BLE_WGTRESULT_V2:
				{

					Scaler d = (Scaler) msg.obj;


					if(d==null)
					{
						return;
					}
					theActivity.updateState();

					theActivity.tv_weight.setText(d.getDispalyWeight());
					theActivity.set_zero_state(d.isZero());
					theActivity.set_still_state(d.isStandstill());
					theActivity.set_tare_state(!d.isGross());
					theActivity.tv_unit.setText(d.getUnit());
					theActivity.txtTare.setText(Utils.FormatFloatValue(d.getTare(), d.GetDotNum()));
					//theActivity.tv_weight.setText(Utils.FormatFloatValue(d.getDispalyWeight(), d.GetDotNum()));

					break;
				}
				case Global.MSG_BLE_DISCONNECTRESULT:
				{
					String addr =(String)msg.obj;
					Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");
					
					//theActivity.tv_conn.setText("已断开");
					//WorkService.connectAll();
					//mHandler.postDelayed(r, delayMillis)
					break;
				}
				case Global.MSG_BLE_FAILERESULT:
				{

					//Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1) +"  " + WorkService.getFailType(msg.arg2), Toast.LENGTH_SHORT).show();
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
						theActivity.showFailBox(theActivity.getString(R.string.prompt_conn_timeout));
						//Toast.makeText(theActivity.getActivity(),"timeout",Toast.LENGTH_SHORT).show();
					}
				}
				case Global.MSG_SCALER_CTRL_RESULT:
				{
					//Toast.makeText(theActivity.getActivity(), "success!", Toast.LENGTH_SHORT).show();
					break;
				}
				case Global.MSG_SCALER_POWER_RESULT:
				{
//					int result = msg.arg1;
//					theActivity.btn_power.refreshPower((float)result/1000.0f);
					break;
				}
			}
			
		}
	}
	


};