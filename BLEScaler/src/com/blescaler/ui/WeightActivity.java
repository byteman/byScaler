package com.blescaler.ui;

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
import android.view.KeyEvent;
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
import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.ConnectBTPairedActivity;
import com.blescaler.ui.SearchBTActivity;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;
import com.blescaler.ui.R;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.xtremeprog.sdk.ble.BleRequest.RequestType;

public class WeightActivity extends Activity implements View.OnClickListener {


	private TextView txtWgt;
	private Button btnSave;
	
	private WeightDao wDao;
	private Timer pTimer;
	private boolean pasue = false;
	private static Handler mHandler = null;
	
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
	
		wDao = new WeightDao(this);
		pTimer = new Timer();
		pTimer.schedule(new ReadWgtTimer(), 0, 100);

	}

	private void initResource() {
		// TODO Auto-generated method stub
		txtWgt = (TextView) findViewById(R.id.txtWgt);
		

	
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
			Intent intent = new Intent(this, DeviceScanActivity.class);
			intent.putExtra("address", "C4:BE:84:22:91:E2");
			startActivity(intent);
			//WorkService.requestReadPar("C4:BE:84:22:91:E2");
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
			intent.putExtra("address", "C4:BE:84:22:91:E2");
			startActivity(intent);
		}
		else if(id == R.id.menu_data) //过磅数据管理.
		{
			Intent intent = new Intent(this, DBActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_save:
			saveWeight();

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
		
		WeightRecord item = new WeightRecord(); //这里会自动读取重量并填充.
	
		wDao.saveWeight(item);
		
		//adapter.arr.add(item);
		//adapter.notifyDataSetChanged();
		item = null;
	}
	private long exitTime = 0;

	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK
	                && event.getAction() == KeyEvent.ACTION_DOWN) {
	            if ((System.currentTimeMillis() - exitTime) > 2000) {
	                Toast.makeText(getApplicationContext(), "再按一次退出程序",
	                        Toast.LENGTH_SHORT).show();
	                exitTime = System.currentTimeMillis();
	            } else {
	            	WorkService.requestDisConnectAll();
	                moveTaskToBack(false);
	                finish();

	            }
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
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

						Toast.makeText(theActivity, WorkService.getFailReason(msg.arg1), Toast.LENGTH_SHORT).show();
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
