package com.blescaler.ui.ble;

import com.blescaler.ui.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFragment1 extends BaseFragment implements View.OnClickListener {

	View root;
	TextView tv;
	int pos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_setting1, container, false);

		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Bundle bundle = this.getArguments();
		pos = bundle.getInt("pos");
		tv = (TextView) root.findViewById(R.id.tv);
		tv.setText("这是第" + pos + "项设置");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}

	}

	public static SettingFragment1 newFragment(int pos) {
		SettingFragment1 f = new SettingFragment1();
		Bundle bundle = new Bundle();

		bundle.putInt("pos", pos);

		f.setArguments(bundle);
		return f;
	}

}
