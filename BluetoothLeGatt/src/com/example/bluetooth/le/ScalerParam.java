package com.example.bluetooth.le;

import java.util.HashMap;
import java.util.Map;

public class ScalerParam {
	private int nov;
	private int mtd;
	private String mdtstr;
	public static Map<Integer, String> mtdmaps = new HashMap<Integer, String>() ;
	public static Map<Integer, String> zerotrackmaps = new HashMap<Integer, String>() ;
	public static Map<Integer, String> pwrzeromaps = new HashMap<Integer, String>() ;
	public static Map<Integer, String> resmaps = new HashMap<Integer, String>() ;
	public static Map<Integer, String> digmaps = new HashMap<Integer, String>() ;
	
	static {
		mtdmaps.put(0, "OFF");
		mtdmaps.put(1, "+-0.25d");
		mtdmaps.put(2, "+-0.5d");
		mtdmaps.put(3, "+-1.0d");
		mtdmaps.put(4, "+-2.0d");
		mtdmaps.put(5, "+-3.0d");
		
		zerotrackmaps.put(0, "OFF");
		zerotrackmaps.put(1, "0.5d/s");
		zerotrackmaps.put(2, "1.0d/s");
		zerotrackmaps.put(3, "2.0d/s");
		zerotrackmaps.put(4, "3.0d/s");
		
		pwrzeromaps.put(0, "OFF");
		pwrzeromaps.put(1, "+-2%(NOV)");
		pwrzeromaps.put(2, "+-5%(NOV)");
		pwrzeromaps.put(3, "+-10%(NOV)");
		pwrzeromaps.put(4, "+-20%(NOV)");
		
		resmaps.put(0, "1d");
		resmaps.put(1, "2d");
		resmaps.put(2, "5d");
		resmaps.put(3, "10d");
		resmaps.put(4, "20d");
		resmaps.put(5, "50d");
		resmaps.put(6, "100d");
		
		
		digmaps.put(0, "xxxxxxx.");
		digmaps.put(1, "xxxxxx.x");
		digmaps.put(2, "xxxxx.xx");
		digmaps.put(3, "xxxx.xxx");
		digmaps.put(4, "xxx.xxxx");
		digmaps.put(5, "xx.xxxxx");
		digmaps.put(6, "x.xxxxxx");
		
	}
	public void setMtd(int mtd) {
		this.mtd = mtd;
	}
	public int getMtd()
	{
		return this.mtd;
	}
	
}
