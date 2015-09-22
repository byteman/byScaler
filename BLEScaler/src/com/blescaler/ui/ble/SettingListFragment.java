package com.blescaler.ui.ble;



import com.blescaler.ui.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingListFragment extends BaseFragment implements View.OnClickListener {
	FragmentCallback mCallback;
	View root;
	TextView set_1,set_2,set_3,set_4,set_5,set_6,set_7,set_8,set_9,set_10,set_11,set_12,prevTextView;
	public void setCallback(FragmentCallback mCallback) {
		this.mCallback = mCallback;

	}
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	root = inflater.inflate(R.layout.fragment_setting_menu, container, false);
	

	return root;
}
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	set_1=(TextView) root.findViewById(R.id.set_1);
	set_2=(TextView) root.findViewById(R.id.set_2);
	set_3=(TextView) root.findViewById(R.id.set_3);
	set_4=(TextView) root.findViewById(R.id.set_4);
	set_5=(TextView) root.findViewById(R.id.set_5);
	set_6=(TextView) root.findViewById(R.id.set_6);
	set_7=(TextView) root.findViewById(R.id.set_7);
	set_8=(TextView) root.findViewById(R.id.set_8);
	set_9=(TextView) root.findViewById(R.id.set_9);
	set_10=(TextView) root.findViewById(R.id.set_10);
	set_11=(TextView) root.findViewById(R.id.set_11);
	set_12=(TextView) root.findViewById(R.id.set_12);
	set_1.setOnClickListener(this);
	set_2.setOnClickListener(this);
	set_3.setOnClickListener(this);
	set_4.setOnClickListener(this);
	set_5.setOnClickListener(this);
	set_6.setOnClickListener(this);
	set_7.setOnClickListener(this);
	set_8.setOnClickListener(this);
	set_9.setOnClickListener(this);
	set_10.setOnClickListener(this);
	set_11.setOnClickListener(this);
	set_12.setOnClickListener(this);
}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		

	}
	@Override
	public void onClick(View v) {
	switch(v.getId()){
	case R.id.set_1:
		
		mCallback.onItemSelected(1);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_1.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_1;
		break;
	case R.id.set_2:
		mCallback.onItemSelected(2);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_2.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_2;
		break;
	case R.id.set_3:
		mCallback.onItemSelected(3);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_3.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_3;
		break;
	case R.id.set_4:
		mCallback.onItemSelected(4);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_4.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_4;
		break;
	case R.id.set_5:
		mCallback.onItemSelected(5);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_5.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_5;
		break;
	case R.id.set_6:
		mCallback.onItemSelected(6);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_6.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_6;
		break;
	case R.id.set_7:
		mCallback.onItemSelected(7);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_7.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_7;
		break;
	case R.id.set_8:
		mCallback.onItemSelected(8);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_8.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_8;
		break;
	case R.id.set_9:
		mCallback.onItemSelected(9);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_9.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_9;
		break;
	case R.id.set_10:
		mCallback.onItemSelected(10);
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_10.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_10;
		break;
	case R.id.set_11:
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_11.setBackgroundColor(getResources().getColor(R.color.set_item_click));
		prevTextView=set_11;
		mCallback.onItemSelected(11);
		break;
	case R.id.set_12:
		if(prevTextView!=null)prevTextView.setBackgroundColor(getResources().getColor(R.color.set_item_unclick));
		set_12.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevTextView = set_12;
		mCallback.onItemSelected(12);
		break;
	
	
	}
		
	}

}
