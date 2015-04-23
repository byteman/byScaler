package com.example.bluetooth.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.anim;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DeviceMgrActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	private SimpleAdapter mAdapter;
	private ListView listMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device);
		String menustrs[] = {"标定","搜索","升级"};
		int imgs[] = {R.drawable.calib,R.drawable.query,R.drawable.upload};
		List<Map<String, Object>> menus = new ArrayList<Map<String,Object>>();
		
		for(int i = 0; i < menustrs.length; i++)
		{
			Map<String,Object> item = new HashMap<String, Object>();
			item.put("img", imgs[i] );
			item.put("lable", menustrs[i]);
			menus.add(item);
		}
		listMenu = (ListView) findViewById(R.id.listmenu);
		mAdapter = new SimpleAdapter(this, menus, R.layout.device_menu_item, new String[] {"img","lable"}, new int[] {R.id.imgTitle,R.id.tvTitle});
		
		listMenu.setAdapter(mAdapter);
		
		
	}

}
