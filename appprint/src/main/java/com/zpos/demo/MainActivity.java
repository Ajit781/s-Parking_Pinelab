package com.zpos.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imagpay.PrnStrFormat;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.enums.PrnTextFont;
import com.imagpay.mpos.MposHandler;
import com.zpos.ui.ImageInfo;
import com.zpos.ui.MyPagerAdapter;
import com.zxing.android.QRtestActivity;

import java.io.InputStream;
import java.util.ArrayList;

@SuppressLint({"ResourceType", "HandlerLeak"})
public class MainActivity extends Activity implements
		MyPagerAdapter.notify, SwipeListener {
	ArrayList<ImageInfo> data;
	private static TextView mynum;
	MyPagerAdapter adapter;
	Button btn;

	/**** SDK ***/
	private static String TAG = "PosDemo";
	private MposHandler handler;
	private Settings setting;
	private Context context;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101:
				Toast.makeText(MainActivity.this,
						"Printing now,pls wait for a moment", Toast.LENGTH_LONG)
						.show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mynum = (TextView) findViewById(R.id.mynum);
		initSDK();
		initData();
		initView();
	}

	/**** SDK ***/
	private void initSDK() {
		// Init SDK,call singleton function,so that you can keeping on the
		// connect in the whole life cycle
		handler = MposHandler.getInstance(this);
		handler.setShowLog(true);
		// add linstener for connection
		handler.addSwipeListener(this);
		setting = Settings.getInstance(handler);
		// power on the device when you need to read card or print
		setting.mPosPowerOn();
		try {
			// for 90,delay 1S and then connect
			// Thread.sleep(1000);
			// connect device via serial port
			if (!handler.isConnected()) {
				sendMessage("Connect Res:" + handler.connect());
			} else {
				handler.close();
				sendMessage("ReConnect Res:" + handler.connect());
			}
		} catch (Exception e) {
			sendMessage(e.getMessage());

		}
		// add linstener for read IC chip card
		// handler.addEMVListener(this);
	}

	@Override
	public void onItemClick(int position) {
		switch (position) {
		case 0:
		    //printText();
			printTicket();
			//printQueue();
			break;
		case 1:
			startActivity(new Intent(MainActivity.this, CardtestActivity.class));
			break;
		case 2:
			readPsam();
			break;
		case 3:
			startActivity(new Intent(MainActivity.this, QRtestActivity.class));
			break;
		case 4:
			startActivity(new Intent(MainActivity.this,
					FingerprintActivity.class));
			break;

		case 5:
			runOnUiThread(new Runnable() {
				public void run() {
					String ver = setting.readVersion();
					String sn = setting.setReadSN();
					showInfor(ver, sn);
					Toast.makeText(getApplicationContext(),
							setting.getSDKversion(), Toast.LENGTH_LONG).show();
				}
			});
			break;
		default:
			break;
		}
	}

	private void readPsam() {
		int slot = 1;
		String respp1 = setting.reset(slot);
		if (respp1 != null) {
			sendMessage("" + getResources().getText(R.string.testpsam1ok));
			showToast("" + getResources().getText(R.string.testpsam1ok));
			Toast.makeText(getApplicationContext(),
					getResources().getText(R.string.testpsam1ok),
					Toast.LENGTH_LONG).show();
			String apduap1 = "0084000008";
			sendMessage("apdu send:" + apduap1);
			String resp3p1 = "";
			resp3p1 = setting.getDataWithAPDUForStr(slot, apduap1);
			sendMessage("resp:" + resp3p1);
			showToast("apdu send:" + apduap1 + ";\n" + "apdu resp:"
					+ resp3p1);
			Toast.makeText(
					getApplicationContext(),
					"apdu send:" + apduap1 + ";\n" + "apdu resp:" + resp3p1,
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getText(R.string.testpsam1check),
					Toast.LENGTH_LONG).show();
			sendMessage(""
					+ getResources().getText(R.string.testpsam1check));
		}
		setting.off(slot);
	}

	protected void sendMessage(String string) {
		Log.i(TAG, "==>:" + string);
	}

	private void printText() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (setting.isPrinting()) {// check print status
					Message msg = new Message();
					msg.what = 101;
					mHandler.sendMessage(msg);
					Log.d(TAG, "setting.isPrinting():" + setting.isPrinting());
					return;
				}
				StringBuffer receipts = new StringBuffer();
				receipts.append("The cardholder stub   \nPlease properly keep\n");
				// 1. print text with default style and font
				setting.prnStr(receipts.toString());
				receipts.setLength(0);
				receipts.append("Merchant Name:ABC\n");
				receipts.append("Merchant No.:846584000103052\n");
				receipts.append("Terminal No.:12345678\n");
				PrnStrFormat psf = new PrnStrFormat();
				psf.setFont(PrnTextFont.MONOSPACE);// specified font
				// // 2. print text with specified style and font
				setting.prnStr(receipts.toString(), psf);
				receipts.setLength(0);
				receipts.append("print with DejaVuSansMono.ttf\n");
				receipts.append("Trade Type:consumption\n");
				receipts.append("Serial No.:000024  \nAuthenticode:096706\n");
				receipts.append("Date/Time:2016/09/01 11:27:12\n");
				receipts.append("Ref.No.:123456789012345\n");
				receipts.append("Amount:$ 100.00\n");
				// // 3. print text with custom font
				psf.setFont(PrnTextFont.CUSTOM);
				psf.setAm(getAssets());
				psf.setPath("fonts/DejaVuSansMono.ttf");
				setting.prnStr(receipts.toString(), psf);

				receipts.setLength(0);
				receipts.append("print with DejaVuSansMono-Oblique.ttf\n");
				receipts.append("Trade Type:consumption\n");
				receipts.append("Serial No.:000024  \nAuthenticode:096706\n");
				receipts.append("Date/Time:2016/09/01 11:27:12\n");
				receipts.append("Ref.No.:123456789012345\n");
				receipts.append("Amount:$ 100.00\n");
				// 3. print text with custom font
				psf.setFont(PrnTextFont.CUSTOM);
				psf.setAm(getAssets());
				psf.setPath("fonts/DejaVuSansMono-Oblique.ttf");
				setting.prnStr(receipts.toString(), psf);

				receipts.setLength(0);
				receipts.append("print with DejaVuSansMono-BoldOblique.ttf\n");
				receipts.append("Trade Type:consumption\n");
				receipts.append("Serial No.:000024  \nAuthenticode:096706\n");
				receipts.append("Date/Time:2016/09/01 11:27:12\n");
				receipts.append("Ref.No.:123456789012345\n");
				receipts.append("Amount:$ 100.00\n");
				// 3. print text with custom font
				psf.setFont(PrnTextFont.CUSTOM);
				psf.setAm(getAssets());
				psf.setPath("fonts/DejaVuSansMono-BoldOblique.ttf");
				setting.prnStr(receipts.toString(), psf);

				receipts.setLength(0);
				receipts.append("print with DejaVuSansMono-Bold.ttf\n");
				receipts.append("Trade Type:consumption\n");
				receipts.append("Serial No.:000024  \nAuthenticode:096706\n");
				receipts.append("Date/Time:2016/09/01 11:27:12\n");
				receipts.append("Ref.No.:123456789012345\n");
				receipts.append("Amount:$ 100.00\n");
				// 3. print text with custom font
				psf.setFont(PrnTextFont.CUSTOM);
				psf.setAm(getAssets());
				psf.setPath("fonts/DejaVuSansMono-Bold.ttf");
				setting.prnStr(receipts.toString(), psf);
				// 4. start to print
				setting.prnStart();
			}
		}).start();
	}

	private void printTicket() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Process.setThreadPriority(-20);
				if (setting.isPrinting()) {// check print status
					Message msg = new Message();
					msg.what = 101;
					mHandler.sendMessage(msg);
					Log.d(TAG, "setting.isPrinting():" + setting.isPrinting());
					return;
				}
				StringBuffer receipts = new StringBuffer();
				receipts.append("POS Signed Order\n");
				PrnStrFormat psf = new PrnStrFormat();
				psf.setTextSize(34);
				psf.setAli(Alignment.ALIGN_CENTER);
				setting.prnStr(receipts.toString(), psf);
				receipts.setLength(0);
				receipts.append("The cardholder stub   \nPlease properly keep\n");
				receipts.append("-----------------------------------------------\n");
				receipts.append("Merchant Name:ABC\n");
				receipts.append("Merchant No.:846584000103052\n");
				receipts.append("Terminal No.:12345678\n");
				receipts.append("categories:visa card\n");
				receipts.append("Period of Validity:2018/04\n");
				receipts.append("Batch no:000101\n");
				receipts.append("Card Number:\n");
				receipts.append("622202400******0269\n");
				receipts.append("Trade Type:consumption\n");
				receipts.append("Serial No.:000024  \nAuthenticode:096706\n");
				receipts.append("Date/Time:2018/04/28 11:27:12\n");
				receipts.append("Ref.No.:123456789012345\n");
				receipts.append("Amount:$ 100.00\n");
				receipts.append("-----------------------------------------------\n");
				setting.prnStr(receipts.toString());
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				InputStream is = getResources().openRawResource(
						R.drawable.ooooo);
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
				setting.prnBitmap(bitmap);
				setting.prnStart();
			}
		}).start();
	}

	private void printQueue() {
		handler.prnQueueStart();// open print queue
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer receipts = new StringBuffer();
				receipts.append("The cardholder stub   \nPlease properly keep\n");
				// 1. print text with default style and font
				setting.printAppendStr(receipts.toString());
				receipts.setLength(0);
				receipts.append("Merchant Name:ABC\n");
				receipts.append("Merchant No.:846584000103052\n");
				receipts.append("Terminal No.:12345678\n");
				PrnStrFormat psf = new PrnStrFormat();
				psf.setFont(PrnTextFont.MONOSPACE);// specified font
				setting.printAppendStr(receipts.toString(), psf);
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				InputStream is = getResources().openRawResource(R.drawable.bustest);
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
				setting.printAppenBitmap(bitmap);
			}
		}).start();
	}

	@Override
	public void onCardDetect(CardDetected arg0) {

	}

	@Override
	public void onConnected(SwipeEvent arg0) {

	}

	@Override
	public void onDisconnected(SwipeEvent arg0) {

	}

	@Override
	public void onEmvStatus(EmvStatus arg0) {

	}

	@Override
	public void onParseData(SwipeEvent arg0) {

	}

	@Override
	public void onPrintStatus(PrintStatus arg0) {
		Log.d(TAG, "printStatus:" + arg0.toString());
		if (arg0 == PrintStatus.IMAGES) {

		} else if (arg0 == PrintStatus.EXIT) {
			// setting.mPosExitPrint();
			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			// setting.prnStatus();
			// }
			// }).start();
		} else if (arg0 == PrintStatus.NO_PAPER) {

		} else if (arg0 == PrintStatus.LACK_PAPER) {

		}

	}

	@Override
	protected void onDestroy() {
		// power off the device when you do not need to read card or print for a
		// long time
		setting.mPosPowerOff();
		// ondestroy the sdk when you exit the app
		handler.onDestroy();
		setting.onDestroy();
		super.onDestroy();
	}

	/**** SDK end ****/

	private void showToast(String mesg) {
		Message mssg = new Message();
		mssg.what = 10;
		mssg.obj = "" + mesg;
		handleros.sendMessage(mssg);
	}

	Handler handleros = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 10:
				Toast.makeText(getApplicationContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};

	private String hexToSting(String ver) {
		if (ver == null)
			return "";
		String[] tmps = ver.trim().replaceAll("..", "$0 ").split(" ");
		StringBuffer sbf = new StringBuffer();
		for (String str : tmps) {
			sbf.append((char) Integer.parseInt(str, 16));
		}
		ver = sbf.toString();
		return ver;
	}

	private void showInfor(String ver, String sn) {
		final AlertDialog.Builder normalDialog = new AlertDialog.Builder(
				MainActivity.this);
		normalDialog.setTitle(""
				+ getResources().getString(R.string.device_information));

		normalDialog.setMessage("" + getResources().getString(R.string.version)
				+ ":" + ver + "\n" + "sn:" + sn);
		normalDialog.show();
	}

	private void initData() {
		data = new ArrayList<ImageInfo>();
		mynum.setText("1");
		data.add(new ImageInfo(getResources().getString(R.string.printtest),
				R.drawable.icon1, R.drawable.icon_bg01));
		data.add(new ImageInfo(getResources().getString(
				R.string.contactless_card_test), R.drawable.icon2,
				R.drawable.icon_bg01));
		data.add(new ImageInfo(getResources().getString(R.string.test_psam1),
				R.drawable.icon3, R.drawable.icon_bg01));
		data.add(new ImageInfo(getResources().getString(R.string.qr_test),
				R.drawable.icon4, R.drawable.icon_bg02));
		data.add(new ImageInfo(getResources().getString(R.string.finger_test),
				R.drawable.icon5, R.drawable.icon_bg02));
		data.add(new ImageInfo(getResources().getString(
				R.string.device_information), R.drawable.icon7,
				R.drawable.icon_bg02));

	}

	private void initView() {
		ViewPager vpager = (ViewPager) findViewById(R.id.vPager);
		adapter = new MyPagerAdapter(MainActivity.this, data);
		adapter.addNotify(this);
		vpager.setAdapter(adapter);
		vpager.setPageMargin(50);
		vpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mynum.setText("" + (int) (arg0 + 1));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		btn = (Button) findViewById(R.id.set);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String versionname = "";
				PackageManager pm = getPackageManager();
				try {
					PackageInfo packageInfo = pm.getPackageInfo(
							getPackageName(), 0);
					versionname = packageInfo.versionName;
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), versionname,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
