package com.blescaler.ui.ble;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;

import com.blescaler.ui.R;

import com.blescaler.utils.LogUtils;
import com.blescaler.utils.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.WorkService;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WeightDataFragment extends BaseFragment implements
		View.OnClickListener {

	private View root;
	private static Handler mHandler = null;
	private static String TAG = "DataLog";
	private MyAdapter adapter;
	private ListView listData;
	private WeightDao wDao;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LogUtils.LOGD(TAG, "onCreateView");
		root = inflater.inflate(R.layout.activity_form, container, false);

		root.findViewById(R.id.buttonPrintForm).setOnClickListener(this);

		listData = (ListView) root.findViewById(R.id.list);
		wDao = new WeightDao(this.getActivity());
		adapter = new MyAdapter(this.getActivity());
		listData.setAdapter(adapter);
		loadDBData();
		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);

		return root;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.LOGD(TAG, "onDestroy");
		WorkService.delHandler(mHandler);
		mHandler = null;
		
		
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		LogUtils.LOGD(TAG, "onAttach");
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		LogUtils.LOGD(TAG, "onDetach");
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtils.LOGD(TAG, "onPause");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtils.LOGD(TAG, "onResume");
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtils.LOGD(TAG, "onStart");
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils.LOGD(TAG, "onStop");
	}
	private void loadDBData() {
		List<WeightRecord> items = new ArrayList<WeightRecord>();

		items = wDao.getWeightList();

		for (WeightRecord item : items) {
			adapter.arr.add(item);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonPrintForm: {
			if (!WorkService.hasConnectPrinter()) {

				Utils.Msgbox(this.getActivity(), "请先连接打印机");

				break;
			}

			break;

		}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public static Fragment newFragment() {
		WeightDataFragment f = new WeightDataFragment();
		Bundle bundle = new Bundle();

		f.setArguments(bundle);
		return f;
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
			// String index = String.valueOf(arr.size()+1);
			edit.setText(arr.get(position).getID());
			final TextView time = (TextView) view.findViewById(R.id.time);
			String timeString = Utils
					.getNormalTime(arr.get(position).getTime());
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

		WeakReference<WeightDataFragment> mActivity;

		MHandler(WeightDataFragment activity) {
			mActivity = new WeakReference<WeightDataFragment>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			WeightDataFragment theActivity = mActivity.get();
			switch (msg.what) {

			case Global.CMD_POS_WRITERESULT: {
				int result = msg.arg1;
				Utils.Msgbox(theActivity.getActivity(),
						(result == 1) ? Global.toast_success
								: Global.toast_fail);

				break;
			}

			}
		}
	}

}
