package com.blescaler.utils;

import android.util.Log;


public class LogUtils {
	private static final boolean isLOGD = true;
	private static final boolean isLOGI = false;
	private static final boolean isLOGV = false;
	private static final boolean isLOGE = false;
	private static final boolean isLOGW = false;
	private static final String LOG_PREFIX = "yc_";
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_LENGTH = 23;

	public static String makeLogTag(String str) {
		if (str.length() > MAX_LOG_LENGTH - LOG_PREFIX_LENGTH) {
			return LOG_PREFIX
					+ str.substring(0, MAX_LOG_LENGTH - LOG_PREFIX_LENGTH - 1);
		}
		return LOG_PREFIX + str;
	}

	
	public static String makeLogTag(Class cls) {
		return makeLogTag(cls.getSimpleName());
	}

	public static void LOGD(final String tag, String message) {
	
		if (isLOGD) Log.d(tag, message);
		
	}

	

	public static void LOGV(final String tag, String message) {

		if (isLOGV)Log.v(tag, message);

	}



	public static void LOGI(String tag, String message) {
		if (isLOGI)Log.i(tag, message);
	}

	

	public static void LOGW(String tag, String message) {
		if (isLOGW)Log.w(tag, message);
	}

	

	public static void LOGE(String tag, String message) {
		if (isLOGE)Log.e(tag, message);
	}

	

	private LogUtils() {
	}
}
