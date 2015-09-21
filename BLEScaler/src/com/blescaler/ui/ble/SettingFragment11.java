package com.example.bluetooth.le.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.SettingActivity;
import com.example.worker.Global;
import com.example.worker.WorkService;
import com.lvrenyang.utils.DataUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SettingFragment11 extends BaseFragment implements OnClickListener {

	View root;
	private LinearLayout linearlayoutdevices;
	private ProgressBar progressBarSearchStatus;
	private static ProgressDialog dialog;

	private BroadcastReceiver broadcastReceiver = null;
	private IntentFilter intentFilter = null;

	private static Handler mHandler = null;

	int pos;
	SettingActivity theActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_setting11, container, false);

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
		initBroadcast();
		getBoundedPrinters();
		mHandler = new MHandler(theActivity);
		WorkService.addHandler(mHandler);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = this.getArguments();
		pos = bundle.getInt("pos");

		root.findViewById(R.id.buttonSearch).setOnClickListener(this);
		progressBarSearchStatus = (ProgressBar) root.findViewById(R.id.progressBarSearchStatus);
		linearlayoutdevices = (LinearLayout) root.findViewById(R.id.linearlayoutdevices);
		dialog = new ProgressDialog(getActivity());

		

	}

	@Override
	public void onStop() {
		WorkService.delHandler(mHandler);
		mHandler = null;
		uninitBroadcast();
		super.onStop();
	}
	public static SettingFragment11 newFragment(int pos) {
		SettingFragment11 f = new SettingFragment11();
		Bundle bundle = new Bundle();

		bundle.putInt("pos", pos);

		f.setArguments(bundle);
		return f;
	}
	

	@Override
	public void onAttach(Activity activity) {
		theActivity = (SettingActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setTitle("打印机设置");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
	
	Set<BluetoothDevice> pairedDevices;
	private void getBoundedPrinters() {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			return ;
		}
        pairedDevices = mBluetoothAdapter
				.getBondedDevices();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSearch: {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (null == adapter) {
				// finish();
				break;
			}

			if (!adapter.isEnabled()) {
				if (adapter.enable()) {
					while (!adapter.isEnabled())
						;

				} else {
					// finish();
					break;
				}
			}

			adapter.cancelDiscovery();
			linearlayoutdevices.removeAllViews();
			adapter.startDiscovery();
			break;
		}
		}
	}

	//
	private void initBroadcast() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				
				String action = intent.getAction();
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					if (device == null)
						return;
					final String address = device.getAddress();
					String name = device.getName();
					//这个BT是干什么的
					if (name == null)
						name = "BT";
					else if (name.equals(address))
						name = "BT";
					Button button = new Button(context);
					for(BluetoothDevice pairedDev : pairedDevices){
						if(pairedDev.getAddress().equals(address)){
							button.setTextColor(Color.BLUE);
							
						}
					}
					
					button.setText(name + ": " + address);
					button.setGravity(android.view.Gravity.CENTER_VERTICAL | Gravity.LEFT);
					button.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// 只有没有连接且没有在用，这个才能改变状态
							dialog.setMessage(Global.toast_connecting + " " + address);
							dialog.setIndeterminate(true);
							dialog.setCancelable(false);
							dialog.show();
							WorkService.workThread.connectBt(address);
						}
					});
					button.getBackground().setAlpha(100);
					linearlayoutdevices.addView(button);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
					progressBarSearchStatus.setIndeterminate(true);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					progressBarSearchStatus.setIndeterminate(false);
				}

			}

		};
		intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
	}

	private void uninitBroadcast() {
		if (broadcastReceiver != null)
			getActivity().unregisterReceiver(broadcastReceiver);
	}

	static class MHandler extends Handler {

		WeakReference<SettingActivity> mActivity;

		MHandler(SettingActivity activity) {
			mActivity = new WeakReference<SettingActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SettingActivity theActivity = mActivity.get();
			switch (msg.what) {
			/**
			 * DrawerService 的 onStartCommand会发送这个消息
			 */

			case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
				int result = msg.arg1;
				Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
						Toast.LENGTH_SHORT).show();

				dialog.cancel();
				if (1 == result) {
					PrintTest();
				}
				break;
			}

			}
		}

		void PrintTest() {
			String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n";
			byte[] tmp1 = { 0x1b, 0x40, (byte) 0xB2, (byte) 0xE2, (byte) 0xCA, (byte) 0xD4, (byte) 0xD2, (byte) 0xB3,
					0x0A };
			byte[] tmp2 = { 0x1b, 0x21, 0x01 };
			byte[] tmp3 = { 0x0A, 0x0A, 0x0A, 0x0A };
			byte[] buf = DataUtils.byteArraysToBytes(new byte[][] { tmp1, str.getBytes(), tmp2, str.getBytes(), tmp3 });
			if (WorkService.workThread.isConnected()) {
				Bundle data = new Bundle();
				data.putByteArray(Global.BYTESPARA1, buf);
				data.putInt(Global.INTPARA1, 0);
				data.putInt(Global.INTPARA2, buf.length);
				WorkService.workThread.handleCmd(Global.CMD_WRITE, data);
			} else {
				Toast.makeText(mActivity.get(), Global.toast_notconnect, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
