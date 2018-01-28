package com.blescaler.db;

/**
 * Created by byteman on 2018/1/28.
 */

public class UwInfo {
  public UwInfo()
  {
    isValid = false;
    weight = 1.0f;
  }
  boolean isValid;
  float weight;

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public float getWeight() {
    return weight;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }
}
