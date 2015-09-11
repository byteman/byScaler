/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This XPG software is supplied to you by Xtreme Programming Group, Inc.
 * ("XPG") in consideration of your agreement to the following terms, and your
 * use, installation, modification or redistribution of this XPG software
 * constitutes acceptance of these terms.锟�If you do not agree with these terms,
 * please do not use, install, modify or redistribute this XPG software.
 * 
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, XPG grants you a non-exclusive license, under XPG's
 * copyrights in this original XPG software (the "XPG Software"), to use and
 * redistribute the XPG Software, in source and/or binary forms; provided that
 * if you redistribute the XPG Software, with or without modifications, you must
 * retain this notice and the following text and disclaimers in all such
 * redistributions of the XPG Software. Neither the name, trademarks, service
 * marks or logos of XPG Inc. may be used to endorse or promote products derived
 * from the XPG Software without specific prior written permission from XPG.锟�
 * Except as expressly stated in this notice, no other rights or licenses,
 * express or implied, are granted by XPG herein, including but not limited to
 * any patent rights that may be infringed by your derivative works or by other
 * works in which the XPG Software may be incorporated.
 * 
 * The XPG Software is provided by XPG on an "AS IS" basis.锟�XPG MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE XPG SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 * 
 * IN NO EVENT SHALL XPG BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE XPG SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER
 * THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR
 * OTHERWISE, EVEN IF XPG HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ABOUT XPG: Established since June 2005, Xtreme Programming Group, Inc. (XPG)
 * is a digital solutions company based in the United States and China. XPG
 * integrates cutting-edge hardware designs, mobile applications, and cloud
 * computing technologies to bring innovative products to the marketplace. XPG's
 * partners and customers include global leading corporations in semiconductor,
 * home appliances, health/wellness electronics, toys and games, and automotive
 * industries. Visit www.xtremeprog.com for more information.
 * 
 * Copyright (C) 2013 Xtreme Programming Group, Inc. All Rights Reserved.
 */

package com.example.bluetooth.le;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;

import com.example.worker.Global;
import com.example.worker.WorkService;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private Handler mHandler2;
	private String mAddress;
	private static final int REQUEST_ENABLE_BT = 1;
	private ProgressDialog progressDialog = null;
	private String TAG = "DeviceScan";
	private Timer pTimer = null;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

	

	private void onDeviceDisconnected() {
		// TODO Auto-generated method stub

	}

	private void createWeightActivity() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				Intent intent = new Intent(DeviceScanActivity.this,
						WeightActivity.class);
				
				startActivity(intent);
			}
		});
	
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.title_devices);
		mHandler = new Handler();
		
		mHandler2 = new MHandler(this);
		WorkService.addHandler(mHandler2);
		
		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning) {
			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.menu_stop).setVisible(true);
			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.actionbar_indeterminate_progress);
		}
		return true;
	}

	private void refreshList() {
		mLeDeviceListAdapter.clear();
		mLeDeviceListAdapter.notifyDataSetChanged();
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
	}

	@Override
	protected void onResume() {
		super.onResume();
	

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		Log.e(TAG, "OnResume");
		
		
		if (!WorkService.adapterEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		refreshList();
		scanLeDevice(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "onPause");


		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if (device == null)
			return;

		mAddress = device.getAddress();
		WorkService.requestConnect(mAddress);
		progressDialog = ProgressDialog.show(DeviceScanActivity.this, "蓝牙称",
				"蓝牙称正在连接中....！");

		/*
		 * pTimer = new Timer();
		 * 
		 * pTimer.schedule(new TimerTask() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * progressDialog.dismiss(); Toast.makeText(DeviceScanActivity.this,
		 * "蓝牙称连接失败", Toast.LENGTH_SHORT).show(); pTimer.cancel(); } }, 0,5000);
		 */
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					Toast.makeText(DeviceScanActivity.this, "蓝牙称连接失败",
							Toast.LENGTH_SHORT).show();
				}

			}
		}, 5000);
	}

	private void scanLeDevice(final boolean enable) {
		
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					WorkService.stopScan();
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			WorkService.startScan();
		} else {
			mScanning = false;
			WorkService.stopScan();
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
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
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
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
	}
	
	static class MHandler extends Handler {

		WeakReference<DeviceScanActivity> mActivity;

		MHandler(DeviceScanActivity activity) {
			mActivity = new WeakReference<DeviceScanActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			DeviceScanActivity theActivity = mActivity.get();
			switch (msg.what) {

				case Global.MSG_BLE_SCANRESULT: 
				{
					Log.e("scan", "scan");
					BluetoothDevice device = (BluetoothDevice) msg.obj;
					theActivity.mLeDeviceListAdapter.addDevice(device);
					theActivity.mLeDeviceListAdapter.notifyDataSetChanged();
					break;
				}
				case Global.MSG_BLE_SERVICEDISRESULT: 
				{
					theActivity.progressDialog.dismiss();
					
					Intent intent = new Intent(theActivity,
							WeightActivity.class);
					
					theActivity.startActivity(intent);
					break;
				}
			}
			
		}
		}
}