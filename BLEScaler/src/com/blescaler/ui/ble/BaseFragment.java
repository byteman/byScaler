package com.blescaler.ui.ble;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Clude on 13-12-2.
 */
public class BaseFragment extends Fragment {

  public Context mContext;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mContext = inflater.getContext();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

 
  public void makeToast(CharSequence msg) {
    makeToast(msg, Toast.LENGTH_SHORT);
  }

  public void makeToast(CharSequence msg, int duration) {
    Toast.makeText(mContext, msg, duration).show();
  }

  
  public void makeToast(int resId) {
    makeToast(resId, Toast.LENGTH_SHORT);
  }

  
  public void makeToast(int resId, int duration) {
    Toast.makeText(mContext, resId, duration).show();
  }

  
  public void toggleSoftInput() {
    InputMethodManager imm =
        (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm.isActive()) {
      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  
  public void hideSoftInput(EditText et) {
    InputMethodManager imm =
        (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm.isActive()) {
      imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
  }
}
