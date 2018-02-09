package com.blescaler.ui.ble;

import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blescaler.db.WeightDao;
import com.blescaler.db.WeightRecord;
import com.blescaler.ui.HistoryWeightActivity;
import com.blescaler.ui.R;
import com.blescaler.util.IntValue;
import com.blescaler.util.NumberValues;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class OneWeightFragment extends BaseFragment implements View.OnClickListener {
  View root;
  Button btn_save = null;
  ImageView btn_print = null;
  Button btn_tare = null;
  Button btn_zero = null;
  Button btn_switch_ng = null;
  Button btn_switch_unit = null;
  Button btn_history = null;
  ImageView img_zero = null;
  ImageView img_still = null;
  ImageView img_tare = null;
  ImageView img_conn = null;
  TextView tv_weight = null, tv_unit = null;
  TextView txtTare = null;
  TextView txtNG = null;
  TextView txtState = null;
  public long lSaveWetTs = 0;
  public boolean  bSaveWet = false;
  public boolean  bStop = false;
  public int  online_cout_3s = 0;
  private static String address;


  private static ProgressDialog progressDialog = null;
  private static Handler mHandler = null;
  protected static final String TAG = "weight_activity";
  private static String unit = "g";
  private boolean isGross = true;
  WeightDao dao = null;

  private void setOnline(boolean online) {
    if (online) {
      img_conn.getDrawable().setLevel(1);
      //87CEEB
      tv_weight.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
      online_cout_3s = 30;
    } else {
      online_cout_3s = 0;
      img_conn.getDrawable().setLevel(0);
      tv_weight.setTextColor(Color.rgb(0x80, 0x80, 0x80));
    }

  }

  private void updateState() {

    if (online_cout_3s > 0) {
      online_cout_3s--;
    }
    if (online_cout_3s == 0) {
      setOnline(false);
    }
  }

  private void reReadWgt() {

    if (!bStop) {
      if (WorkService.hasConnected(address)) {
        WorkService.common_msg(address, Global.REG_OPERATION, 6);
      }
    }
  }

  private Runnable watchdog = new Runnable() {

    @Override public void run() {
      // TODO Auto-generated method stub

      reReadWgt();
      updateState();
      mHandler.postDelayed(this, 100);
    }
  };

  private void popConnectProcessBar(Context ctx) {
    address = WorkService.getDeviceAddress(this.getActivity(), 0);
    if (address == "") {
      showFailBox(ctx.getString(R.string.prompt_scan));
      return;
    }
    if (WorkService.hasConnected(address)) return;

    if (progressDialog != null && progressDialog.isShowing()) {
      return;
    }
    progressDialog = ProgressDialog.show(ctx, ctx.getString(R.string.prompt_title),
        ctx.getString(R.string.connect_ble));

    if (!WorkService.requestConnect(address)) {

      if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss(); //关闭进度条
      //Toast.makeText(this.getActivity(),"连接错误",Toast.LENGTH_SHORT).show();
      return;
    }

    Message msg = mHandler.obtainMessage(Constant.MSG_TIMEOUT);

    mHandler.sendMessageDelayed(msg, 5000);
  }

  public void onPause() {
    super.onPause();
    mHandler.removeCallbacks(watchdog);

    WorkService.delHandler(mHandler);
  }

  @Override public void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    //super.onPause();
    mHandler.removeCallbacks(watchdog);

    WorkService.delHandler(mHandler);
    Log.e(TAG, "onStop");
  }

  @Override public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    WorkService.addHandler(mHandler);
    mHandler.postDelayed(watchdog, 100);

    updateState();
    tv_unit.setText(unit);

    popConnectProcessBar(this.getActivity());
  }

  private void initUI() {

    //--------------------------------
    tv_weight = root.findViewById(R.id.tv_weight);
    tv_unit =  root.findViewById(R.id.textView2);
    txtTare =  root.findViewById(R.id.txtTare);
    txtState = root.findViewById(R.id.id_state);
    txtNG = root.findViewById(R.id.tv_oneweight_net_title);
    tv_weight.setOnClickListener(this);

    //----------------------------------------------------------
    img_zero = (ImageView) root.findViewById(R.id.img_zero);
    img_still = (ImageView) root.findViewById(R.id.img_still);
    img_tare = (ImageView) root.findViewById(R.id.img_tare);
    img_conn = root.findViewById(R.id.img_conn_state);
    img_conn.getDrawable().setLevel(0);

    // Buttons
    btn_switch_unit = root.findViewById(R.id.btn_switch_unit);
    btn_switch_unit.setOnClickListener(this);

    btn_switch_ng =  root.findViewById(R.id.btn_switch_ng);
    btn_switch_ng.setOnClickListener(this);

    btn_tare = root.findViewById(R.id.btn_tare);
    btn_tare.setOnClickListener(this);

    btn_zero = root.findViewById(R.id.btn_zero);
    btn_zero.setOnClickListener(this);

    btn_save = root.findViewById(R.id.btn_save);
    btn_save.setOnClickListener(this);

    btn_print = root.findViewById(R.id.btn_print);
    btn_print.setOnClickListener(this);

    btn_history = root.findViewById(R.id.btn_history);
    btn_history.setOnClickListener(this);
    //----------------------------------------

  }

  private void initRes() {
    mHandler = new MHandler(this);

  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    root = inflater.inflate(R.layout.activity_oneweight_table, container, false);

    initUI();
    initRes();
    dao = new WeightDao(this.getActivity());
    return root;
  }

  public void SendDelayMsg(int code,long time)
  {
    bStop = true;
    mHandler.sendEmptyMessageDelayed(code,time);
  }
  @Override public void onClick(View arg0) {

    switch (arg0.getId()) {

      case R.id.btn_history:
        Intent intent = new Intent(this.getActivity(), HistoryWeightActivity.class);

        startActivity(intent);

        break;
      case R.id.btn_save:
        sendSaveCmd();

        break;

      case R.id.btn_tare:
        SendDelayMsg(Constant.MSG_DISCARD_TARE,300);

        break;
      case R.id.tv_weight:
        popConnectProcessBar(this.getActivity());

        break;
      case R.id.btn_zero:
        SendDelayMsg(Constant.MSG_SET_ZERO,300);
        break;
      case R.id.btn_switch_unit:
        //净重和毛重切换
        SendDelayMsg(Constant.MSG_SWITCH_UNIT,300);
        break;
      case R.id.btn_switch_ng:
        //单位切换
        SendDelayMsg(Constant.MSG_SWITCH_NG,300);
        break;

    }
  }
  private void sendSaveCmd()
  {
    bSaveWet = true;
    lSaveWetTs = System.currentTimeMillis();
  }
  private boolean saveWeight(Scaler d) {
    // TODO Auto-generated method stub
    //净重的时候 毛重=显示重量+皮重
    //毛重的时候 毛重=显示重量
    if(!bSaveWet)
    {
        return false;
    }
    bSaveWet = false;
    if((System.currentTimeMillis() -  lSaveWetTs) > 1000)
    {
      Utils.Msgbox(this.getActivity(), getString(R.string.saveok));

      return  false;
    }

    WeightRecord rec = new WeightRecord();

    String gross = Utils.FormatFloatValue(d.getGross(),d.GetDotNum()) + d.getUnit();
    String tare  = Utils.FormatFloatValue(d.getTare(),d.GetDotNum())  + d.getUnit();
    String net   = Utils.FormatFloatValue(d.getNet(),d.GetDotNum())   + d.getUnit();


    rec.setGross(gross);
    rec.setTare(tare);
    rec.setNet(net);

    if (dao == null)
    {
      Utils.Msgbox(this.getActivity(), getString(R.string.savefail));

      return false;
    }
    boolean ok = dao.saveWeight(rec);
    if(ok)
    {
      Utils.Msgbox(this.getActivity(), getString(R.string.saveok));
    }else {
      Utils.Msgbox(this.getActivity(), getString(R.string.savefail));

    }
    return ok;
  }
  public void SendCtrlMsg(String address, int code)
  {
    WorkService.common_msg(address, Global.REG_OPERATION, code);
    bStop = false;
  }
  private void showFailBox(String msg) {
    new AlertDialog.Builder(this.getActivity()).setTitle(
        this.getString(R.string.prompt_title))//设置对话框标题

        .setMessage(msg)//设置显示的内容

        .setPositiveButton(this.getString(R.string.ok),
            new DialogInterface.OnClickListener() {//添加确定按钮

              @Override

              public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                // TODO Auto-generated method stub

                dialog.dismiss();
              }
            }).show();//在按键响应事件中显示此对话框
  }

  public void set_zero_state(boolean bZero) {
    img_zero.getDrawable().setLevel(bZero ? 1 : 0);
  }

  public void set_still_state(boolean bStill) {
    img_still.getDrawable().setLevel(bStill ? 1 : 0);
  }

  public void set_tare_state(boolean bTare) {
    img_tare.getDrawable().setLevel(bTare ? 1 : 0);
  }

  public static Fragment newFragment() {
    OneWeightFragment f = new OneWeightFragment();
    Bundle bundle = new Bundle();

    f.setArguments(bundle);
    return f;
  }

  static class MHandler extends Handler {

    WeakReference<OneWeightFragment> mActivity;

    MHandler(OneWeightFragment activity) {
      mActivity = new WeakReference<OneWeightFragment>(activity);
    }

    @Override public void handleMessage(Message msg) {
      OneWeightFragment theActivity = mActivity.get();
      switch (msg.what) {
        case Constant.MSG_TIMEOUT: {
          try {
            if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss(); //关闭进度条
              theActivity.showFailBox(theActivity.getString(R.string.prompt_conn_timeout));
            }
          }catch (Exception e)
          {
              e.printStackTrace();
          }
        }
        case Constant.MSG_SET_ZERO:
        {
          theActivity.SendCtrlMsg(address, 1);
          break;
        }
        case Constant.MSG_SWITCH_UNIT:
        {
          theActivity.SendCtrlMsg(address, 14);
          break;
        }
        case Constant.MSG_DISCARD_TARE:
        {
          theActivity.SendCtrlMsg(address, 2);
          break;
        }
        case Constant.MSG_SWITCH_NG:
        {
          theActivity.SendCtrlMsg(address, 5);
          break;
        }
        case Global.MSG_BLE_WGTRESULT_V2: {

          Scaler d = (Scaler) msg.obj;

          if (d == null) {
            return;
          }

          theActivity.setOnline(true);

          String display = d.getDispalyWeight();
          if (d.isGross_overflow()) {
            display = "-------";
          }
          theActivity.tv_weight.setText(display);
          //theActivity.set_zero_state(d.isZero());
          theActivity.set_still_state(d.isStandstill());
          theActivity.set_tare_state(!d.isGross()); //皮重状态就是净重状态.
          theActivity.tv_unit.setText(d.getUnit());
          theActivity.txtState.setText("" + d.getState());
          theActivity.txtNG.setText(d.isGross() ? theActivity.getText(R.string.gross)
              : theActivity.getText(R.string.net));
          theActivity.txtTare.setText(
              Utils.FormatFloatValue(d.getTare(), d.GetDotNum()) + d.getUnit());
          theActivity.saveWeight(d);
          break;
        }
        case Global.MSG_BLE_DISCONNECTRESULT: {

          theActivity.setOnline(false);

          break;
        }
        case Global.MSG_BLE_FAILERESULT: {

          //Toast.makeText(theActivity.getActivity(), WorkService.getFailReason(msg.arg1) +"  " + WorkService.getFailType(msg.arg2), Toast.LENGTH_SHORT).show();
          break;
        }

        case Global.MSG_SCALER_CONNECT_OK: {

          try {
            if (progressDialog != null && progressDialog.isShowing()) {

              progressDialog.dismiss(); //关闭进度条
            }
          }
          catch(Exception e) {
          }
          //Toast.makeText(theActivity.getActivity(),"all connect",Toast.LENGTH_SHORT).show();

          break;
        }

        case Global.MSG_SCALER_CTRL_RESULT: {
          //Toast.makeText(theActivity.getActivity(), "success!", Toast.LENGTH_SHORT).show();
          break;
        }
        case Global.MSG_SCALER_POWER_RESULT: {
          //					int result = msg.arg1;
          //					theActivity.btn_power.refreshPower((float)result/1000.0f);
          break;
        }
      }
    }
  }
};