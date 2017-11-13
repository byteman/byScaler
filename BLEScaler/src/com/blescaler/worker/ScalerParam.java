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
	public short rain;
	public ScalerParam()
	{
		
	}
	public int ipToLong(String ipAddress) {  
		  
		int result = 0;  
		  
		String[] ipAddressInArray = ipAddress.split("\\.");  
		  
		for (int i = 3; i >= 0; i--) {  
		  
		    long ip = Long.parseLong(ipAddressInArray[3 - i]);  
		  
		    //left shifting 24,16,8,0 and bitwise OR  
		  
		    //1. 192 << 24  
		    //1. 168 << 16  
		    //1. 1   << 8  
		    //1. 2   << 0  
		    result |= ip << (i * 8);  
		  
		}  
		return result;  
	} 
	public boolean SetHostString(String ip)
	{
		hostip=ipToLong(ip);
		return true;
	}
	public String longToIp2(int ip) {  
		  
		return ((ip >> 24) & 0xFF) + "."   
		    + ((ip >> 16) & 0xFF) + "."   
		    + ((ip >> 8) & 0xFF) + "."   
		    + (ip & 0xFF);  
		 }  
	public String GetHostString()
	{
		String ip = longToIp2(hostip);
		return ip;
			
	}
	public String GetTimeString()
	{
		String time = String.format("%d-%d-%d %d:%d:%d",  2000 + ((0xff00 & year_month) >> 8),(byte) (0xff & year_month),((0xff00 & day_hour) >> 8),(byte) (0xff & day_hour),((0xff00 & min_second) >> 8),(byte) (0xff & min_second)); 
		
		return time;
	}
	public static short bytesToShort(byte[] bytes,int index){
		int number = bytes[index+1] & 0xFF;
		// "|="按位或赋值。
		number |= ((bytes[index] << 8) & 0xFF00);

		//int number = (bytes[index+2] <<24)+ (bytes[index+3] <<16)+(bytes[index]<<8)+bytes[index+1];
		return (short) number;
	}
	public boolean SetNowTime()
	{
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
		t.setToNow();
	
		year_month = (short) (((t.year-2000)<<8)+t.month+1);  
		day_hour = (short) ((t.monthDay<<8)+t.hour);  
		min_second=(short) ((t.minute<<8)+t.second); 
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
