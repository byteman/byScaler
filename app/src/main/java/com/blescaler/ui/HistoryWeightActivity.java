package com.blescaler.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.blescaler.db.Config;
import com.blescaler.db.WeightRecord;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.ScalerParam;
import com.blescaler.worker.WorkService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HistoryWeightActivity extends Activity implements OnClickListener {



	private ListView history;
	private WeightListAdapter mWeightListAdapter = null;
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//WorkService.requestReadPar(address);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}
	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		history = findViewById(R.id.listView);
		mWeightListAdapter = new WeightListAdapter();
		//lv_Devices.setListAdapter(mLeDeviceListAdapter);
		history.setAdapter(mWeightListAdapter);

	}
	private class WeightListAdapter extends BaseAdapter {
		private ArrayList<WeightRecord> items;

		private LayoutInflater mInflator;


		public WeightListAdapter() {
			super();
			items = new ArrayList<WeightRecord>();

			mInflator = HistoryWeightActivity.this.getLayoutInflater();

			initData();

		}
		// 初始化isSelected的数据
		private void initData() {

		}

		public void addItem(WeightRecord item) {
			if (!items.contains(item)) {
				items.add(item);

			}

		}


		public void clear() {
			items.clear();

		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			HistoryWeightActivity.ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listview_weight_item, null);
				viewHolder = new HistoryWeightActivity.ViewHolder();
				viewHolder.tv_index = view
						.findViewById(R.id.index);
				viewHolder.tv_time =  view
						.findViewById(R.id.time);
				viewHolder.tv_gross =  view
						.findViewById(R.id.gross);
				viewHolder.tv_tare = view
						.findViewById(R.id.tare);
				viewHolder.tv_net = view
						.findViewById(R.id.net);


				view.setTag(viewHolder);
			} else {
				viewHolder = (HistoryWeightActivity.ViewHolder) view.getTag();
			}

			WeightRecord item = items.get(i);
			final String date = item.getFormatDate();
			viewHolder.tv_time.setText(item.getID());
			viewHolder.tv_time.setText(date);
			viewHolder.tv_gross.setText(item.getGross());
			viewHolder.tv_tare.setText(item.getTare());
			viewHolder.tv_net.setText(item.getNet());

			return view;
		}
	}

	static class ViewHolder {
		TextView tv_index;
		TextView tv_time;
		TextView tv_gross;
		TextView tv_tare;
		TextView tv_net;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_read:


			break;
		case R.id.btn_save:

			break;
		case R.id.btn_eeprom:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		default:
			break;
		}
	}
	


}
