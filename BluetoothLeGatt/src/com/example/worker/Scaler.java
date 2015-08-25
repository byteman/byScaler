package com.example.worker;

public class Scaler {
	private String address;
	private boolean connected;
	private int weight;
	private void clear()
	{
		this.address = "";
		this.connected = false;
		this.weight = 0;
		
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
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
