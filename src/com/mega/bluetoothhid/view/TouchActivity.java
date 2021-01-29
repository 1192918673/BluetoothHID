package com.mega.bluetoothhid.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevicePicker;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.mega.bluetoothhid.bluetooth.HidDataSender;
import com.mega.bluetoothhid.input.TouchScreen.Report;
import com.mega.bluetoothhid.R;

public class TouchActivity extends Activity implements View.OnClickListener {
    private final static String TAG = TouchActivity.class.getSimpleName();

    private BluetoothAdapter   mAdapter;
    private HidDataSender mHidSender;
    private Button mBtnSelect;
    private Report mReport;

    private final static byte REPORT_ID = 1;
    private final static String HID_NAME = "TouchScreen";
    private final static String HID_DESCRIPTION = "Mega Touch Screen";
    private final static String HID_PROVIDER = "Mega Touch Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_activity);
        mBtnSelect = findViewById(R.id.btn_select);
        mBtnSelect.setOnClickListener(this);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mReport = new Report();
        mHidSender = new HidDataSender(this,
                REPORT_ID,
                HID_NAME,
                HID_DESCRIPTION,
                HID_PROVIDER,
                (byte)0x05);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Car：1280 × 720
    private static final float RESOLUTION_INPUT_HOST_X = 1279;
    private static final float RESOLUTION_INPUT_HOST_Y = 719;

    //Pad：2560 × 1800
    private static final float RESOLUTION_DEVICE_X = 2559;
    private static final float RESOLUTION_DEVICE_Y = 1799;

    private float RESOLUTION_SCALE_HEIGHT = (RESOLUTION_DEVICE_Y + 1) / (RESOLUTION_INPUT_HOST_Y + 1);
    private float SHADOW_IN_X = (RESOLUTION_INPUT_HOST_X - RESOLUTION_DEVICE_X / RESOLUTION_SCALE_HEIGHT) / 2;
    private float VIEWABLE_IN_X = SHADOW_IN_X + RESOLUTION_DEVICE_X / RESOLUTION_SCALE_HEIGHT;

    private float lastX = RESOLUTION_INPUT_HOST_X / 2;
    private float lastY = RESOLUTION_INPUT_HOST_Y / 2;
    private float curX = 0;
    private float curY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        curX = event.getRawX();
        curY = event.getRawY();
        if (curX < SHADOW_IN_X)
            curX = (float) SHADOW_IN_X;
        else if (curX > VIEWABLE_IN_X)
            curX = (float) VIEWABLE_IN_X;
        final float x = (curX - lastX) * RESOLUTION_SCALE_HEIGHT;
        final float y = (curY - lastY) * RESOLUTION_SCALE_HEIGHT;
        //final float x = 500;
        //final float y = 500;
        Log.d(TAG, "dispatchTouchEvent("+curX+","+curY+")"+",action:"+event.getAction()
                +",Relative offset("+x+","+y+")");

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mReport.setBtnTouch((byte)1, (int)x, (int)y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //mReport.setBtnTouch((byte)2, (int)x, (int)y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mReport.setBtnTouch((byte)0, (int)x, (int)y);
        }

        mHidSender.sendReport(mReport, mReport.getReportQueue());
        if (!mReport.isBtnTouch()) {
            mReport.clearAbsXY();
            lastX = curX;
            lastY = curY;
        }
        return super.dispatchTouchEvent(event);
    }

    // Touch Screen
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        curX = event.getRawX();
        curY = event.getRawY();
        if (curX < SHADOW_IN_X)
            curX = (float) SHADOW_IN_X;
        else if (curX > VIEWABLE_IN_X)
            curX = (float) VIEWABLE_IN_X;

        float x = curX * RESOLUTION_SCALE_HEIGHT;
        float y = curY * RESOLUTION_SCALE_HEIGHT;
        Log.d(TAG, "dispatchTouchEvent("+curX+","+curY+")"+",action:"+event.getAction()
                +",Touch screen("+x+","+y+")");

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mReport.setBtnTouch((byte)REPORT_ID, (byte)1, (short)x, (short)y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mReport.setBtnTouch((byte)REPORT_ID, (byte)2, (short)x, (short)y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mReport.setBtnTouch((byte)REPORT_ID, (byte)0, (short)x, (short)y);
        }
        mHidSender.sendReport(mReport);
        if (!mReport.isBtnTouch()) {
            mReport.clearAbsXY();
        }

        return super.dispatchTouchEvent(event);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = ((MotionEvent) event).getRawX();
        final float y = ((MotionEvent) event).getRawY();
        //Log.d(TAG, "onTouchEvent (" + x + "," + y + ")");
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select:
                launchDevicePicker();
                break;
        }
    }

    /**
     * Turns on Bluetooth if not already on, or launches device picker if Bluetooth is on
     * @return
     */
    private final void launchDevicePicker() {
        // TODO: In the future, we may send intent to DevicePickerActivity
        // directly,
        // and let DevicePickerActivity to handle Bluetooth Enable.
        if (mAdapter.isEnabled()) {
            Log.v(TAG, "BT already enabled!!");
            Intent in1 = new Intent(BluetoothDevicePicker.ACTION_LAUNCH);
            in1.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            in1.putExtra(BluetoothDevicePicker.EXTRA_NEED_AUTH, false);
            in1.putExtra(BluetoothDevicePicker.EXTRA_FILTER_TYPE,
                    BluetoothDevicePicker.FILTER_TYPE_ALL);
            in1.putExtra(BluetoothDevicePicker.EXTRA_LAUNCH_PACKAGE,
                    "com.mega.bluetoothhid");
            in1.putExtra(BluetoothDevicePicker.EXTRA_LAUNCH_CLASS,
                    TouchActivity.class.getName());
            Log.d(TAG,"Launching " + BluetoothDevicePicker.ACTION_LAUNCH);
            startActivity(in1);
        } else {
            Log.v(TAG, "BT not enabled!");
        }
    }
}