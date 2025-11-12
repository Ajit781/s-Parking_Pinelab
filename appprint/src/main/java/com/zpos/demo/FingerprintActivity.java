package com.zpos.demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagpay.MessageHandler;
import com.imagpay.fingerprint.FingerResult;
import com.imagpay.fingerprint.FingerprintListener;
import com.imagpay.fingerprint.FingerprintManager;
import com.imagpay.fingerprint.Result;
import com.imagpay.utils.StringUtils;
import com.zpos.ui.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("HandlerLeak")
public class FingerprintActivity extends BaseActivity implements FingerprintListener, OnClickListener {
	private MessageHandler msghandler;
	protected Button mBtAdd;
	protected Button mBtVerify;
	protected Button mBtGetFeature;
	protected Button mBtEnroll;
	protected Button mBtGetCount;
	protected Button mBtGetEnrolledList;
	protected Button mBtDelete;
	protected Button mBtDeleteAll;
	protected Button mBtIso;
	private EditText mEtFingerId;
	private TextView tv_text;
	private ImageView iv_result;
	private String files = "/sdcard/";
	private int mFingerId = 0;

	private FingerprintManager mFingerprintManager;
	private long lastClick = 0;
	Handler handleros = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 101:
					iv_result.setVisibility(View.VISIBLE);
					iv_result.setImageBitmap((Bitmap) msg.obj);
					break;
				case 100:
					msghandler.sendMessage((String) msg.obj);
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setChildContentView(R.layout.activity_finger);
		setTitle(getResources().getString(R.string.finger_test));
		initView();
		// init fignerprint SDK
		mFingerprintManager = FingerprintManager.getInstance();
		// add linstener for operate fignerprint
		mFingerprintManager.init();
		mFingerprintManager.addFignerprintListener(this);
	}

	private void initView() {
		tv_text = (TextView) findViewById(R.id.f_status);
		msghandler = new MessageHandler(tv_text);
		iv_result = (ImageView) findViewById(R.id.iv_result);
		mEtFingerId = (EditText) findViewById(R.id.et_finger_id);
		mBtAdd = (Button) findViewById(R.id.bt_add);
		mBtAdd.setOnClickListener(FingerprintActivity.this);
		mBtVerify = (Button) findViewById(R.id.bt_verify);
		mBtVerify.setOnClickListener(FingerprintActivity.this);
		mBtGetFeature = (Button) findViewById(R.id.bt_get_feature);
		mBtGetFeature.setOnClickListener(FingerprintActivity.this);
		mBtEnroll = (Button) findViewById(R.id.bt_enroll);
		mBtEnroll.setOnClickListener(FingerprintActivity.this);
		mBtGetCount = (Button) findViewById(R.id.bt_get_count);
		mBtGetCount.setOnClickListener(FingerprintActivity.this);
		mBtGetEnrolledList = (Button) findViewById(R.id.bt_get_enrolled_list);
		mBtGetEnrolledList.setOnClickListener(FingerprintActivity.this);
		mBtDelete = (Button) findViewById(R.id.bt_delete);
		mBtDelete.setOnClickListener(FingerprintActivity.this);
		mBtDeleteAll = (Button) findViewById(R.id.bt_delete_all);
		mBtDeleteAll.setOnClickListener(FingerprintActivity.this);
		mBtIso = (Button) findViewById(R.id.bt_iso);
		mBtIso.setOnClickListener(FingerprintActivity.this);
		mEtFingerId.setSelection(mEtFingerId.getText().toString().length());
	}

	@Override
	public void onAuthenticationFailed(int arg0) {
		showLog("Authentication Failed:" + arg0);
	}

	@Override
	public void onAuthenticationSucceeded(int arg0, Object arg1) {
		showLog("Authentication Succeeded:" + arg0 + "\tscore: " + arg1);
	}

	@Override
	public void onEnrollmentProgress(int arg0, int arg1, int arg2) {
		if (arg2 == 0 && arg1 == 0) {
			showLog("Fingerprint ID:" + arg0 + "  Enrollment SUCCESSFUL!");
		} else {
			showLog("Fingerprint ID:" + arg0);
			showLog("remaining times:" + arg1);
			showLog("reason:" + arg2);
		}
	}

	private void showLog(String msg) {
		msghandler.sendMessage(msg);
	}

	@Override
	protected void onDestroy() {
		mFingerprintManager.close();
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		if (!clickCheck()) {
			showLog(getResources().getString(R.string.button_tip));
			return;
		}
		tv_text.setText("");
		iv_result.setVisibility(View.GONE);
		String fingerText = mEtFingerId.getText().toString().trim();
		mFingerId = Integer.parseInt(TextUtils.isEmpty(fingerText) ? "0" : fingerText);
		if (view.getId() == R.id.bt_add) {
			mFingerprintManager.capture();
		} else if (view.getId() == R.id.bt_get_feature) {
			mFingerprintManager.captureAndGetFeature();
		} else if (view.getId() == R.id.bt_iso) {
			mFingerprintManager.captureAndGetISOFeature();
		} else if (view.getId() == R.id.bt_enroll) {
			showLog("Start to enrollment fignerprint...");
			mFingerprintManager.enrollment(mFingerId);
		} else if (view.getId() == R.id.bt_verify) {
			mFingerprintManager.authenticate(mFingerId);
		} else if (view.getId() == R.id.bt_get_count) {
			Result result = mFingerprintManager.getEnrolledCount();
			showLog("getEnrolledCount:  " + result.error + "  count: " + result.arg1);
		} else if (view.getId() == R.id.bt_get_enrolled_list) {
			Result result = mFingerprintManager.getEnrolledFingerprints();
			showLog("getEnrolledFingerprints: " + result.error + "\t" + result.data.toString());
		} else if (view.getId() == R.id.bt_delete) {
			int ret = mFingerprintManager.remove(mFingerId);
			showLog("remove: " + ret);
		} else if (view.getId() == R.id.bt_delete_all) {
			int ret = mFingerprintManager.removeAll();
			showLog("removeAll: " + ret);
		}
	}

	@Override
	public void onGetImageComplete(int arg0, byte[] arg1) {
		final Message msg = new Message();
		if (arg0 == 0) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String name = sdf.format(new Date()) + ".bmp";
				// convert image data to bmp
				Bitmap bitmap = mFingerprintManager.generateBmp(arg1, files + name);
				msg.what = 101;
				msg.obj = bitmap;
				handleros.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			msg.what = 100;
			if (arg0 == FingerResult.TRANSFER_ERROR) {
				msg.obj = getResources().getString(R.string.finger_tip01);
			} else if (arg0 == FingerResult.NO_FINGER_DETECTED) {
				msg.obj = getResources().getString(R.string.finger_tip02);
			} else if (arg0 == FingerResult.ENCROLL_FAILED) {
				msg.obj = getResources().getString(R.string.finger_tip03);
			} else if (arg0 == FingerResult.NO_SUPPORTED_CMD) {
				msg.obj = getResources().getString(R.string.finger_tiperr);
			}
			handleros.sendMessage(msg);
		}

	}

	@Override
	public void onGetImageFeature(int arg0, byte[] arg1) {
		if (arg0 == FingerResult.RESULT_OK) {
			showLog("Feature:  " + StringUtils.convertBytesToHex(arg1));
		} else {
			showLog("getImageFeature:  " + arg0);
		}
	}

	@Override
	public void onGetImageISOFeature(int arg0, byte[] arg1) {
		if (arg0 == FingerResult.RESULT_OK) {
			showLog("ISO Feature:  " + StringUtils.convertBytesToHex(arg1));
		} else {
			showLog("getImageISOFeature:  " + arg0);
		}
	}

	private boolean clickCheck() {
		if (System.currentTimeMillis() - lastClick <= 3000) {
			return false;
		}
		lastClick = System.currentTimeMillis();
		return true;
	}

}
