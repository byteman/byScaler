package com.blescaler.ui.ble;

import com.blescaler.db.Config;
import com.blescaler.util.FloatValue;
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

import com.blescaler.db.CountDao;
import com.blescaler.db.CountRecord;
import com.blescaler.ui.HistoryCountActivity;
import com.blescaler.ui.R;
import com.blescaler.util.IntValue;
import com.blescaler.util.NumberValues;
import com.blescaler.util.Utils;
import com.blescaler.worker.Global;
import com.blescaler.worker.Scaler;
import com.blescaler.worker.WorkService;

public class OneCountFragment extends BaseFragment implements View.OnClickListener {
  View root;
  Button btn_save = null;
  ImageView btn_print = null;
  Button btn_tare = null;
  Button btn_zero = null;
  Button btn_switch_ng = null;
  Button btn_switch_unit = null;

  ImageView img_zero = null;
  ImageView img_still = null;
  ImageView img_tare = null;
  ImageView img_conn = null;

  Button    btn_history = null;
  TextView tv_weight = null, tv_quantity = null, tv_uw = null;
  TextView txtTare = null;
  TextView tv_ng = null;
  TextView tv_calc_weight = null;
  CountDao dao = null;
  Button btn_sample, btn_reset_count, btn_save_uw, btn_preset_uw;

  public int online_count_3s = 0;
  private float uw = 0;
  private boolean bStop = false;
  private static String address;
  private static final int MSG_TIMEOUT = 0x0001;
  private static ProgressDialog progressDialog = null;
  private static Handler mHandler = null;
  protected static final String TAG = "weight_activity";
  private static String unit = "g";
  public  Counter _counter = new Counter();
  private void setOnline(boolean online) {
    if (online) {
      img_conn.getDrawable().setLevel(1);
      //87CEEB
      tv_weight.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
      online_count_3s = 30;
    } else {
      online_count_3s = 0;
      img_conn.getDrawable().setLevel(0);
      tv_weight.setTextColor(Color.rgb(0x80, 0x80, 0x80));
    }
  }

  private void updateState() {

    if (online_count_3s > 0) {
      online_count_3s--;
    }
    if (online_count_3s == 0) {
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

    Message msg = mHandler.obtainMessage(MSG_TIMEOUT);

    mHandler.sendMessageDelayed(msg, 5000);
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

    popConnectProcessBar(this.getActivity());
  }

  private void initUI() {


    img_zero = root.findViewById(R.id.img_zero);
    img_still = root.findViewById(R.id.img_still);
    img_tare = root.findViewById(R.id.img_tare);
    img_conn = root.findViewById(R.id.img_conn_state);
    img_conn.getDrawable().setLevel(0);

    tv_weight = root.findViewById(R.id.tv_weight);
    tv_weight.setOnClickListener(this);

    tv_uw = root.findViewById(R.id.tv_uw);
    txtTare = root.findViewById(R.id.tv_tare);
    tv_quantity = root.findViewById(R.id.tv_quantity);
    tv_ng = root.findViewById(R.id.tv_ng);
    tv_calc_weight = root.findViewById(R.id.tv_calc_weight);



    btn_save = (Button) root.findViewById(R.id.btn_save);
    btn_save.setOnClickListener(this);

    btn_print = (ImageView) root.findViewById(R.id.btn_print);
    btn_print.setOnClickListener(this);

    btn_tare = (Button) root.findViewById(R.id.btn_tare);
    btn_tare.setOnClickListener(this);

    btn_switch_ng = (Button) root.findViewById(R.id.btn_switch_ng);
    btn_switch_ng.setOnClickListener(this);

    btn_history = root.findViewById(R.id.btn_history);
    btn_history.setOnClickListener(this);


    btn_switch_unit = (Button) root.findViewById(R.id.btn_switch_unit);
    btn_switch_unit.setOnClickListener(this);

    btn_zero = (Button) root.findViewById(R.id.btn_zero);
    btn_zero.setOnClickListener(this);

    btn_sample = (Button) root.findViewById(R.id.btn_sample);
    btn_sample.setOnClickListener(this);

    btn_reset_count = (Button) root.findViewById(R.id.btn_reset_uw);
    btn_reset_count.setOnClickListener(this);

    btn_save_uw = (Button) root.findViewById(R.id.btn_save_uw);
    btn_save_uw.setOnClickListener(this);

    btn_preset_uw = (Button) root.findViewById(R.id.btn_preset_uw);
    btn_preset_uw.setOnClickListener(this);

    _counter.load(this.getActivity());

  }

  private void initRes() {
    mHandler = new MHandler(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    root = inflater.inflate(R.layout.fragment_count, container, false);

    initUI();
    initRes();
    dao = new CountDao(this.getActivity());
    return root;
  }

  private void inputSampleDialog() {

    final EditText inputServer = new EditText(this.getActivity());
    inputServer.setFocusable(true);

    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setTitle(this.getString((R.string.input_sample)))
        .setIcon(android.R.drawable.ic_dialog_info)
        .setView(inputServer)
        .setNegativeButton(this.getString(R.string.cancle), null);
    builder.setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        String inputValue = inputServer.getText().toString();

        IntValue count = NumberValues.GetIntValue(inputValue);

        if (count.ok) {
          //通知去采样重量和计算单重.
          _counter.sampleUw(count.value);

        }
      }
    });
    builder.show();
  }
  private void inputTitleDialog() {

    final EditText inputServer = new EditText(this.getActivity());
    inputServer.setFocusable(true);

    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setTitle(this.getString(R.string.input))
        .setIcon(android.R.drawable.ic_dialog_info)
        .setView(inputServer)
        .setNegativeButton(this.getString(R.string.cancle), null);
    builder.setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int which) {
        String inputValue = inputServer.getText().toString();

        FloatValue wgt = NumberValues.GetFloatValue(inputValue);

        if (wgt.ok) {
          _counter.inputUw(wgt.value);

        }
      }
    });
    builder.show();
  }
  public void SendDelayMsg(int code,long time)
  {
    bStop = true;
    mHandler.sendEmptyMessageDelayed(code,time);
  }
  public void Display(Scaler d)
  {

    setOnline(true);
    WorkService.requestReadWgtV2(d.getAddress());
    String dspwet = d.getDispalyWeight() + d.getUnit();
    if (d.isGross_overflow()) {
      dspwet = "-------";
    }
    set_zero_state(d.isZero());
    set_still_state(d.isStandstill());
    set_tare_state(!d.isGross());
    tv_ng.setText(d.isGross()? getText(R.string.gross):getText(R.string.net));
    tv_calc_weight.setText("" + d.getCalcWeight());

    //皮重是传出来的
    txtTare.setText(
        Utils.FormatFloatValue(d.getTare(), d.GetDotNum()) + d.getUnit());
    //显示重量是传出来的.
    tv_weight.setText(dspwet);
    tv_uw.setText(_counter.getUw() + d.getUnit());
    tv_quantity.setText("" + _counter.calcCount(d));


  }
  @Override public void onClick(View arg0) {

    switch (arg0.getId()) {
      case R.id.btn_save_uw:
        _counter.saveUw(this.getActivity());
        break;
      case R.id.btn_reset_uw:
        _counter.resetUw(this.getActivity());
        break;
      case R.id.btn_sample:
        //单位重量重新更新.
        inputSampleDialog();

        break;
      case R.id.btn_history:
        Intent intent = new Intent(this.getActivity(), HistoryCountActivity.class);

        startActivity(intent);

        break;
      case R.id.btn_save:
        if (saveWeight()) {
          Utils.Msgbox(this.getActivity(), getString(R.string.saveok));
        } else {
          Utils.Msgbox(this.getActivity(), getString(R.string.savefail));
        }

        break;
      case R.id.btn_print:
        WorkService.common_msg(address, Global.REG_OPERATION, 99);
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
      case R.id.btn_preset_uw:
        inputTitleDialog();
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

  private boolean saveWeight() {
    // TODO Auto-generated method stub

    CountRecord rec = new CountRecord();

    rec.setCount(tv_quantity.getText().toString());
    rec.setUw(tv_uw.getText().toString());
    rec.setTotalWeight(tv_weight.getText().toString());
    if (dao == null) return false;

    return dao.saveOne(rec);
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
    OneCountFragment f = new OneCountFragment();
    Bundle bundle = new Bundle();

    f.setArguments(bundle);
    return f;
  }
  public void SendCtrlMsg(String address, int code)
  {
    WorkService.common_msg(address, Global.REG_OPERATION, code);
    bStop = false;
  }
  static class MHandler extends Handler {

    WeakReference<OneCountFragment> mActivity;

    MHandler(OneCountFragment activity) {
      mActivity = new WeakReference<OneCountFragment>(activity);
    }


    @Override public void handleMessage(Message msg) {
      OneCountFragment theActivity = mActivity.get();
      switch (msg.what) {
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
          theActivity.Display(d);

          break;
        }
        case Global.MSG_BLE_DISCONNECTRESULT: {
          theActivity.setOnline(false);
          break;
        }
        case Global.MSG_BLE_FAILERESULT: {
          break;
        }

        case Global.MSG_SCALER_CONNECT_OK: {

          if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); //关闭进度条
          }

          break;
        }
        case MSG_TIMEOUT: {

          if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); //关闭进度条
            theActivity.showFailBox(theActivity.getString(R.string.prompt_conn_timeout));
          }
        }

        case Global.MSG_SCALER_POWER_RESULT: {
          //int result = msg.arg1;
          //theActivity.btn_power.refreshPower((float)result/1000.0f);
          break;
        }
      }
    }
  }
};