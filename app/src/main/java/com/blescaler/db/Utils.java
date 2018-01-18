package com.blescaler.db;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class Utils {
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
}
