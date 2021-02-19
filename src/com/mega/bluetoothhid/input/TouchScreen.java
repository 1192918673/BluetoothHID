package com.mega.bluetoothhid.input;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class TouchScreen implements HidDescription {
	private final static String TAG = "TouchScreen";
	private static short mAbsX = 0;
	private static short mAbsY = 0;
	private static byte  mBtnTouch = 0;
	private static boolean mMove = false;

	@Override
	public byte[] getDescription(byte id) {
		return new byte[]{
				(byte)0x05, (byte)0x01, /* USAGE_PAGE (Generic Desktop) */
				(byte)0x09, (byte)0x02, /* USAGE (Mouse) */
				(byte)0xa1, (byte)0x01, /* COLLECTION (Application) */
				(byte)0x09, (byte)0x01,  /* USAGE (Pointer) */
				(byte)0xa1, (byte)0x00,  /* COLLECTION (Physical) */

				(byte)0x85, (byte)0x01,   /* REPORT_ID (1) */
				(byte)0x05, (byte)0x09,   /* USAGE_PAGE (Button) */
				(byte)0x19, (byte)0x01,   /* USAGE_MINIMUM (Button 1) */
				(byte)0x29, (byte)0x03,   /* USAGE_MAXIMUM (Button 3) */
				(byte)0x15, (byte)0x00,   /* LOGICAL_MINIMUM (0) */
				(byte)0x25, (byte)0x01,   /* LOGICAL_MAXIMUM (1) */
				(byte)0x95, (byte)0x03,   /* REPORT_COUNT (3) */
				(byte)0x75, (byte)0x01,   /* REPORT_SIZE (1) */
				(byte)0x81, (byte)0x02,   /* INPUT (Data,Var,Abs) */
				(byte)0x95, (byte)0x01,   /* REPORT_COUNT (1) */
				(byte)0x75, (byte)0x05,   /* REPORT_SIZE (5) */
				(byte)0x81, (byte)0x01,   /* INPUT (Cnst,Var,Abs) */

				(byte)0x05, (byte)0x01,   /* USAGE_PAGE (Generic Desktop) */
				(byte)0x09, (byte)0x30,   /* USAGE (X) */
				(byte)0x09, (byte)0x31,   /* USAGE (Y) */
				(byte)0x09, (byte)0x38,   /* USAGE (WHEEL) */
				(byte)0x15, (byte)0x81,   /* LOGICAL_MINIMUM (-127) */
				(byte)0x25, (byte)0x7f,   /* LOGICAL_MAXIMUM (127) */
				(byte)0x75, (byte)0x08,   /* REPORT_SIZE (8) */
				(byte)0x95, (byte)0x03,   /* REPORT_COUNT (3) */
				(byte)0x81, (byte)0x06,   /* INPUT (Data,Var,Rel) */
				(byte)0xc0,   /* END_COLLECTION */
				(byte)0xc0,  /* END_COLLECTION */
		};
	}

//	@Override
//	public byte[] getDescription(byte id) {
//		return new byte[]{
//				//每行开始的第一字节为该条目的前缀，前缀的格式为：
//				//D7~D4：bTag。D3~D2：bType；D1~D0：bSize。以下分别对每个条目注释。
//				/* 鼠标 */
//				(byte)0x05, (byte)0x01, /* USAGE_PAGE (Generic Desktop) */ // 是一个全部条目。表示用途页为通用桌面设备
//				(byte)0x09, (byte)0x02, /* USAGE (Mouse) */                // 是一个局部条目。表示用途为鼠标
//				(byte)0xa1, (byte)0x01, /* COLLECTION (Application) */     // 表示应用集合，必须要以END_COLLECTION来结束它，见最后的END_COLLECTION
//				(byte)0x09, (byte)0x01,  /* USAGE (Pointer) */             // 是一个局部条目。说明用途为指针集合
//				(byte)0xa1, (byte)0x00,  /* COLLECTION (Physical) */       // 这是一个主条目，开集合，后面跟的数据0x00表示该集合是一个物理集合，用途由前面的局部条目定义为指针集合。
//				/* report_id */
//				(byte)0x85, id,   /* REPORT_ID (1) */
//				/* 左键：1，右键：2，滚轮：3 */
//				(byte)0x05, (byte)0x09,   /* USAGE_PAGE (Button) */      // 这是一个全局条目，选择用途页为按键（Button Page(0x09)）
//				(byte)0x19, (byte)0x01,   /* USAGE_MINIMUM (Button 1) */ // 这是一个局部条目，说明用途的最小值为1。实际上是鼠标左键。--键值从1到3
//				(byte)0x29, (byte)0x03,   /* USAGE_MAXIMUM (Button 3) */ // 这是一个局部条目，说明用途的最大值为3。实际上是鼠标中键。
//				(byte)0x15, (byte)0x00,   /* LOGICAL_MINIMUM (0) */ // 这是一个全局条目，说明返回的数据的逻辑值（就是我们返回的数据域的值啦）
//				(byte)0x25, (byte)0x01,   /* LOGICAL_MAXIMUM (1) */ // 只有0和1，表示有（1-3）3个按键，每个按键有两种状态，分别有0和1表示
//				(byte)0x95, (byte)0x03,   /* REPORT_COUNT (3) */    // 这是一个全局条目，说明数据域数量为3个
//				(byte)0x75, (byte)0x01,   /* REPORT_SIZE (1) */     // 这是一个全局条目，说明每个数据域的长度为1bit。
//				(byte)0x81, (byte)0x02,   /* INPUT (Data,Var,Abs) */// 这是一个主条目，标识上面的3个bits是独立的。
//				(byte)0x95, (byte)0x01,   /* REPORT_COUNT (1) */     //数据域1个
//				(byte)0x75, (byte)0x05,   /* REPORT_SIZE (5) */      //每个数据域的长度为5bit
//				(byte)0x81, (byte)0x01,   /* INPUT (Cnst,Var,Abs) */ //它的属性为常量（即返回的数据一直是0）。这个只是为了凑齐一个字节（前面用了3个bit）
//				/* x，y坐标 */
//				(byte)0x05, (byte)0x01,   /* USAGE_PAGE (Generic Desktop) */ // 这是一个全局条目，选择用途页为普通桌面Generic Desktop Page(0x01)
//				/* x坐标 */
//				(byte)0x09, (byte)0x30,   /* USAGE (X) */     // 这是一个局部条目，说明用途为X轴
//				//(byte)0x09, (byte)0x38,   /* USAGE (WHEEL) */ // 这是一个局部条目，说明用途为滚轴
//				/* x逻辑值 */
//				(byte)0x15, (byte)0x00,              /* LOGICAL_MINIMUM (-127) */ // 这是一个全局条目，说明返回的逻辑最小为-128。
//				(byte)0x26, (byte)0x00, (byte)0x0a,   /* LOGICAL_MAXIMUM (127) */  // 这是一个全局条目，说明返回的逻辑最大为127。
//				/* x物理值 */
//				(byte)0x35, (byte)0x00,
//				(byte)0x46, (byte)0x00, (byte)0x0a,
//				(byte)0x75, (byte)0x10,   /* REPORT_SIZE (16) */      //每个数据域的长度为16bit
//				(byte)0x95, (byte)0x01,   /* REPORT_COUNT (1) */     //数据域1个
//				(byte)0x81, (byte)0x02,
//				/* y坐标 */
//				(byte)0x09, (byte)0x31,   /* USAGE (Y) */     // 这是一个局部条目，说明用途为Y轴
//				/* y逻辑值 */
//				(byte)0x15, (byte)0x00,             // LOGICAL_MINIMUM (0)
//				(byte)0x26, (byte)0x08, (byte)0x07, // LOGICAL_MAXIMUM (1079)
//				/* y物理值 */
//				(byte)0x35, (byte)0x00,             // Physical Minimum (0)
//				(byte)0x46, (byte)0x08, (byte)0x07, // Physical Maximum(1079)
//				(byte)0x75, (byte)0x10,   /* REPORT_SIZE (16) */      //每个数据域的长度为16bit
//				(byte)0x95, (byte)0x01,   /* REPORT_COUNT (1) */     //数据域1个
//				(byte)0x81, (byte)0x02,   /* INPUT (Data,Var,Rel) */ // 这是一个主条目。标识上面的3个数据是绝对值。
//				(byte)0xc0,   /* END_COLLECTION */
//				(byte)0xc0,  /* END_COLLECTION */
//
//				//1byte报告ID + 按键(3bit)+填充行(5bit)1byte + 坐标（16bits*2个）4bytes；所以上报数据就是6bytes；
//				//Car：1280 × 720
//				//Pad：2560 × 1800
//		};
//	}

    /*@Override
	public byte[] getDescription(byte id) {
		return new byte[] {
				(byte)0x05, (byte)0x0d,                         // USAGE_PAGE (Digitizers)
				(byte)0x09, (byte)0x04,                         // USAGE (Touch Screen)
				(byte)0xa1, (byte)0x01,                         // COLLECTION (Application)

				(byte)0x85, id,                                 //   REPORT_ID (Touch)
				(byte)0x09, (byte)0x22,                         //   USAGE (Finger)
				(byte)0xa1, (byte)0x00,                         //   COLLECTION (Physical)

				(byte)0x09, (byte)0x33,                         //     USAGE (Touch)
				(byte)0x15, (byte)0x00,                         //     LOGICAL_MINIMUM (0)
				(byte)0x25, (byte)0x01,                         //     LOGICAL_MAXIMUM (1)
				(byte)0x75, (byte)0x01,                         //     REPORT_SIZE (1)
				(byte)0x95, (byte)0x01,                         //     REPORT_COUNT (1)
				(byte)0x81, (byte)0x02,                         //     INPUT (Data,Var,Abs)

				(byte)0x75, (byte)0x07,                         //     Report Size (7)
				(byte)0x95, (byte)0x01,
				(byte)0x81, (byte)0x03,                         //     Input (Data, Var,Abs) - Button states

				(byte)0x05, (byte)0x01,                         //     USAGE_PAGE (Generic Desktop)
				(byte)0x09, (byte)0x30,                         //     USAGE (X)
				(byte)0x09, (byte)0x31,                         //     USAGE (Y)
				(byte)0x75, (byte)0x10,                         //     REPORT_SIZE (16)
				(byte)0x95, (byte)0x02,                         //     REPORT_COUNT (2)

				(byte)0x15, (byte)0x00,                         //     LOGICAL_MINIMUM (0)
				(byte)0x26, (byte)0xff, (byte)0x7f,             //     LOGICAL_MAXIMUM (32767)

				(byte)0x35, (byte)0x00,                         //     PHYSICAL_MINIMUM (0)
				(byte)0x46, (byte)0xff, (byte)0x7f,             //     PHYSICAL_MAXIMUM (0)

				(byte)0x65, (byte)0x00,                         //     UNIT (None)
				(byte)0x55, (byte)0x00,                         //     UNIT_EXPONENT (0)
				(byte)0x81, (byte)0x02,                         //     INPUT (Data,Var,Abs)

				(byte)0xc0,                                     //   END_COLLECTION
				(byte)0xc0,                                     // END_COLLECTION
		};
	}*/

	final public static class Report implements com.mega.bluetoothhid.input.Report {
		final private ByteBuffer report = ByteBuffer.allocate(6);
		public Queue mQueue = new LinkedList<ByteBuffer>();

		/**
		 * Send Keyboard data to the connected HID Host device. Up to six buttons pressed
		 * simultaneously are supported (not including modifier keys).
		 *
		 * @param report id
		 * @param BTN_TOUCH  1:down 0:up
		 * @param abs_x_low
		 * @param abs_x_high
		 * @param abs_y_low
		 * @param abs_y_high
		 */
		public Report(byte id,
					  byte btn_touch,
					  short x,
					  short y) {
			mBtnTouch = btn_touch;
			mAbsX = x;
			mAbsY = y;

			byte absX_low = (byte)(mAbsX % 256);
			byte absX_high = (byte)(mAbsX / 256);
			byte absY_low = (byte)(mAbsY % 256);
			byte absY_high = (byte)(mAbsY / 256);

			report.put(0, mBtnTouch);
			report.put(1, absX_low);
			report.put(2, absX_high);
			report.put(3, absY_low);
			report.put(4, absY_high);
		}

		public Report() {
			mBtnTouch = 0;
			mAbsY = 0;
			mAbsX = 0;
		}

		public void setBtnTouch(byte id, byte btn_touch, short x, short y) {
			if (x < 0 || x > 32767) {
				btn_touch = 0;
				x = 0;
			}

			if (y < 0 || y > 32767) {
				btn_touch = 0;
				y = 0;
			}

			if (btn_touch == 2) {
				mBtnTouch = 1;
			} else {
				mBtnTouch = btn_touch;
			}

			mAbsX = x;
			mAbsY = y;

			byte absX_low = (byte)(mAbsX % 256);
			byte absX_high = (byte)(mAbsX / 256);
			byte absY_low = (byte)(mAbsY % 256);
			byte absY_high = (byte)(mAbsY / 256);
			Log.d(TAG, "btn_touch="+btn_touch
					+",absX_high="+absX_high+",absX_low="+absX_low
					+",absY_high="+absY_high+",absY_low="+absY_low);

			report.put(0, mBtnTouch);
			report.put(1, absX_low);
			report.put(2, absX_high);
			report.put(3, absY_low);
			report.put(4, absY_high);

		}

		public synchronized void setBtnTouch(byte btn_touch, int x, int y) {
			Log.d(TAG, "btnTouch=" + mBtnTouch + ",x=" + x + ",y=" + y);
			if (btn_touch == 2) {
				mBtnTouch = 1;
			} else if (btn_touch == 0){
				mBtnTouch = btn_touch;
				ByteBuffer report = ByteBuffer.allocate(3);
				report.put(0, (byte) 0); //up
				report.put(1, (byte) 0);
				report.put(2, (byte) 0);
				mQueue.offer(report);
				Log.d(TAG, "set button touch up end!");
				return;
			} else {
				mBtnTouch = btn_touch;
			}

			do {
				int dx = 0;
				if (x > 0) {
					if (x >= 127) {
						x = x - 127;
						dx = 127;
					} else if (x != 0) {
						dx = x;
						x = 0;
					}
					//Log.d(TAG, "1.x>0情况， x="+x+",dx="+dx);
				} else if (x < 0) {
					if (x <= -127) {
						x = x + 127;
						dx = -127;
					} else if (x != 0) {
						dx = x;
						x = 0;
					}
					//Log.d(TAG, "2.x<0情况， x="+x+",dx="+dx);
				}

				int dy = 0;
				if (y > 0) {
					if (y >= 127) {
						y = y - 127;
						dy = 127;
					} else if (y != 0) {
						dy = y;
						y = 0;
					}
					//Log.d(TAG, "3.y>0情况， y="+y+",dy="+dy);
				} else if (y < 0) {
					if (y <= -127) {
						y = y + 127;
						dy = -127;
					} else if (y != 0) {
						dy = y;
						y = 0;
					}
					//Log.d(TAG, "4.y<0情况， y="+y+",dy="+dy);
				}
				//Log.d(TAG, "5.x="+x+",dx="+dx+",y="+y+",dy="+dy);

				ByteBuffer report = ByteBuffer.allocate(3);
				report.put(0, (byte) 0); //move
				report.put(1, (byte) dx);
				report.put(2, (byte) dy);
				Log.d(TAG, "report queue add element! dx="+dx+",dy="+dy);
				mQueue.offer(report);
			} while (x != 0 || y != 0);

			ByteBuffer report = ByteBuffer.allocate(3);
			report.put(0, (byte) 1); //down
			report.put(1, (byte) 0);
			report.put(2, (byte) 0);
			mQueue.offer(report);
			Log.d(TAG, "set button touch end!");
		}

		public boolean isBtnTouch() {
			return (mBtnTouch == 1);
		}

		public void clearAbsXY() {
			mAbsX = 0;
			mAbsY = 0;
			mQueue.clear();
		}

		@Override
		public byte[] build() {
			return this.report.array();
		}

		@Override
		public Queue getReportQueue() {
			return this.mQueue;
		}

		@Override
		public Class getHidDescription() {
			return TouchScreen.class;
		}
	}
}
