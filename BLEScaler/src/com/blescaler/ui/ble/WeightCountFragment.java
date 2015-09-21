package com.example.bluetooth.le.fragment;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.R.layout;
import com.example.bluetooth.le.base.BaseActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeightCountFragment extends BaseFragment implements View.OnClickListener {
	View root;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_mianweight, container, false);

		return root;
	}

	@Override
	public void onClick(View v) {

	}

	public static Fragment newFragment() {
		WeightCountFragment f = new WeightCountFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
	}

}
