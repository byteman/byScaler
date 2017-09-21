package com.blescaler.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class PairedScalerActivity extends Activity {


	private static Handler mHandler = null;
	private String myact;
	private ListView lv_devs;
	LeDeviceListAdapter mLeDeviceListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_scaler_list);
		myact = getIntent().getStringExtra("act");
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		lv_devs = (ListView) findViewById(R.id.lv_scan);
		findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(arg0.getId() == R.id.btn_return)
				{
					finish();
				}
			}
		});
		lv_devs.setAdapter(mLeDeviceListAdapter);
		lv_devs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) view.getTag();
				String addr = holder.deviceAddress.getText().toString();
				
				if(myact.equals("param"))
				{
					Intent intent = new Intent(PairedScalerActivity.this, ScalerParamActivity.class);
					intent.putExtra("address",addr);
					startActivity(intent);

				}
				else if(myact.equals("calib"))
				{
					Intent intent = new Intent(PairedScalerActivity.this, CalibActivity.class);
					intent.putExtra("address",addr);
					startActivity(intent);
				}
				else if(myact.equals("debug"))
				{
					Intent intent = new Intent(PairedScalerActivity.this, SysParamActivity.class);
					intent.putExtra("address",addr);
					startActivity(intent);
				}
			
				
			}
		});
		//mHandler = new MHandler(this);
		//WorkService.addHandler(mHandler);
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLeDeviceListAdapter.notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
	}
	
	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<Scaler> mLeDevices;
		private LayoutInflater mInflator;
		 // 用来控制CheckBox的选中状况  
	    private HashMap<Integer, Boolean> isSelected;  
		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<Scaler>();
			mInflator = PairedScalerActivity.this.getLayoutInflater();
			initDate();
		}
		 // 初始化isSelected的数据  
	    private void initDate() {  
	       int n = WorkService.getScalerCount();
	       mLeDevices.clear();
	       for(int i = 0; i < n; i++)
	       {
	    	   Scaler s = WorkService.getScaler(i);
	    	   if(s!=null)
	    	   {
	    		   mLeDevices.add(s);
	    	   }
	       }
	    } 
	   
		public void addDevice(Scaler device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public Scaler getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}
	
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				viewHolder.cb = (CheckBox) view
						.findViewById(R.id.device_cbx);
				viewHolder.cb.setVisibility(View.GONE);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			Scaler device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());
		
			
			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		CheckBox cb;
	}
	static class MHandler extends Handler {

		WeakReference<PairedScalerActivity> mActivity;

		MHandler(PairedScalerActivity activity) {
			mActivity = new WeakReference<PairedScalerActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			PairedScalerActivity theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_ALLTHREAD_READY: 
				{
					
					break;
				}

			}
		}
	}
}
