package com.blescaler.ui.ble;

import com.blescaler.ui.ble.BaseActivity;
import com.blescaler.ui.ble.FlourWeightFragment;
import com.blescaler.ui.ble.OneWeightFragment;
import com.blescaler.ui.ble.WeightCountFragment;
import com.blescaler.ui.BleApplication;
import com.blescaler.ui.CalibActivity;
import com.blescaler.ui.DBActivity;
import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.PairedScalerActivity;
import com.blescaler.ui.ScalerParamActivity;
import com.blescaler.ui.R;
import com.blescaler.ui.SearchBTActivity;
import com.blescaler.ui.SysParamActivity;
import com.blescaler.worker.WorkService;

import android.R.color;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {
	private DrawerLayout mDrawerLayout;
	TextView menu_serach, menu_one_scaler, menu_print, //
			menu_calib, menu_para, menu_data_report, menu_setting, menu_four_scaler, prevLeftMenu;
	GestureDetector simpleGestureListener;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.layout_content);
		//initView();

//		simpleGestureListener = new GestureDetector(this, new GestureDetector.OnGestureListener() {
//
//			@Override
//			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//
//				return false;
//			}
//
//			@Override
//			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//				if(e1==null) return false;
//				if(e2==null) return false;
//				if (e1.getX() - e2.getX() < -20) {
//
//					mDrawerLayout.openDrawer(Gravity.LEFT);
//					return true;
//				}
//				if (e1.getX() - e2.getX() > 20) {
//
//					mDrawerLayout.closeDrawers();
//					return true;
//				}
//				return false;
//			}
//
//			@Override
//			public boolean onDown(MotionEvent e) {
//				return false;
//			}
//
//			@Override
//			public void onLongPress(MotionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onShowPress(MotionEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public boolean onSingleTapUp(MotionEvent e) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		goToFragment(7);
		

	}

//	private void initView() {
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
//
//		mDrawerLayout.setOnTouchListener(this);
//		mDrawerLayout.setFocusable(true);
//		mDrawerLayout.setClickable(true);
//		mDrawerLayout.setLongClickable(true);
//
//		menu_serach = (TextView) findViewById(R.id.menu_serach);
//		menu_one_scaler = (TextView) findViewById(R.id.menu_one_scaler);
//		menu_print = (TextView) findViewById(R.id.menu_print);
//		menu_calib = (TextView) findViewById(R.id.menu_calib);
//		menu_para = (TextView) findViewById(R.id.menu_para);
//		menu_data_report = (TextView) findViewById(R.id.menu_data_report);
//		menu_setting = (TextView) findViewById(R.id.menu_setting);
//		menu_four_scaler = (TextView) findViewById(R.id.menu_four_scaler);
//		menu_serach.setOnClickListener(this);
//		menu_one_scaler.setOnClickListener(this);
//		menu_print.setOnClickListener(this);
//		menu_calib.setOnClickListener(this);
//		menu_para.setOnClickListener(this);
//		menu_data_report.setOnClickListener(this);
//		menu_setting.setOnClickListener(this);
//		menu_four_scaler.setOnClickListener(this);
//	
//		//ActionBar actionBar=getActionBar();
//		 //actionBar.setDisplayShowHomeEnabled(true);
//	
//		
//
//	}

	@Override
	public void onClick(View v) {
		mDrawerLayout.closeDrawers();
		switch (v.getId()) {
		case R.id.menu_serach:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(2);
			menu_serach.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = menu_serach;  
			break;
		case R.id.menu_one_scaler:
//			if (prevLeftMenu != null)
//				prevLeftMenu.setBackgroundColor(color.transparent);
//			goToFragment(7);
//			menu_one_scaler.setBackgroundColor(getResources().getColor(R.color.set_item_click));
//			prevLeftMenu = menu_one_scaler;
			break;
		case R.id.menu_print:
//			if (prevLeftMenu != null)
//				prevLeftMenu.setBackgroundColor(color.transparent);
//			goToFragment(6);
//			menu_print.setBackgroundColor(getResources().getColor(R.color.set_item_click));
//			prevLeftMenu = menu_print;
			break;
		case R.id.menu_calib:
//			if (prevLeftMenu != null)
//				prevLeftMenu.setBackgroundColor(color.transparent);
//			goToFragment(8);
//
//			menu_calib.setBackgroundColor(getResources().getColor(R.color.set_item_click));
//			prevLeftMenu = menu_calib;
			break;
		case R.id.menu_para:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			menu_para.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = menu_para;
			goToFragment(4);

			break;
		case R.id.menu_data_report:
//			if (prevLeftMenu != null)
//				prevLeftMenu.setBackgroundColor(color.transparent);
//			goToFragment(1);
//			menu_data_report.setBackgroundColor(getResources().getColor(R.color.set_item_click));
//			prevLeftMenu = menu_data_report;
			break;
		case R.id.menu_setting:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			menu_data_report.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = menu_setting;
			//StartActivity(SysParamActivity.class, null);
			goToFragment(9);
			break;
		case R.id.menu_four_scaler:
//			if (prevLeftMenu != null)
//				prevLeftMenu.setBackgroundColor(color.transparent);
//			menu_four_scaler.setBackgroundColor(getResources().getColor(R.color.set_item_click));
//			prevLeftMenu = menu_four_scaler;
//			goToFragment(3);
			
			break;
		}

	}

	private void goToFragment(int pos) {
		// 璺宠浆涓嶅悓鐨勯〉闈?
		Fragment newFragment = null;
		if(pos ==  1)
		{
			//newFragment = WeightDataFragment.newFragment();
		   Intent intent = new Intent(this, DBActivity.class);
		   startActivity(intent); 
		   return;
		}
		if(pos == 2)
		{
		
		   Intent intent = new Intent(this, DeviceScanActivity.class);
		   startActivity(intent); 
		   return;
		}
		if(pos == 4)
		{
		
//		   Intent intent = new Intent(this, PairedScalerActivity.class);
//		   intent.putExtra("act", "param");
//		   startActivity(intent); 
		   
		   Intent intent = new Intent(MainActivity.this, ScalerParamActivity.class);
		   intent.putExtra("address","00");
		   startActivity(intent);
			
		   return;
		}
		if(pos == 5)
		{
		
		   Intent intent = new Intent(this, ScalerParamActivity.class);
		   startActivity(intent); 
		   return;
		}
		if (pos == 7)
			newFragment = OneWeightFragment.newFragment();
		//if (pos == 3)
		//	newFragment = FlourWeightFragment.newFragment();
		if (pos == 6)
		{
			 Intent intent = new Intent(this, SearchBTActivity.class);
			
			 startActivity(intent); 
			 return;
		}
		if(pos == 8)
		{
//			 Intent intent = new Intent(this, PairedScalerActivity.class);
//			 intent.putExtra("act", "calib");
//			 startActivity(intent); 
			  Intent intent = new Intent(MainActivity.this, CalibActivity.class);
			   intent.putExtra("address","00");
			   startActivity(intent);
			   
			 return;
		}
		if(pos == 9)
		{
//			  Intent intent = new Intent(this, PairedScalerActivity.class);
//			  intent.putExtra("act", "debug");
//			  startActivity(intent); 
			 Intent intent = new Intent(MainActivity.this, SysParamActivity.class);
			   intent.putExtra("address","00");
			   startActivity(intent);
			   
			   
			  return;
		}
		if (newFragment == null)
			return;
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.replace(R.id.content_frame, newFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commitAllowingStateLoss();

	}

	
	private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
            	WorkService.requestDisConnectAll();
                moveTaskToBack(false);
                
                finish();
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
}

}
