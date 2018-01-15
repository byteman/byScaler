package com.blescaler.worker;


/**
 * @defgroup message 消息类型
 *    @{
 * 蓝牙SDK是发生消息给注册客户端
 * 
 * @author byteman
 * 
 */
public class Global {

	public static final String PREFERENCES_FILENAME = "com.lvrenyang.drawer.PREFERENCES_FILENAME";

	public static final String PREFERENCES_IPADDRESS = "com.lvrenyang.drawer.PREFERENCES_IPADDRESS";
	public static final String PREFERENCES_PORTNUMBER = "com.lvrenyang.drawer.PREFERENCES_PORTNUMBER";
	public static final String PREFERENCES_BTADDRESS = "com.lvrenyang.drawer.PREFERENCES_BTADDRESS";

	public static final int MSG_WORKTHREAD_HANDLER_CONNECTNET = 100000;
	public static final int MSG_WORKTHREAD_SEND_CONNECTNETRESULT = 100001;
	public static final int MSG_WORKTHREAD_HANDLER_OPENDRAWERNET = 100002;
	public static final int MSG_WORKTHREAD_SEND_OPENDRAWERNETRESULT = 100003;
	public static final int MSG_WORKTHREAD_HANDLER_CONNECTBT = 100004;
	public static final int MSG_WORKTHREAD_SEND_CONNECTBTRESULT = 100005;
	public static final int MSG_WORKTHREAD_HANDLER_OPENDRAWERBT = 100006;
	public static final int MSG_WORKTHREAD_SEND_OPENDRAWERBTRESULT = 100007;
	public static final int MSG_WORKTHREAD_HANDLER_STRINGINFOBT = 100008;
	public static final int MSG_WORKTHREAD_SEND_STRINGINFOBTRESULT = 100009;
	public static final int MSG_WORKTHREAD_HANDLER_STRINGINFONET = 100010;
	public static final int MSG_WORKTHREAD_SEND_STRINGINFONETRESULT = 100011;
	public static final int MSG_WORKTHREAD_HANDLER_SETKEYBT = 100012;
	public static final int MSG_WORKTHREAD_SEND_SETKEYBTRESULT = 100013;
	public static final int MSG_WORKTHREAD_HANDLER_SETKEYNET = 100014;
	public static final int MSG_WORKTHREAD_SEND_SETKEYNETRESULT = 100015;
	public static final int MSG_WORKTHREAD_HANDLER_SETBTPARABT = 100016;
	public static final int MSG_WORKTHREAD_SEND_SETBTPARABTRESULT = 100017;
	public static final int MSG_WORKTHREAD_HANDLER_SETBTPARANET = 100018;
	public static final int MSG_WORKTHREAD_SEND_SETBTPARANETRESULT = 100019;
	public static final int MSG_WORKTHREAD_HANDLER_SETIPPARABT = 100020;
	public static final int MSG_WORKTHREAD_SEND_SETIPPARABTRESULT = 100021;
	public static final int MSG_WORKTHREAD_HANDLER_SETIPPARANET = 100022;
	public static final int MSG_WORKTHREAD_SEND_SETIPPARANETRESULT = 100023;
	public static final int MSG_WORKTHREAD_HANDLER_SETWIFIPARABT = 100024;
	public static final int MSG_WORKTHREAD_SEND_SETWIFIPARABTRESULT = 100025;
	public static final int MSG_WORKTHREAD_HANDLER_SETWIFIPARANET = 100026;
	public static final int MSG_WORKTHREAD_SEND_SETWIFIPARANETRESULT = 100027;
	public static final int MSG_WORKTHREAD_HANDLER_CONNECTUSB = 100028;
	public static final int MSG_WORKTHREAD_SEND_CONNECTUSBRESULT = 100029;
	
	
	//add for BLE 
	public static final int MSG_BLE_SCANRESULT=100031;	/*!<扫描蓝牙设备的结果 */ 
	public static final int MSG_BLE_CONNECTRESULT=100032;	//连接设备的响应消息
	public static final int MSG_BLE_DISCONNECTRESULT=100033;	//设备断开命令的响应
	public static final int MSG_BLE_SERVICEDISRESULT=100034;	//设备服务列举的响应
	public static final int MSG_BLE_WGTRESULT=100035;	//读取重量的响应
	public static final int MSG_BLE_FAILERESULT=100036;	//命令响应失败的消息
	public static final int MSG_BLE_NOT_SUPPORT=100037;	//手机不支持蓝牙BLE
	public static final int MSG_SCALER_PAR_SET_RESULT=100038;	//设置秤参数的响应
	public static final int MSG_SCALER_PAR_GET_RESULT=100039;	//读取秤参数的响应	
	public static final int MSG_SCALER_ZERO_QUERY_RESULT=100040;	//读取零点ad值
	public static final int MSG_SCALER_ZERO_CALIB_RESULT=100041;	//标定零点ad值
	public static final int MSG_SCALER_K_QUERY_RESULT=100042;	//读取重量系数
	public static final int MSG_SCALER_K_CALIB_RESULT=100043;	//标定重量系数
	public static final int MSG_BLE_NO_BT_ADAPTER=10044; 	//手机不支持蓝牙
	public static final int MSG_SCALER_SAVE_EEPROM=10045; 	//存储数据到eeprom
	public static final int MSG_SCALER_CONNECT_OK=10046; 	//存储数据到eeprom
	public static final int MSG_SCALER_PAR_GET_DIGDOT_RESULT=100047;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_DIV_RESULT=100048;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_SPAN_RESULT=100049;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_UNIT_RESULT=100050;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_PWR_ZERO_RESULT=100051;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_HAND_ZERO_RESULT=100052;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_ZEROTRACK_RESULT=100053;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_STABLE_RESULT=100054;	//读取秤参数的响应
	public static final int MSG_SCALER_PAR_GET_FITER_RESULT=100055;	//读取秤参数的响应
	public static final int MSG_SCALER_CTRL_RESULT=100056;	//读取秤参数的响应
	public static final int MSG_SCALER_POWER_RESULT=100057;	//读取秤参数的响应
	public static final int MSG_SCALER_AD_CHAN1_RESULT=100058;	//读取秤参数的响应
	public static final int MSG_SCALER_AD_CHAN2_RESULT=100059;	//读取秤参数的响应
	



	public static final int MSG_ALLTHREAD_READY = 100300;
	public static final int MSG_PAUSE_HEARTBEAT = 100301;
	public static final int MSG_RESUME_HEARTBEAT = 100302;
	public static final int MSG_ON_RECIVE = 100303;
	public static final int CMD_WRITE = 100304;
	public static final int CMD_WRITERESULT = 100305;
	public static final int CMD_POS_PRINTBWPICTURE = 100306;
	public static final int CMD_POS_WRITE_BT_FLOWCONTROL = 100307; // 使用蓝牙流控
	public static final int CMD_POS_WRITE_BT_FLOWCONTROL_RESULT = 100308;
	public static final int CMD_UPDATE_PROGRAM = 100309;
	public static final int CMD_UPDATE_PROGRAM_RESULT = 100310;
	public static final int CMD_UPDATE_PROGRAM_PROGRESS = 100311;
	public static final int CMD_EMBEDDED_SEND_TO_UART = 100312;
	public static final int CMD_EMBEDDED_SEND_TO_UART_RESULT = 100313;
	
	public static final int CMD_CONNECT_SCALER=100314;
	
	public static final int REG_WEIGHT =0; /*!< 重量数据 */
	public static final int REG_OPERATION = 2;
	public static final int REG_DOTNUM = 3;
	public static final int REG_TARE = 6;
	public static final int REG_DIV1 = 8;
	public static final int REG_SPAN1 = 10;
	public static final int REG_UNIT = 14;
	public static final int REG_PWR_ZERO_SPAN = 15;
	public static final int REG_HAND_ZERO_SPAN = 16;
	public static final int REG_ZERO_TRACK_SPAN = 17;
	public static final int REG_STILL_DIS_SPAN = 18;
	public static final int REG_STILL_FILTER_LEVEL = 19;
	public static final int REG_CALIB_INDEX = 20; //标定序号 0 标0
	public static final int REG_CALIB_EXEC = 21;
	public static final int REG_CALIB_VALUE = 22;
	public static final int REG_SENSOR_DIFF_K1 = 36;
	public static final int REG_SENSOR_DIFF_K2 = 38;
	public static final int REG_SENSOR_DIFF_K3 = 40;
	public static final int REG_SENSOR_DIFF_K4 = 42;
	public static final int REG_AUTO_DIFF_CALIB_INDEX = 44;
	public static final int REG_SLEEP_S = 45;
	public static final int REG_SENSOR_NUM = 46;
	public static final int REG_BATTERY = 47;
	
	public static final int REG_LAMP_CTRL = 48;
	public static final int REG_AD_CHAN1 = 49;
	public static final int REG_AD_CHAN2 = 51;
	public static final int REG_AD_CHAN3 = 53;
	public static final int REG_AD_CHAN4 = 55;
	
	public static final int REG_WEIGHT_V2 = 100;
	
	
	public static String toast_success = "Done";
	public static String toast_fail = "Fail";
	public static String toast_notconnect = "Please connect printer";
	public static String toast_usbpermit = "Please permit app use usb and reclick this button";
	public static String toast_connecting = "Connecting";
}
/** @} */ // end of group1
