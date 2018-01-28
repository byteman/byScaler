package com.blescaler.ui.ble;

import android.content.Context;
import com.blescaler.db.Config;
import com.blescaler.db.UwInfo;
import com.blescaler.worker.Scaler;

/**
 * Created by byteman on 2018/1/28.
 */


public class Counter {
  public Counter(){}
  private int   _sample_count; //采样个数
  private float _sample_uw; //单位重量
  private float _avg_total; //总重
  private float _tare; //皮重
  private UwInfo _uw_db = new UwInfo();
  private int   _state = 0; //当前的状态. 0->未初始化 1->正在采样 2->采样完成.
  public void load(Context ctx)
  {
      Config.getInstance(ctx).getUw(_uw_db);
  }
  //返回计算的个数.
  public int calcCount(Scaler d)
  {
  //  public void calc(OneCountFragment theActivity, Scaler d) {
  //  int quantity = 0;
  //  //bGetUw==2 单位重量已经被更新，可以刷新.
  //  if (theActivity.bGetUw == 2) {
  //    if (d.isGross()) {
  //      //毛重状态下的 物品个数 = (内部重量 )/单位重量
  //      quantity = (int) (d.getCalcWeight() / theActivity.uw);
  //    } else {
  //      //净重状态下的 物品个数 = (内部重量 - 你发的皮重)/单位重量
  //      quantity = (int) (d.getCalcWeight() - d.getTare() / theActivity.uw);
  //    }
  //  }
  //  //个数是计算出来的.
  //  theActivity.tv_quantity.setText("" + quantity);
  //}
    return 0;
  }
  //采样单位重量
  public boolean sampleUw(int num)
  {
      return false;
  }
  //手工输入单重.
  public boolean inputUw(float uw)
  {
      return false;
  }
  //保存当前的单重.
  public boolean saveUw()
  {
    return false;
  }
  //重新复位单重.清空保存的单重.
  public void resetUw()
  {

  }
}
