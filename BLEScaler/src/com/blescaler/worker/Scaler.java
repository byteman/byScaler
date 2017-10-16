package com.blescaler.worker;

import android.os.Message;
import android.util.Log;

import com.blescaler.utils.Utils;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;

public class Scaler {

	public ScalerParam para;
	private String address;
	private String name;
	private boolean connected = false; // 地址已经连接
	private boolean discovered = false; // 服务已经被发现
	private int weight; //保存最后一次采集的重量
	private int zeroValue; //保存上次标定的零点值
	private int weightVlaue; //保存上次的砝码重量
	private int loadValue; //保存上次砝码标定时的ad值 
	private BleGattCharacteristic characteristic;
	private int rx_cnt = 0;
	private long waitTime = 0;
	private String unit = "kg";
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	private int dot_num = 0;
	private boolean zero = false;
	private boolean standstill = false; //重量稳定
	private boolean net_overflow = false; //Tare value too high
	private boolean gross_overflow = false; //Scaling too sensitive
	private boolean ad_overflow = false; //ADC overflow 
	private boolean ng_state = false;
	private boolean sleep_mode = false;
	public boolean isSleep_mode() {
		return sleep_mode;
	}
	public void setSleep_mode(boolean sleep_mode) {
		this.sleep_mode = sleep_mode;
	}
	public float[] allks = new float[4];
	public float[] all_zx = new float[6];
	public float[] all_wd = new float[6];
	public boolean isStandstill() {
		return standstill;
	}
	public boolean isGross() {
		return ng_state;
	}
	public int GetDotNum()
	{
		return dot_num;
	}
	public void setStandstill(boolean standstill) {
		this.standstill = standstill;
	}
	public boolean isNet_overflow() {
		return net_overflow;
	}
	public void setNet_overflow(boolean net_overflow) {
		this.net_overflow = net_overflow;
	}
	public boolean isGross_overflow() {
		return gross_overflow;
	}
	public void setGross_overflow(boolean gross_overflow) {
		this.gross_overflow = gross_overflow;
	}
	public boolean isAd_overflow() {
		return ad_overflow;
	}
	public void setAd_overflow(boolean ad_overflow) {
		this.ad_overflow = ad_overflow;
	}
	public boolean isZero() {
		return zero;
	}
	
	public void setZero(boolean zero) {
		this.zero = zero;
	}
	public int getRx_cnt() {
		return rx_cnt;
	}
	public void dump_info()
	{
		//Log.e("weight",address+" rx " + rx_cnt);
	}
	public void setRx_cnt(int rx_cnt) {
		this.rx_cnt = rx_cnt;
	}

	public boolean isDiscovered() {
		return discovered;
	}

	public void setDiscovered(boolean discovered) {
		this.discovered = discovered;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public int getLoadValue() {
		return loadValue;
	}

	public void setLoadValue(int loadValue) {
		this.loadValue = loadValue;
	}

	private void init() {
		this.address = "";
		this.connected = false;
		this.weight = 0;
		para = new ScalerParam();
	}

	public BleGattCharacteristic GetBleChar() {
		return characteristic;
	}

	public void SetBleChar(BleGattCharacteristic ble_char) {
		characteristic = ble_char;
	}

	public Scaler() {
		init();
	}

	public Scaler(String addr) {
		init();
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

	public void setConnected(boolean connected, BleGattCharacteristic ble_char) {
		characteristic = ble_char;
		this.connected = connected;
	}

	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight,int dot) {
		
		waitTime  = (System.currentTimeMillis() - waitTime);  
		this.rx_cnt++;
		//Log.e("scaler",address+" wait="+waitTime);
		waitTime = System.currentTimeMillis();
		//if(Math.abs(weight-this.weight)< 1000)
		{
			this.weight = weight;
		}
		
	}
	public void setDot(int num)
	{
		
	}
	private void parseState(short st)
	{
				
		this.standstill = ((st&0x1)!=0)?true:false;
		this.zero = ((st&0x2)!=0)?true:false;
		this.ng_state = ((st&0x4)!=0)?true:false;
		if(((st>>9)&1)!=0)
		{
			this.unit="kg";
		}
		else if(((st>>10)&1)!=0)
		{
			this.unit="g";
		}
		else if(((st>>11)&1)!=0)
		{
			this.unit="lb";
		}
		if(((st>>8)&1)!=0)
		{
			this.sleep_mode=false;
		}else{
			this.sleep_mode=true;
		}
	}
	//
	public int parseData(byte[] val, Message msg) {
		int msgType = 0;
		if(msg == null) return msgType;
		if(val.length < 5) return msgType;
		msg.obj = this;
		//地址  0x20 (1bytes)
		//类型 0x03 (1bytes) 读取 (0x10 写入)
		//数据长度 (1bytes) 
		//寄存器首地址 (2bytes)
		//数据 
		//CRC16
		
		if ((val[0] == 0x20) ){
			if( (val[2] + 5)!= val.length)  return msgType;
			if(val[1] == 0x03)
			{
				int reg_num = (val[2]-2)/2;
				int reg_addr = Utils.bytesToShort(val,3);
				
				switch(reg_addr)
				{
					case Global.REG_DEV_VERSION:
					{
						if(val.length < 9) return 0;
						para.version = Utils.bytesToShort(val,5);
						para.dev_id  = Utils.bytesToShort(val,7);
						msgType = Global.MSG_GET_PARAM1_RESULT;
						msg.arg1 = 0;	
						break;
					}
					case Global.REG_CHAN1_ZX:
					case Global.REG_CHAN2_ZX:
					case Global.REG_CHAN3_ZX:
					case Global.REG_CHAN4_ZX:
					case Global.REG_CHAN5_ZX:
					case Global.REG_CHAN6_ZX:
					{
						msgType = Global.MSG_GET_CHANNELS_RESULT;
						int index = (reg_addr - Global.REG_CHAN1_ZX)/4;
						all_zx[index] = (float)Utils.bytesToInt(val, 5)/(float)100.0f;
						all_wd[index] = (float)Utils.bytesToInt(val, 9)/(float)100.0f;				
						msg.arg1 = index;

						break;
					}
					case Global.REG_HOST_IP:
					{
						if(val.length < 13) return 0;
						para.hostip = Utils.bytesToInt(val, 5);					
						para.hostport = Utils.bytesToInt(val, 9);					
						msgType = Global.MSG_GET_PARAM2_RESULT;
						break;
					}
					case Global.REG_SEND_TIME:
					{
						para.send_time_s = Utils.bytesToShort(val,5);
						para.heart = Utils.bytesToShort(val,7);
						para.channel = Utils.bytesToShort(val,9);
						para.acquire_s = Utils.bytesToShort(val,11);
						msgType = Global.MSG_GET_PARAM3_RESULT;
						break;
					}
					case Global.REG_WRITE_INDEX:
					{
						para.write_index = Utils.bytesToShort(val,5);
						para.read_index = Utils.bytesToShort(val,7);
						msgType = Global.MSG_GET_PARAM4_RESULT;
						msg.arg1 = 1;
						break;
					}
					case Global.REG_TIME:
					{
						para.year_month = Utils.bytesToShort(val,5);
						para.day_hour = Utils.bytesToShort(val,7);
						para.min_second = Utils.bytesToShort(val,9);
						
						msgType = Global.MSG_GET_PARAM5_RESULT;
						msg.arg1 = Utils.bytesToShort(val,5);
						break;
					}
					case Global.REG_GPRS_SIGNAL:
					{
						msgType = Global.MSG_GET_GPRS_SIGNAL_RESULT;
						msg.arg1 = 2;
						break;
					}
					case Global.REG_DEV_STATUS:
					{
						msgType = Global.MSG_GET_DEV_STATUS_RESULT;
						msg.arg1 = Utils.bytesToShort(val,5);
						break;
					}
					
					
					default:
					{
						break;
					}
					
				}

			}
			else if(val[1] == 0x10)
			{
				//写入的通知.
				int reg_addr = Utils.bytesToShort(val,2);
				if(reg_addr == Global.REG_SEND_TIME)
				{
					msgType = Global.MSG_SCALER_PAR_SET_RESULT;
					msg.arg1 = 0;		
				}
				else if(reg_addr == Global.REG_DEV_ID)
				{
					msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
					msg.arg1 = 0;
				}
			}
			else if(val[1] == 0x83)
			{
				
			}
		}


		return msgType;
	}
}
