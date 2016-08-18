package com.blescaler.ui.ble;



import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.blescaler.db.WeightDao;
import com.blescaler.ui.R;
import com.blescaler.ui.ble.OneWeightFragment.MHandler;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FlourWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	
	private int cout = 0,timeout=0;
	private boolean pause = false,disconnect=false;
	private int scaler_cout = 0;
	private static Handler mHandler = null;
	protected static final String TAG = "weight_activity";
	private TextView[] tv_weight = {null,null,null,null}; 
	private TextView[] tv_conns = {null,null,null,null}; 
	private TextView[] tv_standstill = {null,null,null,null}; 
	private TextView[] tv_name = {null,null,null,null}; 
	private static Map<String,TextView> scalers = new HashMap<String,TextView>();
	private void connect(int index)
	{
		if(index >= WorkService.getScalerCount()) return;
		String addr = WorkService.getDeviceAddress(this.getActivity(), index);
		//if(false == WorkService.hasConnected(addr))
			WorkService.requestConnect(addr);
	}
	private View.OnClickListener listen1 = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			connect(0);
		}
	};
private View.OnClickListener listen2 = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			connect(1);
		}
	};
private View.OnClickListener listen3 = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			connect(2);
		}
	};
private View.OnClickListener listen4 = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			connect(3);
		}
	};
	private Runnable watchdog = new Runnable()
	{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			   if(timeout++ > 2)
			   {
				   //WorkService.readNextWgt(false);
				   timeout = 0;
			   }
			   if(cout++ > 5)
			   {
				   int count = WorkService.getScalerCount();
				   for(int i = 0; i < count;i++)
				   {
					   Scaler s = WorkService.getScaler(i);
					   if(s == null) tv_conns[i].setText("x");
					   {
					   		tv_conns[i].setText(s.isConnected()?"已连接":"断开");
					   		tv_standstill[i].setText(s.isStandstill()?"稳定":"不稳定");
					   		if(s.isConnected())
					   		{
					   			tv_weight[i].setTextColor(Color.rgb(0xFF, 0x00, 0x00));
					   			
					   		}
					   		else
					   		{
					   			tv_weight[i].setTextColor(Color.rgb(0x80, 0x80, 0x80));
					   		}
					   }
				   }
				   //tv_conns[3].setText(WorkService.getQueSize()+"");
				   cout = 0;
				   
				   
			   }
			  
			   mHandler.postDelayed(this, 200);  
		}
		
	};

	private static String unit="g";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_flourweight, container, false);
		initRes();
		return root;
	}
	@Override
	public void onClick(View v) {
		
	}
	public static Fragment newFragment() {
		FlourWeightFragment f = new FlourWeightFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
		
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
		unit = WorkService.getUnit();
		mHandler.postDelayed(watchdog, 200);
		
	}
	private void initRes()
	{
		tv_weight[0] = (TextView) root.findViewById(R.id.weight1_ll).findViewById(R.id.textView1);
		tv_weight[1] = (TextView) root.findViewById(R.id.weight2_ll).findViewById(R.id.textView1);
		tv_weight[2] = (TextView) root.findViewById(R.id.weight3_ll).findViewById(R.id.textView1);
		tv_weight[3] = (TextView) root.findViewById(R.id.weight4_ll).findViewById(R.id.textView1);
		
		tv_weight[0].setOnClickListener(listen1);
		tv_weight[1].setOnClickListener(listen2);
		tv_weight[2].setOnClickListener(listen3);
		tv_weight[3].setOnClickListener(listen4);
		
		
		tv_conns[0] = (TextView) root.findViewById(R.id.weight1_ll).findViewById(R.id.textView5);
		tv_conns[1] = (TextView) root.findViewById(R.id.weight2_ll).findViewById(R.id.textView5);
		tv_conns[2] = (TextView) root.findViewById(R.id.weight3_ll).findViewById(R.id.textView5);
		tv_conns[3] = (TextView) root.findViewById(R.id.weight4_ll).findViewById(R.id.textView5);
		
		tv_standstill[0] = (TextView) root.findViewById(R.id.weight1_ll).findViewById(R.id.textView6);
		tv_standstill[1] = (TextView) root.findViewById(R.id.weight2_ll).findViewById(R.id.textView6);
		tv_standstill[2] = (TextView) root.findViewById(R.id.weight3_ll).findViewById(R.id.textView6);
		tv_standstill[3] = (TextView) root.findViewById(R.id.weight4_ll).findViewById(R.id.textView6);
		
		tv_name[0] = (TextView) root.findViewById(R.id.weight1_ll).findViewById(R.id.button1);
		tv_name[1] = (TextView) root.findViewById(R.id.weight2_ll).findViewById(R.id.button1);
		tv_name[2] = (TextView) root.findViewById(R.id.weight3_ll).findViewById(R.id.button1);
		tv_name[3] = (TextView) root.findViewById(R.id.weight4_ll).findViewById(R.id.button1);
		
		
		scaler_cout = WorkService.getScalerCount();
		scalers.clear();
		for(int i = 0; i < scaler_cout; i++)
		{
			Scaler s = WorkService.getScaler(i);
			if(s!=null)
			{
				scalers.put(s.getAddress(), tv_weight[i]);
				tv_name[i].setText(s.getAddress());
			}
		}
		
		mHandler = new MHandler(this);

		mHandler.postDelayed(watchdog, 200);
		
	}
	
	static class MHandler extends Handler {

		WeakReference<FlourWeightFragment> mActivity;

		MHandler(FlourWeightFragment activity) {
			mActivity = new WeakReference<FlourWeightFragment>(activity);
			
		}
		
		@Override
		public void handleMessage(Message msg) {
			FlourWeightFragment theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_BLE_WGTRESULT:
				{
					//BluetoothDevice device = (BluetoothDevice) msg.obj;
					
					Scaler d = (Scaler) msg.obj;
					
					TextView v = scalers.get(d.getAddress());
					if(v != null)
					{
						v.setText(d.getWeight()+" " + unit);
					}
					theActivity.timeout = 0;
					
					WorkService.readNextWgt(false);
					break;
				}
				case Global.MSG_BLE_DISCONNECTRESULT:
				{
					String addr =(String)msg.obj;
					//Utils.Msgbox(theActivity.getActivity(), addr + " has disconnect!!");
					break;
				}
		
				case Global.MSG_BLE_FAILERESULT:
				{

					//Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1), Toast.LENGTH_SHORT).show();
					break;
				}
				
			
			}
			
		}
	}
	

}
