package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;
import java.util.HashMap;

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

import com.blescaler.db.Channel;
import com.blescaler.db.Config;
import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.CalcParamActivity;
import com.blescaler.ui.DBActivity;
import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.R;
import com.blescaler.ui.ScalerParamActivity;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

public class OneWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	
	BatteryState btn_power = null;
	

	private WeightDao wDao;
	private AutoBgButton btn_reconn=null,btn_send=null,btn_search,btn_setting;
	private TextView tvch1,tvch2,tvch3,tvch4,tvch5,tvch6;
	private TextView dev_signal,dev_status;
	private HashMap<Integer,Channel> chans = new HashMap<Integer,Channel>();
	
	private static final int MSG_TIMEOUT = 0x0001;
	private static ProgressDialog progressDialog = null;
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";

	private void updateState()
	{
		
		 if(!WorkService.hasConnectAll())
		   {
			 btn_reconn.setText("点击连接");
			 btn_reconn.setTextColor(Color.rgb(0x80, 0x80, 0x80));
			 
		   }
		   else
		   {
			   //87CEEB
			   btn_reconn.setText("已经连接");
			   btn_reconn.setTextColor(Color.rgb(0xFF, 0x00, 0x00));  
			   
		   }
		
	}
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateState();
				try {
					WorkService.requestChannels("",6);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   mHandler.postDelayed(this, 2000);  
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
	    progressDialog =ProgressDialog.show(ctx, "bleScaler", "connecting scaler");     
	    //new ProgressDialog(ctx);
	    
	    //progressDialog.setButton("取消", new SureButtonListener());
	    //progressDialog.show(ctx, "蓝牙秤hhhh", "正在连接,请稍候！");                                
	    WorkService.connectNext();   
	  
	    
        Message msg = mHandler.obtainMessage(MSG_TIMEOUT);
        
	    mHandler.sendMessageDelayed(msg, 2000);
	}
    

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(watchdog);
		
		
		WorkService.delHandler(mHandler);
		Log.e(TAG, "onStop");
	}
	private void reload()
	{
		for(int i = 1; i <= 6; i++)
		{
			
			chans.put(i,  Config.getInstance(this.getActivity()).getChannel(i));
			
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reload();
		WorkService.addHandler(mHandler);
		mHandler.postDelayed(watchdog, 2000);
		
	
		popConnectProcessBar(this.getActivity());
	}
	private void initUI()
	{
		btn_power=(BatteryState)root.findViewById(R.id.bs_power);
		btn_power.setPowerQuantity(1);
		btn_reconn = (AutoBgButton) root.findViewById(R.id.btn_reconn_device);
	
		tvch1 = (TextView) root.findViewById(R.id.val_chan1);
		tvch2 = (TextView) root.findViewById(R.id.val_chan2);
		tvch3 = (TextView) root.findViewById(R.id.val_chan3);
		tvch4 = (TextView) root.findViewById(R.id.val_chan4);
		tvch5 = (TextView) root.findViewById(R.id.val_chan5);
		tvch6 = (TextView) root.findViewById(R.id.val_chan6);
		
		tvch1.setOnClickListener(this);
		tvch2.setOnClickListener(this);
		tvch3.setOnClickListener(this);
		tvch4.setOnClickListener(this);
		tvch5.setOnClickListener(this);
		tvch6.setOnClickListener(this);
		
		dev_signal= (TextView) root.findViewById(R.id.dev_signal);
		dev_status= (TextView) root.findViewById(R.id.dev_status);
		btn_send= (AutoBgButton) root.findViewById(R.id.btn_send_now);
		btn_reconn.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_search = (AutoBgButton) root.findViewById(R.id.btn_search_me);
		btn_search.setOnClickListener(this);
		btn_setting = (AutoBgButton) root.findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(this);
		reload();
	}
	private void initRes()
	{
		mHandler = new MHandler(this);
		
		wDao = new WeightDao(this.getActivity());

		mHandler.postDelayed(watchdog, 300);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_oneweight, container, false);
		initUI();
		initRes();
	
		return root;
	}

	
	@Override
	public void onClick(View arg0) {
		
		switch(arg0.getId())
		{
		
		case R.id.btn_reconn_device:
			popConnectProcessBar(this.getActivity());
			
			break;
		case R.id.btn_send_now:
			WorkService.requestSendNow();
			break;
		case R.id.btn_setting:
		{
		   Intent intent = new Intent(this.getActivity(), ScalerParamActivity.class);
		   intent.putExtra("address","00");
		   startActivity(intent);
			
			break;
		}
		case R.id.btn_search_me:
		{
			 Intent intent = new Intent(this.getActivity(), DeviceScanActivity.class);
			 startActivity(intent); 
				
			break;
		}
		
		case R.id.val_chan1:
		case R.id.val_chan2:
		case R.id.val_chan3:
		case R.id.val_chan4:
		case R.id.val_chan5:
		case R.id.val_chan6:
			Intent intent = new Intent(this.getActivity(), CalcParamActivity.class);
			if(arg0.getId() == R.id.val_chan1)intent.putExtra("channel",1);
			else if(arg0.getId() == R.id.val_chan2)intent.putExtra("channel",2);
			else if(arg0.getId() == R.id.val_chan3)intent.putExtra("channel",3);
			else if(arg0.getId() == R.id.val_chan4)intent.putExtra("channel",4);
			else if(arg0.getId() == R.id.val_chan5)intent.putExtra("channel",5);
			else if(arg0.getId() == R.id.val_chan6)intent.putExtra("channel",6);
			startActivity(intent); 
				
			break;
		}
		
		
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
	double CalcValue(int index,double P_AD,double T_AD)
	{
		Channel ch = chans.get(index);
		if(ch == null)
		{
			return 0;
		}
		return ch.CalcValue(P_AD, T_AD);
//	    double a = 0.0014051;
//	    double b = 0.0002369;
//	    double c = 0.0000001019;
//	    //const double d = 101.97;
//	    //计算温度
//	    double N2 = Math.log(T_AD);
//	    double T1 =  1/(a+ b*N2 + c*N2*N2*N2)-273.2; //温度值
//	    //计算
//	    double R1 = (P_AD*P_AD) / 1000; //渗透压.
//
//	    double P  = ch.getG() * ( R1 - ch.getR0() ) + ch.getK() * ( T1 - ch.getT0() );
//
//
//	    double H = P * ch.getC() + ch.getDiff();
//	    if(H < 0) return -H;
//	    return H;
	}
	double FilterValue(int index,double P_AD,double T_AD)
	{
		double v = CalcValue(index,P_AD, T_AD);
		
		return v;
	}
	public boolean showChannels(Scaler sp,int channel)
	{
		float zx = sp.all_zx[channel];
		float wd = sp.all_wd[channel];
		String v = String .format("%.2f",CalcValue(channel+1,zx,wd));
		switch(channel)
		{
		case 0:
			tvch1.setText("zx:" + zx + " wd:" + wd + " " + v);
			break;
		case 1:
			tvch2.setText("zx:" + zx + " wd:" + wd+ " " + v);
			break;
		case 2:
			tvch3.setText("zx:" + zx + " wd:" + wd+ " " + v);
			break;
		case 3:
			tvch4.setText("zx:" + zx + " wd:" + wd+ " " + v);
			break;
		case 4:
			tvch5.setText("zx:" + zx + " wd:" + wd+ " " + v);
			break;
		case 5:
			tvch6.setText("zx:" + zx + " wd:" + wd+ " " + v);
			break;
		}
		return true;
		
	}
	public void showSignal(int signal)
	{
		dev_signal.setText("信号强度:"+signal+"%");
	}
	public void showStatus(int status)
	{
		String status_text = "";
		if((status&0x1)==0){
			status_text+="未连接";
		}
		if((status&0x2)==0){
			status_text+="GPRS初始化失败";
		}
		if((status&0x4)==0){
			status_text+="发送失败";
		}
		if((status&0x8)==0){
			status_text+="太阳能电压故障";
		}
		if((status&0x16)==0){
			status_text+="DC电压故障";
		}
		
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

				case Global.MSG_GET_CHANNELS_RESULT:
				{
					
					Scaler d = (Scaler) msg.obj;
					
					theActivity.showChannels(d,msg.arg1);
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
						theActivity.showFailBox("连接超时，点击重新连接！");
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
				case Global.MSG_GET_GPRS_SIGNAL_RESULT:
					theActivity.showSignal(msg.arg1);
					break;
				case Global.MSG_GET_DEV_STATUS_RESULT:
					theActivity.showStatus(msg.arg1);
					break;
			}
			
		}
	}
	


};