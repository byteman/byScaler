package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.R;
import com.blescaler.utils.Config;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class OneWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	AutoBgButton btn_save = null;
	AutoBgButton btn_print = null;
	AutoBgButton btn_tare = null;
	AutoBgButton btn_zero = null;
	AutoBgButton btn_swtich = null;
	AutoBgButton btn_red_on = null;
	AutoBgButton btn_yellow_on = null;
	AutoBgButton btn_green_on = null;
	AutoBgButton btn_red_off = null;
	AutoBgButton btn_yellow_off = null;
	AutoBgButton btn_green_off = null;
	AutoBgButton btn_is_zero = null;
	AutoBgButton btn_sleep,btn_wakeup,btn_unit,btn_still = null;
	BatteryState btn_power = null;
	TextView tv_weight = null,tv_unit=null;
	
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
	
	public boolean connectAll()
	{
		boolean need_connect = false;
	
		return !need_connect;
	}
	private void updateState()
	{
		 
		   if(!WorkService.hasConnected(address))
		   {
			   tv_weight.setTextColor(Color.rgb(0x80, 0x80, 0x80));
		   }
		   else
		   {
			   //87CEEB
			   tv_weight.setTextColor(Color.rgb(0xFF, 0x00, 0x00));  
		   }
		
	}
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 
			
			   if(cont++ >= 1)
			   {
				   
				   WorkService.requestReadWgt(address);
				   cont = 0;
			   }
			   if(cout_2s++ > 10)
			   {
				   updateState();
				   WorkService.readPower(address);
				   cout_2s = 0;
			   }
			   if(cout_3s > 0)
			   {
				   cout_3s--;
			   }
			   mHandler.postDelayed(this, 200);  
		}
		
	};

	private void popConnectProcessBar(Context ctx)
	{
		address = WorkService.getDeviceAddress(this.getActivity(), 0);
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
    

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(watchdog);
		
		WorkService.delHandler(mHandler);
		Log.e(TAG, "onStop");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WorkService.addHandler(mHandler);
		mHandler.postDelayed(watchdog, 200);
		//WorkService.connectPrinter(null);
		
		updateState();
		tv_unit.setText(unit);
		
		
		popConnectProcessBar(this.getActivity());
	}
	private void initUI()
	{
		btn_save  = (AutoBgButton) root.findViewById(R.id.btn_save);
		btn_print = (AutoBgButton) root.findViewById(R.id.btn_print);
		btn_tare  = (AutoBgButton) root.findViewById(R.id.btn_tare);
		btn_swtich = (AutoBgButton) root.findViewById(R.id.btn_switch);
		tv_weight = (TextView) root.findViewById(R.id.tv_weight);
		btn_zero = (AutoBgButton) root.findViewById(R.id.btn_zero);
		btn_ng = (AutoBgButton) root.findViewById(R.id.btn_ng);
		btn_preset = (AutoBgButton) root.findViewById(R.id.btn_preset);
		tv_unit = (TextView) root.findViewById(R.id.textView2);
		btn_power=(BatteryState)root.findViewById(R.id.bs_power);
		btn_red_on = (AutoBgButton) root.findViewById(R.id.btn_red_light_on);
		btn_yellow_on = (AutoBgButton) root.findViewById(R.id.btn_yellow_light_on);
		btn_green_on = (AutoBgButton) root.findViewById(R.id.btn_green_light_on);
		btn_red_off = (AutoBgButton) root.findViewById(R.id.btn_red_light_off);
		btn_yellow_off = (AutoBgButton) root.findViewById(R.id.btn_yellow_light_off);
		btn_green_off = (AutoBgButton) root.findViewById(R.id.btn_green_light_off);
		btn_is_zero = (AutoBgButton) root.findViewById(R.id.btn_zero1);
		btn_sleep = (AutoBgButton) root.findViewById(R.id.btn_sleep);
		btn_wakeup = (AutoBgButton) root.findViewById(R.id.btn_wake);
		btn_unit = (AutoBgButton) root.findViewById(R.id.btn_unit);
		btn_still = (AutoBgButton) root.findViewById(R.id.btn_still);
		btn_sleep.setOnClickListener(this);
		btn_wakeup.setOnClickListener(this);
		btn_unit.setOnClickListener(this);
		
		btn_power.setPowerQuantity(1);
		btn_save.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		btn_tare.setOnClickListener(this);
		tv_weight.setOnClickListener(this);
		btn_zero.setOnClickListener(this);
		btn_swtich.setOnClickListener(this);
		btn_preset.setOnClickListener(this);
		//btn_still.setOnClickListener(this);
		
//		btn_green_on.setOnClickListener(this);
//		btn_yellow_on.setOnClickListener(this);
//		btn_red_on.setOnClickListener(this);
//		btn_green_off.setOnClickListener(this);
//		btn_yellow_off.setOnClickListener(this);
//		btn_red_off.setOnClickListener(this);
		
	
		
	}
	private void initRes()
	{
		mHandler = new MHandler(this);
		
		
		mHandler.postDelayed(watchdog, 200);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_oneweight_table, container, false);
		
		initUI();
		initRes();
	
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
	                        if(WorkService.setPreTare(address,Integer.parseInt(inputValue)))
	                        {
	                        	
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
			saveWeight();
			Utils.Msgbox(this.getActivity(), "保存成功");
			break;
		case R.id.btn_print:
			WorkService.common_msg(address,Global.REG_OPERATION,99);
			break;
		case R.id.btn_tare:
			
			WorkService.common_msg(address,Global.REG_OPERATION,2);
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
			
//			new Thread(new Runnable(){
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					WorkService.common_msg(address,Global.REG_OPERATION,2);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if(!WorkService.setZero(address))
//					{
//						//Utils.Msgbox(this.getActivity(), "清零失败，净重状态不允许清零");
//					}
//					
//				}
//				
//			}).start();
			
			break;
		case R.id.tv_weight:
			popConnectProcessBar(this.getActivity());
			
			break;
		case R.id.btn_zero:
			//清零
			if(!WorkService.setZero(address))
			{
				Utils.Msgbox(this.getActivity(), "清零失败，净重状态不允许清零");
			}
			break;
		case R.id.btn_switch:
			//净重和毛重切换
			WorkService.common_msg(address,Global.REG_OPERATION,5);
			break;
//		case R.id.btn_green_light_on:
//			WorkService.CtrlLight(address,3);
//			break;
//		case R.id.btn_yellow_light_on:
//			WorkService.CtrlLight(address,5);
//			break;
//		case R.id.btn_red_light_on:
//			WorkService.CtrlLight(address,1);
//			break;
//		case R.id.btn_green_light_off:
//			WorkService.CtrlLight(address,4);
//			break;
//		case R.id.btn_yellow_light_off:
//			WorkService.CtrlLight(address,6);
//			break;
//		case R.id.btn_red_light_off:
//			WorkService.CtrlLight(address,2);
//			break;
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

	private void saveWeight() {
		// TODO Auto-generated method stub
		String kgs = tv_weight.getText().toString();
		
	}
	
	private void showFailBox(String msg)
	{
		 new AlertDialog.Builder(this.getActivity()).setTitle("prompt")//设置对话框标题  
		  
	     .setMessage(msg)//设置显示的内容  
	  
	     .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮  
	  
	          
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
	  
	             // TODO Auto-generated method stub  
	  
	            dialog.dismiss();
	  
	         }  
	  
	     }).show();//在按键响应事件中显示此对话框  
	  
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

				case Global.MSG_BLE_WGTRESULT:
				{
					
					Scaler d = (Scaler) msg.obj;
					int totalweight = d.getWeight();
					float wf = totalweight;
					String weight = "";
					
					
					if(d!=null)d.dump_info();
					if(theActivity.cout_3s > 0)
					{
						return;
					}
					WorkService.requestReadWgt(address);
					
					int dot = d.GetDotNum();
					
					switch(dot)
				    {
				        case 1:
				        	weight = String.format("%.1f",wf/10).toString();
				            break;
				        case 2:
				        	weight = String.format("%.2f ",wf/100).toString();
				            break;
				        case 3:
				        	weight = String.format("%.3f",wf/1000).toString();
				            break;
				        case 4:
				        	weight = String.format("%.4f",wf/10000).toString();
				            break;
				        default:
				        	weight = String.format("%d",totalweight);
				            break;
				    }
					theActivity.tv_weight.setText(weight);
					if(d.isZero())
					{
						theActivity.btn_is_zero.setText(">0<");
					}else
					{
						theActivity.btn_is_zero.setText("");
					}
					if(d.isStandstill())
					{
						theActivity.btn_still.setText("--");
					}else
					{
						theActivity.btn_still.setText("~~");
					}
					if(d.isGross())
					{
						theActivity.btn_ng.setText("Net");
					}else
					{
						theActivity.btn_ng.setText("Gross");
					}
					
					theActivity.tv_unit.setText(d.getUnit());
					
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
						theActivity.showFailBox("连接超时，点击重量显示可重新连接！");
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
					int result = msg.arg1;
					theActivity.btn_power.refreshPower((float)result/1000.0f);
					break;
				}
			}
			
		}
	}
	


};