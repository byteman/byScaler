package com.blescaler.worker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lvrenyang.pos.IO;
import com.lvrenyang.pos.Pos;
import com.lvrenyang.pos.Protocol;
import com.lvrenyang.rwbt.BTHeartBeatThread;
import com.lvrenyang.rwbt.BTRWThread;

import com.lvrenyang.utils.DataUtils;
import com.lvrenyang.utils.FileUtils;

public class WorkThread extends Thread {

	private static final String TAG = "WorkThread";
	public static Handler workHandler = null;
	private static Looper mLooper = null;
	public static Handler targetHandler = null;
	

	public static BTRWThread btRW;
	public static BTHeartBeatThread btHeartBeat;

	private static boolean threadInitOK = false;
	private static boolean isConnecting = false;

	public WorkThread(Handler handler) {
		threadInitOK = false;
		targetHandler = handler;
		
		btRW = BTRWThread.InitInstant();
		btHeartBeat = BTHeartBeatThread.InitInstant(targetHandler);
		
	}

	@Override
	public void start() {
		super.start();
		while (!threadInitOK)
			;

		
		btRW.start();
		btHeartBeat.start();
		
	}

	@Override
	public void run() {
		Looper.prepare();
		mLooper = Looper.myLooper();
		if (null == mLooper)
			Log.v(TAG, "mLooper is null pointer");
		else
			Log.v(TAG, "mLooper is valid");
		workHandler = new WorkHandler();
		threadInitOK = true;
		Looper.loop();
	}

	private static class WorkHandler extends Handler {

		boolean needPauseHeartBeat(int msgCode) {
			boolean isNeeded = true;

			switch (msgCode) {
			case Global.CMD_POS_WRITE:
			case Global.CMD_POS_READ:
			case Global.CMD_POS_SETKEY:
			case Global.CMD_POS_CHECKKEY:
			case Global.CMD_POS_PRINTPICTURE:
			case Global.CMD_POS_PRINTBWPICTURE:
			case Global.CMD_POS_SALIGN:
			case Global.CMD_POS_SETLINEHEIGHT:
			case Global.CMD_POS_SETRIGHTSPACE:
			case Global.CMD_POS_STEXTOUT:
			case Global.CMD_POS_SETCHARSETANDCODEPAGE:
			case Global.CMD_POS_SETBARCODE:
			case Global.CMD_POS_SETQRCODE:
			case Global.CMD_EPSON_SETQRCODE:
			case Global.MSG_PAUSE_HEARTBEAT:
			case Global.CMD_UPDATE_PROGRAM:
				isNeeded = true;
				break;
			default:
				isNeeded = false;
				break;
			}

			return isNeeded;
		}

		boolean needResumeHeartBeat(int msgCode) {
			boolean isNeeded = true;

			switch (msgCode) {
			case Global.CMD_POS_WRITE:
			case Global.CMD_POS_READ:
			case Global.CMD_POS_SETKEY:
			case Global.CMD_POS_CHECKKEY:
			case Global.CMD_POS_PRINTPICTURE:
			case Global.CMD_POS_PRINTBWPICTURE:
			case Global.CMD_POS_SALIGN:
			case Global.CMD_POS_SETLINEHEIGHT:
			case Global.CMD_POS_SETRIGHTSPACE:
			case Global.CMD_POS_STEXTOUT:
			case Global.CMD_POS_SETCHARSETANDCODEPAGE:
			case Global.CMD_POS_SETBARCODE:
			case Global.CMD_POS_SETQRCODE:
			case Global.CMD_EPSON_SETQRCODE:
			case Global.MSG_RESUME_HEARTBEAT:
			case Global.CMD_UPDATE_PROGRAM:
				isNeeded = true;
				break;
			default:
				isNeeded = false;
				break;
			}

			return isNeeded;
		}

		@Override
		public void handleMessage(Message msg) {

			FileUtils.DebugAddToFile(
					"begin handleMessage:" + msg.what + "\r\n",
					FileUtils.sdcard_dump_txt);

			// 有些消息，需要暂停心跳线程
			if (needPauseHeartBeat(msg.what)) {
				BTHeartBeatThread.PauseHeartBeat();
				
				FileUtils.DebugAddToFile("PauseHeartBeat\r\n",
						FileUtils.sdcard_dump_txt);
			}

			switch (msg.what) {
			case Global.MSG_WORKTHREAD_HANDLER_CONNECTBT: {
				isConnecting = true;
				IO.SetCurPort(IO.PORT_BT);
				String BTAddress = (String) msg.obj;
				// boolean result = BTRWThread.Open(BTAddress);
				boolean result = BTRWThread.OpenOfficial(BTAddress);
				Message smsg = targetHandler
						.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT);
				if (result) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				if (result) {
					// 开启心跳线程
					BTHeartBeatThread.BeginHeartBeat();
				}
				isConnecting = false;
				break;
			}

			case Global.CMD_WRITE: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_WRITERESULT);
				if (IO.Write(buffer, offset, count) == count) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_WRITE: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_WRITERESULT);

				if (result) {
					IO.Write(buffer, offset, count);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_WRITE_BT_FLOWCONTROL: {

				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);
				int percount = data.getInt(Global.INTPARA3);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_WRITE_BT_FLOWCONTROL_RESULT);

				if ((result) && (IO.GetCurPort() == IO.PORT_BT)) {
					int idx = Pos.POS_Write_FlowControl(buffer, offset, count,
							percount);
					if (idx == count)
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETKEY: {
				Bundle data = msg.getData();
				byte[] key = data.getByteArray(Global.BYTESPARA1);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETKEYRESULT);

				if (result) {
					Pos.POS_SetKey(key);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_CHECKKEY: {
				Bundle data = msg.getData();
				byte[] key = data.getByteArray(Global.BYTESPARA1);
				byte[] random = data.getByteArray(Global.BYTESPARA2);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_CHECKKEYRESULT);
				if (result) {
					if (Pos.POS_CheckKey(key, random))
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_PRINTPICTURE: {
				Bundle data = msg.getData();
				// Bitmap mBitmap = (Bitmap) data.get(Global.OBJECT1);
				Bitmap mBitmap = (Bitmap) data.getParcelable(Global.PARCE1);
				int nWidth = data.getInt(Global.INTPARA1);
				int nMode = data.getInt(Global.INTPARA2);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_PRINTPICTURERESULT);
				if (result) {
					Pos.POS_PrintPicture(mBitmap, nWidth, nMode);
					if (Pos.POS_QueryStatus(precbuf, timeout))
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_PRINTBWPICTURE: {
				Bundle data = msg.getData();
				// Bitmap mBitmap = (Bitmap) data.get(Global.OBJECT1);
				Bitmap mBitmap = (Bitmap) data.getParcelable(Global.PARCE1);
				int nWidth = data.getInt(Global.INTPARA1);
				int nMode = data.getInt(Global.INTPARA2);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_PRINTPICTURERESULT);
				if (result) {
					Pos.POS_PrintBWPic(mBitmap, nWidth, nMode);
					if (Pos.POS_QueryStatus(precbuf, timeout))
						smsg.arg1 = 1;
					else
						smsg.arg1 = 0;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SALIGN: {
				Bundle data = msg.getData();
				int align = data.getInt(Global.INTPARA1);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SALIGNRESULT);
				if (result) {
					Pos.POS_S_Align(align);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETLINEHEIGHT: {
				Bundle data = msg.getData();
				int nHeight = data.getInt(Global.INTPARA1);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETLINEHEIGHTRESULT);
				if (result) {
					Pos.POS_SetLineHeight(nHeight);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETRIGHTSPACE: {
				Bundle data = msg.getData();
				int nDistance = data.getInt(Global.INTPARA1);
				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETRIGHTSPACERESULT);
				if (result) {
					Pos.POS_SetRightSpacing(nDistance);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_STEXTOUT: {
				Bundle data = msg.getData();
				String pszString = data.getString(Global.STRPARA1);
				String encoding = data.getString(Global.STRPARA2);
				int nOrgx = data.getInt(Global.INTPARA1);
				int nWidthTimes = data.getInt(Global.INTPARA2);
				int nHeightTimes = data.getInt(Global.INTPARA3);
				int nFontType = data.getInt(Global.INTPARA4);
				int nFontStyle = data.getInt(Global.INTPARA5);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_STEXTOUTRESULT);
				if (result) {
					Pos.POS_S_TextOut(pszString, encoding, nOrgx, nWidthTimes,
							nHeightTimes, nFontType, nFontStyle);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETCHARSETANDCODEPAGE: {
				Bundle data = msg.getData();
				int nCharSet = data.getInt(Global.INTPARA1);
				int nCodePage = data.getInt(Global.INTPARA2);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETCHARSETANDCODEPAGERESULT);
				if (result) {
					Pos.POS_SetCharSetAndCodePage(nCharSet, nCodePage);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETBARCODE: {
				Bundle data = msg.getData();
				String strBarcode = data.getString(Global.STRPARA1);
				int nOrgx = data.getInt(Global.INTPARA1);
				int nType = data.getInt(Global.INTPARA2);
				int nWidthX = data.getInt(Global.INTPARA3);
				int nHeight = data.getInt(Global.INTPARA4);
				int nHriFontType = data.getInt(Global.INTPARA5);
				int nHriFontPosition = data.getInt(Global.INTPARA6);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETBARCODERESULT);
				if (result) {
					Pos.POS_S_SetBarcode(strBarcode, nOrgx, nType, nWidthX,
							nHeight, nHriFontType, nHriFontPosition);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_POS_SETQRCODE: {
				Bundle data = msg.getData();
				String strQrcode = data.getString(Global.STRPARA1);
				int nWidthX = data.getInt(Global.INTPARA1);
				int necl = data.getInt(Global.INTPARA2);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_POS_SETQRCODERESULT);
				if (result) {
					Pos.POS_S_SetQRcode(strQrcode, nWidthX, necl);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_EPSON_SETQRCODE: {
				Bundle data = msg.getData();
				String strQrcode = data.getString(Global.STRPARA1);
				int nWidthX = data.getInt(Global.INTPARA1);
				int necl = data.getInt(Global.INTPARA2);

				byte[] precbuf = new byte[1];
				int timeout = 1000;
				boolean result = Pos.POS_QueryStatus(precbuf, timeout);
				Message smsg = targetHandler
						.obtainMessage(Global.CMD_EPSON_SETQRCODERESULT);
				if (result) {
					Pos.POS_EPSON_SetQRCode(strQrcode, nWidthX, necl);
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}

			case Global.CMD_UPDATE_PROGRAM: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_UPDATE_PROGRAM_RESULT);
				if (UpdateProgram(buffer)) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);
				break;
			}

			case Global.CMD_EMBEDDED_SEND_TO_UART: {
				Bundle data = msg.getData();
				byte[] buffer = data.getByteArray(Global.BYTESPARA1);
				int offset = data.getInt(Global.INTPARA1);
				int count = data.getInt(Global.INTPARA2);

				Message smsg = targetHandler
						.obtainMessage(Global.CMD_EMBEDDED_SEND_TO_UART_RESULT);
				if (Pos.EMBEDDED_WriteToUart(buffer, offset, count)) {
					smsg.arg1 = 1;
				} else {
					smsg.arg1 = 0;
				}
				targetHandler.sendMessage(smsg);

				break;
			}
			}

			// 如果暂停了心跳线程，在这里需要恢复
			if (needResumeHeartBeat(msg.what)) {
				BTHeartBeatThread.ResumeHeartBeat();
			
				FileUtils.DebugAddToFile("ResumeHeartBeat\r\n",
						FileUtils.sdcard_dump_txt);
			}

			FileUtils.DebugAddToFile("end handleMessage:" + msg.what + "\r\n",
					FileUtils.sdcard_dump_txt);
		}

		private boolean UpdateProgram(byte[] buffer) {

			byte[] testData = "DEVICE??".getBytes();

			int[] reclen = new int[1];
			byte[] precbuf = new byte[512];
			int timeout = 1000;

			byte[] sendData = new byte[0x100];
			int len = buffer.length;
			int addr = 0;
			Message smsg;

			if (Protocol.ProtoPackage(0x20, 0, testData.length, testData,
					reclen, precbuf, timeout)) {
				Log.i(TAG, "Test Success!");
			} else {
				Log.i(TAG, "Test Failed!");
				return false;
			}

			if (Protocol.ProtoPackage(0x2F, 0, 0, null, reclen, precbuf,
					timeout)) {
				Log.i(TAG, "UPDATE CMD 0x2F Success!");
			} else {
				Log.i(TAG, "UPDATE CMD 0x2F Failed!");
				return false;
			}
			smsg = targetHandler
					.obtainMessage(Global.CMD_UPDATE_PROGRAM_PROGRESS);
			smsg.arg1 = addr;
			smsg.arg2 = buffer.length;
			targetHandler.sendMessage(smsg);
			while (len > 0) {

				if (len < 0x100)
					DataUtils.BytesArrayFill(sendData, 0, sendData.length,
							(byte) 0xFF);

				DataUtils.copyBytes(buffer, addr, sendData, 0,
						(len < 0x100) ? len : 0x100);
				if (Protocol.ProtoPackage(0x2e, addr, 0x100, sendData, reclen,
						precbuf, timeout)) {
					Log.i(TAG, "UPDATE CMD 0x2E Success!" + "Addr:" + addr);
					smsg = targetHandler
							.obtainMessage(Global.CMD_UPDATE_PROGRAM_PROGRESS);
					smsg.arg1 = addr;
					smsg.arg2 = buffer.length;
					targetHandler.sendMessage(smsg);
				} else {
					Log.i(TAG, "UPDATE CMD 0x2E Failed!" + "Addr:" + addr);
					return false;
				}

				if (len > 0x100)
					len -= 0x100;
				else
					len = 0;

				addr += 0x100;
			}
			Protocol.ProtoPackage(0x2F, 0xFFFFFFFF, 0, null, reclen, precbuf,
					timeout);
			smsg = targetHandler
					.obtainMessage(Global.CMD_UPDATE_PROGRAM_PROGRESS);
			smsg.arg1 = buffer.length;
			smsg.arg2 = buffer.length;
			targetHandler.sendMessage(smsg);
			Log.i(TAG, "UPDATE Success");
			return true;
		}
	}

	public void quit() {
		try {
			BTHeartBeatThread.Quit();
			btHeartBeat = null;
			BTRWThread.Quit();
			btRW = null;


			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectBt() {
		try {
			BTRWThread.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connectBt(String BTAddress) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBT);
			msg.obj = BTAddress;
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public boolean isConnected() {
		if (BTRWThread.IsOpened())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param cmd
	 */
	public void handleCmd(int cmd, Bundle data) {
		if ((null != workHandler) && (null != mLooper)) {
			Message msg = workHandler.obtainMessage(cmd);
			msg.setData(data);
			workHandler.sendMessage(msg);
		} else {
			if (null == workHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}

}
