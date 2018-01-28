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
import com.blescaler.db.WeightDao;
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
    WeightDao dao = null;
    private Button btn_previous_page,btn_next_page,btn_back;
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
        btn_previous_page = findViewById(R.id.btn_previous_page);
        btn_next_page = findViewById(R.id.btn_next_page);
        btn_back = findViewById(R.id.btn_back);
        dao = new WeightDao(this);
		mWeightListAdapter = new WeightListAdapter(dao);
		//lv_Devices.setListAdapter(mLeDeviceListAdapter);
		history.setAdapter(mWeightListAdapter);
        btn_next_page.setOnClickListener(this);
        btn_previous_page.setOnClickListener(this);
        btn_back.setOnClickListener(this);


    }
	private class WeightListAdapter extends BaseAdapter {
		private ArrayList<WeightRecord> items;

		private LayoutInflater mInflator;
        private  WeightDao mDao = null ;
        private int m_page = 0,m_page_size = 5;
        private  boolean m_isEnd = false;
        public WeightListAdapter(WeightDao dao) {
			super();
            mDao = dao;
			items = new ArrayList<WeightRecord>();

			mInflator = HistoryWeightActivity.this.getLayoutInflater();

            getData(mDao, 0);

		}
		// 初始化isSelected的数据
		private int getData(WeightDao dao,int page) {
            clear();
            List<WeightRecord> wlist = dao.getPageWeightList(page,m_page_size);
            for(int i = 0; i < wlist.size(); i++)
            {
                addItem(wlist.get(i));
            }

            notifyDataSetChanged();
            if(wlist.size() > 0)
            {
                m_isEnd = false;
            }else
            {
                m_isEnd = true;
            }
            return  wlist.size();
		}
        public void next()
        {
            if(!m_isEnd)
            {
                ++m_page;
            }
            if(getData(mDao,m_page) == 0)
            {

            }

        }
        public void prev()
        {
            if(m_page>0)
            {
                m_page--;
                getData(mDao,m_page);
            }

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
//				viewHolder.tv_index = view
//						.findViewById(R.id.index);
				viewHolder.tv_time =  view
						.findViewById(R.id.time);
				viewHolder.tv_total =  view
						.findViewById(R.id.gross);
				viewHolder.tv_uw = view
						.findViewById(R.id.tare);
				viewHolder.tv_count = view
						.findViewById(R.id.net);


				view.setTag(viewHolder);
			} else {
				viewHolder = (HistoryWeightActivity.ViewHolder) view.getTag();
			}

			WeightRecord item = items.get(i);
			final String date = item.getFormatDate() + " " +item.getFormatTime();
			//viewHolder.tv_index.setText(item.getID());
			viewHolder.tv_time.setText(date);
			viewHolder.tv_total.setText(item.getGross());
			viewHolder.tv_uw.setText(item.getTare());
			viewHolder.tv_count.setText(item.getNet());

			return view;
		}
	}

	static class ViewHolder {
		//TextView tv_index;
		TextView tv_time;
		TextView tv_total;
		TextView tv_uw;
		TextView tv_count;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next_page:
            mWeightListAdapter.next();

			break;
		case R.id.btn_previous_page:
            mWeightListAdapter.prev();
            break;
		case R.id.btn_back:
			
			finish();
			//WorkService.requestSaveParam(address);
			break;
		default:
			break;
		}
	}
	


}
