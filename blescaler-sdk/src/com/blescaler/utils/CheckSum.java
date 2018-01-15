package com.blescaler.utils;
public class CheckSum {  
	public static byte calc(byte[] data, int offset, int length)
	{
		 byte sum = 0;
		 for(int i = offset; i < (offset+length); i++)
		 {
			 sum += data[i];
		 }
		 return sum;
	}
    public static boolean isValid(byte[] data)
	{
		if(data.length < 2) return false;
		byte result =calc(data,0,data.length-1);
		byte sum = data[data.length-1];
		return sum==result;
		
	}
}