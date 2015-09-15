package com.blescaler.worker;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;

public class Scaler {

	
	public ScalerParam para;
	private String address;
	private String name;
	private boolean connected;
	private int weight;
	private int zeroValue;
	private int weightVlaue;
	private int loadValue;
	private BleGattCharacteristic characteristic;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getZeroValue() {
		return zeroValue;
	}
	public void setZeroValue(int zeroValue) {
		this.zeroValue = zeroValue;
	}
	public int getWeightVlaue() {
		return weightVlaue;
	}
	public void setWeightVlaue(int weightVlaue) {
		this.weightVlaue = weightVlaue;
	}
	public int getLoadValue() {
		return loadValue;
	}
	public void setLoadValue(int loadValue) {
		this.loadValue = loadValue;
	}
	private void init()
	{
		this.address = "";
		this.connected = false;
		this.weight = 0;
		para = new ScalerParam();
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
		init();
	}
	public Scaler(String addr)
	{
		init();
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
