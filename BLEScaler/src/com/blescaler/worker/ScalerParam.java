package com.blescaler.worker;

import android.text.format.Time;


public class ScalerParam {
	
	public int hostip;
	public int hostport;
	public short dev_id;
	public short heart;
	public short channel;
	public short send_time_s;
	public short acquire_s;
	public short write_index,read_index;
	public short version;
	public short year_month,day_hour,min_second;
	
	public ScalerParam()
	{
		
	}
	public String GetHostString()
	{
		String ip = String.format("%d.%d.%d.%d", (byte) ((0xff000000 & hostip) >> 24), (byte) ((0xff0000 & hostip) >> 16), (byte) ((0xff00 & hostip) >> 8),(byte) (0xff & hostip)); 
		
		return ip;
			
	}
	public String GetTimeString()
	{
		String time = String.format("%d-%d-%d.%d",  2000 + ((0xff00 & year_month) >> 8),(byte) (0xff & year_month),((0xff00 & day_hour) >> 8),(byte) (0xff & day_hour),((0xff00 & min_second) >> 8),(byte) (0xff & min_second)); 
		
		return time;
	}
	public boolean SetNowTime()
	{
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
		t.setToNow();
		year_month = (short) (((t.year-2000)>>8)+t.month);  
		day_hour = (short) ((t.monthDay>>8)+t.hour);  
		min_second=(short) ((t.minute>>8)+t.second); 
		return true;
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
		return "";
	}
	
	public boolean checkValid()
	{
		
		return true;
	}
	public boolean parseParaBuffer(byte[] buffer)
	{
		
		return true;
	}
}
