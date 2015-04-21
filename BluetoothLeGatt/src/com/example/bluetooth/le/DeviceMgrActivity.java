package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class DeviceMgrActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	private SimpleAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device);
		String menustrs[] = {"标定","搜索","升级"};
		private int imgs[] = {1,2};
		List<Map<String, Object>> menus = new ArrayList<Map<String,Object>>();
		
		for(int i = 0; i < menustrs.length; i++)
		{
			Map<String,Object> item = new HashMap<String, Object>();
			item.put("img", imgs[i] );
			item.put("lable", menustrs[i]);
			menus.add(item);
		}
		mAdapter = new SimpleAdapter(this, menus, R.layout., null, null);
	}

}
