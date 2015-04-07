package com.example.bluetooth.le;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;  
import android.os.Message;

import java.lang.reflect.Array;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.IBle;
import com.xtremeprog.sdk.ble.BleService;

public class WeightActivity extends Activity {
	class WeightData
	{
		
		public String sid;
		public String stime;
		public String skg;
		public WeightData(int id, String kg)
		{
			sid = String.valueOf(id);
			//Date date = new Date();
		//   System.out.println("日期转字符串：" + HelloTest.DateToStr(date));
			long time =System.currentTimeMillis();
			
			stime = getCurrentTime(time);
			skg = kg+"kg";
		}
		
	};
	//final int weight = 0;
	private int index = 0;
	private int weight = 0;
	private String mDeviceAddress;
	private IBle mBle;
	private boolean mNotifyStarted;
	private BleGattCharacteristic mCharacteristic;
	private TextView txtWgt ;
	private ListView listData;
	private Button button,add,btnZero; 
    private TextView text; 
    private ListView listview; 
    
    public MyAdapter adapter; 
    public static String getCurrentTime(long date) 
    {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String str = format.format(new Date(date));
    	return str;
    }
    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
       
         
        intValue =  ((b[3] & 0xFF) << 24) + ((b[2] & 0xFF) << 16) + ((b[1] & 0xFF) << 8) + ((b[3] & 0xFF)); 
        return intValue;
       
    }
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			if (!mDeviceAddress.equals(extras.getString(BleService.EXTRA_ADDR))) {
				return;
			}

			String uuid = extras.getString(BleService.EXTRA_UUID);
			if (uuid != null
					&& !mCharacteristic.getUuid().toString().equals(uuid)) {
				return;
			}

			String action = intent.getAction();
			if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(WeightActivity.this,
						"Device disconnected...", Toast.LENGTH_SHORT).show();
				finish();
			} else if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);
				//weight = extras.getInt(BleService.EXTRA_VALUE);
				//recv data	
				
				weight = byte2Int(val);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					// refresh ui 的操作代码
						
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
				
				Toast.makeText(WeightActivity.this,
						"Connect ok!" + extras.getString(BleService.EXTRA_ADDR), Toast.LENGTH_SHORT).show();
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				
			} else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				Toast.makeText(WeightActivity.this,
						"service discovery!" + extras.getString(BleService.EXTRA_ADDR), Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight);
		txtWgt = (TextView)findViewById(R.id.txtWgt);
		listData = (ListView) findViewById(R.id.list); 
		
		adapter = new MyAdapter(this); 
		listData.setAdapter(adapter); 
		
		btnZero = (Button)findViewById(R.id.btn_zero);
		final Button btnSave = (Button)findViewById(R.id.btn_save);
			
		btnSave.setOnClickListener(new OnClickListener() { 
             @Override 
             public void onClick(View arg0) { 
                 // TODO Auto-generated method stub 
            	 String kgs = txtWgt.getText().toString();
            	 WeightData data = new WeightData(index++,kgs);
            	 
                 adapter.arr.add(data); 
                 adapter.notifyDataSetChanged(); 
             } 
         }); 

		

		mDeviceAddress = getIntent().getStringExtra("address");
		String service = getIntent().getStringExtra("service");
		String characteristic = getIntent().getStringExtra("characteristic");
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		mCharacteristic = mBle.getService(mDeviceAddress,
				UUID.fromString(service)).getCharacteristic(
				UUID.fromString(characteristic));
		
		new Timer().schedule(new TimerTask()
		{
			public void run()
			{
				
				mBle.requestReadCharacteristic(mDeviceAddress, mCharacteristic);
			}
		},0,100);
		
		//mNotifyStarted = true;
		//mBle.requestCharacteristicNotification(mDeviceAddress,
		//		mCharacteristic);

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
		if (id == R.id.action_settings) {
			if (mNotifyStarted) {
				mBle.requestStopNotification(mDeviceAddress,
						mCharacteristic);
			} else {
				mBle.requestCharacteristicNotification(mDeviceAddress,
						mCharacteristic);
			}
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
	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
		unregisterReceiver(mBleReceiver);
	}
	
	private class MyAdapter extends BaseAdapter { 
	    
        private Context context; 
        private LayoutInflater inflater; 
        public ArrayList<WeightData> arr; 
        public MyAdapter(Context context) { 
            super(); 
            this.context = context; 
            inflater = LayoutInflater.from(context); 
            arr = new ArrayList<WeightData>(); 
            for(int i=0;i<3;i++){    //listview初始化3个子项 
                //arr.add(""); 
            } 
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
            if(view == null){ 
                view = inflater.inflate(R.layout.listview_item, null); 
            } 
            final TextView edit = (TextView) view.findViewById(R.id.index); 
            edit.setText(arr.get(position).sid);    //在重构adapter的时候不至于数据错乱 
            final TextView time = (TextView) view.findViewById(R.id.time); 
            time.setText(arr.get(position).stime);    //在重构adapter的时候不至于数据错乱 
            final TextView kg = (TextView) view.findViewById(R.id.kg); 
            kg.setText(arr.get(position).skg);    //在重构adapter的时候不至于数据错乱 
            
            return view; 
        } 
    } 



}
