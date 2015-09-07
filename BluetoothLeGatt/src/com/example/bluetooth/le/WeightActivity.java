package com.example.bluetooth.le;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.R.integer;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.StringSplitter;
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

import com.aiven.alert.util.MineAlert;
import com.example.worker.Global;
import com.example.worker.Scaler;
import com.example.worker.ScalerParam;
import com.example.worker.WorkService;
import com.example.bluetooth.le.ConnectBTPairedActivity;
import com.example.bluetooth.le.SearchBTActivity;
import com.example.db.WeightDao;
import com.example.db.WeightRecord;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

public class WeightActivity extends Activity implements View.OnClickListener {

	private int index = 0;

	private BleGattCharacteristic mCharacteristicWgt;
	private TextView txtWgt;
	private Button btnSave;
	private ListView listData;
	private MyAdapter adapter;
	private Timer pTimer;
	private boolean pasue = false;
	private static final int REQUEST_ENABLE_BT = 1;
	private static Handler mHandler = null;
	private WeightDao wDao;
	protected static final String TAG = "weight";

	private final class ReadWgtTimer extends TimerTask {
		
		
		public void run() {
			if(pasue)
			{
				return;
			}
			if(WorkService.hasConnectAll())
			{
				WorkService.readAllWgt();
			}
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight);

		initResource();

		btnSave.setOnClickListener(this);


		mHandler = new MHandler(this);
		//WorkService.addHandler(mHandler);
		
	
		wDao = new WeightDao(this);
		loadDBData();
		pTimer = new Timer();
		//pTimer.schedule(new ReadWgtTimer(), 0, 100);

	}

	private void initResource() {
		// TODO Auto-generated method stub
		txtWgt = (TextView) findViewById(R.id.txtWgt);
		listData = (ListView) findViewById(R.id.list);

		adapter = new MyAdapter(this);
		listData.setAdapter(adapter);
		btnSave = (Button) findViewById(R.id.btn_save);
		findViewById(R.id.btn_con_all2).setOnClickListener(this);
		findViewById(R.id.btn_print).setOnClickListener(this);
		
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

			WorkService.requestReadPar("C4:BE:84:22:91:E2");
			return true;
		}
		else if(id == R.id.connect_printer)
		{
	
			startActivity(new Intent(this, SearchBTActivity.class));
		}
		else if(id == R.id.connect_paired)
		{
			startActivity(new Intent(this, ConnectBTPairedActivity.class));
		}
		else if(id == R.id.calib)
		{
			Intent intent = new Intent(this, CalibActivity.class);
			intent.putExtra("address", "C4:BE:84:22:8F:B0");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	private void loadDBData()
	{
		List<WeightRecord> items = new ArrayList<WeightRecord>();
		
		items = wDao.getWeightList();
		
		for(WeightRecord item : items)
		{
			adapter.arr.add(item);		
		}
		adapter.notifyDataSetChanged();
	}
/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/
	@Override
	protected void onResume() {
		super.onResume();
		
	/*	if (!WorkService.adapterEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}*/
		WorkService.addHandler(mHandler);
		
		//WorkService.connectPrinter(null);
		if(!WorkService.hasConnectAll())
		{
			WorkService.connectAll();
		}
		pasue = false;
		
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		pasue = true;
		
		WorkService.delHandler(mHandler);
		Log.e(TAG, "onStop");
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//if(pTimer != null)
		//	pTimer.cancel();
		//WorkService.requestDisConnectAll();
		//WorkService.delHandler(mHandler);
		//mHandler = null;
		//wDao = null;
		System.exit(0);
		Log.e(TAG, "OnDestory");
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		public ArrayList<WeightRecord> arr;

		public MyAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
			arr = new ArrayList<WeightRecord>();

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
			//String index = String.valueOf(arr.size()+1);
			edit.setText(arr.get(position).getID());
			final TextView time = (TextView) view.findViewById(R.id.time);
			String timeString = Utils.getNormalTime(arr.get(position).getTime());
			time.setText(timeString);
			final TextView kg = (TextView) view.findViewById(R.id.kg);
			kg.setText(arr.get(position).getGross());

			return view;
		}
	}
	private void testWriteParam()
	{
		//int nov, byte mtd, byte zt,byte pzt,byte dig, byte res, String unit
		
		ScalerParam s = new ScalerParam(5000,(byte)2,(byte)3,(byte)1,(byte)2,(byte)2,"t");
	
		
		WorkService.requestWriteParamValue("C4:BE:84:22:91:E2",s);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_save:
			saveWeight();
			testWriteParam();
			
			break;
	
		case R.id.btn_print:
			//startActivity(new Intent(this, FormActivity.class));
			if(!WorkService.hasConnectPrinter())
			{
				Toast.makeText(this, "请先连接打印机", Toast.LENGTH_SHORT).show();
				
				break;		
			}
			WeightRecord data = new WeightRecord();
			if(wDao != null)
				if(wDao.getWeightRecord(data))
					WorkService.requestPrint(data);
			data = null;
			break;
		case R.id.btn_con_all2:
			//WorkService.connectPrinter(null);
			WorkService.connectAll();
			break;
		default:
			break;
		}
		
	}

	private void saveWeight() {
		// TODO Auto-generated method stub
		String kgs = txtWgt.getText().toString();
		
		WeightRecord item = new WeightRecord();
		item.setGross(kgs);
		item.setTare("0");
		item.setNet(kgs);
		item.setID(String.valueOf(adapter.getCount()+1));
		
		wDao.saveWeight(item);
		
		adapter.arr.add(item);
		adapter.notifyDataSetChanged();
	}

	static class MHandler extends Handler {

			WeakReference<WeightActivity> mActivity;
	
			MHandler(WeightActivity activity) {
				mActivity = new WeakReference<WeightActivity>(activity);
				
			}
	
			@Override
			public void handleMessage(Message msg) {
				WeightActivity theActivity = mActivity.get();
				switch (msg.what) {
	
					case Global.MSG_BLE_WGTRESULT:
					{
						//BluetoothDevice device = (BluetoothDevice) msg.obj;
						int weight = msg.arg1;
						theActivity.txtWgt.setText(String.valueOf(weight));
						break;
					}
					case Global.MSG_BLE_DISCONNECTRESULT:
					{
						String addr =(String)msg.obj;
						Toast.makeText(theActivity, addr + " has disconnect!!", Toast.LENGTH_SHORT).show();
						WorkService.connectAll();
						//mHandler.postDelayed(r, delayMillis)
						break;
					}
					case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT:
					{
						int result = msg.arg1;
						Toast.makeText(
								theActivity,
								(result == 1) ? Global.toast_success
										: Global.toast_fail, Toast.LENGTH_SHORT).show();
						Log.v(TAG, "Connect Result: " + result);
						
						String addr = (String)(msg.obj);
						WorkService.setPrinterAddress(theActivity,addr);
						break;
						
					}
					case Global.MSG_BLE_FAILERESULT:
					{
						Toast.makeText(theActivity, "failed", Toast.LENGTH_SHORT).show();
						break;
					}
					case Global.MSG_SCALER_PAR_GET_RESULT:
					{
						Scaler s = (Scaler)msg.obj;
						if(s!=null)
						{
							MineAlert diag = new MineAlert(theActivity);
							DialogInterface.OnClickListener lister = new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									
								}

								
								
							};
							
							diag.createAlert(s.para.toString(), lister, lister);
							diag.show();
							
						}
						
						break;
					}
					case Global.MSG_SCALER_PAR_SET_RESULT:
					{
						Toast.makeText(theActivity, "write param ok", Toast.LENGTH_SHORT).show();
						break;
					}
				}
				
			}
		}
}
