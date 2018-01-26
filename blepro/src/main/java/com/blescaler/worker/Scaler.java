package com.blescaler.worker;

import android.os.Message;

import com.blescaler.util.CheckSum;
import com.blescaler.util.Utils;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;

public class Scaler {

	public ScalerParam para;
	private String address;
	private String name;
	private boolean connected = false; //是否已经连接
	private boolean discovered = false; //
	private float weight; //显示重量
	private String sWeight; //显示重量的字符串.

	public float getTare() {
		return tare;
	}

	public void setTare(float tare) {
		this.tare = tare;
	}
	public float getNet() {
		return net;
	}

	public void setNet(float net) {
		this.net = net;
	}
	private float tare; //皮重
	private float net; //净重
	private float calcWeight; //内部重量.

	public String getDispalyWeight() {
		return sWeight;
	}

	public void setDisplayWeight(String sWeight) {
		this.sWeight = sWeight;
	}

	public float getCalcWeight() {
		return calcWeight;
	}

	public void setCalcWeight(float calcWeight) {
		this.calcWeight = calcWeight;
	}




	private int loadValue; //
	private BleGattCharacteristic characteristic; //蓝牙称所关联的特征描述符.
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
	private boolean standstill = false; //
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

	public float getWeight() {
		return weight;
	}
	
	public void setWeight(float weight,int dot) {
		
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
		if(((st>>8)&1)!=0)
		{
			this.sleep_mode=false;
		}else{
			this.sleep_mode=true;
		}

		//9-11 单位 kg=0 g=1 lb=2 pcs=3 oz=4
		//11-13 小数点位数 0-7 bit
		int u = (st>>9)&7;

		switch (u){
			case 0:
				this.unit = "kg";
				break;
			case 1:
				this.unit = "g";
				break;
			case 2:
				this.unit = "lb";
				break;
			case 3:
				this.unit = "pcs";
				break;
			default:
				this.unit = "kg";
				break;
		}
		this.dot_num = (st>>11)&7;
	}
//	class Message{
//		int Addr;
//		int Type;
//		int DataSize;
//		int RegAddr;
//		byte[] Data;
//	};
	//
	public int parseData(byte[] val, Message msg) {
		int msgType = 0;
		if(msg == null) return msgType;
		
		
		if(val.length < 5) return msgType;
		msg.obj = this;
		//地址  (1bytes) [0x20 固定的0x20 也可以当同步头用]
		//类型  (1bytes) [0x03->读取,0x10->写入]
		//数据长度 (1bytes) 
		//寄存器首地址 (2bytes)
		//数据 (最大14bytes)
		//SUM (1byte)
		

		if ((val[0] == 0x20) ){
			//if( (val[2] + 5)!= val.length)  return msgType;
			if(!CheckSum.isValid(val))
			{
				//return msgType;
			}
			if(val[1] == Global.CMD_READ_REGS)
			{
				int 	reg_num  = (val[2]-2)/2;
				short 	reg_addr = Utils.bytesToShort(val,3);
				
				if(reg_addr == Global.REG_WEIGHT)
				{
					
					byte w[] = { 0, 0, 0, 0 };
					if(val.length <  15)
					{
						 return 0;
					}
					System.arraycopy(val, 5, w, 0, 4);
					
					parseState(Utils.bytesToShort(val,9));
					short dot = Utils.bytesToShort(val,11);
					setWeight(Utils.bytesToWeight(w),dot);
					this.dot_num = val[12];
					
					msgType = Global.MSG_BLE_WGTRESULT;

					
				}
				else if(reg_addr == Global.REG_DOTNUM )
				{					
					para.setDignum(val[6]);
					msg.arg1 = 0;				
				}
				else if(reg_addr == Global.REG_DIV1)
				{
					para.setResultionx(val[6]);
					
					para.setNov(Utils.bytesToInt(val,9));
					
					
				}
				else if(reg_addr == Global.REG_UNIT)
				{
					if(val.length < 17) return 0;
					para.setUnit(val[6]);
					para.setPwr_zerotrack(val[8]);
					para.setHand_zerotrack(val[10]);
					para.setZerotrack(val[12]);
					para.setMtd(val[14]);
					para.setFilter(val[16]);
					
				}
				
				else if(reg_addr == Global.REG_SENSOR_DIFF_K1)
				{
					
					allks[0]=(float)Utils.bytesToInt(val, 5)/1000.0f;
					allks[1]=(float)Utils.bytesToInt(val, 9)/1000.0f;
					msgType = Global.MSG_SCALER_K_QUERY_RESULT;
					msg.arg1 = 1;
				}
				else if(reg_addr == Global.REG_SENSOR_DIFF_K3)
				{
					
					allks[2]=(float)Utils.bytesToInt(val, 5)/1000.0f;
					allks[3]=(float)Utils.bytesToInt(val, 9)/1000.0f;
					msgType = Global.MSG_SCALER_K_QUERY_RESULT;
					msg.arg1 = 2;
				}
				else if(reg_addr == Global.REG_AD_CHAN1)
				{
					
					msg.arg1 = Utils.bytesToInt(val, 5);
					msg.arg2 = Utils.bytesToInt(val, 9);
					
					msgType = Global.MSG_SCALER_AD_CHAN1_RESULT;
					
				}
				else if(reg_addr == Global.REG_AD_CHAN3)
				{
					
					msg.arg1 = Utils.bytesToInt(val, 5);
					msg.arg2 = Utils.bytesToInt(val, 9);
					
					msgType = Global.MSG_SCALER_AD_CHAN2_RESULT;
					
				}
				else if(reg_addr == Global.REG_BATTERY)
				{
					msgType = Global.MSG_SCALER_POWER_RESULT;
					
					msg.arg1 = Utils.bytesToShort(val,5);
				}
				else if(reg_addr == Global.REG_SLEEP_S)
				{
					
					para.setSleep(Utils.bytesToShort(val,5));
					para.setSnr_num(Utils.bytesToShort(val,7));
					msgType = Global.MSG_SCALER_PAR_GET_RESULT;
					msg.arg1 = 0;			
				}
				else if(reg_addr == Global.REG_WEIGHT_V2)
				{
					//蓝牙演示版本数据格式 内部重量+显示重量+皮重

					if(val.length !=  20)
					{
						return 0;
					}
					parseState(Utils.bytesToShort(val,17));
					this.calcWeight = Utils.Int2Float(Utils.bytesToInt(val, 5), this.dot_num);

					this.tare = Utils.Int2Float(Utils.bytesToInt(val, 13), this.dot_num);

					int iWeight = Utils.bytesToInt(val, 9);
					this.weight = Utils.Int2Float(iWeight, this.dot_num);
					this.sWeight = Utils.FormatIntValue(iWeight, this.dot_num);

					msgType = Global.MSG_BLE_WGTRESULT_V2;

				}
			}
			else if(val[1] == Global.CMD_WRITE_REGS)
			{

				int reg_addr = Utils.bytesToShort(val,2);
				if(reg_addr == Global.REG_SLEEP_S)
				{
					msgType = Global.MSG_SCALER_PAR_SET_RESULT;
					msg.arg1 = 0;		
				}
				else if(reg_addr == Global.REG_CALIB_INDEX)
				{
					msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
					msg.arg1 = 0;
				}
				else if(reg_addr == Global.REG_AUTO_DIFF_CALIB_INDEX)
				{
					msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
					msg.arg1 = 0;
				}
		
				else if(reg_addr == Global.REG_LAMP_CTRL)
				{
					msgType = Global.MSG_SCALER_CTRL_RESULT;
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
