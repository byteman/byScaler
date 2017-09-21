package com.blescaler.utils;

import java.nio.ByteBuffer;

import com.blescaler.worker.CRC16;

public class modbus {
	
	public static byte[]  begin_write_registers(short reg_addr, int reg_num)
	{
		///设备地址 1byte
		//命令类型 0x10
		//起始寄存器地址 reg_addr
		//寄存器数量 2bytes(需要写入的寄存器数量)
		//数据字节数 1byte (2*N)
		//寄存器值 (2*N)字节.
		//crc16
		short u_reg_addr = (short)reg_addr;
		short u_reg_num  = (short)reg_num;
		
		//byte buffer[]={0x20,0x10,(byte)((u_reg_addr>>8)&0xff),(byte)(u_reg_addr&0xFF),(byte)((u_reg_num>>8)&0xff),(byte)(u_reg_num&0xFF),0,0};
		
		byte[] buffer = new byte[9+reg_num*2];
		
		ByteBuffer buff = ByteBuffer.allocate(100);
		buff.putInt(12);
		
		return buffer;
				
	}
	public static int  write_short(byte[] buffer,int offset,short value)
	{
		return 0;	
	}
	public static int  write_int(byte[] buffer,int offset,int value)
	{
		return 0;	
	}
	
}
