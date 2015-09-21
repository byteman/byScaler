package com.example.bluetooth.le.fragment;

import com.example.bluetooth.le.R;
import com.example.bluetooth.le.R.layout;
import com.example.bluetooth.le.base.BaseActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeightDataFragment extends BaseFragment implements View.OnClickListener {
	
	private View root;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_form, container, false);
		
		return root;
	}

	@Override
	public void onClick(View v) {

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
	

}
