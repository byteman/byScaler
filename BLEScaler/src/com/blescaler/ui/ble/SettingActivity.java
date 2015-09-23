package com.blescaler.ui.ble;



import com.blescaler.ui.R;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SettingActivity extends BaseActivity implements FragmentCallback {

	SettingListFragment mSettingListFragment;
	FragmentCallback mCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		/*ActionBar actionBar=getActionBar();
		actionBar.setTitle("设置");
		 actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);*/
        int settingPos=getIntent().getIntExtra(Constant.INTENTKEY_SETTING_POS, 0);
        onItemSelected(settingPos);
		mSettingListFragment = (SettingListFragment) getFragmentManager().findFragmentById(R.id.setting_menu_list);
		mSettingListFragment.setCallback(this);

		
	}

	@Override
	public void onItemSelected(Integer pos) {
		Fragment newFragment ;
		if(pos==12){
			newFragment = SettingFragment12.newFragment(pos);
		}else if(pos==11){
			newFragment = SettingFragment11.newFragment(pos);
		}else{
			newFragment = SettingFragment1.newFragment(pos);
		}
		// 跳转不同的页面
		
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.replace(R.id.setting_content_fragment, newFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commitAllowingStateLoss();

	}

}
