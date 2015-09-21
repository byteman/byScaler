package com.blescaler.ui.ble;



import com.blescaler.ui.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FlourWeightFragment extends BaseFragment implements View.OnClickListener {
	View root;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.activity_flourweight, container, false);

		return root;
	}
	@Override
	public void onClick(View v) {

	}
	public static Fragment newFragment() {
		FlourWeightFragment f = new FlourWeightFragment();
		Bundle bundle = new Bundle();


		f.setArguments(bundle);
		return f;
		
	}

}
