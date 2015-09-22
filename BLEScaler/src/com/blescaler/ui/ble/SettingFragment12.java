package com.blescaler.ui.ble;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.blescaler.ui.R;
import com.blescaler.worker.WorkService;



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
	
	private Handler mHandler;
	private Handler mHandler2;
	Button buttonSearch,buttonSave;
	

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
			
				buttonSearch.setEnabled(false);
				
			}
		});
		buttonSave.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
		mHandler = new Handler();


		
		device_lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				clickItem(position);

			}
		});
	}

	protected void clickItem(int position) {


	}

	@Override
	public void onAttach(Activity activity) {
	
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
		
			break;
		case R.id.menu_stop:
	
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

	

	public static SettingFragment12 newFragment(int pos) {
		SettingFragment12 f = new SettingFragment12();
		Bundle bundle = new Bundle();

		bundle.putInt("pos", pos);

		f.setArguments(bundle);
		return f;
	}

	
	
}
