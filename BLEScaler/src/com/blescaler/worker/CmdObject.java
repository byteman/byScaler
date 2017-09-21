package com.blescaler.worker;

public class CmdObject {
	CmdObject(byte[] buffer)
	{
		reg_addr = buffer[3];
		value=buffer;
		send_time = System.currentTimeMillis();
		removed = false;
		count = 0;
	}
	void reset()
	{
		//send_time = System.currentTimeMillis();
		count = 0;
	}
	boolean needRemove()
	{
		long curr = System.currentTimeMillis();
		long diff = curr -send_time;
		if(diff  > 3000)
		{
			return true;
		}
		return false;
	}
	boolean isTimeout()
	{
		count++;
		if(count >= 4)
		{
			return true;
		}
		return false;

	}
	int reg_addr;
	byte[] value;
	long send_time;
	boolean removed;
	int count;
}
