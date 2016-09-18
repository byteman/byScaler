package com.blescaler.worker;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ScalerParam {
	
	private byte mtd=0;	//绋冲畾绾у埆
	private int nov=10000;	//閲忕▼.
	private byte zerotrack=0;	//闆剁偣璺熻釜閫熷害
	private byte pwr_zerotrack=0; //寮?鏈虹疆闆惰寖鍥?
	private byte resultion=1; //鍒嗗害鏁?
	private byte dignum=0;//灏忔暟鐐逛綅鏁?
	
	private String unit="g"; //鍗曚綅
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
	public ScalerParam()
	{
		
	}
	public ScalerParam(int nov, byte mtd, byte zt,byte pzt,byte dig, byte res, String unit)
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
	public byte getResultion() {
		return resultion;
	}
	public void setResultion(byte resultion) {
		this.resultion = resultion;
	}
	public byte getDignum() {
		return dignum;
	}
	public void setDignum(byte dignum) {
		this.dignum = dignum;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
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
	public byte[] getSetCmdBuffer()
	{
		byte[] send = new byte[15] ; //最多只能发生15个字符.
		Arrays.fill(send, (byte) 0); //清零
		byte[] nov_arr = intToByte(this.nov);
		send[0] = 'P';
		send[1] = 'A';
		send[2] = 'R';
		send[3] = ':';
		
		System.arraycopy(nov_arr, 0, send, 4, 4); //鎷疯礉nov
		byte tmp = (byte) ((this.mtd)|(this.zerotrack<<3));
		send[8] = tmp;//this.mtd;
		tmp = (byte) ((this.pwr_zerotrack)|(this.resultion<<3));
		send[9] = tmp;

		send[10] = this.dignum;
		int len = this.unit.length();
		if(len > 3 ) len = 3;
		System.arraycopy(this.unit.getBytes(), 0, send, 11, len); //最多复制3个字节 的unit.
		send[14] = ';';
		return send;
		
	}
	public boolean checkValid()
	{
		if(this.nov==0)this.nov=1000000;
		if(this.mtd > mtdmaps.size() || this.mtd<0) this.mtd = 0;
		if(this.zerotrack > zerotrackmaps.size() || this.zerotrack<0) this.zerotrack = 0;
		if(this.pwr_zerotrack > pwrzeromaps.size() || this.pwr_zerotrack<0) this.pwr_zerotrack = 0;
		if(this.resultion > resmaps.size() || this.resultion<0) this.resultion = 0;
		if(this.dignum > digmaps.size() || this.dignum<0) this.dignum = 0;
		
		return true;
	}
	public boolean parseParaBuffer(byte[] buffer)
	{
		if(buffer.length < 17) return false;
		if( (buffer[0] != 'P')||  (buffer[1] != 'A') ||  (buffer[2] != 'R') ||  (buffer[3] != '?'))
			return false;
		this.nov = bytesToInt(Arrays.copyOfRange(buffer,4,8));
		
		this.mtd = buffer[8];
		this.zerotrack = buffer[9];
		this.pwr_zerotrack = buffer[10];
		this.resultion = buffer[11];
		
		if(this.resultion < 1) this.resultion = 1;
		this.resultion--;
		this.dignum = buffer[12];
		String ut = new String(Arrays.copyOfRange(buffer,13,17));
		this.unit = ut;
		//System.arraycopy(buffer, 0, send, 13, this.unit.length()); 
		checkValid();
		
		return true;
	}
}
