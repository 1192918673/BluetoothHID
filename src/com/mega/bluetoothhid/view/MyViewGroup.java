package com.mega.bluetoothhid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MyViewGroup extends LinearLayout {
    private final static String TAG = MyViewGroup.class.getSimpleName();

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyViewGroup(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnClickListener(mOnClickListener);
        setOnLongClickListener(mOnLongClickListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final float x = ((MotionEvent) event).getRawX();
        final float y = ((MotionEvent) event).getRawY();
        Log.d(TAG, "onInterceptTouchEvent (" + x + "," + y + ")");
        boolean returnValue = super.onInterceptTouchEvent(event);

        // This method JUST determines whether we want to intercept the motion.
        // If we return true, onTouchEvent will be called

        Log.d(TAG, "onInterceptTouchEvent returnValue:" + returnValue);
        return returnValue;

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

    // ViewGroup自己的Touch事件处理，如果在onInterceptTouchEvent返回true，则会到这里处理，不传入child
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
            // onClick是ACTION_UP后调用的

        }
    };

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            // onLongClick按下到一定的时间就调用了
            Log.d(TAG, "mOnLongClickListener");
            // 如果返回false，则长按结束的ACTION_UP调用onClick
            // 如果返回true，onLongClick后不再调用onClick
            return true;
        }
    };

}