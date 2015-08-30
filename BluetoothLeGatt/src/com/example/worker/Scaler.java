package com.example.worker;

import android.text.GetChars;

import com.xtremeprog.sdk.ble.BleGattCharacteristic;

public class Scaler {
	private String address;
	private boolean connected;
	private int weight;
	private BleGattCharacteristic characteristic;
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
