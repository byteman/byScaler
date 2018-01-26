package com.blescaler.printer;

import android.os.Bundle;

import com.blescaler.db.CountRecord;
import com.blescaler.db.WeightRecord;
import com.blescaler.worker.Global;
import com.blescaler.worker.WorkService;
import com.lvrenyang.utils.DataUtils;

/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class Printer {
    public static String formatUnit(String kg)
    {
        return kg ;
    }
    public static boolean requestPrintWeightRecord(WeightRecord data)
    {
        byte[] header =  { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,0x01 };
        byte[] setHT = {0x1b,0x44,0x10,0x00};
        byte[] HT = {0x09};
        byte[] LF = {0x0d,0x0a};
        if(!WorkService.hasConnectPrinter()) return false;
        byte[][] allbuf = new byte[][]{header,
                setHT,"流水号".getBytes(),HT,data.getID().getBytes(),LF,LF,

                setHT,"日期".getBytes(),HT,data.getFormatDate().getBytes(),LF,
                setHT,"时间".getBytes(),HT,data.getFormatTime().getBytes(),LF,
                setHT,"毛重".getBytes(),HT,formatUnit(data.getGross()).getBytes(),LF,
                setHT,"皮重".getBytes(),HT,formatUnit(data.getTare()).getBytes(),LF,
                setHT,"净重".getBytes(),HT,formatUnit(data.getNet()).getBytes(),LF,LF,
        };
        byte[] buf = DataUtils.byteArraysToBytes(allbuf);
        if (WorkService.workThread.isConnected()) {
            Bundle d = new Bundle();
            d.putByteArray(Global.BYTESPARA1, buf);
            d.putInt(Global.INTPARA1, 0);
            d.putInt(Global.INTPARA2, buf.length);
            WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, d);
        } else {
            //打印机未连接
            return false;
        }
        return true;

    }
    public static boolean requestPrintCountRecord(CountRecord data)
    {
        byte[] header =  { 0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39,0x01 };
        byte[] setHT = {0x1b,0x44,0x10,0x00};
        byte[] HT = {0x09};
        byte[] LF = {0x0d,0x0a};
        if(!WorkService.hasConnectPrinter()) return false;
        byte[][] allbuf = new byte[][]{header,
                setHT,"流水号".getBytes(),HT,data.getID().getBytes(),LF,LF,

                setHT,"日期".getBytes(),HT,data.getFormatDate().getBytes(),LF,
                setHT,"时间".getBytes(),HT,data.getFormatTime().getBytes(),LF,
                setHT,"总重".getBytes(),HT,formatUnit(data.getTotalWeight()).getBytes(),LF,
                setHT,"单重".getBytes(),HT,formatUnit(data.getUw()).getBytes(),LF,
                setHT,"个数".getBytes(),HT,formatUnit(data.getCount()).getBytes(),LF,LF,
        };
        byte[] buf = DataUtils.byteArraysToBytes(allbuf);
        if (WorkService.workThread.isConnected()) {
            Bundle d = new Bundle();
            d.putByteArray(Global.BYTESPARA1, buf);
            d.putInt(Global.INTPARA1, 0);
            d.putInt(Global.INTPARA2, buf.length);
            WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, d);
        } else {
            //打印机未连接
            return false;
        }
        return true;

    }

}
