package com.blescaler.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

public class Utils {
	public static Map<String, String> BLE_SERVICES = new HashMap<String, String>();
	public static Map<String, String> BLE_CHARACTERISTICS = new HashMap<String, String>();
	public static String UUID_SRV="0000FFF0-0000-1000-8000-00805F9B34FB";
	public static String UUID_AD="0000FFF4-0000-1000-8000-00805F9B34FB";
	public static String UUID_DATA="0000FFF1-0000-1000-8000-00805F9B34FB";
	public static String UUID_WGT="0000FFF3-0000-1000-8000-00805F9B34FB";
	public static String UUID_K="0000FFF2-0000-1000-8000-00805F9B34FB";
	static {
		BLE_SERVICES.put("00001811-0000-1000-8000-00805F9B34FB", "Alert Notification Service");
		BLE_SERVICES.put("0000180F-0000-1000-8000-00805F9B34FB", "Battery Service");
		BLE_SERVICES.put("00001810-0000-1000-8000-00805F9B34FB", "Blood Pressure");
		BLE_SERVICES.put("00001805-0000-1000-8000-00805F9B34FB", "Current Time Service");
		BLE_SERVICES.put("00001818-0000-1000-8000-00805F9B34FB", "Cycling Power");
		BLE_SERVICES.put("00001816-0000-1000-8000-00805F9B34FB", "Cycling Speed and Cadence");
		BLE_SERVICES.put("0000180A-0000-1000-8000-00805F9B34FB", "Device Information");
		BLE_SERVICES.put("00001800-0000-1000-8000-00805F9B34FB", "Generic Access");
		BLE_SERVICES.put("00001801-0000-1000-8000-00805F9B34FB", "Generic Attribute");
		BLE_SERVICES.put("00001808-0000-1000-8000-00805F9B34FB", "Glucose");
		BLE_SERVICES.put("00001809-0000-1000-8000-00805F9B34FB", "Health Thermometer");
		BLE_SERVICES.put("0000180D-0000-1000-8000-00805F9B34FB", "Heart Rate");
		BLE_SERVICES.put("00001812-0000-1000-8000-00805F9B34FB", "Human Interface Device");
		BLE_SERVICES.put("00001802-0000-1000-8000-00805F9B34FB", "Immediate Alert");
		BLE_SERVICES.put("00001803-0000-1000-8000-00805F9B34FB", "Link Loss");
		BLE_SERVICES.put("00001819-0000-1000-8000-00805F9B34FB", "Location and Navigation");
		BLE_SERVICES.put("00001807-0000-1000-8000-00805F9B34FB", "Next DST Change Service");
		BLE_SERVICES.put("0000180E-0000-1000-8000-00805F9B34FB", "Phone Alert Status Service");
		BLE_SERVICES.put("00001806-0000-1000-8000-00805F9B34FB", "Reference Time Update Service");
		BLE_SERVICES.put("00001814-0000-1000-8000-00805F9B34FB", "Running Speed and Cadence");
		BLE_SERVICES.put("00001813-0000-1000-8000-00805F9B34FB", "Scan Parameters");
		BLE_SERVICES.put("00001804-0000-1000-8000-00805F9B34FB", "Tx Power");
		
		BLE_CHARACTERISTICS.put("00002A43-0000-1000-8000-00805F9B34FB", "Alert Category ID");
		BLE_CHARACTERISTICS.put("00002A42-0000-1000-8000-00805F9B34FB", "Alert Category ID Bit Mask");
		BLE_CHARACTERISTICS.put("00002A06-0000-1000-8000-00805F9B34FB", "Alert Level");
		BLE_CHARACTERISTICS.put("00002A44-0000-1000-8000-00805F9B34FB", "Alert Notification Control Point");
		BLE_CHARACTERISTICS.put("00002A3F-0000-1000-8000-00805F9B34FB", "Alert Status");
		BLE_CHARACTERISTICS.put("00002A01-0000-1000-8000-00805F9B34FB", "Appearance");
		BLE_CHARACTERISTICS.put("00002A19-0000-1000-8000-00805F9B34FB", "Battery Level");
		BLE_CHARACTERISTICS.put("00002A49-0000-1000-8000-00805F9B34FB", "Blood Pressure Feature");
		BLE_CHARACTERISTICS.put("00002A35-0000-1000-8000-00805F9B34FB", "Blood Pressure Measurement");
		BLE_CHARACTERISTICS.put("00002A38-0000-1000-8000-00805F9B34FB", "Body Sensor Location");
		BLE_CHARACTERISTICS.put("00002A22-0000-1000-8000-00805F9B34FB", "Boot Keyboard Input Report");
		BLE_CHARACTERISTICS.put("00002A32-0000-1000-8000-00805F9B34FB", "Boot Keyboard Output Report");
		BLE_CHARACTERISTICS.put("00002A33-0000-1000-8000-00805F9B34FB", "Boot Mouse Input Report");
		BLE_CHARACTERISTICS.put("00002A5C-0000-1000-8000-00805F9B34FB", "CSC Feature");
		BLE_CHARACTERISTICS.put("00002A5B-0000-1000-8000-00805F9B34FB", "CSC Measurement");
		BLE_CHARACTERISTICS.put("00002A2B-0000-1000-8000-00805F9B34FB", "Current Time");
		BLE_CHARACTERISTICS.put("00002A66-0000-1000-8000-00805F9B34FB", "Cycling Power Control Point");
		BLE_CHARACTERISTICS.put("00002A65-0000-1000-8000-00805F9B34FB", "Cycling Power Feature");
		BLE_CHARACTERISTICS.put("00002A63-0000-1000-8000-00805F9B34FB", "Cycling Power Measurement");
		BLE_CHARACTERISTICS.put("00002A64-0000-1000-8000-00805F9B34FB", "Cycling Power Vector");
		BLE_CHARACTERISTICS.put("00002A08-0000-1000-8000-00805F9B34FB", "Date Time");
		BLE_CHARACTERISTICS.put("00002A0A-0000-1000-8000-00805F9B34FB", "Day Date Time");
		BLE_CHARACTERISTICS.put("00002A09-0000-1000-8000-00805F9B34FB", "Day of Week");
		BLE_CHARACTERISTICS.put("00002A00-0000-1000-8000-00805F9B34FB", "Device Name");
		BLE_CHARACTERISTICS.put("00002A0D-0000-1000-8000-00805F9B34FB", "DST Offset");
		BLE_CHARACTERISTICS.put("00002A0C-0000-1000-8000-00805F9B34FB", "Exact Time 256");
		BLE_CHARACTERISTICS.put("00002A26-0000-1000-8000-00805F9B34FB", "Firmware Revision String");
		BLE_CHARACTERISTICS.put("00002A51-0000-1000-8000-00805F9B34FB", "Glucose Feature");
		BLE_CHARACTERISTICS.put("00002A18-0000-1000-8000-00805F9B34FB", "Glucose Measurement");
		BLE_CHARACTERISTICS.put("00002A34-0000-1000-8000-00805F9B34FB", "Glucose Measurement Context");
		BLE_CHARACTERISTICS.put("00002A27-0000-1000-8000-00805F9B34FB", "Hardware Revision String");
		BLE_CHARACTERISTICS.put("00002A39-0000-1000-8000-00805F9B34FB", "Heart Rate Control Point");
		BLE_CHARACTERISTICS.put("00002A37-0000-1000-8000-00805F9B34FB", "Heart Rate Measurement");
		BLE_CHARACTERISTICS.put("00002A4C-0000-1000-8000-00805F9B34FB", "HID Control Point");
		BLE_CHARACTERISTICS.put("00002A4A-0000-1000-8000-00805F9B34FB", "HID Information");
		BLE_CHARACTERISTICS.put("00002A2A-0000-1000-8000-00805F9B34FB", "IEEE 11073-20601 Regulatory Certification Data List");
		BLE_CHARACTERISTICS.put("00002A36-0000-1000-8000-00805F9B34FB", "Intermediate Cuff Pressure");
		BLE_CHARACTERISTICS.put("00002A1E-0000-1000-8000-00805F9B34FB", "Intermediate Temperature");
		BLE_CHARACTERISTICS.put("00002A6B-0000-1000-8000-00805F9B34FB", "LN Control Point");
		BLE_CHARACTERISTICS.put("00002A6A-0000-1000-8000-00805F9B34FB", "LN Feature");
		BLE_CHARACTERISTICS.put("00002A0F-0000-1000-8000-00805F9B34FB", "Local Time Information");
		BLE_CHARACTERISTICS.put("00002A67-0000-1000-8000-00805F9B34FB", "Location and Speed");
		BLE_CHARACTERISTICS.put("00002A29-0000-1000-8000-00805F9B34FB", "Manufacturer Name String");
		BLE_CHARACTERISTICS.put("00002A21-0000-1000-8000-00805F9B34FB", "Measurement Interval");
		BLE_CHARACTERISTICS.put("00002A24-0000-1000-8000-00805F9B34FB", "Model Number String");
		BLE_CHARACTERISTICS.put("00002A68-0000-1000-8000-00805F9B34FB", "Navigation");
		BLE_CHARACTERISTICS.put("00002A46-0000-1000-8000-00805F9B34FB", "New Alert");
		BLE_CHARACTERISTICS.put("00002A04-0000-1000-8000-00805F9B34FB", "Peripheral Preferred Connection Parameters");
		BLE_CHARACTERISTICS.put("00002A02-0000-1000-8000-00805F9B34FB", "Peripheral Privacy Flag");
		BLE_CHARACTERISTICS.put("00002A50-0000-1000-8000-00805F9B34FB", "PnP ID");
		BLE_CHARACTERISTICS.put("00002A69-0000-1000-8000-00805F9B34FB", "Position Quality");
		BLE_CHARACTERISTICS.put("00002A4E-0000-1000-8000-00805F9B34FB", "Protocol Mode");
		BLE_CHARACTERISTICS.put("00002A03-0000-1000-8000-00805F9B34FB", "Reconnection Address");
		BLE_CHARACTERISTICS.put("00002A52-0000-1000-8000-00805F9B34FB", "Record Access Control Point");
		BLE_CHARACTERISTICS.put("00002A14-0000-1000-8000-00805F9B34FB", "Reference Time Information");
		BLE_CHARACTERISTICS.put("00002A4D-0000-1000-8000-00805F9B34FB", "Report");
		BLE_CHARACTERISTICS.put("00002A4B-0000-1000-8000-00805F9B34FB", "Report Map");
		BLE_CHARACTERISTICS.put("00002A40-0000-1000-8000-00805F9B34FB", "Ringer Control Point");
		BLE_CHARACTERISTICS.put("00002A41-0000-1000-8000-00805F9B34FB", "Ringer Setting");
		BLE_CHARACTERISTICS.put("00002A54-0000-1000-8000-00805F9B34FB", "RSC Feature");
		BLE_CHARACTERISTICS.put("00002A53-0000-1000-8000-00805F9B34FB", "RSC Measurement");
		BLE_CHARACTERISTICS.put("00002A55-0000-1000-8000-00805F9B34FB", "SC Control Point");
		BLE_CHARACTERISTICS.put("00002A4F-0000-1000-8000-00805F9B34FB", "Scan Interval Window");
		BLE_CHARACTERISTICS.put("00002A31-0000-1000-8000-00805F9B34FB", "Scan Refresh");
		BLE_CHARACTERISTICS.put("00002A5D-0000-1000-8000-00805F9B34FB", "Sensor Location");
		BLE_CHARACTERISTICS.put("00002A25-0000-1000-8000-00805F9B34FB", "Serial Number String");
		BLE_CHARACTERISTICS.put("00002A05-0000-1000-8000-00805F9B34FB", "Service Changed");
		BLE_CHARACTERISTICS.put("00002A28-0000-1000-8000-00805F9B34FB", "Software Revision String");
		BLE_CHARACTERISTICS.put("00002A47-0000-1000-8000-00805F9B34FB", "Supported New Alert Category");
		BLE_CHARACTERISTICS.put("00002A48-0000-1000-8000-00805F9B34FB", "Supported Unread Alert Category");
		BLE_CHARACTERISTICS.put("00002A23-0000-1000-8000-00805F9B34FB", "System ID");
		BLE_CHARACTERISTICS.put("00002A1C-0000-1000-8000-00805F9B34FB", "Temperature Measurement");
		BLE_CHARACTERISTICS.put("00002A1D-0000-1000-8000-00805F9B34FB", "Temperature Type");
		BLE_CHARACTERISTICS.put("00002A12-0000-1000-8000-00805F9B34FB", "Time Accuracy");
		BLE_CHARACTERISTICS.put("00002A13-0000-1000-8000-00805F9B34FB", "Time Source");
		BLE_CHARACTERISTICS.put("00002A16-0000-1000-8000-00805F9B34FB", "Time Update Control Point");
		BLE_CHARACTERISTICS.put("00002A17-0000-1000-8000-00805F9B34FB", "Time Update State");
		BLE_CHARACTERISTICS.put("00002A11-0000-1000-8000-00805F9B34FB", "Time with DST");
		BLE_CHARACTERISTICS.put("00002A0E-0000-1000-8000-00805F9B34FB", "Time Zone");
		BLE_CHARACTERISTICS.put("00002A07-0000-1000-8000-00805F9B34FB", "Tx Power Level");
		BLE_CHARACTERISTICS.put("00002A45-0000-1000-8000-00805F9B34FB", "Unread Alert Status");
	}
	
	private static Utils INSTANCE;
	
	private Utils() {}
	
	public static Utils getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Utils();
		}
		return INSTANCE;
	}
	public static byte[] intToByte(int number) {
		byte[] abyte = new byte[4];
		// "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
		abyte[0] = (byte) (0xff & number);
		// ">>"右移位，若为正数则高位补0，若为负数则高位补1
		abyte[1] = (byte) ((0xff00 & number) >> 8);
		abyte[2] = (byte) ((0xff0000 & number) >> 16);
		abyte[3] = (byte) ((0xff000000 & number) >> 24);
		return abyte;
	}
	int String2Int(String str)
	{
		
		return Integer.parseInt(str);
	}
	public static void Msgbox(Context ctx,String msg)
	{
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
	/**
	 *基于位移的 byte[]转化成int
	 * @param byte[] bytes
	 * @return int  number
	 */

	public static int bytesToWeight(byte[] bytes) {
		int number = bytes[2] & 0xFF;
		// "|="按位或赋值。
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[0] << 16) & 0xFF0000);
		if((bytes[0]&0x80) == 0x80)
			number |= ((0xFF << 24) & 0xFF000000);
		else
			number |= ((0 << 24) & 0xFF000000);
		return number;
	}
	public static int bytesToString(byte[] bytes,int from, int to) throws Exception
	{
		int size = to - from;
		if(size < 1) throw new Exception("长度不正确");
		byte[] tmp = new byte[size];
		System.arraycopy(bytes, from, tmp, 0, size);
		String s = new String(tmp);
		return Integer.parseInt(s);
	
	}
	public static int bytesToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;
		// "|="按位或赋值。
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[2] << 16) & 0xFF0000);
		number |= ((bytes[3] << 24) & 0xFF000000);
		
		return number;
	}
	/**
	 * 浮点转换为字节
	 * 
	 * @param f
	 * @return
	 */
	public static byte[] float2byte(float f) {
		
		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);
		
		byte[] b = new byte[4];  
	    for (int i = 0; i < 4; i++) {  
	        b[i] = (byte) (fbit >> (24 - i * 8));  
	    } 
	    
	    // 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}
	    
	    return dest;
	    
	}
	
	/**
	 * 字节转换为浮点
	 * 
	 * @param b 字节（至少4个字节）
	 * @param index 开始位置
	 * @return
	 */
	public static float byte2float(byte[] b, int index) {  
	    int l;                                           
	    l = b[index + 0];                                
	    l &= 0xff;                                       
	    l |= ((long) b[index + 1] << 8);                 
	    l &= 0xffff;                                     
	    l |= ((long) b[index + 2] << 16);                
	    l &= 0xffffff;                                   
	    l |= ((long) b[index + 3] << 24);                
	    return Float.intBitsToFloat(l);                  
	}
	
	public static String getNormalDate(long value)  
	{  
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd") ;  
	    String time = format.format(new Date(value)) ;  
	    return time;  
	}  
	public static String getNormalTime(long value)  
	{  
	    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss") ;  
	    String time = format.format(new Date(value)) ;  
	    return time;  
	}  
	public static String getNormalDateTime(long value)  
	{  
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;  
	    String time = format.format(new Date(value)) ;  
	    return time;  
	}  
	public static void setDiscoverableTimeout(int timeout) {
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);
             
            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static  void closeDiscoverableTimeout() {
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);
             
            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
