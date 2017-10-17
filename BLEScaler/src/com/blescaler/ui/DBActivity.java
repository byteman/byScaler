package com.blescaler.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blescaler.worker.WorkService;
import com.blescaler.worker.Global;
import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;

import com.blescaler.utils.Utils;
import com.lvrenyang.utils.DataUtils;

public class DBActivity extends Activity implements OnClickListener {

	private static Handler mHandler = null;
	private static String TAG = "FormActivity";
	private MyAdapter adapter;
	private ListView listData;
	private WeightDao wDao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);

		findViewById(R.id.buttonPrintForm).setOnClickListener(this);
		findViewById(R.id.btn_quit).setOnClickListener(this);
		listData = (ListView) findViewById(R.id.list);
		wDao = new WeightDao(this);
		adapter = new MyAdapter(this);	
		listData.setAdapter(adapter);
		loadDBData();
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.buttonPrintForm: {
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
				
			}
			case R.id.btn_quit:
				finish();
				break;
		}
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
			final TextView gross = (TextView) view.findViewById(R.id.gross);
			gross.setText(arr.get(position).getGross());

			final TextView tare = (TextView) view.findViewById(R.id.tare);
			tare.setText(arr.get(position).getTare());

			final TextView net = (TextView) view.findViewById(R.id.net);
			net.setText(arr.get(position).getNet());

			
			return view;
		}
	}
	static class MHandler extends Handler {

		WeakReference<DBActivity> mActivity;

		MHandler(DBActivity activity) {
			mActivity = new WeakReference<DBActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			DBActivity theActivity = mActivity.get();
			switch (msg.what) {

			case Global.CMD_POS_WRITERESULT: {
				int result = msg.arg1;
				Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, "Result: " + result);
				break;
			}

			}
		}
	}

}
