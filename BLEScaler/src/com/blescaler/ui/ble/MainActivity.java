package com.blescaler.ui.ble;

import com.blescaler.ui.ble.BaseActivity;
import com.blescaler.ui.ble.FlourWeightFragment;
import com.blescaler.ui.ble.OneWeightFragment;
import com.blescaler.ui.ble.WeightCountFragment;
import com.blescaler.ui.DBActivity;
import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.PairedScalerActivity;
import com.blescaler.ui.ParamActivity;
import com.blescaler.ui.R;
import com.blescaler.ui.SearchBTActivity;
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

public class MainActivity extends BaseActivity implements OnTouchListener, OnClickListener {
	private DrawerLayout mDrawerLayout;
	TextView leftmenu1, leftmenu2, leftmenu3, //
			leftmenu4, leftmenu5, leftmenu6, leftmenu7, leftmenu8, prevLeftMenu;
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
		initView();

		simpleGestureListener = new GestureDetector(this, new GestureDetector.OnGestureListener() {

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.e("1111", "onFling" + (e1.getX() - e2.getX()));
				if (e1.getX() - e2.getX() < -20) {

					mDrawerLayout.openDrawer(Gravity.LEFT);
					return true;
				}
				if (e1.getX() - e2.getX() > 20) {

					mDrawerLayout.closeDrawers();
					return true;
				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		goToFragment(7);
		

	}

	private void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

		mDrawerLayout.setOnTouchListener(this);
		mDrawerLayout.setFocusable(true);
		mDrawerLayout.setClickable(true);
		mDrawerLayout.setLongClickable(true);

		leftmenu1 = (TextView) findViewById(R.id.leftmenu1);
		leftmenu2 = (TextView) findViewById(R.id.leftmenu2);
		leftmenu3 = (TextView) findViewById(R.id.leftmenu3);
		leftmenu4 = (TextView) findViewById(R.id.leftmenu4);
		leftmenu5 = (TextView) findViewById(R.id.leftmenu5);
		leftmenu6 = (TextView) findViewById(R.id.leftmenu6);
		leftmenu7 = (TextView) findViewById(R.id.leftmenu7);
		leftmenu8 = (TextView) findViewById(R.id.leftmenu8);
		leftmenu1.setOnClickListener(this);
		leftmenu2.setOnClickListener(this);
		leftmenu3.setOnClickListener(this);
		leftmenu4.setOnClickListener(this);
		leftmenu5.setOnClickListener(this);
		leftmenu6.setOnClickListener(this);
		leftmenu7.setOnClickListener(this);
		leftmenu8.setOnClickListener(this);
	
		//ActionBar actionBar=getActionBar();
		 //actionBar.setDisplayShowHomeEnabled(true);
	
		

	}

	@Override
	public void onClick(View v) {
		mDrawerLayout.closeDrawers();
		switch (v.getId()) {
		case R.id.leftmenu1:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(1);
			leftmenu1.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu1;
			break;
		case R.id.leftmenu2:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(2);
			leftmenu2.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu2;
			break;
		case R.id.leftmenu3:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(3);
			leftmenu3.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu3;
			break;
		case R.id.leftmenu4:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(4);

			leftmenu4.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu4;
			break;
		case R.id.leftmenu5:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			leftmenu5.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu5;
			StartActivity(SettingActivity.class, null);

			break;
		case R.id.leftmenu6:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			goToFragment(6);
			leftmenu6.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu6;
			break;
		case R.id.leftmenu7:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			leftmenu6.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu7;
			goToFragment(7);

			break;
		case R.id.leftmenu8:
			if (prevLeftMenu != null)
				prevLeftMenu.setBackgroundColor(color.transparent);
			leftmenu8.setBackgroundColor(getResources().getColor(R.color.set_item_click));
			prevLeftMenu = leftmenu8;
			goToFragment(8);
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
		
		   Intent intent = new Intent(this, PairedScalerActivity.class);
		   intent.putExtra("act", "param");
		   startActivity(intent); 
		   return;
		}
		if(pos == 5)
		{
		
		   Intent intent = new Intent(this, ParamActivity.class);
		   startActivity(intent); 
		   return;
		}
		if (pos == 7)
			newFragment = OneWeightFragment.newFragment();
		if (pos == 3)
			newFragment = FlourWeightFragment.newFragment();
		if (pos == 6)
		{
			 Intent intent = new Intent(this, SearchBTActivity.class);
			
			 startActivity(intent); 
			 return;
		}
		if(pos == 8)
		{
			 Intent intent = new Intent(this, PairedScalerActivity.class);
			 intent.putExtra("act", "calib");
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return simpleGestureListener.onTouchEvent(event);
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

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
}

}
