package com.blescaler.db;

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
	private int ScalerCount = 0;
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
	public Channel getChannel(int index)
	{
		Channel chan = new Channel();
		
		chan.setC( mSharedPre.getFloat("channel"+index+".c", (float) 0.10197));
		chan.setK( mSharedPre.getFloat("channel"+index+".k", (float) -0.12088));
		chan.setG( mSharedPre.getFloat("channel"+index+".g", (float) -0.002256));
		chan.setR0( mSharedPre.getFloat("channel"+index+".r0", (float) 8880.4));
		chan.setT0( mSharedPre.getFloat("channel"+index+".t0", (float) 18.3));	
		chan.setT0( mSharedPre.getFloat("channel"+index+".diff", (float) 0));	
		return chan;
	}
	public void setChannel(int index, Channel chan)
	{
		mEditor.putFloat("channel"+index+".c", chan.getC());
		mEditor.putFloat("channel"+index+".k", chan.getK());
		mEditor.putFloat("channel"+index+".g", chan.getG());
		mEditor.putFloat("channel"+index+".r0", chan.getR0());
		mEditor.putFloat("channel"+index+".t0", chan.getT0());
		mEditor.putFloat("channel"+index+".diff",chan.getDiff());
		
		mEditor.commit();
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
}
