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
	private boolean standstill = false; //重量稳定
	private boolean net_overflow = false; //Tare value too high
	private boolean gross_overflow = false; //Scaling too sensitive
	private boolean ad_overflow = false; //ADC overflow 
	
	public boolean isStandstill() {
		return standstill;
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
	public int getRx_cnt() {
		return rx_cnt;
	}
	public void dump_info()
	{
		Log.e("weight",address+" rx " + rx_cnt);
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

	public int getZeroValue() {
		return zeroValue;
	}

	public void setZeroValue(int zeroValue) {
		this.zeroValue = zeroValue;
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
	
	public void setWeight(int weight) {
		
		waitTime  = (System.currentTimeMillis() - waitTime);  
		this.rx_cnt++;
		Log.e("scaler",address+" wait="+waitTime);
		waitTime = System.currentTimeMillis();
		this.weight = weight;
	}
	private void parseState(byte st)
	{
		this.net_overflow = ((st&0x1)!=0)?true:false;
		this.gross_overflow = ((st&0x2)!=0)?true:false;
		this.ad_overflow = ((st&0x4)!=0)?true:false;
		this.standstill = ((st&0x8)!=0)?true:false;
		
	}
	//
	public int parseData(byte[] val, Message msg) {
		int msgType = 0;
		if(msg == null) return msgType;
		
		msg.obj = this;
		if ((val[0] == 'A') && (val[1] == 'D') && (val[2] == 'V')) {

			if (val[3] == ':') {
				if (val.length < 8)
					return 0;
				byte w[] = { 0, 0, 0, 0 };
				System.arraycopy(val, 4, w, 0, 4);

				parseState(val[7]);
				setWeight(Utils.bytesToWeight(w));
				
				msgType = Global.MSG_BLE_WGTRESULT;
				msg.arg1 = weightVlaue;
				
			}

		} else if ((val[0] == 'P') && (val[1] == 'A') && (val[2] == 'R')) {

			if (val[3] == '?') // 参数读取的返回值.
			{

				int ret = para.parseParaBuffer(val) ? 0 : 1;
				msgType = Global.MSG_SCALER_PAR_GET_RESULT;
				msg.arg1 = ret;				

			} 
			else if (val[3] == ':') // 参数设置的返回值.
			{
				msgType = Global.MSG_SCALER_PAR_SET_RESULT;
				msg.arg1 = val[4] - '0';
				
			}
		} else if ((val[0] == 'C') && (val[1] == 'L') && (val[2] == 'Z')) {

			if (val[3] == '?') // 参数读取的返回值.
			{

				int ret = 0;

				try {
					int zero = Utils.bytesToString(val, 4, val.length);
					this.zeroValue = zero;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					ret = 1;
					e.printStackTrace();
				}
				msgType = Global.MSG_SCALER_ZERO_QUERY_RESULT;
				msg.arg1 = ret;

			} 
			else if (val[3] == ':') // 参数设置的返回值.
			{

				if (val[4] == '0') {
					try {
						int zero = Utils.bytesToString(val, 6, val.length);
						this.zeroValue = zero;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				msgType = Global.MSG_SCALER_ZERO_CALIB_RESULT;
				msg.arg1 = val[4] - '0';

			}

		}

		else if ((val[0] == 'C') && (val[1] == 'L') && (val[2] == 'K')) {

			if (val[3] == '?') // 参数读取的返回值.
			{

				int ret = 0;

				try {
				
					this.loadValue = Utils.bytesToString(val, 4, val.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					ret = 1;
					e.printStackTrace();
				}
				msgType = Global.MSG_SCALER_K_QUERY_RESULT;

				msg.obj = this;
				msg.arg1 = ret;

			} 
			else if (val[3] == ':') // 参数设置的返回值.
			{

				if (val[4] == '0') {
					try {
						this.loadValue = Utils.bytesToString(val, 6, val.length);
				
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				msgType = Global.MSG_SCALER_K_CALIB_RESULT;

				msg.arg1 = val[4] - '0';

			}

		} else if ((val[0] == 'S') && (val[1] == 'A') && (val[2] == 'V')) {
			if (val[3] == ':') {
				msgType = Global.MSG_SCALER_SAVE_EEPROM;

				msg.arg1 = val[4] - '0';

			}
		}

		return msgType;
	}
}
