package com.blescaler.utils;

import java.nio.ByteBuffer;

public class Register {
	private ByteBuffer buffer = ByteBuffer.allocate(256);
	private short reg_num = 0;
    static byte[] crc16_tab_h = { (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,  
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,  
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,  
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,  
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40 };  
  
    static byte[] crc16_tab_l = { (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC, (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,  
            (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,  
            (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0, (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,  
            (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54, (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,  
            (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40 };  
  
    /** 
     * 锟斤拷锟斤拷CRC16校锟斤拷 
     *  
     * @param data 
     *            锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟� 
     * @return CRC16校锟斤拷值 
     */  
    public static int calcCrc16(byte[] data) {  
        return calcCrc16(data, 0, data.length);  
    }  
  
    /** 
     * 锟斤拷锟斤拷CRC16校锟斤拷 
     *  
     * @param data 
     *            锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟� 
     * @param offset 
     *            锟斤拷始位锟斤拷 
     * @param len 
     *            锟斤拷锟斤拷 
     * @return CRC16校锟斤拷值 
     */  
    public static int calcCrc16(byte[] data, int offset, int len) {  
        return calcCrc16(data, offset, len, 0xffff);  
    }  
  
    /** 
     * 锟斤拷锟斤拷CRC16校锟斤拷 
     *  
     * @param data 
     *            锟斤拷要锟斤拷锟斤拷锟斤拷锟斤拷锟� 
     * @param offset 
     *            锟斤拷始位锟斤拷 
     * @param len 
     *            锟斤拷锟斤拷 
     * @param preval 
     *            之前锟斤拷校锟斤拷值 
     * @return CRC16校锟斤拷值 
     */  
    public static int calcCrc16(byte[] data, int offset, int len, int preval) {  
        int ucCRCHi = (preval & 0xff00) >> 8;  
        int ucCRCLo = preval & 0x00ff;  
        int iIndex;  
        for (int i = 0; i < len; ++i) {  
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;  
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];  
            ucCRCHi = crc16_tab_l[iIndex];  
        }  
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;  
    }  
	public  Register()
	{

	}
	public  void BeginWrite(int reg_addr)
	{
		///锟借备锟斤拷址 1byte
		//锟斤拷锟斤拷锟斤拷锟斤拷 0x10
		//锟斤拷始锟侥达拷锟斤拷锟斤拷址 reg_addr 2bytes
		//锟侥达拷锟斤拷锟斤拷锟斤拷 2bytes(锟斤拷要写锟斤拷募拇锟斤拷锟斤拷锟斤拷锟�)
		//锟斤拷锟斤拷锟街斤拷锟斤拷 1byte (2*N)
		//锟侥达拷锟斤拷值 (2*N)锟街斤拷.
		//crc16
		reg_num = 0;
		short u_reg_addr = (short)reg_addr;
		short u_reg_num  = (short)reg_num;
		
		byte header[]={0x20,0x10,(byte)((u_reg_addr>>8)&0xff),(byte)(u_reg_addr&0xFF),(byte)((u_reg_num>>8)&0xff),(byte)(u_reg_num&0xFF),0};
		buffer.clear();	
		buffer.put(header);

	}
	public  byte[] BeginRead(int reg_addr,int reg_num)
	{
		//锟借备锟斤拷址 1byte
		//锟斤拷锟斤拷锟斤拷锟斤拷 0x3
		//锟斤拷始锟侥达拷锟斤拷锟斤拷址 reg_addr2 bytes
		//锟侥达拷锟斤拷锟斤拷锟斤拷 2bytes(锟斤拷要锟斤拷取锟侥寄达拷锟斤拷锟斤拷锟斤拷)
		//crc16
		
		short u_reg_addr = (short)reg_addr;
		short u_reg_num  = (short)reg_num;
		byte out[]={0x20,0x3,(byte)((u_reg_addr>>8)&0xff),(byte)(u_reg_addr&0xFF),(byte)((u_reg_num>>8)&0xff),(byte)(u_reg_num&0xFF),0,0};
		//byte buffer[]={0x20,0x3,0,0x20,0,1,(byte) 0x83,0x71};
		short crc16 = (short)calcCrc16(out,0,out.length-2);
		out[out.length-1] = (byte)(crc16>>8&0xFF);
		out[out.length-2] = (byte)((crc16)&0xff);
		
		return out;

	}
	public boolean putInt(int value)
	{
		buffer.put((byte)((value>>8)&0xff));
		buffer.put((byte)((value)&0xff));
		buffer.put((byte)((value>>24)&0xff));
		buffer.put((byte)((value>>16)&0xff));
		reg_num+=2;
		return true;
	}
	public boolean putInts(int... values)
	{
		for(int i = 0; i < values.length; i++)
		{
			putInt(values[i]);
		}
		return true;
	}
	public boolean putShort(short value)
	{
		buffer.put((byte)((value>>8)&0xff));
		buffer.put((byte)((value)&0xff));
		reg_num+=1;
		return true;
	}
	public boolean putShorts(short... values)
	{
		for(int i = 0; i < values.length; i++)
		{
			putShort(values[i]);
		}
		return true;
	}
	public byte[] getResult()
	{
		int length = buffer.position();
		
		byte[] out  = new byte[length+2];
		byte[] data = buffer.array();
		System.arraycopy(data, 0, out , 0, length);
		out[4] = (byte)((reg_num>>8)&0xff);
		out[5] = (byte)((reg_num)&0xff);
		out[6] = (byte)(reg_num*2);
		short crc16 = (short)calcCrc16(out,0,length);
		out[length+1] = (byte)((crc16>>8)&0xff);
		out[length+0] = (byte)(crc16&0xFF);
		
		return out;
		
	}
	
}
