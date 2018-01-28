package com.blescaler.ui.ble;


public class Constant {
	//preferences
	public static final String PREFKEY_BLUETOOTH_COUNT= "bluetooth_count";
	public static final String PREFKEY_PRINTERBLUETOOTH= "printer_bluetooth";
	
	//
    public static final String INTENTKEY_SETTING_POS= "setting_pos";
    
    //保存的蓝牙列表
    public  static String[] blueTooths;
    
    //保存的蓝牙列表
    public  static String printerBlueTooth;
    public static final int MSG_TIMEOUT = 0x0001;
    public static final int MSG_SET_ZERO = 0x0002;
    public static final int MSG_SWITCH_UNIT = 0x0003;
    public static final int MSG_DISCARD_TARE = 0x0004;
    public static final int MSG_SWITCH_NG = 0x0005;

}
