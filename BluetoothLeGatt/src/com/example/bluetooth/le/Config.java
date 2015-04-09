package com.example.bluetooth.le;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config 
{
	//private String mDevAddress;
	//private String mDevName;
	private Context mContext;
	private SharedPreferences mSharedPre;
	private Editor mEditor;
	//private String mUser;
	static private Config mConfig;
	private Config(Context pContext) 
	{
		mContext = pContext;
		mSharedPre = mContext.getSharedPreferences("device",
				Activity.MODE_PRIVATE);
		mEditor = mSharedPre.edit();
	}
	public static Config getInstance(Context pContext)
	{
		if(mConfig == null)
		{
			mConfig = new Config(pContext);
		}
		return mConfig;
	}

	public String getDevAddress() {
		return mSharedPre.getString("address", "");
	}

	public void setDevAddress(String pDevAddress) {
		//this.mDevAddress = pDevAddress;
		mEditor.putString("address", pDevAddress);
		mEditor.commit();
	}

	public String getDevName() {
		return mSharedPre.getString("devname", "");
	}

	public void setDevName(String pDevName) {
		//this.mDevName = pDevName;
		mEditor.putString("address", pDevName);
		mEditor.commit();
	}
	public String getUser() {
		return mSharedPre.getString("user", "");
	}
	public void setUser(String pUser) {
		mEditor.putString("user", pUser);
		mEditor.commit();
	}
}
