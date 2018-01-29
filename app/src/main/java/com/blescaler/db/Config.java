package com.blescaler.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config 
{

	private Context mContext;
	private SharedPreferences mSharedPre;
	private Editor mEditor;


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

	public String getPrinterAddress() {
		return mSharedPre.getString("printer_address", "");
	}

	public void setPrinterAddress(String pDevAddress) {
		//this.mDevAddress = pDevAddress;
		mEditor.putString("printer_address", pDevAddress);
		mEditor.commit();
	}
	//获取第n个设备的蓝牙地址
	public String getDevAddress(int index) {
		return mSharedPre.getString("address"+index, "");
	}
	//设置第n个设备的蓝牙地址
	public void setDevAddress(int index,String pDevAddress) {
		//this.mDevAddress = pDevAddress;
		mEditor.putString("address"+index, pDevAddress);
	
		mEditor.commit();
	}
	public void setUw(float wg)
	{
		mEditor.putFloat("uw", wg);
		mEditor.commit();
	}
	public void clearUw()
	{
		mEditor.remove("uw");
		mEditor.commit();
	}
	public boolean getUw(UwInfo uw)
	{
		if(uw ==null) return false;
		if(!mSharedPre.contains("uw"))
		{
			 uw.isValid = false;
			 uw.weight = 0;
			 return true;
		}

		uw.weight = mSharedPre.getFloat("uw",0);
		uw.isValid = true;
		return true;
	}

	public String getDevName(int index) {
		return mSharedPre.getString("name"+index,"unknown");
	}

	public void setDevName(int index,String pDevName) {
		//this.mDevName = pDevName;
		mEditor.putString("name"+index, pDevName);
		mEditor.commit();
	}
	public String getUser() {
		return mSharedPre.getString("user", "");
	}
	public void setUser(String pUser) {
		mEditor.putString("user", pUser);
		mEditor.commit();
	}
	/**
	 * @return the scalerCount
	 */
	public int getScalerCount() {
		return mSharedPre.getInt("maxcount", 1);
	}
	/**
	 * @param scalerCount the scalerCount to set
	 */
	public void setScalerCount(int scalerCount) {
		mEditor.putInt("maxcount", scalerCount);
		mEditor.commit();
	}
	public String getUnit()
	{
		return mSharedPre.getString("unit", "kg");
	}
	public void setUnit(String unit) {
		mEditor.putString("unit", unit);
		mEditor.commit();
	}
	public boolean setLanguage(String lang)
	{
		mEditor.putString("lang", lang);
		return mEditor.commit();
	}
	public String getLanguage()
	{
		return mSharedPre.getString("lang","en");
	}
	public boolean setLastPage(int page)
	{
		mEditor.putInt("page", page);
		return mEditor.commit();
	}
	public int getLastPage()
	{
		return mSharedPre.getInt("page",7);
	}
}
