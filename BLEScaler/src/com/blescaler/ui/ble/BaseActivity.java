package com.blescaler.ui.ble;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;

public class BaseActivity extends Activity {
	public Context context;
	public BaseActivity baseAt;
	// public AlertDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		baseAt = this;
		
	}
	
	/**
	 * activity跳转动画 统一跳转方法
	 */
	public void StartActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent(context, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}

		startActivity(intent);

	}
	public void startFragment(Fragment fragment) {
		
	}
	/**
	 * activity关闭动画 统一关闭方法
	 * */
	public void FinishActivity() {
		finish();
	}




}
