package com.mega.bluetoothhid.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputHost;
import android.bluetooth.BluetoothHidDeviceAppConfiguration;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothHidDeviceCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.mega.bluetoothhid.input.HidDescription;
import com.mega.bluetoothhid.input.Report;
import com.mega.bluetoothhid.input.TouchScreen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class HidDataSender implements BluetoothProfile.ServiceListener {
	private final static String TAG = HidDataSender.class.getSimpleName();

	private final static int MSG_ID_SERVICE_CONNECTED = 0x100;
	private final static int MSG_ID_SERVICE_DISCONNECTED = 0x101;

	private Context mContext;
	private BluetoothAdapter   mAdapter;
	private BluetoothInputHost mInputDevice;
	private byte mReportId;
	private String mName;
	private String mDescription;
	private String mProvidor;
	private byte mSubClass;

	private HidDescription  mHidDescription;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothHidDeviceAppConfiguration mAppConfiguration;

	private BluetoothHidDeviceCallback mCallback = new BluetoothHidDeviceCallback() {
		@Override
		public void onAppStatusChanged(BluetoothDevice pluggedDevice,
									   BluetoothHidDeviceAppConfiguration config, boolean registered) {
			Log.d(TAG, "onAppStatusChanged: pluggedDevice=" + pluggedDevice + " registered="
					+ registered);
			synchronized (HidDataSender.this) {
				mAppConfiguration = config;
			}
		}

		@Override
		public void onConnectionStateChanged(BluetoothDevice device, int state) {
			Log.d(TAG, "onConnectionStateChanged: device=" + device + " state=" + state);
			if (state == BluetoothProfile.STATE_CONNECTED) {
				if (mBluetoothDevice != null && !mBluetoothDevice.equals(device)) {
					mInputDevice.disconnect(device);
				} else if (mBluetoothDevice == null) {
					mBluetoothDevice = device;
				}
			} else if (state == BluetoothProfile.STATE_DISCONNECTED) {
				if (mBluetoothDevice != null && mBluetoothDevice.equals(device)) {
					mBluetoothDevice = null;
				}
			}
		}

		@Override
		public void onGetReport(BluetoothDevice device, byte type, byte id, int bufferSize) {
			Log.d(TAG, "onGetReport: device=" + device + " type=" + type + " id=" + id + " bufferSize="
					+ bufferSize);
		}

		@Override
		public void onSetReport(BluetoothDevice device, byte type, byte id, byte[] data) {
			Log.d(TAG, "onSetReport: device=" + device + " type=" + type + " id=" + id);
		}

		@Override
		public void onSetProtocol(BluetoothDevice device, byte protocol) {
			Log.d(TAG, "onSetProtocol: device=" + device + " protocol=" + protocol);
		}

		@Override
		public void onIntrData(BluetoothDevice device, byte reportId, byte[] data) {
			Log.d(TAG, "onIntrData: device=" + device + " reportId=" + reportId);
		}

		@Override
		public void onVirtualCableUnplug(BluetoothDevice device) {
			Log.d(TAG, "onVirtualCableUnplug: device=" + device);
		}
    };

	public HidDataSender(Context context,
						 byte id,
						 String name,
						 String description,
						 String providor,
						 byte subClass) {
		mContext = context;
		mReportId = id;
		mName = name;
		mDescription = description;
		mProvidor = providor;
		mSubClass = subClass;
		Log.d(TAG, "new HidDataSender name: " + mName + ", description: " + mDescription
				+ ", provider: " + mProvidor + ", subClass: " + subClass + ", reportId: " + id);
		mHidDescription = new TouchScreen();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			Log.e(TAG, "BluetoothAdapter not ready!");
			return;
		}
		mAdapter.getProfileProxy(mContext, this, BluetoothProfile.INPUT_HOST);
	}

	@Override
	public void onServiceConnected(int profile, BluetoothProfile proxy) {
		Log.d(TAG, "onServiceConnected profile: " + profile);
		if (profile == BluetoothProfile.INPUT_HOST) {
			synchronized (HidDataSender.this) {
				mInputDevice = (BluetoothInputHost)proxy;
			}
			mInputDevice.registerApp(
					new BluetoothHidDeviceAppSdpSettings(mName,
							mDescription,
							mProvidor,
							mSubClass,
							mHidDescription.getDescription(mReportId)),
					null,
					new BluetoothHidDeviceAppQosSettings(
							BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
							800, 9, 0, 11250, -1),
					mCallback);
		}
	}

	@Override
	public void onServiceDisconnected(int profile) {
		Log.d(TAG, "onServiceDisconnected profile: " + profile);
		if (profile == BluetoothProfile.INPUT_HOST) {
			synchronized (HidDataSender.this) {
				mInputDevice = null;
			}
		}
	}

	public void cleanup() {
		if (!isReady()) {
			return;
		}

		mAdapter.closeProfileProxy(BluetoothProfile.INPUT_HOST, mInputDevice);
	}

	public void sendReport(Report report, Queue<ByteBuffer> queue) {
		int state = BluetoothProfile.STATE_DISCONNECTED;
		if (!isReady()) {
			Log.e(TAG, "not ready, sendReport failed!");
			return;
		}

		if (mBluetoothDevice == null) {
			Log.e(TAG, "device error, sendReport failed!");
			return;
		}

		state = mInputDevice.getConnectionState(mBluetoothDevice);
		if (state != BluetoothProfile.STATE_CONNECTED) {
			Log.e(TAG, "error state, sendReport failed!");
			return;
		}

		ByteBuffer temp;
		while ((temp = queue.poll()) != null) {
			Log.d(TAG, "send report:" + temp.get(0)+":"+temp.get(1)+":"+temp.get(2));
			mInputDevice.sendReport(mBluetoothDevice, mReportId, temp.array());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.d(TAG, "send report end!");
	}

	public void sendReport(Report report) {
		int state = BluetoothProfile.STATE_DISCONNECTED;
		if (!isReady()) {
			Log.e(TAG, "not ready, sendReport failed!");
			return;
		}

		if (mBluetoothDevice == null) {
			Log.e(TAG, "device error, sendReport failed!");
			return;
		}

		state = mInputDevice.getConnectionState(mBluetoothDevice);
		if (state != BluetoothProfile.STATE_CONNECTED) {
			Log.e(TAG, "error state, sendReport failed!");
			return;
		}

		mInputDevice.sendReport(mBluetoothDevice, mReportId, report.build());
		Log.d(TAG, "send report end!");
	}

	public void connect(BluetoothDevice device) {
		List<BluetoothDevice> connList;

		if (!isReady()) {
			Log.e(TAG, "not ready, connect failed!");
			return;
		}

		mBluetoothDevice = device;
		connList = mInputDevice.getConnectedDevices();
		for (BluetoothDevice dev : connList) {
			if (!dev.equals(device)) {
				mInputDevice.disconnect(dev);
			} else {
				Log.w(TAG, device + " has connected!");
				return;
			}
		}
		mInputDevice.connect(device);
	}

	public void disconnect(BluetoothDevice device) {
		int state = BluetoothProfile.STATE_DISCONNECTED;

		if (!isReady()) {
			Log.e(TAG, "not ready, disconnect failed!");
			return;
		}

		state = mInputDevice.getConnectionState(device);
		if (state != BluetoothProfile.STATE_CONNECTED) {
			Log.e(TAG, "error state, disconnect failed!");
			return;
		}
		mInputDevice.disconnect(device);
	}

	private boolean isReady() {
		synchronized (HidDataSender.this) {
			return (mInputDevice != null);
		}
	}
}
