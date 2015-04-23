package com.example.bluetooth.le;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

public class WeightActivity extends Activity implements View.OnClickListener {

	private int index = 0;

	private String mDeviceAddress;

	private IBle mBle;
	private BleGattCharacteristic mCharacteristicWgt;
	private TextView txtWgt;
	private Button btnSave;
	private ListView listData;
	private MyAdapter adapter;
	private Timer pTimer;
	protected static final String TAG = "weight";

	private final class ReadWgtTimer extends TimerTask {
		public void run() {
			if(mBle.hasConnected(mDeviceAddress))
			{
				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristicWgt);
			}
			else 
			{
				mBle.requestConnect(mDeviceAddress);
			}
			
		}
	}

	private class WeightData {

		public String sid;
		public String stime;
		public String skg;

		public WeightData(int id, String kg) {
			sid = String.valueOf(id);
			long time = System.currentTimeMillis();

			stime = getCurrentTime(time);
			skg = kg + "kg";
		}

	};

	public static String getCurrentTime(long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(new Date(date));
		return str;
	}

	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		private int weight;
		private boolean mNotifyStarted;

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();

			if (!mDeviceAddress.equals(extras.getString(BleService.EXTRA_ADDR))) {
				return;
			}

			String uuid = extras.getString(BleService.EXTRA_UUID);
			if (uuid != null
					&& !mCharacteristicWgt.getUuid().toString().equals(uuid)) {
				return;
			}

			String action = intent.getAction();
			Log.e(TAG, action);
			if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(WeightActivity.this, "Device disconnected...",
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);

				weight = Utils.bytesToInt(val);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						txtWgt.setText(String.valueOf(weight));
					}
				});

			} else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION
					.equals(action)) {
				Toast.makeText(WeightActivity.this,
						"Notification state changed!", Toast.LENGTH_SHORT)
						.show();
				mNotifyStarted = extras.getBoolean(BleService.EXTRA_VALUE);
				if (mNotifyStarted) {

				} else {

				}
			} else if (BleService.BLE_CHARACTERISTIC_INDICATION.equals(action)) {
				Toast.makeText(WeightActivity.this,
						"Indication state changed!", Toast.LENGTH_SHORT).show();
			} else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
				Toast.makeText(WeightActivity.this, "Write success!",
						Toast.LENGTH_SHORT).show();
			} else if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				Config.getInstance(WeightActivity.this).setDevAddress(mDeviceAddress);
				Toast.makeText(
						WeightActivity.this,
						"Connect ok!" + extras.getString(BleService.EXTRA_ADDR),
						Toast.LENGTH_SHORT).show();
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(
						WeightActivity.this,
						"Disconnect!" + extras.getString(BleService.EXTRA_ADDR),
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				Toast.makeText(
						WeightActivity.this,
						"service discovery!"
								+ extras.getString(BleService.EXTRA_ADDR),
						Toast.LENGTH_SHORT).show();
			} else if (BleService.BLE_REQUEST_FAILED.equals(action)) {
				Toast.makeText(
						WeightActivity.this,
						"request failed"
								+ extras.getString(BleService.EXTRA_ADDR),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight);

		initResource();

		btnSave.setOnClickListener(this);

		mDeviceAddress = getIntent().getStringExtra("address");
		
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle != null) {

		}
		mCharacteristicWgt = mBle.getService(mDeviceAddress,
				UUID.fromString(Utils.UUID_SRV)).getCharacteristic(
				UUID.fromString(Utils.UUID_WGT));
		if (mCharacteristicWgt == null) {
			return;
		}
	
		pTimer = new Timer();
		pTimer.schedule(new ReadWgtTimer(), 0, 1000);

		// mNotifyStarted = true;
		// mBle.requestCharacteristicNotification(mDeviceAddress,
		// mCharacteristic);

	}

	private void initResource() {
		// TODO Auto-generated method stub
		txtWgt = (TextView) findViewById(R.id.txtWgt);
		listData = (ListView) findViewById(R.id.list);

		adapter = new MyAdapter(this);
		listData.setAdapter(adapter);
		btnSave = (Button) findViewById(R.id.btn_save);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.device_settings) {

			Intent intent = new Intent(WeightActivity.this, CalibActivity.class);
			intent.putExtra("address", mDeviceAddress);

			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mBleReceiver, BleService.getIntentFilter());

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop");
		unregisterReceiver(mBleReceiver);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		pTimer.cancel();
		mBle.disconnect(mDeviceAddress);
		Log.e(TAG, "OnDestory");
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		public ArrayList<WeightData> arr;

		public MyAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<WeightData>();

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.listview_item, null);
			}
			final TextView edit = (TextView) view.findViewById(R.id.index);
			edit.setText(arr.get(position).sid);
			final TextView time = (TextView) view.findViewById(R.id.time);
			time.setText(arr.get(position).stime);
			final TextView kg = (TextView) view.findViewById(R.id.kg);
			kg.setText(arr.get(position).skg);

			return view;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_save:
			saveWeight();
			break;

		default:
			break;
		}
		
	}

	private void saveWeight() {
		// TODO Auto-generated method stub
		String kgs = txtWgt.getText().toString();
		WeightData data = new WeightData(index++, kgs);

		adapter.arr.add(data);
		adapter.notifyDataSetChanged();
	}

}
