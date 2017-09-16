package com.example.administrator.batterytest;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;


public class MainActivity extends Activity {
    private Context mContext;
    private EditText mEtPower;
    private TextView mBtnTry;
    private BatteryState mBsPower;
    private TextView mTvPopup;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_main);
        mEtPower = (EditText) findViewById(R.id.et_power);
        mTvPopup=(TextView)findViewById(R.id.tv_popup);
        mBtnTry = (TextView) findViewById(R.id.btn_try);
        mBtnTry.setText("刷新电量");
//        mBtnTry.setBackground(getResources().getDrawable(R.drawable.maxwell_sun_5_bar));
        mBsPower = (BatteryState) findViewById(R.id.bs_power);
        mBtnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float power = Integer.parseInt(mEtPower.getText().toString());
                float p = power / 100;
                mBsPower.refreshPower(p);
            }
        });
        mTvPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
    }
    private void showPopupWindow(View view) {

        // һ���Զ���Ĳ��֣���Ϊ��ʾ������
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.list_users, null);
        // ���ð�ť�ĵ���¼�

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // ��������true�Ļ���touch�¼���������
                // ���غ� PopupWindow��onTouchEvent�������ã��������ⲿ�����޷�dismiss
            }
        });

        // �������PopupWindow�ı����������ǵ���ⲿ������Back���޷�dismiss����
        // �Ҿ���������API��һ��bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.maxwell_android_message_back_button));

        // ���úò���֮����show
        popupWindow.showAsDropDown(view);

    }
}
