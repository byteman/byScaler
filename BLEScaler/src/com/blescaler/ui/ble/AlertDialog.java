package com.blescaler.ui.ble;
//package a.ysyh55.electronicbalance.view;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import a.ysyh55.electronicbalance.R;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class AlertDialog extends Dialog {
//
//	private long mTimeOut = 0;// Ĭ��timeOutΪ0�����޴�
//	private OnTimeOutListener mTimeOutListener = null;// timeOut��Ĵ�����
//	private Timer mTimer = null;// ��ʱ��
//	private AlertDialog dialog;
//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			if (isShowing()) {
//				if (mTimeOutListener != null) {
//					mTimeOutListener.onTimeOut(AlertDialog.this);
//					dismiss();
//				} else {
//					dismiss();
//				}
//			}
//		}
//	};
//	private Animation animation;
//
//	/**
//	 * ����timeOut���ȣ��ʹ�����
//	 * 
//	 * @param t
//	 *            timeoutʱ�䳤��
//	 * @param timeOutListener
//	 *            ��ʱ��Ĵ�����
//	 */
//	public void setTimeOut(long t, OnTimeOutListener timeOutListener) {
//		mTimeOut = t;
//		if (timeOutListener != null) {
//			this.mTimeOutListener = timeOutListener;
//		}
//	}
//
//	private Context context;
//	private String mTitleStr;
//	private TextView title;
//	private ImageView load;
//
//	public AlertDialog(Context context, String titleStr) {
//		super(context, R.style.dialog_style);
//		// TODO Auto-generated constructor stub
//		this.context = context;
//		this.mTitleStr = titleStr;
//	}
//
//	public AlertDialog(Context context, int theme, String titleStr) {
//		super(context, theme);
//		this.context = context;
//		this.mTitleStr = titleStr;
//	}
//
//	@Override
//	public void dismiss() {
//		// TODO Auto-generated method stub
//		if (isShowing()) {
//			super.dismiss();
//		}
//	}
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.dialog_alert);
//		title = (TextView) findViewById(R.id.title);
////		if (mTitleStr != null) {
////			title.setText(mTitleStr);
////		}
//		load = (ImageView) findViewById(R.id.load);
//		animation = AnimationUtils.loadAnimation(context,
//				R.anim.loading);
//		load.startAnimation(animation);
//	}
//
//	@Override
//	protected void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		if (mTimer != null) {
//			mTimer.cancel();
//			mTimer = null;
//		}
//	}
//	@Override
//	public void show() {
//		// TODO Auto-generated method stub
//		if (!this.isShowing()) {
//			super.show();
//			if(load!=null && animation!=null) {
//				load.startAnimation(animation);
//			}
//		}
//	}
//
//	@Override
//	public void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		if (mTimeOut != 0) {
//			mTimer = new Timer();
//			TimerTask timerTast = new TimerTask() {
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Message msg = mHandler.obtainMessage();
//					mHandler.sendMessage(msg);
//				}
//			};
//			mTimer.schedule(timerTast, mTimeOut);
//		}
//	}
//
//	/**
//	 * ͨ��̬Create�ķ�ʽ����һ��ʵ�����
//	 * 
//	 * @param context
//	 * @param time
//	 *            timeoutʱ�䳤��
//	 * @param listener
//	 *            timeOutListener ��ʱ��Ĵ�����
//	 * @return MyProgressDialog ����
//	 */
//	public static AlertDialog createAlertDialog(Context context, String title,
//			long time, OnTimeOutListener listener) {
//		AlertDialog Dialog = new AlertDialog(context, title);
//		if (time != 0) {
//			Dialog.setTimeOut(time, listener);
//		}
//		Dialog.setCancelable(false);
//		return Dialog;
//	}
//
//	/**
//	 * 
//	 * ���?ʱ�ĵĽӿ�
//	 * 
//	 */
//	public interface OnTimeOutListener {
//
//		/**
//		 * ��progressDialog��ʱʱ���ô˷���
//		 */
//		abstract public void onTimeOut(Dialog dialog);
//	}
//
//}
//
