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

import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.R;

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
	TextView tv_weight = null,tv_unit=null;
	
	AutoBgButton btn_ng = null;
	AutoBgButton btn_preset = null;
	private WeightDao wDao;
	
	private int timeout=0;
	private int cont=0;
	private boolean pause = false,disconnect=false;
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";
	private static String unit="g";
	private void updateState()
	{
		unit = WorkService.getUnit();
		 if(!WorkService.hasConnectAll())
		   {
			   tv_weight.setTextColor(Color.rgb(0x80, 0x80, 0x80));
		   }
		   else
		   {
			   //87CEEB
			   tv_weight.setTextColor(Color.rgb(0xFF, 0x00, 0x00));
			   
			   
		   }
		   if(WorkService.isNetState()) btn_ng.setText("Net");
		   else btn_ng.setText("Gross");
	}
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 
			
			   if(cont++ >= 5)
			   {
				   updateState();
				   WorkService.readNextWgt(true);
				   cont = 0;
			   }
			  
			   mHandler.postDelayed(this, 200);  
		}
		
	};
	 private class SureButtonListener implements android.content.DialogInterface.OnClickListener{  
		  
	        public void onClick(DialogInterface dialog, int which) {  
	            //点击“确定按钮”取消对话框  
	            dialog.cancel();  
	        }  
	          
	    }  
	private void popConnectProcessBar(Context ctx)
	{
		if(WorkService.getScalerCount() == 0)
		{
			showFailBox("没有选择要连接的蓝牙秤，请先扫描！");
			return;
		}
		if(WorkService.hasConnectAll()) return;
		if(progressDialog!=null && progressDialog.isShowing())
		{
			return;
		}
	    progressDialog =ProgressDialog.show(ctx, "蓝牙秤", "正在连接,请稍候！");     
	    //new ProgressDialog(ctx);
	    
	    //progressDialog.setButton("取消", new SureButtonListener());
	    //progressDialog.show(ctx, "蓝牙秤hhhh", "正在连接,请稍候！");                                
	    WorkService.connectNext();   
	  
        
        Message msg = mHandler.obtainMessage(MSG_TIMEOUT);
        
	    mHandler.sendMessageDelayed(msg, 15000);
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
		pause = false;
		if(!WorkService.hasConnectPrinter())
		{
			//WorkService.connectPrinter(null);
		}
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
		btn_save.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		btn_tare.setOnClickListener(this);
		tv_weight.setOnClickListener(this);
		btn_zero.setOnClickListener(this);
		btn_swtich.setOnClickListener(this);
		btn_preset.setOnClickListener(this);
		unit = WorkService.getUnit();
		tv_unit.setText(unit);
	}
	private void initRes()
	{
		mHandler = new MHandler(this);
		
		wDao = new WeightDao(this.getActivity());
		
		
		mHandler.postDelayed(watchdog, 200);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_oneweight, container, false);
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
	                        if(WorkService.setPreTare(Integer.parseInt(inputValue)))
	                        {
	                        	btn_ng.setText("Net");
	                        }
	                    }
	                });
	        builder.show();
	    }
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
		case R.id.btn_save:
			saveWeight();
			Utils.Msgbox(this.getActivity(), "保存成功");
			break;
		case R.id.btn_print:
			printWeight();
			break;
		case R.id.btn_tare:
			if(WorkService.discardTare())
			{
				btn_ng.setText("Net");
			}
			break;
		case R.id.tv_weight:
			popConnectProcessBar(this.getActivity());
			
			break;
		case R.id.btn_zero:
			//清零
			if(!WorkService.setZero())
			{
				Utils.Msgbox(this.getActivity(), "清零失败，净重状态不允许清零");
			}
			break;
		case R.id.btn_switch:
			//净重和毛重切换
			boolean is_net = WorkService.switchNetGross();
			if(is_net)
			{
				btn_ng.setText("Net");
				
			}
			else
			{
				btn_ng.setText("Gross");
			}
			break;
		case R.id.btn_preset:
			inputTitleDialog();
			break;
		
		}
		
	}

	private void saveWeight() {
		// TODO Auto-generated method stub
		String kgs = tv_weight.getText().toString();
		
		WeightRecord item = new WeightRecord(); //这里会自动读取重量并填充.
	
		wDao.saveWeight(item);

		item = null;
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
	private boolean printWeight()
	{
		if(!WorkService.hasConnectPrinter())
		{
			WorkService.connectPrinter(null);
			Toast.makeText(this.getActivity(), "正在连接打印机，请等待", Toast.LENGTH_SHORT).show();
			
			return false;		
		}
		WeightRecord data = new WeightRecord();
		if(wDao != null)
			if(wDao.getWeightRecord(data))
				WorkService.requestPrint(data);
		
		return true;
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
					//BluetoothDevice device = (BluetoothDevice) msg.obj;
					//int weight = msg.arg1;
					Scaler d = (Scaler) msg.obj;
					int totalweight = WorkService.getNetWeight();
					theActivity.tv_weight.setText(String.valueOf(totalweight));
					theActivity.timeout = 0;
					if(d!=null)d.dump_info();
					WorkService.readNextWgt(true);
					break;
				}
				case Global.MSG_BLE_DISCONNECTRESULT:
				{
					String addr =(String)msg.obj;
					//Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");
					
					//theActivity.tv_conn.setText("已断开");
					//WorkService.connectAll();
					//mHandler.postDelayed(r, delayMillis)
					break;
				}
				case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT:
				{
					int result = msg.arg1;
				
					if(result == 1)
					{
						String addr = (String)(msg.obj);
						WorkService.setPrinterAddress(theActivity.getActivity(),addr);
						theActivity.printWeight();
					}
					else
					{
						//Utils.Msgbox(theActivity.getActivity(), "连接打印机失败");
					}
					
					break;
					
				}
				case Global.MSG_BLE_FAILERESULT:
				{

					//Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1) +"  " + WorkService.getFailType(msg.arg2), Toast.LENGTH_SHORT).show();
					break;
				}
				
				case Global.MSG_SCALER_CONNECT_OK:
				{
					if(WorkService.hasConnectAll())
					{
						if(progressDialog!=null && progressDialog.isShowing())
							progressDialog.dismiss(); //关闭进度条
						//Toast.makeText(theActivity.getActivity(),"all connect",Toast.LENGTH_SHORT).show();
					}
					else {
						
						WorkService.connectNext(); 
					}
						
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
			}
			
		}
	}
	


};