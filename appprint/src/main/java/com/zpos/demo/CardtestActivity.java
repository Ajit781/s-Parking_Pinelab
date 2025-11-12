package com.zpos.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.emv.TransConstants;
import com.imagpay.emv.TransListener;
import com.imagpay.emv.nfc.NFCEmvHandler;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;
import com.zpos.ui.BaseActivity;
import com.zpos.util.PublicUtil;

import java.util.Map;

public class CardtestActivity extends BaseActivity implements TransListener {

	final static String TAG = "xtztt";
	private TextView tv_text;
	private MessageHandler msghandler;
	MposHandler handler;
	NFCEmvHandler nfc;
	Settings settings;
	Context mctx;
	private ProgressDialog dialog;
	protected boolean isprinttest;
	private final int showReadDailog = 101;
	private final int showPrintDailog = 99;
	private final int dismissDailog = 100;
	Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case dismissDailog:
				if (dialog != null)
					dialog.dismiss();
				if (isprinttest) {
					isprinttest = false;
				}
				break;
			case showReadDailog:
				dialog = PublicUtil.getDialog(CardtestActivity.this, "",
						"reading card......");
				dialog.show();
				break;
			case showPrintDailog:
				dialog = PublicUtil.getDialog(CardtestActivity.this, "",
						"printting......");
				dialog.show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setChildContentView(R.layout.activity_main1);
		setTitle(getResources().getString(R.string.contactless_card_test));
		tv_text = (TextView) findViewById(R.id.status);
		msghandler = new MessageHandler(tv_text);
		mctx = CardtestActivity.this;
		handler = MposHandler.getInstance(this);
		handler.setShowLog(true);
		settings = Settings.getInstance(handler);
		handler.addSwipeListener(new SwipeListener() {

			@Override
			public void onParseData(SwipeEvent event) {
				// sendMessage("onParseData:" + event.getValue());
			}

			@Override
			public void onDisconnected(SwipeEvent event) {
			}

			@Override
			public void onConnected(SwipeEvent event) {
			}

			@Override
			public void onCardDetect(CardDetected type) {
			}

			@Override
			public void onPrintStatus(PrintStatus status) {
				Log.i("xtztt", "onPrintStatus:" + status.toString());
				new Thread(new Runnable() {
					public void run() {
						Log.i("xtztt",
								"handler.isConnected:" + handler.isConnected());
						boolean resp = settings.printExitDetection();
						if (resp) {
							sendMessage("exit print............");
						}
					}
				}).start();

			}

			@Override
			public void onEmvStatus(EmvStatus arg0) {
			}

		});

		// add
		nfc = NFCEmvHandler.getInstance(this);
		nfc.addTransListener(this);
	}

	private void sendMessage(String msg) {
		msghandler.sendMessage(msg);
	}

	public void test(View v) {
		switch (v.getId()) {
		case R.id.ttlM1Plus:
			mifarePlus();
			break;
		case R.id.ttlM1:
			m1Test();
			break;
		case R.id.icTest:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					nfctest();
				}
			});
			break;
		default:
			break;
		}

	}

	// show read QuickPass、VISA、Master contactless card
	private void nfctest() {
		nfc.kernelInit("100");// 100 cents
		// Search card
		String reset = settings.nfcReset();
		if (reset != null) {
			sendMessage("card near field");
		} else {
			sendMessage("no card near field");
			return;
		}
		nfc.process();
		settings.nfcOff();
	}

	private void m1Test() {
		sendMessage("Start to read M1 card......");
		// handleros.sendEmptyMessage(101);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// sendMessage(settings.m1ReadSec("ffffffffffff", "00"));
				handler.setShowLog(true);
				sendMessage(settings.m1Request());
				sendMessage(settings.m1Auth(Settings.M1_KEY_A, "00",
						"FFFFFFFFFFFF") + "");
				sendMessage("S00-B00:" + settings.m1ReadBlock("00"));
				sendMessage("S00-B01:" + settings.m1ReadBlock("01"));
				sendMessage("S00-B02:" + settings.m1ReadBlock("02"));
				sendMessage("S00-B03:" + settings.m1ReadBlock("03"));
				settings.off(Settings.SLOT_NFC);
				// sendMessage("03"+settings.m1Auth(Settings.M1_KEY_A, "03",
				// "FFFFFFFFFFFF") + "");
				// sendMessage(StringUtils.covertHexToASCII(settings.m1ReadBlock("00")));
				// sendMessage(StringUtils.covertHexToASCII(settings.m1ReadBlock("01")));
				// sendMessage(StringUtils.covertHexToASCII(settings.m1ReadBlock("02")));
				// sendMessage(settings.m1ReadBlock("03"));
				// sendMessage("04"+settings.m1Auth(Settings.M1_KEY_A, "04",
				// "FFFFFFFFFFFF") + "");
				// sendMessage(StringUtils.covertHexToASCII(settings.m1ReadBlock("00")));
				// sendMessage(settings.m1ReadBlock("01"));
				// sendMessage(settings.m1ReadBlock("02"));
				// sendMessage(settings.m1ReadBlock("03"));
				// sendMessage("03"+settings.m1Auth(Settings.M1_KEY_A, "03",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("04"+settings.m1Auth(Settings.M1_KEY_A, "04",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("05"+settings.m1Auth(Settings.M1_KEY_A, "05",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("06"+settings.m1Auth(Settings.M1_KEY_A, "06",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("07"+settings.m1Auth(Settings.M1_KEY_A, "07",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("08"+settings.m1Auth(Settings.M1_KEY_A, "08",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("09"+settings.m1Auth(Settings.M1_KEY_A, "09",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0a"+settings.m1Auth(Settings.M1_KEY_A, "0a",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0b"+settings.m1Auth(Settings.M1_KEY_A, "0b",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0c"+settings.m1Auth(Settings.M1_KEY_A, "0c",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0d"+settings.m1Auth(Settings.M1_KEY_A, "0d",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0e"+settings.m1Auth(Settings.M1_KEY_A, "0e",
				// "FFFFFFFFFFFF") + "");
				// sendMessage("0f"+settings.m1Auth(Settings.M1_KEY_A, "0f",
				// "FFFFFFFFFFFF") + "");

				// sendMessage(settings.m1WriteBlock("00",
				// "aaaaaaaabbbbbbbbccccccccdddddddd"));
				// sendMessage(settings.m1Request());
				// sendMessage(settings.m1Auth(Settings.M1_KEY_B, "0B",
				// "ffffffffffff")+"");
				// sendMessage(settings.m1ReadBlock("00"));
				// settings.m1WriteSecPass("0A",
				// Settings.M1_KEY_B,"ffffffffffff",
				// Settings.M1_KEY_B,"EEEEEEEEEEEE");
				// handleros.sendEmptyMessage(100);
			}
		}).start();
	}

	private void mifarePlus() {
		sendMessage("Start to read  card......");
		// handleros.sendEmptyMessage(101);
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendMessage("nfc Reset:" + settings.nfcReset());
				sendMessage("mifarePlusAuthentication:"
						+ settings.mifarePlusAuthenticationSector(0,
								"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
				sendMessage("mifarePlusReadblook:"
						+ settings.mifarePlusReadblook(1));
				sendMessage("mifarePlusReadblook:"
						+ settings.mifarePlusWriteBlook(1,
								"11223344112233441122334411223344"));
				sendMessage("mifarePlusReadblook:"
						+ settings.mifarePlusReadblook(1));
			}
		}).start();
	}

	@Override
	public void onTransCompleted(boolean arg0, Map<String, Object> arg1) {
		if (arg0) {
			Log.d(TAG, "trans process success...");
			if (nfc.getTransResult() != TransConstants.NFC_DECLINE) {
				String pan = String.valueOf(arg1
						.get(TransConstants.CARD_MASKEDPAN));
				String track2 = String.valueOf(arg1
						.get(TransConstants.CARD_TRACK2));
				String exp = String.valueOf(arg1
						.get(TransConstants.CARD_EXPIRYDATE));
				String iccData = String.valueOf(arg1
						.get(TransConstants.CARD_55FIELD));
				sendMessage("55data:" + iccData);
				sendMessage("IC	tracks:" + track2);
				sendMessage("expiration date:" + exp);
				sendMessage("card No.:" + pan);
			} else {
				sendMessage("trans decline");
			}
		} else {
			Log.d(TAG, "trans process fail...");
		}

		sendMessage("====" + nfc.getMaskedPAN());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		nfc.onDestroy();// need to release the nfc object
	}
}