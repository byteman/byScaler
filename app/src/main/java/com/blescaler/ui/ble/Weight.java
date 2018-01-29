package com.blescaler.ui.ble;

import android.content.Context;

import com.blescaler.db.Config;
import com.blescaler.db.UwInfo;
import com.blescaler.util.Utils;
import com.blescaler.worker.Scaler;

/**
 * Created by byteman on 2018/1/28.
 */


public class Weight {
  public Weight(){}
  private int   _sample_count; //采样个数
  private float _sample_uw; //单位重量
  private float _avg_weight; //上一次收到的稳定重量.
  private float _tare; //皮重
  private int _index = 0;
  private float _total = 0; //采样n次的累计重量
  private int _index_count = 5;
  private UwInfo _uw_db = new UwInfo();
  private int   _state = 0; //当前的状态. 0->未初始化 1->正在采样 2->采样完成.
  public void reset()
  {
    _sample_count = 0;
    _sample_uw = 0;
    _state = 0;
    _total = 0;

  }
  public void load(Context ctx)
  {
      reset();
      Config.getInstance(ctx).getUw(_uw_db);

      if(_uw_db.isValid())
      {
        _sample_uw = _uw_db.getWeight();
        _state = 2; //如果保存有重量，就直接进入计数状态
      }
  }

  public int _sample_uw(Scaler d)
  {
     if(_index > 0 )
     {
       _total+=d.getCalcWeight();
       _index--;
       if(_index <= 0)
       {
          _sample_uw = _total / (_index_count*_sample_count);

          _state = 2;
       }
     }
     return 0;
  }
  public String getUw(int dot)
  {
    return Utils.FormatFloatValue(_sample_uw,dot);
  }
  int floatToInt(float f){
    int i = 0;
    if(f>0) //正数
      i = (int) ((f*10 + 5)/10);
    else if(f<0) //负数
      i = (int) ((f*10 - 5)/10);
    else i = 0;

    return i;

  }
  public int _calc_count(Scaler d)
  {
    int quantity = 0;
    if (d.isGross()) {
      //毛重状态下的 物品个数 = (内部重量 )/单位重量
      quantity = floatToInt (d.getCalcWeight() / _sample_uw);
    } else {
      //净重状态下的 物品个数 = (内部重量 - 你发的皮重)/单位重量
      quantity = floatToInt (d.getCalcWeight() - d.getTare() / _sample_uw);
    }
    return quantity;
  }
  //返回计算的个数.
  public int calcCount(Scaler d)
  {

    int quantity = 0;
    if(d.isStandstill() && !d.isGross_overflow())
    {
      _avg_weight = d.getCalcWeight();
    }
    switch(_state)
    {
      case 0:break;
      case 1:
        quantity = _sample_uw(d);
        break;
      case 2:
        quantity = _calc_count(d);
        break;
    }

    return quantity;
  }
  //采样单位重量
  public boolean sampleUw(int num)
  {
      _total = 0;
      _index = 5;
      _state = 1;
      _sample_count = num;
      return true;
  }
  //手工输入单重.
  public boolean inputUw(float uw)
  {
      _sample_uw = uw;
      _state = 2;
      return true;
  }
  //保存当前的单重.
  public boolean saveUw(Context ctx)
  {
    if(_state != 2) return false;
    Config.getInstance(ctx).setUw(_sample_uw);
    return true;
  }
  //重新复位单重.清空保存的单重.
  public void resetUw(Context ctx)
  {
    reset();
    Config.getInstance(ctx).clearUw();
  }
}
