package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Fragment;
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
		   if(WorkService.isNetState()) btn_ng.setText("净重");
		   else btn_ng.setText("毛重");
	}
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 
			   if(!pause)
			   {
				   
					//WorkService.readAllWgt();
			   }
			   else
			   {
				   if(disconnect)
				   {
					   
					
						
					   
				   }
			   }
			 
			   if(timeout++ > 2)
			   {
				   WorkService.readNextWgt(true);
				   timeout = 0;
			   }
			   if(cont++ >= 5)
			   {
				   updateState();
				   cont = 0;
			   }
			  
			   mHandler.postDelayed(this, 200);  
		}
		
	};
	
	
	private void ScreenDetect()
	{
		int t = this.getResources().getConfiguration().orientation ;
         
        if(t == Configuration.ORIENTATION_LANDSCAPE){
            
        } else if(t ==Configuration.ORIENTATION_PORTRAIT){
           
        }
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
		if(!WorkService.hasConnectAll())
		{
			//WorkService.connectAll();
		}
		updateState();
		tv_unit.setText(unit);
		pause = false;
		if(!WorkService.hasConnectPrinter())
		{
			WorkService.connectPrinter(null);
		}
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
	                        	btn_ng.setText("净重");
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
				btn_ng.setText("净重");
			}
			break;
		case R.id.tv_weight:
			WorkService.connectAll();
			
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
				btn_ng.setText("净重");
				
			}
			else
			{
				btn_ng.setText("毛重");
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
					Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");
					
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
						Utils.Msgbox(theActivity.getActivity(), "连接打印机失败");
					}
					
					break;
					
				}
				case Global.MSG_BLE_FAILERESULT:
				{

					Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1), Toast.LENGTH_SHORT).show();
					break;
				}
				
				case Global.MSG_BLE_SERVICEDISRESULT:
				{
					if(WorkService.hasConnectAll())
					{
						//theActivity.tv_conn.setText("已连接");
					}
						
					break;
				}
			}
			
		}
	}
	


};