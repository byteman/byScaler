package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.blescaler.db.CountDao;
import com.blescaler.db.CountRecord;
import com.blescaler.ui.R;
import com.blescaler.util.IntValue;
import com.blescaler.util.NumberValues;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class OneCountFragment extends BaseFragment implements View.OnClickListener {
	View root;
	Button btn_save = null;
	ImageView btn_print = null;
	Button btn_tare = null;
	Button btn_zero = null;
	Button btn_swtich = null;

	ImageView img_zero = null;
	ImageView img_still = null;
	ImageView img_tare = null;
	ImageView img_conn=null;
	Button btn_switch_unit,btn_still = null;
	BatteryState btn_power = null;
	TextView tv_weight = null,tv_quantity=null,tv_uw=null;
	TextView txtTare=null;
    CountDao dao = null;
    Button btn_sample,btn_reset_count,btn_save_uw,btn_preset_uw;
	
	//Scaler scaler = null;
	public int cont=0,cout_2s,cout_3s=0;
	private int  uw = 0;
	private boolean enable_uw = false;
	private static String address;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";
	private static String unit="g";
	

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

		popConnectProcessBar(this.getActivity());
	}
	private void initUI()
	{
		btn_save  = (Button) root.findViewById(R.id.btn_save);
		btn_print = (ImageView) root.findViewById(R.id.btn_print);
		btn_tare  = (Button) root.findViewById(R.id.btn_tare);
		btn_swtich = (Button) root.findViewById(R.id.btn_switch);
		tv_weight = (TextView) root.findViewById(R.id.tv_weight);
        tv_uw = root.findViewById(R.id.tv_uw);
		btn_zero = (Button) root.findViewById(R.id.btn_zero);
		btn_switch_unit = (Button) root.findViewById(R.id.btn_switch_unit);

		btn_sample = (Button) root.findViewById(R.id.btn_sample);
		btn_reset_count= (Button) root.findViewById(R.id.btn_reset_uw);
		btn_save_uw = (Button) root.findViewById(R.id.btn_save_uw);
		btn_preset_uw = (Button) root.findViewById(R.id.btn_preset_uw);
		tv_quantity = (TextView) root.findViewById(R.id.tv_quantity);


		img_zero =  root.findViewById(R.id.img_zero);
		img_still = root.findViewById(R.id.img_still);
		img_tare =  root.findViewById(R.id.img_tare);
		img_conn =  root.findViewById(R.id.img_conn_state);
		img_conn.getDrawable().setLevel(0);
		btn_switch_unit.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		btn_tare.setOnClickListener(this);
		tv_weight.setOnClickListener(this);
		btn_zero.setOnClickListener(this);
		btn_swtich.setOnClickListener(this);
		btn_sample.setOnClickListener(this);
		btn_reset_count.setOnClickListener(this);
		btn_save_uw.setOnClickListener(this);
		btn_preset_uw.setOnClickListener(this);


		
	}
	private void initRes()
	{
		mHandler = new MHandler(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_count, container, false);
		
		initUI();
		initRes();
        dao = new CountDao(this.getActivity());
		return root;
	}

	 private void inputTitleDialog() {

	        final EditText inputServer = new EditText(this.getActivity());
	        inputServer.setFocusable(true);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
	        builder.setTitle(this.getString(R.string.input)).setIcon(
	        		android.R.drawable.ic_dialog_info).setView(inputServer).setNegativeButton(
	                this.getString(R.string.cancle), null);
	        builder.setPositiveButton(this.getString(R.string.ok),
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
				Utils.Msgbox(this.getActivity(), getString(R.string.zero_failed));
			}
			break;
		case R.id.btn_switch:
			//净重和毛重切换
			img_conn.getDrawable().setLevel(1);
			img_still.getDrawable().setLevel(1);
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

        CountRecord rec = new CountRecord();

        rec.setCount(tv_quantity.getText().toString());
        rec.setUw(tv_uw.getText().toString());
        rec.setTotalWeight(tv_weight.getText().toString());
        if(dao == null) return false;

        return dao.saveOne(rec);
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
		OneCountFragment f = new OneCountFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
	}

	static class MHandler extends Handler {

		WeakReference<OneCountFragment> mActivity;
		

		MHandler(OneCountFragment activity) {
			mActivity = new WeakReference<OneCountFragment>(activity);
			
		}
		public void calc(OneCountFragment theActivity,Scaler d)
		{
			int quantity = 0;
			if(d.isGross())
			{
				//毛重状态下的 物品个数 = (内部重量 )/单位重量
				quantity = (d.getCalcWeight() / theActivity.uw);
			}
			else
			{
				//净重状态下的 物品个数 = (内部重量 - 你发的皮重)/单位重量
				quantity = (d.getCalcWeight() - d.getTare() / theActivity.uw);

			}
			theActivity.tv_quantity.setText("" + quantity);
		}
		@Override
		public void handleMessage(Message msg) {
			OneCountFragment theActivity = mActivity.get();
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
					//theActivity.tv_unit.setText(d.getUnit());
					theActivity.txtTare.setText(Utils.FormatFloatValue(d.getTare(), d.GetDotNum()));
					//theActivity.tv_weight.setText(Utils.FormatFloatValue(d.getDispalyWeight(), d.GetDotNum()));
					theActivity.tv_weight.setText(d.getDispalyWeight());
					calc(theActivity,d);
					break;
				}
				case Global.MSG_BLE_DISCONNECTRESULT:
				{
					String addr =(String)msg.obj;
					Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");

					break;
				}
				case Global.MSG_BLE_FAILERESULT:
				{
					break;
				}
				
				case Global.MSG_SCALER_CONNECT_OK:
				{
				
					if(progressDialog!=null && progressDialog.isShowing())
						progressDialog.dismiss(); //关闭进度条

					break;
				}
				case MSG_TIMEOUT:
				{
				
					if(progressDialog!=null && progressDialog.isShowing())
					{
						progressDialog.dismiss(); //关闭进度条
						theActivity.showFailBox(theActivity.getString(R.string.prompt_conn_timeout));

					}
				}

				case Global.MSG_SCALER_POWER_RESULT:
				{
					int result = msg.arg1;
					theActivity.btn_power.refreshPower((float)result/1000.0f);
					break;
				}
			}
			
		}
	}
	


};