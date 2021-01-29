package com.mega.bluetoothhid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MyView extends TextView {
    private final static String TAG = MyView.class.getSimpleName();

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context) {
        super(context);
    }

    private void init() {

        setOnClickListener(mOnClickListener);
        setOnLongClickListener(mOnLongClickListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float x = ((MotionEvent) event).getRawX();
        final float y = ((MotionEvent) event).getRawY();
        Log.d(TAG, "dispatchTouchEvent (" + x + "," + y + ")");
        boolean returnValue = super.dispatchTouchEvent(event);
        Log.d(TAG, "dispatchTouchEvent returnValue:" + returnValue);
        return returnValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = ((MotionEvent) event).getRawX();
        final float y = ((MotionEvent) event).getRawY();
        Log.d(TAG, "onTouchEvent (" + x + "," + y + ")");
        boolean returnValue = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent returnValue:" + returnValue);

        return returnValue;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "mOnClickListener");

        }
    };

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "mOnLongClickListener");

            // 如果返回false，则长按结束的ACTION_UP调用onClick
            return false;
        }
    };

}