package com.example.bluetooth.le.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SettingActivity;
import com.example.bluetooth.le.WeightActivity;
import com.example.db.BluetoothDeviceWrapper;
import com.example.worker.Global;
import com.example.worker.Scaler;
import com.example.worker.WorkService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SettingFragment12 extends BaseFragment {

	View root;
	ListView device_lv;
	int pos;
	SettingActivity theActivity;
	private Handler mHandler;
	private Handler mHandler2;
	Button buttonSearch,buttonSave;
	
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private ProgressDialog progressDialog = null;

	private boolean mScanning;

	private static final long SCAN_PERIOD = 10000;
	private static final int REQUEST_ENABLE_BT = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_setting12, container, false);

		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}
@Override
public void onResume() {
	super.onResume();
	if (!WorkService.adapterEnabled()) {//蓝牙开启,蓝牙打开
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

	
}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = this.getArguments();
		pos = bundle.getInt("pos");
		device_lv = (ListView) root.findViewById(R.id.device_lv);
		buttonSearch=(Button) root.findViewById(R.id.buttonSearch);
		buttonSave=(Button) root.findViewById(R.id.buttonSave);
		buttonSearch.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshList();
				
				scanLeDevice(true);
				buttonSearch.setEnabled(false);
				
			}
		});
		buttonSave.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int index=0;
				for(int i=0;i<mLeDeviceWrappers.size();i++){
					if(mLeDeviceWrappers.get(i).isChecked){
						BluetoothDevice bluetoothDevice=mLeDeviceWrappers.get(i).bluetoothDevice;
						theActivity.bleApplication.getmWorkService().setDeviceAddress(theActivity, index, bluetoothDevice.getAddress(),bluetoothDevice.getName());
						index++;
					}
				}
				//TODO 启动线程每隔3秒连接一次
				theActivity.bleApplication.getmWorkService().connectAll();
			}
		});
		mHandler = new Handler();

		mHandler2 = new MHandler(theActivity);
		WorkService.addHandler(mHandler2);

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		device_lv.setAdapter(mLeDeviceListAdapter);
		//TODO  暂时不行 获得保存的地址	
//		Iterator iter = theActivity.bleApplication.getmWorkService().scalers.entrySet().iterator();  
//		while (iter.hasNext()) {  
//		    Map.Entry entry = (Map.Entry) iter.next();  
//		    Scaler val = (Scaler) entry.getValue(); 
//		    mLeDeviceListAdapter.addDevice( );
//		}  
		
		device_lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				clickItem(position);

			}
		});
	}

	protected void clickItem(int position) {
		//TODO 判断是否有4个了
		mLeDeviceWrappers.get(position).isChecked=true;
	mLeDeviceListAdapter.notifyDataSetChanged();
//		String mAddress;
//		 BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//		if (device == null)
//			return;
//
//		mAddress = device.getAddress();
//		WorkService.requestConnect(mAddress);
//		progressDialog = ProgressDialog.show(theActivity, "蓝牙称", "蓝牙称正在连接中....！");
//
//		/*
//		 * pTimer = new Timer();
//		 * 
//		 * pTimer.schedule(new TimerTask() {
//		 * 
//		 * @Override public void run() { // TODO Auto-generated method stub
//		 * progressDialog.dismiss(); Toast.makeText(DeviceScanActivity.this,
//		 * "蓝牙称连接失败", Toast.LENGTH_SHORT).show(); pTimer.cancel(); } }, 0,5000);
//		 */
//		mHandler.postDelayed(new Runnable() {//5秒没返回连接失败
//
//			@Override
//			public void run() {
//				if (progressDialog.isShowing()) {
//					progressDialog.dismiss();
//					Toast.makeText(theActivity, "蓝牙称连接失败", Toast.LENGTH_SHORT).show();
//				}
//
//			}
//		}, 5000);

	}

	@Override
	public void onAttach(Activity activity) {
		theActivity = (SettingActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setTitle("蓝牙扫描");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.main, menu);
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_scan:
			refreshList();
			Log.e("DeviceScan", "scan");
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		}
		return true;

		// return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			// finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshList() {
		mLeDeviceListAdapter.clear();
		mLeDeviceListAdapter.notifyDataSetChanged();
	}

	private void scanLeDevice(final boolean enable) {

		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					WorkService.stopScan();
					buttonSearch.setEnabled(true);
					//theActivity.invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);//10秒后停止扫描

			mScanning = true;
			WorkService.startScan();
		} else {
			mScanning = false;
			WorkService.stopScan();
		}

		theActivity.invalidateOptionsMenu();
	}

	public static SettingFragment12 newFragment(int pos) {
		SettingFragment12 f = new SettingFragment12();
		Bundle bundle = new Bundle();

		bundle.putInt("pos", pos);

		f.setArguments(bundle);
		return f;
	}

	class MHandler extends Handler {

		WeakReference<SettingActivity> mActivity;

		MHandler(SettingActivity activity) {
			mActivity = new WeakReference<SettingActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SettingActivity theActivity = mActivity.get();
			switch (msg.what) {
          //TODO requestConnect会调用MSG_BLE_SCANRESULT MSG_BLE_SERVICEDISRESULT
			case Global.MSG_BLE_SCANRESULT: {
				Log.e("SettingFragment12", "发现设备"+msg.obj);
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				mLeDeviceListAdapter.addDevice(device);
				mLeDeviceListAdapter.notifyDataSetChanged();
				break;
			}
			case Global.MSG_BLE_SERVICEDISRESULT: {
				Log.e("1111", "启动WeightActivity"+msg.obj);
				//TODO String address
				progressDialog.dismiss();

				Intent intent = new Intent(theActivity, WeightActivity.class);

				theActivity.startActivity(intent);
				break;
			}
			case Global.MSG_BLE_CONNECTRESULT: {
				Log.e("SettingFragment12", "设备已连接"+msg.obj);
				//TODO 加入本地perfs,isChecked
				progressDialog.dismiss();
				 BluetoothDevice device =(BluetoothDevice) msg.obj;
				 for(int i=0;i<mLeDeviceWrappers.size();i++){
						if(mLeDeviceWrappers.get(i).bluetoothDevice.getAddress().equals(device.getAddress())){
							mLeDeviceWrappers.get(i).isChecked=true;
							mLeDeviceListAdapter.notifyDataSetChanged();
							break;
						}
					}
				
				Intent intent = new Intent(theActivity, WeightActivity.class);

				theActivity.startActivity(intent);
				break;
			}
			}

		}
	}
	private ArrayList<BluetoothDeviceWrapper> mLeDeviceWrappers;
	// Adapter
	private class LeDeviceListAdapter extends BaseAdapter {
	
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDeviceWrappers = new ArrayList<BluetoothDeviceWrapper>();
			mInflator = theActivity.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			Boolean isContain=false; 
			for(int i=0;i<mLeDeviceWrappers.size();i++){
				if(mLeDeviceWrappers.get(i).bluetoothDevice.equals(device)){
					isContain=true;
					break;
				}
			}
			if(!isContain){
				BluetoothDeviceWrapper bluetoothDeviceWrapper=new BluetoothDeviceWrapper();
				bluetoothDeviceWrapper.bluetoothDevice=device;
				
				mLeDeviceWrappers.add(bluetoothDeviceWrapper);
			}
			
			
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDeviceWrappers.get(position).bluetoothDevice;
		}

		public void clear() {
			mLeDeviceWrappers.clear();
		}

		@Override
		public int getCount() {
			return mLeDeviceWrappers.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDeviceWrappers.get(i);
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
				
				viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDeviceWrappers.get(i).bluetoothDevice;
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName+device.getAddress());
			else
				viewHolder.deviceName.setText("未知设备"+device.getAddress());
			
			 
			   if(mLeDeviceWrappers.get(i).isChecked){
				   view.setBackgroundColor(Color.BLUE);
			   }else{
				   view.setBackgroundColor(Color.GRAY);
			   }

			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		
	}
}
