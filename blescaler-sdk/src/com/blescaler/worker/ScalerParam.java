package com.blescaler.worker;


import java.util.HashMap;
import java.util.Map;


public class ScalerParam {
	
	private byte mtd=-1;	
	private int nov=-1;	
	private byte zerotrack=-1;	
	private byte pwr_zerotrack=-1; 
	private byte hand_zerotrack=-1;
	private byte resultion=1; 
	private byte dignum=-1;//
	private byte filter=-1;
	private byte unit=-1; //
	private short sleep = 0;
	public short getSleep() {
		return sleep;
	}
	public void setSleep(short sleep) {
		this.sleep = sleep;
	}
	public short getSnr_num() {
		return snr_num;
	}
	public void setSnr_num(short snr_num) {
		this.snr_num = snr_num;
	}
	private short snr_num = 0;
	
	private String mdtstr;
	
	public ScalerParam()
	{
		
	}
	public ScalerParam(int nov, byte mtd, byte zt,byte pzt,byte dig, byte res, byte unit)
	{
		
		this.nov = nov;
		this.mtd = mtd;
		this.zerotrack = zt;
		this.pwr_zerotrack = pzt;
		this.dignum = dig;
		this.resultion = res;
		this.unit = unit;
	}
	public void setMtd(byte mtd) {
		this.mtd = mtd;
	}
	public byte getMtd()
	{
		return this.mtd;
	}
	public void setFilter(byte val) {
		this.filter = val;
	}
	public byte getFilter()
	{
		return this.filter;
	}
	public int getNov() {
		return nov;
	}
	public void setNov(int nov) {
		this.nov = nov;
	}
	public byte getZerotrack() {
		return zerotrack;
	}
	public void setZerotrack(byte zerotrack) {
		this.zerotrack = zerotrack;
	}
	public byte getPwr_zerotrack() {
		return pwr_zerotrack;
	}
	public void setPwr_zerotrack(byte pwr_zerotrack) {
		this.pwr_zerotrack = pwr_zerotrack;
	}
	public byte getHand_zerotrack() {
		return hand_zerotrack;
	}
	public void setHand_zerotrack(byte zerotrack) {
		this.hand_zerotrack = zerotrack;
	}

	public byte getResultionx() {
		return resultion;
	}
	public byte getResultionIndex() {
		if(resultion==1) return 0;
		else if(resultion==2)   return 1;
		else if(resultion==5)   return 2;
		else if(resultion==10)  return 3;
		else if(resultion==20)  return 4;
		else if(resultion==50)  return 5;
		else if(resultion==100) return 6 ;
		return 0;
	}
	public void setResultionx(byte resultion) {
		this.resultion = resultion;
	
	}
	public void setResultionIndex(byte index) {
		
		if(index==0) this.resultion=1;
		else if(index==1) this.resultion=2;
		else if(index==2) this.resultion=5;
		else if(index==3) this.resultion=10;
		else if(index==4) this.resultion=20;
		else if(index==5) this.resultion=50;
		else if(index==6) this.resultion=100;
	}
	
	public byte getDignum() {
		return dignum;
	}
	public void setDignum(byte dignum) {
		this.dignum = dignum;
	}
	public byte getUnit() {
		return unit;
	}
	public void setUnit(byte unit) {
		this.unit = unit;
	}
	public static byte[] intToByte(int number) {
		byte[] abyte = new byte[4];
		// "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
		abyte[0] = (byte) (0xff & number);
		// ">>"右移位，若为正数则高位补0，若为负数则高位补1
		abyte[1] = (byte) ((0xff00 & number) >> 8);
		abyte[2] = (byte) ((0xff0000 & number) >> 16);
		abyte[3] = (byte) ((0xff000000 & number) >> 24);
		return abyte;
	}
	public static int bytesToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;
		// "|="按位或赋值。
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[2] << 16) & 0xFF0000);
		number |= ((bytes[3] << 24) & 0xFF000000);
		
		return number;
	}
	@Override
	public String toString() {
		return "ScalerParam [mtd=" + mtd + ", nov=" + nov + ", zerotrack="
				+ zerotrack + ", pwr_zerotrack=" + pwr_zerotrack
				+ ", resultion=" + resultion + ", dignum=" + dignum + ", unit="
				+ unit + ", mdtstr=" + mdtstr + "]";
	}
	
	public boolean parseParaBuffer(byte[] buffer)
	{
		
		return true;
	}
}
