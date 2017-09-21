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
	private boolean zero = false;
	private boolean standstill = false; //重量稳定
	private boolean net_overflow = false; //Tare value too high
	private boolean gross_overflow = false; //Scaling too sensitive
	private boolean ad_overflow = false; //ADC overflow 
	private boolean ng_state = false;
	public float[] allks = new float[4];
	public boolean isStandstill() {
		return standstill;
	}
	public boolean isGross() {
		return ng_state;
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
	private void parseState(byte st)
	{
				
		this.standstill = ((st&0x1)!=0)?true:false;
		this.zero = ((st&0x2)!=0)?true:false;
		this.ng_state = ((st&0x4)!=0)?true:false;
		
	}
	//
	public int parseData(byte[] val, Message msg) {
		int msgType = 0;
		if(msg == null) return msgType;
		
		msg.obj = this;
		//地址  0x20 (1bytes)
		//类型 0x03 (1bytes) 读取 (0x10 写入)
		//数据长度 (1bytes) 
		//寄存器首地址 (2bytes)
		//数据 
		//CRC16
		
		if ((val[0] == 0x20) ){
			if(val[1] == 0x03)
			{
				int reg_num = (val[2]-2)/2;
				int reg_addr = (val[3]<<8)+val[4];
				
				if(reg_addr == 0x0)
				{
					byte w[] = { 0, 0, 0, 0 };
					if(val.length <  10)
					{
						 return 0;
					}
					System.arraycopy(val, 5, w, 0, 4);
					
					parseState(val[10]);
					short dot = (short) ((val[11]<<8)+val[12]);
					setWeight(Utils.bytesToWeight(w),dot);
					
					msgType = Global.MSG_BLE_WGTRESULT;
					msg.arg1 = weight;
					
				}
				else if(reg_addr == 0x3 )
				{					
					para.setDignum(val[6]);
					msg.arg1 = 0;				
				}
				else if(reg_addr == 8)
				{
					para.setResultionx(val[6]);
					
					para.setNov(Utils.bytesToInt(val,9));
					
					
				}
				else if(reg_addr == 14)
				{
					para.setUnit(val[6]);
					para.setPwr_zerotrack(val[8]);
					para.setHand_zerotrack(val[10]);
					para.setZerotrack(val[12]);
					para.setMtd(val[14]);
					para.setFilter(val[16]);
					msgType = Global.MSG_SCALER_PAR_GET_RESULT;
					msg.arg1 = 0;			
				}
				
				else if(reg_addr == 36)
				{
					allks[0]=(float)Utils.bytesToInt(val, 5)/1000.0f;
					allks[1]=(float)Utils.bytesToInt(val, 9)/1000.0f;
					msgType = Global.MSG_SCALER_K_QUERY_RESULT;
					msg.arg1 = 1;
				}
				else if(reg_addr == 40)
				{
					
					allks[2]=(float)Utils.bytesToInt(val, 5)/1000.0f;
					allks[3]=(float)Utils.bytesToInt(val, 9)/1000.0f;
					msgType = Global.MSG_SCALER_K_QUERY_RESULT;
					msg.arg1 = 2;
				}
				else if(reg_addr == 46)
				{
					msgType = Global.MSG_SCALER_POWER_RESULT;
					msg.arg1 = (val[5]<<8)+val[6];
				}
			}
			else if(val[1] == 0x10)
			{
				//写入的通知.
				int reg_addr = (val[2]<<8)+val[3];
				if(reg_addr == 0x8)
				{
					msgType = Global.MSG_SCALER_PAR_SET_RESULT;
					msg.arg1 = 0;		
				}
				else if(reg_addr == 20)
				{
					msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
					msg.arg1 = 0;
				}
				else if(reg_addr == 44)
				{
					msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
					msg.arg1 = 0;
				}
				else if(reg_addr == 47)
				{
					msgType = Global.MSG_SCALER_CTRL_RESULT;
					msg.arg1 = 0;
				}
			}
			else if(val[1] == 0x83)
			{
				
			}
		}
//		
//		 else if ((val[0] == 'P') && (val[1] == 'A') && (val[2] == 'R')) {
//
//			if (val[3] == '?') // 参数读取的返回值.
//			{
//
//				int ret = para.parseParaBuffer(val) ? 0 : 1;
//				msgType = Global.MSG_SCALER_PAR_GET_RESULT;
//				msg.arg1 = ret;				
//
//			} 
//			else if (val[3] == ':') // 参数设置的返回值.
//			{
//				msgType = Global.MSG_SCALER_PAR_SET_RESULT;
//				msg.arg1 = val[4] - '0';
//				
//			}
//		} else if ((val[0] == 'C') && (val[1] == 'L') && (val[2] == 'Z')) {
//
//			if (val[3] == '?') // 参数读取的返回值.
//			{
//
//				int ret = 0;
//
//				try {
//					int zero = Utils.bytesToString(val, 4, val.length);
//					this.zeroValue = zero;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					ret = 1;
//					e.printStackTrace();
//				}
//				msgType = Global.MSG_SCALER_ZERO_QUERY_RESULT;
//				msg.arg1 = ret;
//
//			} 
//			else if (val[3] == ':') // 参数设置的返回值.
//			{
//
//				if (val[4] == '0') {
//					try {
//						int zero = Utils.bytesToString(val, 6, val.length);
//						this.zeroValue = zero;
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//				msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
//				msg.arg1 = val[4] - '0';
//
//			}
//
//		}
//
//		else if ((val[0] == 'C') && (val[1] == 'L') && (val[2] == 'K')) {
//
//			if (val[3] == '?') // 参数读取的返回值.
//			{
//
//				int ret = 0;
//
//				try {
//				
//					this.loadValue = Utils.bytesToString(val, 4, val.length);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					ret = 1;
//					e.printStackTrace();
//				}
//				msgType = Global.MSG_SCALER_K_QUERY_RESULT;
//
//				msg.obj = this;
//				msg.arg1 = ret;
//
//			} 
//			else if (val[3] == ':') // 参数设置的返回值.
//			{
//
//				if (val[4] == '0') {
//					try {
//						this.loadValue = Utils.bytesToString(val, 6, val.length);
//				
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//				msgType = Global.MSG_SCALER_K_CALIB_RESULT;
//
//				msg.arg1 = val[4] - '0';
//
//			}
//
//		} else if ((val[0] == 'S') && (val[1] == 'A') && (val[2] == 'V')) {
//			if (val[3] == ':') {
//				msgType = Global.MSG_SCALER_SAVE_EEPROM;
//
//				msg.arg1 = val[4] - '0';
//
//			}
//		}

		return msgType;
	}
}
