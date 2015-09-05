package com.example.worker;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.text.GetChars;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;

public class Scaler {
	public static boolean parseData(byte[] val, int size)
	{
		
		return false;
	}
	/**
	 * @return the mtd
	 */
	public String getMtd() {
		return mtd;
	}
	/**
	 * @param mtd the mtd to set
	 */
	public void setMtd(int mtd) {
		if(mtd == 0) this.mtd = "OFF";
		else if(mtd == 1) this.mtd = "+-0.25d";
		else if(mtd == 2) this.mtd = "+-0.5d";
		else if(mtd == 3) this.mtd = "+-1.0d";
		else if(mtd == 4) this.mtd = "+-2.0d";
		else if(mtd == 5) this.mtd = "+-3.0d";
		else this.mtd = "OFF";
	}
	/**
	 * @return the zerotrack
	 */
	public String getZerotrack() {
		return zerotrack;
	}
	/**
	 * @param zerotrack the zerotrack to set
	 */
	public void setZerotrack(int zerotrack) {
		if(zerotrack == 0) this.mtd = "OFF";
		else if(zerotrack == 1) this.mtd = "0.5d/s";
		else if(zerotrack == 1) this.mtd = "1.0d/s";
		else if(zerotrack == 1) this.mtd = "2.0d/s";
		else if(zerotrack == 1) this.mtd = "3.0d/s";
		else this.zerotrack = "OFF";
	}
	/**
	 * @return the init_zerotrack
	 */
	public String getInit_zerotrack() {
		return init_zerotrack;
	}
	/**
	 * @param init_zerotrack the init_zerotrack to set
	 */
	public void setInit_zerotrack(String init_zerotrack) {
		this.init_zerotrack = init_zerotrack;
	}
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * @return the nov
	 */
	public String getNov() {
		return nov;
	}
	/**
	 * @param nov the nov to set
	 */
	public void setNov(String nov) {
		this.nov = nov;
	}
	/**
	 * @return the resolution
	 */
	public String getResolution() {
		return resolution;
	}
	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return the zerotime
	 */
	public String getZerotime() {
		return zerotime;
	}
	/**
	 * @param zerotime the zerotime to set
	 */
	public void setZerotime(String zerotime) {
		this.zerotime = zerotime;
	}
	/**
	 * @return the zerospeed
	 */
	public String getZerospeed() {
		return zerospeed;
	}
	/**
	 * @param zerospeed the zerospeed to set
	 */
	public void setZerospeed(String zerospeed) {
		this.zerospeed = zerospeed;
	}
	private String address;
	private boolean connected;
	private int weight;
	private BleGattCharacteristic characteristic;
	private String mtd; //stand still monitor
	private String zerotrack;
	private String init_zerotrack;
	private String unit;
	private String nov;
	private String resolution;
	private String zerotime;
	private String zerospeed;
	
	//public static Map<Integer, String> MTDSTR = new HashMap<Integer, String>();
	//public static Map<Integer, String> SCALER_PAR = new HashMap<Integer, String>();
	//public static Map<Integer, String> SCALER_PAR = new HashMap<Integer, String>();
	private void clear()
	{
		this.address = "";
		this.connected = false;
		this.weight = 0;
		
	}
	public BleGattCharacteristic GetBleChar()
	{
		return characteristic;
	}
	public void SetBleChar(BleGattCharacteristic ble_char)
	{
		characteristic = ble_char;
	}
	
	public Scaler()
	{
		clear();
	}
	public Scaler(String addr)
	{
		clear();
		this.address = addr;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected,BleGattCharacteristic ble_char) {
		characteristic = ble_char;
		this.connected = connected;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
