package com.blescaler.ui.ble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import com.blescaler.ui.R;


/**
 * Created by Administrator on 2015/12/3.
 */
public class BatteryState extends View {
    private Context mContext;
    private float width;
    private float height;
    private Paint mPaint;
    private float powerQuantity=0.5f;//电量


    public float getPowerQuantity() {
        return powerQuantity;
    }

    public void setPowerQuantity(float powerQuantity) {
        this.powerQuantity = powerQuantity;
    }


    public BatteryState(Context context) {
        super(context);
        mContext=context;
        mPaint = new Paint();

    }

    public BatteryState(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mPaint = new Paint();
    }

    public BatteryState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        计算控件尺寸
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//绘制界面
        super.onDraw(canvas);
        Bitmap batteryBitmap=ReadBitMap(mContext, R.drawable.battery_empty);//读取图片资源
        width=batteryBitmap.getWidth();
        height=batteryBitmap.getHeight();
        if (powerQuantity>0.3f&&powerQuantity<=1) {
//            电量少于30%显示红色
            mPaint.setColor(Color.GREEN);
        }
        else if (powerQuantity>=0&&powerQuantity<=0.3)
        {
            mPaint.setColor(Color.RED);
        }
//        计算绘制电量的区域
        float right=width*0.94f;
        float left=width*0.21f+(right-width*0.21f)*(1-powerQuantity);
        float tope=height*0.45f;
        float bottom=height*0.67f;

        canvas.drawRect(left,tope,right,bottom,mPaint);
        canvas.drawBitmap(batteryBitmap, 0, 0, mPaint);
    }
    public Bitmap ReadBitMap(Context context, int resId)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    public void refreshPower(float power)
    {
        powerQuantity=power;
        if (powerQuantity>1.0f)
            powerQuantity=1.0f;
        if (powerQuantity<0)
            powerQuantity=0;
        invalidate();
    }
}
