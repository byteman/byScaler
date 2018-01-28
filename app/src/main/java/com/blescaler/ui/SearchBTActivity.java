package com.blescaler.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchableInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.blescaler.worker.Global;
import com.blescaler.worker.WorkService;
import com.lvrenyang.utils.DataUtils;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;

public class SearchBTActivity extends Activity implements OnClickListener {

	private SearchBTActivity.BtDeviceListAdapter mLeDeviceListAdapter;

	private ProgressDialog dialog;
	private Handler mHandler2;
	private Button btn_serach;
	private ListView lv_Devices;
	private BroadcastReceiver broadcastReceiver = null;
	private IntentFilter intentFilter = null;

	private static Handler mHandler = null;
	private static String TAG = "SearchBTActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printer_scan);
		lv_Devices =  findViewById(R.id.lv_scan);
		btn_serach =  findViewById(R.id.btn_serach);
		mHandler = new Handler();


		dialog = new ProgressDialog(this);

		initBroadcast();
		// Initializes list view adapter.
		mLeDeviceListAdapter = new SearchBTActivity.BtDeviceListAdapter();
		//lv_Devices.setListAdapter(mLeDeviceListAdapter);
		lv_Devices.setAdapter(mLeDeviceListAdapter);
		lv_Devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				SearchBTActivity.ViewHolder holder = (SearchBTActivity.ViewHolder) view.getTag();
				holder.cb.toggle();
				if(mLeDeviceListAdapter.getIsSelected().size() > position)
					mLeDeviceListAdapter.getIsSelected().put(position, holder.cb.isChecked());

				if (holder.cb.isChecked() == true) {

				} else {

				}
			}
		});

		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);

		mHandler2 = new SearchBTActivity.MHandler(this);
		WorkService.addHandler(mHandler2);
	}
	private void refreshList() {
		mLeDeviceListAdapter.clear();
		mLeDeviceListAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
		uninitBroadcast();
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.buttonSearch: {
			refreshList();
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (null == adapter) {
				finish();
				break;
			}

			if (!adapter.isEnabled()) {
				if (adapter.enable()) {
					while (!adapter.isEnabled())
						;
					Log.v(TAG, "Enable BluetoothAdapter");
				} else {
					finish();
					break;
				}
			}

			adapter.cancelDiscovery();
			adapter.startDiscovery();
			break;
		}
		}
	}

	private void initBroadcast() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					if (device == null)
						return;
					final String address = device.getAddress();
					String name = device.getName();
					if (name == null)
						name = "BT";
					else if (name.equals(address))
						name = "BT";
//					if(device.getType() == DEVICE_TYPE_CLASSIC)
//					{
//
//					}
					Button button = new Button(context);
					button.setText(name + ": " + address);
					button.setGravity(android.view.Gravity.CENTER_VERTICAL
							| Gravity.LEFT);
					button.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							// 只有没有连接且没有在用，这个才能改变状态
//							dialog.setMessage(Global.toast_connecting + " "
//									+ address);
//							dialog.setIndeterminate(true);
//							dialog.setCancelable(false);
//							dialog.show();
							WorkService.workThread.connectBt(address);
						}
					});
					button.getBackground().setAlpha(100);
					mLeDeviceListAdapter.addDevice(device,0);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {

				}

			}

		};
		intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	private void uninitBroadcast() {
		if (broadcastReceiver != null)
			unregisterReceiver(broadcastReceiver);
	}
	private class BtDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private HashMap<String,Integer> mRSSI;
		private LayoutInflater mInflator;
		// 用来控制CheckBox的选中状况
		private HashMap<Integer, Boolean> isSelected;
		public BtDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mRSSI = new HashMap<String,Integer>();
			mInflator = SearchBTActivity.this.getLayoutInflater();
			isSelected = new HashMap<Integer, Boolean>();
			initDate();
			mRSSI.clear();
		}
		// 初始化isSelected的数据
		private void initDate() {
			getIsSelected().clear();
			for (int i = 0; i < mLeDevices.size(); i++) {
				getIsSelected().put(i, false);
			}


		}

		public void addDevice(BluetoothDevice device,int rssi) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);

				initDate();
			}
			if(mRSSI!=null)
			{
				mRSSI.put(device.getAddress(), rssi);
			}

		}
		public List<String> getSelectAddress()
		{


			List<String> devs = new ArrayList<String>();

			for (int i = 0; i < mLeDevices.size(); i++) {
				HashMap<Integer, Boolean> sel = getIsSelected();

				if( sel.get(i))
				{
					devs.add(mLeDevices.get(i).getAddress());
				}
			}
			return devs;
		}
		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
			getIsSelected().clear();
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
		public  HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			DeviceScanActivity.ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new DeviceScanActivity.ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				viewHolder.deviceRssi = (TextView) view
						.findViewById(R.id.device_rssi);

				//viewHolder.cb = (CheckBox) view.findViewById(R.id.device_cbx);

				view.setTag(viewHolder);
			} else {
				viewHolder = (DeviceScanActivity.ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			if(mRSSI!=null && mRSSI.containsKey(device.getAddress()))
			{

				viewHolder.deviceRssi.setText("信号强度:" + mRSSI.get(device.getAddress())+"db");
			}

			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
		CheckBox cb;
	}

	static class MHandler extends Handler {

		WeakReference<SearchBTActivity> mActivity;

		MHandler(SearchBTActivity activity) {
			mActivity = new WeakReference<SearchBTActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SearchBTActivity theActivity = mActivity.get();
			switch (msg.what) {
			/**
			 * DrawerService 的 onStartCommand会发送这个消息
			 */

			case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
				int result = msg.arg1;
				Toast.makeText(
						theActivity,
						(result == 1) ? Global.toast_success
								: Global.toast_fail, Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Connect Result: " + result);
				theActivity.dialog.cancel();
				if (1 == result) {
					PrintTest();
				}
				break;
			}

			}
		}

		void PrintTest() {
			String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n";
			byte[] tmp1 = { 0x1b, 0x40, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA,
					(byte) 0xD4, (byte) 0xD2, (byte) 0xB3, 0x0A };
			byte[] tmp2 = { 0x1b, 0x21, 0x01 };
			byte[] tmp3 = { 0x0A, 0x0A, 0x0A, 0x0A };
			byte[] buf = DataUtils.byteArraysToBytes(new byte[][] { tmp1,
					str.getBytes(), tmp2, str.getBytes(), tmp3 });
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putByteArray(Global.BYTESPARA1, buf);
				data.putInt(Global.INTPARA1, 0);
				data.putInt(Global.INTPARA2, buf.length);
				WorkService.workThread.handleCmd(Global.CMD_WRITE, data);
			} else {
				Toast.makeText(mActivity.get(), Global.toast_notconnect,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
