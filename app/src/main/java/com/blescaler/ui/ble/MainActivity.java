package com.blescaler.ui.ble;

import android.R.color;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.blescaler.db.Config;
import com.blescaler.ui.CalibActivity;
import com.blescaler.ui.ConfigActivity;
import com.blescaler.ui.ConnectBTPairedActivity;
import com.blescaler.ui.DeviceScanActivity;
import com.blescaler.ui.R;
import com.blescaler.ui.ScalerParamActivity;
import com.blescaler.ui.SearchBTActivity;
import com.blescaler.ui.SysParamActivity;
import com.blescaler.view.ImageTextView;

public class MainActivity extends BaseActivity implements OnTouchListener, OnClickListener {


  private DrawerLayout mDrawerLayout;
  TextView menu_serach, menu_one_scaler, menu_print, //
      menu_calib, menu_para, menu_data_report, menu_setting, menu_count, prevLeftMenu;
  GestureDetector simpleGestureListener;

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @BindView(R.id.tv_menu_weight) ImageTextView tvMenuWeight;
  @BindView(R.id.tv_menu_count) ImageTextView tvMenuCount;
 // @BindView(R.id.tv_menu_history) ImageTextView tvMenuHistory;
  @BindView(R.id.tv_menu_set) ImageTextView tvMenuSet;
  @BindView(R.id.tv_menu_device) ImageTextView tvMenuDevice;
  //@BindView(R.id.tv_menu_version) ImageTextView tvMenuVersion;
  @BindView(R.id.tv_menu_printer) ImageTextView tvMenuPrinter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.layout_content);
    ButterKnife.bind(this);
    initView();

    simpleGestureListener = new GestureDetector(this, new GestureDetector.OnGestureListener() {

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null) return false;
        if (e2 == null) return false;
        if (e1.getX() - e2.getX() < -20) {

          mDrawerLayout.openDrawer(Gravity.LEFT);
          return true;
        }
        if (e1.getX() - e2.getX() > 20) {

          mDrawerLayout.closeDrawers();
          return true;
        }
        return false;
      }

      @Override public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

      }

      @Override public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

      }

      @Override public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
      }
    });
    int page = Config.getInstance(this).getLastPage();
    goToFragment(page);
  }

  private void initView() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

    mDrawerLayout.setOnTouchListener(this);
    mDrawerLayout.setFocusable(true);
    mDrawerLayout.setClickable(true);
    mDrawerLayout.setLongClickable(true);

    //menu_serach = (TextView) findViewById(R.id.menu_serach);
    //menu_one_scaler = (TextView) findViewById(R.id.menu_one_scaler);
    //menu_print = (TextView) findViewById(R.id.menu_print);
    //menu_calib = (TextView) findViewById(R.id.menu_calib);
    //menu_para = (TextView) findViewById(R.id.menu_para);
    //menu_data_report = (TextView) findViewById(R.id.menu_data_report);
    //menu_setting = (TextView) findViewById(R.id.menu_setting);
    //menu_count = (TextView) findViewById(R.id.menu_count);
    //menu_serach.setOnClickListener(this);
    //menu_one_scaler.setOnClickListener(this);
    //menu_print.setOnClickListener(this);
    //menu_calib.setOnClickListener(this);
    //menu_para.setOnClickListener(this);
    //menu_data_report.setOnClickListener(this);
    //menu_setting.setOnClickListener(this);
    //menu_count.setOnClickListener(this);

    //ActionBar actionBar=getActionBar();
    //actionBar.setDisplayShowHomeEnabled(true);

    //当工具栏发生变化的时候
    getWindow().getDecorView()
        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
          @Override public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
              getWindow().getDecorView()
                  .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
          }
        });
  }
  @OnClick({
      R.id.tv_menu_weight, R.id.tv_menu_count,
      R.id.tv_menu_device, R.id.tv_menu_set,R.id.tv_menu_printer
  })
  @Override public void onClick(View v) {
    mDrawerLayout.closeDrawers();
    switch (v.getId()) {
      case R.id.tv_menu_weight:
        if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
        goToFragment(7);
        tvMenuWeight.setBackgroundColor(getResources().getColor(R.color.set_item_click));
        prevLeftMenu = tvMenuWeight;
        break;
      case R.id.tv_menu_count:
        if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
        tvMenuCount.setBackgroundColor(getResources().getColor(R.color.set_item_click));
        prevLeftMenu = tvMenuCount;
        goToFragment(3);
        break;
//      case R.id.tv_menu_history:
//        break;
//      case R.id.tv_menu_set:
//        break;
      case R.id.tv_menu_device:
        if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
        goToFragment(2);
        tvMenuDevice.setBackgroundColor(getResources().getColor(R.color.set_item_click));
        prevLeftMenu = tvMenuDevice;
        break;
      case R.id.tv_menu_set:
          if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
          tvMenuSet.setBackgroundColor(getResources().getColor(R.color.set_item_click));
          prevLeftMenu = tvMenuSet;
          goToFragment(4);

          break;

      case R.id.tv_menu_printer:
        if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
        tvMenuSet.setBackgroundColor(getResources().getColor(R.color.set_item_click));
        prevLeftMenu = tvMenuSet;
        //goToFragment(5);
        break;

      default:break;


      //case R.id.menu_print:
      //  //			if (prevLeftMenu != null)
      //  //				prevLeftMenu.setBackgroundColor(color.transparent);
      //  //			goToFragment(6);
      //  //			menu_print.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  //			prevLeftMenu = menu_print;
      //  break;
      //case R.id.menu_calib:
      //  if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
      //  goToFragment(8);
      //
      //  menu_calib.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  prevLeftMenu = menu_calib;
      //  break;
      //case R.id.menu_para:
      //  if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
      //  menu_para.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  prevLeftMenu = menu_para;
      //  goToFragment(4);
      //
      //  break;
      //case R.id.menu_data_report:
      //  if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
      //  goToFragment(1);
      //  menu_data_report.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  prevLeftMenu = menu_data_report;
      //  break;
      //case R.id.menu_setting:
      //  if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
      //  menu_data_report.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  prevLeftMenu = menu_setting;
      //  //StartActivity(SysParamActivity.class, null);
      //  goToFragment(9);
      //  break;
      //case R.id.menu_count:
      //  if (prevLeftMenu != null) prevLeftMenu.setBackgroundColor(color.transparent);
      //  menu_count.setBackgroundColor(getResources().getColor(R.color.set_item_click));
      //  prevLeftMenu = menu_count;
      //  goToFragment(3);
      //
      //  break;
    }
  }

  private void goToFragment(int pos) {

    Fragment newFragment = null;
    if (pos == 1) {

    }
    if (pos == 2) {

      Intent intent = new Intent(this, DeviceScanActivity.class);
      startActivity(intent);
      return;
    }
    if (pos == 3)
    {
      newFragment = OneCountFragment.newFragment();
      Config.getInstance(this).setLastPage(3);
    }
    if (pos == 4) {

      Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
      intent.putExtra("address", "00");
      startActivity(intent);

      return;
    }
    if (pos == 5) {

      Intent intent = new Intent(this, SearchBTActivity.class);
      startActivity(intent);
      return;
    }
    if (pos == 7)
    {
      newFragment = OneWeightFragment.newFragment();
      Config.getInstance(this).setLastPage(7);
    }
    if (pos == 8) {
      Intent intent = new Intent(MainActivity.this, CalibActivity.class);
      intent.putExtra("address", "00");
      startActivity(intent);

      return;
    }
    if (pos == 9) {
      Intent intent = new Intent(MainActivity.this, SysParamActivity.class);
      intent.putExtra("address", "00");
      startActivity(intent);

      return;
    }
    if (newFragment == null) return;

    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    fragmentTransaction.replace(R.id.content_frame, newFragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commitAllowingStateLoss();
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    return simpleGestureListener.onTouchEvent(event);
  }

  private long exitTime = 0;

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
      if ((System.currentTimeMillis() - exitTime) > 2000) {
        Toast.makeText(getApplicationContext(), R.string.exit_app, Toast.LENGTH_SHORT).show();
        exitTime = System.currentTimeMillis();
      } else {

        moveTaskToBack(false);

        finish();
        System.exit(0);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      getWindow().getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }

}
