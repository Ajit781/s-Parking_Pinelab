package com.innovus.vyoma.s_parking_agentApollo;

import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.isBound;
import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.mService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.eze.api.EzeAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.os.Bundle;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.PassStoreListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.GateOpenBoomBarrierBean;
import data_objects.bean.SpclPassStoreBean;
import data_objects.bean.VehicleType;
import dmax.dialog.SpotsDialog;

import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;
import utilities.others.CToast;
import utilities.printer_utils.Utils;


public class VehicleInfoScanActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse/*,
        SwipeListener*/ {
    private Context mContext;
    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;

    /*private BarcodeDetector detector;*/
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    int isServerError =0;
    String payment_mode ="";
    String ivehicletype ="";
    String CheckinTime ="";
    String vehicle_number = "";
    private String version_name = "";
    DataOutputStream mmOutputStream_forboom;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;
    int readBufferPosition;
    volatile boolean stopWorker;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private String m_strUserName;
    private Integer m_iUId;
    EditText scanResults,et_alt_ph_no;
    TextView m_TxtVStartParkingMessage;
    Button m_BtnEnter,btn_ScanQR;
    private Spinner sp_store;
    private ImageView iv_printer,iv_scan;
    private CheckBox check_pass;
    private LinearLayout ll_check_pass,ll_pass_main;
    RemoteAsync remoteAsync;
    private String strStoreid = "0";
    private String passapplied = "0";
    private String bookingNumber = "";
    //private IntentIntegrator m_qrScan = null;
    private Button btn_Enter;
    private SpotsDialog progressDialog;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    private String blockCharacterSet = "@~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹~ ";
    PassStoreListAdapter passStoreListAdapter;

    private final int REQUEST_CODE_PRINT_BITMAP = 10029;// for APi print for pax

    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog pDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    public static final int NOT_FOUND = -1;
    ProgressDialog dialog;


    /******* for custom qr scan **************/
    private LinearLayout lay_scan;
    private RelativeLayout scanlay_main;
    //private ZXingScannerView mScannerView;
    private ImageButton close_btn;
    Animation bottomin,bottomout;


    private FloatView floatView;



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info_scan);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+getResources().getString(R.string.app_name)+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        if (savedInstanceState != null) {
            try {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
                scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            m_strUserName = bundle.getString("myName");
            m_iUId = bundle.getInt("myID");
        }


        initViews();
        if(dataModel.vehicletypeArrayList.size()==0){
            VehicleTypeList();
        }
        //        checking for licence
        if(!SharedStorage.getValue(getApplicationContext(),"licence_renewdate").equals("")){

            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                Log.e("packageInfo", String.valueOf(packageInfo));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            int version_code = packageInfo.versionCode;
            version_name = packageInfo.versionName;
            Log.i("updated version code", String.valueOf(version_code) + "  " + version_name);
            SharedStorage.setValue(getApplicationContext(),"versionname",version_name);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date estimated_date = dateFormat.parse(SharedStorage.getValue(getApplicationContext(),"licence_renewdate"));
                Date current_date = new Date();

                System.out.println("Current Date " + dateFormat.format(current_date));
                System.out.println("Current Date " + dateFormat.format(estimated_date));
                long diff = 0;

                Log.e("splash", "onResume: "+String.valueOf(diff));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    diff = betweenDates(current_date,estimated_date);
                }else {
                    diff = betweenDateslowversion(current_date,estimated_date);
                }


                if(diff > 7){
                    Log.e("within", "within licence period");
                }else{
                    if(diff>0){
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.ok));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.licencence_warning_msg));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }else {
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.retry));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.licencence_msg));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }



    private void initViews() {
        scanResults = (EditText) findViewById(R.id.EditText_Vehicleno);
        et_alt_ph_no = (EditText) findViewById(R.id.et_alt_ph_no);
        btn_Enter = (Button) findViewById(R.id.btn_Enter);
        btn_ScanQR = (Button) findViewById(R.id.btn_ScanQR);
        ll_pass_main = (LinearLayout) findViewById(R.id.ll_pass_main);
        ll_check_pass = (LinearLayout) findViewById(R.id.ll_check_pass);
        check_pass = (CheckBox) findViewById(R.id.check_pass);
        sp_store = (Spinner) findViewById(R.id.sp_store);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);

        /********* in that page scan ***************/
        lay_scan= (LinearLayout) findViewById(R.id.lay_scan);
        scanlay_main= (RelativeLayout) findViewById(R.id.scanlay_main);
        close_btn= (ImageButton) findViewById(R.id.close_btn);
        bottomin = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_in);
        bottomout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_out);

      //  scanResults.setFilters(new InputFilter[] { filter });



        if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
            scanResults.setText(getResources().getString(R.string.statecode_ts));
        } else {
            scanResults.setText(getResources().getString(R.string.statecode));

    }

        scanResults.setSelection(scanResults.getText().toString().length());

        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent openTextScanner = new Intent(VehicleInfoScanActivity.this,VehicleNumberScannerActivity.class);
                startActivityForResult(openTextScanner, 2);

            }
        });

//        if (dataModel.scanVehicle){
//
//            Log.e("ScannedVehicleNumber",dataModel.scanVehicleNumber);
//            scanResults.setText(dataModel.scanVehicleNumber.replaceAll("\\s", ""));
//            dataModel.scanVehicle= false;
//        }
        m_TxtVStartParkingMessage = (TextView)findViewById(R.id.TEXTVIEW_startparkingmessage);
        m_BtnEnter = (Button)findViewById(R.id.btn_Enter);
        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            ll_pass_main.setVisibility(View.VISIBLE);
        }else {
            ll_pass_main.setVisibility(View.GONE);
        }

       // m_qrScan = new IntentIntegrator(this);
        check_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    passapplied = "1";
                    sp_store.setVisibility(View.VISIBLE);
                    SpecialPassList();


                }else {
                    passapplied= "0";
                    strStoreid = "0";
                    sp_store.setVisibility(View.GONE);
                }
            }
        });

        /* onclick initialized */
        btn_Enter.setOnClickListener(this);
        btn_ScanQR.setOnClickListener(this);
        close_btn.setOnClickListener(this);


        if (!SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name").equals("")){


            floatView = FloatView.getInstance(VehicleInfoScanActivity.this);
//            floatView.createFloatView(20, 20);
//            floatView.release();

        }
        dataModel.about_advanced_dash = 1;

    }


    private void CheckAppsVersion(String packagename,String versionname) {
        //String login_url = Urls.CheckAppsVersion+"/1/"+packagename+"/"+versionname;
        String login_url = Urls.CheckAppVersion;


        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.CHECKAPPVERSION;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            //_PTMoradabad
            urlParams = "Package=" + URLEncoder.encode(packagename+"_PTMoradabad", "UTF-8") +
                    "&Version=" + URLEncoder.encode(versionname, "UTF-8");
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Request service for GenerateAuthToken checking
    private void GenerateAuthToken() {
        Urls Urls = new Urls();
        String login_url = Urls.GenerateAuthToken;

        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GENERATEAUTHTOKEN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "userid=" + URLEncoder.encode("AndroidApp", "UTF-8") +
                    "&password=" + URLEncoder.encode("vyoma@123", "UTF-8");
        }catch(Exception e){
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }
    private void SpecialPassList() {
        start_progress_dialog();

        Urls Urls = new Urls();
        String start_parking_url = Urls.SpecialPassList;
        // String start_parking_url = Urls.SetEndParking + "/" + bookingid;


        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.SPECIALPASSLIST;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "ParkingAreaID=" + URLEncoder.encode(SharedStorage.getValue(getApplicationContext(),"parking_area_id"), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }


    private void VehicleTypeList() {
        start_progress_dialog();
        Urls Urls = new Urls();
        String login_url = Urls.VehicleTypeList;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.VEHICLETYPELIST;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "";
        } catch (Exception e) {
            /*Log.e("ParamsException-->", e.getMessage());*/
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>", urlParams);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_Enter) {

            if (validate()) {
                if (isValid(scanResults.getText().toString().trim())) {
                    dataModel.isofflinecheckin = false;
                    if(passapplied.equals("0")){
                        startparkinng(scanResults.getText().toString().trim(), "0", et_alt_ph_no.getText().toString().trim());
                    }else {
                        startparkinng(scanResults.getText().toString().trim(), "4", et_alt_ph_no.getText().toString().trim());
                    }


                }else {
                    ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_vehicle_msg));
                }
            }

        }
//        else if (view.getId() == R.id.btn_ScanQR) {
//            try {
//
//                /*m_qrScan.initiateScan();*/
//
//                scanlay_main.setVisibility(View.VISIBLE);
//                scanlay_main.setAnimation(bottomin);
//                mScannerView = new ZXingScannerView(this);
//                lay_scan.addView(mScannerView);
//                mScannerView.setResultHandler(this);
//                mScannerView.startCamera();
//                mScannerView.setSoundEffectsEnabled(true);
//                mScannerView.setAutoFocus(true);
//
//                //turn on flash light after 5pm
//                String from = "6:00:00";
//                String to = "16:59:59";
//                String dateTime[] = getDateTime();
//                String n = dateTime[2];
//                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                Date date_from = formatter.parse(from);
//                Date date_to = formatter.parse(to);
//                Date dateNow = formatter.parse(n);
//                if (date_from.before(dateNow) && date_to.after(dateNow)) {
//                    Log.e("Yes time between","Yes time between");
//                    mScannerView.setFlash(false);
//                }else {
//                    mScannerView.setFlash(true);
//                    Log.e("Yes time else between","Yes time else between");
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//        else if (view.getId() == R.id.close_btn) {
//            try {
//
//                mScannerView.stopCameraPreview();
//                mScannerView.stopCamera();
//                scanlay_main.setVisibility(View.GONE);
//                scanlay_main.setAnimation(bottomout);
//                lay_scan.removeAllViews();
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
    }

    public static boolean isValid(String str)
    {
        boolean isValid = false;
        String expression = "^[a-z_A-Z][a-z_A-Z0-9]*[0-9]$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }

    private boolean validate(){
        boolean result = true;

        if (/*scanResults.getText().toString().equals("") ||*/ scanResults.getText().toString().length()<6){
            ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,getResources().getString(R.string.vehicle_num_vld));
            result = false;
            return result;
        }
        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            if(check_pass.isChecked()){
                if(strStoreid.equals("0")){
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,getResources().getString(R.string.validate_storepass));
                    result = false;
                    return result;
                }
            }
        }

        if (!et_alt_ph_no.getText().toString().equals("")){
            if (et_alt_ph_no.getText().toString().length()!=10){
                ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,getResources().getString(R.string.validate_mobile));
                result = false;
                return result;
            }
        }

        return result;
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(VehicleInfoScanActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR,"SParkingAgent_startprogress",e.getMessage());
        }

    }

    void stop_progress_dialog() {

        if(progressDialog!=null){

            try{
                progressDialog.dismiss();
                progressDialog=null;
            }catch (Exception e){
                e.printStackTrace();
                Crashlytics.log(Log.ERROR,"SParkingAgent_stopprogress",e.getMessage());
            }

        }

    }

    private void reset(){

        if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            scanResults.setText(getResources().getString(R.string.statecode_ts));
        }else {
            scanResults.setText(getResources().getString(R.string.statecode));
        }
        scanResults.setSelection(scanResults.getText().toString().length());

    }

//    @Override
//    public void handleResult(Result result) {
//        try{
//            if (result != null)
//            {
//                //if qrcode has nothing in it
//                if (result.getText() == null)
//                {
//                    m_TxtVStartParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                }
//                else
//                {
//                    // scanResults.setText(result.getText().toString().toUpperCase());
//                    if (result.getText().contains("#")) {
//
//                        ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,getResources().getString(R.string.proper_qr_read));
//
//                    } else {
//                        scanResults.setText(result.getText().toString().toUpperCase());
//                    }
//                }
//            }
//            else
//            {
//                Log.e("error",getResources().getString(R.string.unable_qr_read));
//                m_TxtVStartParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        mScannerView.stopCameraPreview();
//        mScannerView.stopCamera();
//        scanlay_main.setVisibility(View.GONE);
//        scanlay_main.setAnimation(bottomout);
//        lay_scan.removeAllViews();
//
//
//    }

    public class clientSock extends Thread {
        public void run () {
            try {
                mmOutputStream_forboom.writeBytes(getResources().getString(R.string.commandtofire)); // anything you want
                mmOutputStream_forboom.flush();

                //closeBT_forBoom();

            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }
    private void startparkinng(String veichele_number, String PaymentMode,String alternate_phone) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String userid = SharedStorage.getValue(getApplicationContext(),"UserId");
        /*String start_parking_url = Urls.VehicleCheckIN+"/"+veichele_number;*/
        String start_parking_url = Urls.VehicleCheckIN;

        payment_mode = PaymentMode;
        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.VEHICLECHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(veichele_number, "UTF-8") +
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&AlternateContactNo=" + URLEncoder.encode(alternate_phone, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
            Crashlytics.log(Log.ERROR,"SParkingAgent_startparking",e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    // System.out.println("Input consist of only characters from 'a' to 'z'");
                    return "";
                }
//                if (source != null && blockCharacterSet.contains(("" + source.charAt(i)))) {
//                    return source.toString().substring(start,source.toString().length()-1);
//                }
            }
            return null;
        }
    };

    private void callBT_forboom(GateOpenBoomBarrierBean gateOpenBoomBarrierBean) {
        start_progress_dialog();
        String login_url = "http://"+SharedStorage.getValue(getApplicationContext(),"ip")+"/bbctrl";
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.BBCTRL;
        remoteAsync.delegate = this;

        Gson gson = new Gson();

        String urlParams = "";
        try {

            /*********convert bean class values from Json to Gson ******/
            urlParams = gson.toJson(gateOpenBoomBarrierBean);

        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams----->",urlParams);
        remoteAsync.execute(urlParams);
    }

    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.VEHICLECHECKIN)) {
            stop_progress_dialog();

            try {
                Log.e("Response-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());
                dataModel.isofflinecheckin = true;


                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    check_pass.setChecked(false);
                    et_alt_ph_no.setText("");
                    dataModel.details_shown="1";
                    isServerError = 0;

                    bookingNumber =obj.getString("BookingNumber");
                    ivehicletype =obj.getString("VehicleTypeID");
                    vehicle_number = obj.getString("VehicleNumber");
                    CheckinTime =obj.getString("CheckinTime");

                    if (SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name").equals("")) {
                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));
                        reset();
                        // commented because of bluetooth function

                    }
                    else if (SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name").equals("eazy_Tap")) {
//                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialog.setView(dialogView);

                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));
                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(obj.getString("message"));

                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                try {
//                                    printEazytapBillUsingSDK(CheckinTime,vehicle_number);
                                    printBillUsingPineLab(CheckinTime,vehicle_number);
                                   // printEazytapBillNew(CheckinTime,vehicle_number);
                                } catch (Exception e) {

                                    e.printStackTrace();

                                    if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
                                        scanResults.setText(getResources().getString(R.string.statecode_ts));
                                    } else {
                                        scanResults.setText(getResources().getString(R.string.statecode));
                                    }
                                    scanResults.setSelection(scanResults.getText().toString().length());
                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
                                    scanResults.setText(getResources().getString(R.string.statecode_ts));
                                } else {
                                    scanResults.setText(getResources().getString(R.string.statecode));
                                }
                                scanResults.setSelection(scanResults.getText().toString().length());

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);

                        // commented because of bluetooth function

                    }
                    else if (SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name").equals("verifone")) {
//                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialog.setView(dialogView);

                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));
                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(obj.getString("message"));


                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                try {
                                   printEazytapBillNew(CheckinTime,vehicle_number);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                    if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
                                        scanResults.setText(getResources().getString(R.string.statecode_ts));
                                    } else {
                                        scanResults.setText(getResources().getString(R.string.statecode));
                                    }
                                    scanResults.setSelection(scanResults.getText().toString().length());
                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
                                    scanResults.setText(getResources().getString(R.string.statecode_ts));
                                } else {
                                    scanResults.setText(getResources().getString(R.string.statecode));
                                }
                                scanResults.setSelection(scanResults.getText().toString().length());

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);

                        // commented because of bluetooth function

                    }


                }


                else if(obj.getString("status").equals(Constants.REG)){
                    if (SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name").equals("")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        // Setting Dialog Title
                        //alertDialog.setTitle(title);
                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message
                        msg_txt.setText(obj.getString("message"));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();


                                isServerError = 0;
                                Intent intent=new Intent(VehicleInfoScanActivity.this, VehicleOwnerRegistrationActivity.class);
                                intent.putExtra("myID",m_iUId);
                                intent.putExtra("myName",m_strUserName);
                                intent.putExtra("myVehicleNumber",scanResults.getText().toString());
                                intent.putExtra("alt_mobile",et_alt_ph_no.getText().toString());
                                intent.putExtra("strStoreid",strStoreid);
                                intent.putExtra("passapplied",passapplied);
                                intent.putExtra("isServerError",String.valueOf(isServerError));
                                Log.e("alt_number",et_alt_ph_no.getText().toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();

                    }else{

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        // Setting Dialog Title
                        //alertDialog.setTitle(title);
                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message
                        msg_txt.setText(obj.getString("message"));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                try {
                                    //sendData();
                                    closeBT();
                                    //printPhoto();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                isServerError = 0;
                                Intent intent=new Intent(VehicleInfoScanActivity.this, VehicleOwnerRegistrationActivity.class);
                                intent.putExtra("myID",m_iUId);
                                intent.putExtra("myName",m_strUserName);
                                intent.putExtra("myVehicleNumber",scanResults.getText().toString());
                                intent.putExtra("alt_mobile",et_alt_ph_no.getText().toString());
                                intent.putExtra("strStoreid",strStoreid);
                                intent.putExtra("passapplied",passapplied);
                                intent.putExtra("isServerError",String.valueOf(isServerError));
                                Log.e("alt_number",et_alt_ph_no.getText().toString());
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                    }



                }
                else if (obj.getString("status").equals(Constants.INSUF)){
                    isServerError = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                    alertDialog.setView(dialogView);
                   // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                    Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);

                    msg_txt.setText(obj.getString("message"));

                    bt_cash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            dataModel.isofflinecheckin = false;
                            if(passapplied.equals("0")){
                                startparkinng(scanResults.getText().toString().trim(),"1",et_alt_ph_no.getText().toString().trim());
                            }else {
                                startparkinng(scanResults.getText().toString().trim(), "4", et_alt_ph_no.getText().toString().trim());
                            }

                        }
                    });
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();


                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();


                }
                else if (obj.getString("status").equals(Constants.PASS)){
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,msg.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){

                    if(passapplied.equals("0")){
                        startparkinng(scanResults.getText().toString().trim(),"1",et_alt_ph_no.getText().toString().trim());
                    }else {
                        startparkinng(scanResults.getText().toString().trim(), "4", et_alt_ph_no.getText().toString().trim());
                    }
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        if(passapplied.equals("0")){
                            startparkinng(scanResults.getText().toString().trim(), "0", et_alt_ph_no.getText().toString().trim());
                        }else {
                            startparkinng(scanResults.getText().toString().trim(), "4", et_alt_ph_no.getText().toString().trim());
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else{
                    JSONObject msg = new JSONObject(output);
                    isServerError =0;
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingresp",e.getMessage());
            }
        }
//        else if (type.equals(RemoteAsync.VEHICLECHECKINFORADVNORMAL)) {
//            stop_progress_dialog();
//
//            try {
//                Log.e("Response-->", output.toString());
//                JSONObject obj = new JSONObject(output);
//                Log.e("Response-->", obj.toString());
//                dataModel.isofflinecheckin = true;
//
//
//                if (obj.getString("status").equals(Constants.SUCCESS)) {
//                    check_pass.setChecked(false);
//                    et_alt_ph_no.setText("");
//                    dataModel.details_shown = "1";
//                    isServerError = 0;
//
//                    bookingNumber = obj.getString("BookingNumber");
//                    ivehicletype = obj.getString("VehicleTypeID");
//
//                    if (SharedStorage.getValue(VehicleInfoScanActivity.this, "printer_name").equals("")) {
//                       ShowAlertDialog.showAlertDialog(this, obj.getString("message"));
//
//                    }
//                }
//                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
//
//                    GenerateAuthToken();
//                    try {
//                        Thread.sleep(300);
//                        if(passapplied.equals("0")){
//                            vehicleCheckINForAdvNormal(scanResults.getText().toString().trim(), "0", et_alt_ph_no.getText().toString().trim());
//                        }else {
//                            vehicleCheckINForAdvNormal(scanResults.getText().toString().trim(), "4", et_alt_ph_no.getText().toString().trim());
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingresp",e.getMessage());
//
//            }
//        }
        else if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
              try {
        JSONObject obj = new JSONObject(output);
        Log.e("Response-->", obj.toString());

        if (obj.getString("status").equals(Constants.SUCCESS)) {

            //Redirecting to dashboard screen
            ShowAlertDialog.showAlertDialog(this,obj.getString("message"));
            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                scanResults.setText(getResources().getString(R.string.statecode_ts));
            }else {
                scanResults.setText(getResources().getString(R.string.statecode));
            }
            scanResults.setSelection(scanResults.getText().toString().length());


        }
        else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
            alertDialog.setView(dialogView);
            TextView heading = (TextView) dialogView.findViewById(R.id.heading);
            TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
            Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
            btnOk.setText(getResources().getString(R.string.retry));

            heading.setText(R.string.validation_name);

            // Setting Dialog Message
            /*alertDialog.setMessage(message);*/
            msg_txt.setText(obj.getString("message"));

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();

                }
            });
            //Animate alert dialog box
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out);
            // Showing Alert Message
            alertDialog.show();
            alertDialog.setCancelable(false);
            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                scanResults.setText(getResources().getString(R.string.statecode_ts));
            }else {
                scanResults.setText(getResources().getString(R.string.statecode));
            }
            scanResults.setSelection(scanResults.getText().toString().length());

        }
        else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

            GenerateAuthToken();
            try {
                Thread.sleep(300);
                //get all parking details by parking id
                //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        else {

            JSONObject msg = new JSONObject(output);
            ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this, msg.getString("message"));

            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                scanResults.setText(getResources().getString(R.string.statecode_ts));
            }else {
                scanResults.setText(getResources().getString(R.string.statecode));
            }
            scanResults.setSelection(scanResults.getText().toString().length());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
        else if (type.equals(RemoteAsync.SPECIALPASSLIST)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    JSONArray SpecialPassList = obj.getJSONArray("SpecialPassList");
                    ArrayList<SpclPassStoreBean> spclPassStoreBeanArrayList = new ArrayList<SpclPassStoreBean>();
                    spclPassStoreBeanArrayList.add(new SpclPassStoreBean("0", getResources().getString(R.string.selectpasstype),"","",""));
                    if (SpecialPassList.length() > 0) {
                        for (int i = 0; i < SpecialPassList.length(); i++) {
                            JSONObject object = SpecialPassList.getJSONObject(i);
                            SpclPassStoreBean spclPassStoreBean = new SpclPassStoreBean();


                            spclPassStoreBean.setPassStoreId(object.getString("SpecialPassID"));
                            spclPassStoreBean.setPassStoreName(object.getString("SpecialPassName"));
                            spclPassStoreBean.setSpecialPassAddress(object.getString("SpecialPassAddress"));
                            spclPassStoreBean.setAvgRequiredSpecialPass(object.getString("AvgRequiredSpecialPass"));
                            spclPassStoreBean.setStartingFrom(object.getString("StartingFrom"));


                            spclPassStoreBeanArrayList.add(spclPassStoreBean);

                        }
                        dataModel.spclPassStoreBeanArrayList.removeAll(dataModel.spclPassStoreBeanArrayList);
                        dataModel.spclPassStoreBeanArrayList.addAll(spclPassStoreBeanArrayList);
                        passStoreListAdapter  = new PassStoreListAdapter(VehicleInfoScanActivity.this, dataModel.spclPassStoreBeanArrayList) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                        sp_store.setAdapter(passStoreListAdapter);

                        sp_store.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                strStoreid=dataModel.spclPassStoreBeanArrayList.get(position).getPassStoreId();


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                     /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            SpecialPassList();
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                    /*JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this, msg.getString("message"));*/

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        SpecialPassList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this, msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.GENERATEAUTHTOKEN)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.base_token = obj.getString("access_token");
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialogFailure(VehicleInfoScanActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else  if (type.equals(RemoteAsync.VEHICLETYPELIST)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    JSONArray vehicletypelistArray = obj.getJSONArray("VehicleTypeList");
                    ArrayList<VehicleType> vehicletypeArrayList = new ArrayList<VehicleType>();
                    vehicletypeArrayList.add(new VehicleType("0", getResources().getString(R.string.selectvtype)));
                    if (vehicletypelistArray.length() > 0) {
                        for (int i = 0; i < vehicletypelistArray.length(); i++) {
                            JSONObject object = vehicletypelistArray.getJSONObject(i);
                            VehicleType vehicleType = new VehicleType();
                            vehicleType.setVehicleTypeId(object.getString("vehicle_type_id"));
                            vehicleType.setVehicleTypeName(object.getString("vehicle_type_name"));

                            vehicletypeArrayList.add(vehicleType);

                        }
                        Log.e("vehicleTypeList",vehicletypeArrayList.toString());
                        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
                        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);

                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
        /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            VehicleTypeList();
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        VehicleTypeList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this, msg.getString("message"));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (type.equals(RemoteAsync.CHECKAPPVERSION)) {
            try {
                JSONObject obj = new JSONObject(output);

                Log.e("Response-->", obj.toString());
                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    String versioncode= SharedStorage.getValue(getApplicationContext(),"versionname");
                    if(versioncode.equals(obj.getString("ServerVersion"))){

                        SharedStorage.setValue(getApplicationContext(),"baseUrl",obj.getString("base_url"));
//                        SharedStorage.setValue(getApplicationContext(),"baseUrl","http://103.90.68.14/api/");
//                        SharedStorage.setValue(getApplicationContext(),"baseUrl","http://ec2-13-233-106-250.ap-south-1.compute.amazonaws.com/api/");

                        dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        System.out.println("Current Date " + dateFormat.format(date));

                        // Convert Date to Calendar
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);

                        // Perform addition/subtraction
                        c.add(Calendar.DATE, 45);

                        // Convert calendar back to Date
//                        Date currentDatePlusOne = c.getTime();
//
//                        System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));
                        Date currentDatePlusOne = null;
                        try {
                            currentDatePlusOne = dateFormat.parse(obj.getString("valid_upto"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));

                        SharedStorage.setValue(getApplicationContext(),"licence_renewdate",dateFormat.format(currentDatePlusOne));


                    }else{

                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.retry));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.version_notmatching));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }

                }
                else if(obj.getString("status").equals(Constants.FAILED)){


                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.ok));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.licencence_msg));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                }
                else if(obj.getString("status").equals(Constants.PASS)){
                    SharedStorage.setValue(getApplicationContext(),"baseUrl",obj.getString("base_url"));
//                    SharedStorage.setValue(getApplicationContext(),"baseUrl","http://103.90.68.14/api/");
//                   SharedStorage.setValue(getApplicationContext(),"baseUrl","http://ec2-13-233-106-250.ap-south-1.compute.amazonaws.com/api/");

                    dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    System.out.println("Current Date " + dateFormat.format(date));

                    // Convert Date to Calendar
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);

                    // Perform addition/subtraction
                    c.add(Calendar.DATE, 45);

                    // Convert calendar back to Date
//                    Date currentDatePlusOne = c.getTime();
//
//                    System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));
                    Date currentDatePlusOne = null;
                    try {
                        currentDatePlusOne = dateFormat.parse(obj.getString("valid_upto"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));

                    SharedStorage.setValue(getApplicationContext(),"licence_renewdate",dateFormat.format(currentDatePlusOne));

                }
                else {
                    JSONObject msg = new JSONObject(output);



                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.licencence_msg_new));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//    private void printBillUsingPineLab() {
//
//            if (!isBound || mService == null) {
//                Log.e("PrintService", "Service is not bound or is null");
//                return;
//            }
//
//            // Create the message to be sent to the Plutus Smart API
//            Message message = Message.obtain(null, 1001);  // 1002 is the method code for PrintData
//
//            // Bundle to hold the data
//            Bundle data = new Bundle();
//
//            // Creating the JSON request object
//            JSONObject printRequest = new JSONObject();
//            try {
//                // Header section
//                JSONObject header = new JSONObject();
//                header.put("ApplicationId", "8754f022bd7f475a9f29284a656d3401");  // Your Application ID
//                header.put("UserId", "user1234");
//                header.put("MethodId", "1002");          // PrintData Method ID
//                header.put("VersionNo", "1.0");
//                printRequest.put("Header", header);
//
//                // Detail section
//                JSONObject detail = new JSONObject();
//                detail.put("PrintRefNo", "123456");     // Unique reference number for printing
//                detail.put("SavePrintData", true);        // Option to save the print data on the device
//
//                // Data section: Array of print items (text, barcode, QR code, etc.)
//                JSONArray printData = new JSONArray();
//
//                // Example: Print a text line
//                JSONObject printText = new JSONObject();
//                printText.put("PrintDataType", 0);        // 0 = PrintText
//                printText.put("PrinterWidth", 32);        // Printer width
//                printText.put("IsCenterAligned", true);   // Center alignment
//                printText.put("DataToPrint", "Hello, Plutus Smart!"); // Text to print
//                printData.put(printText);
//
//                // Example: Print a QR Code
//                JSONObject printQR = new JSONObject();
//                printQR.put("PrintDataType", 4);          // 4 = QR code
//                printQR.put("PrinterWidth", 32);
//                printQR.put("IsCenterAligned", true);
//                printQR.put("DataToPrint", "Sample QR Code Data");
//                printData.put(printQR);
//
//                // Add the print data array to the detail section
//                detail.put("Data", printData);
//                printRequest.put("Detail", detail);
//
//                // Add JSON to Bundle
//                data.putString("MASTERAPPREQUEST", printRequest.toString());
//
//                // Prepare the message with the bundle
//                message.setData(data);
//                message.replyTo = new Messenger(new IncomingHandler()); // To handle the response
//
//                // Send the message
//                mService.send(message);
//
//            } catch (JSONException | RemoteException e) {
//                e.printStackTrace();
//            }
//
//    }

    private void printBillUsingPineLab(String checkinTime, String vehicle_number) {

        if (!isBound || mService == null) {
            Log.e("PrintService", "Service is not bound or is null");
            return;
        }

        Message message = Message.obtain(null, 1001);  // PrintData method code
        Bundle data = new Bundle();

        JSONObject printRequest = new JSONObject();
        try {
            // Header
            JSONObject header = new JSONObject();
            header.put("ApplicationId", "8673ddad1b064f25aa3a25c00691fc8f");  // Your Application ID
            header.put("UserId", "user1234");
            header.put("MethodId", "1002");          // PrintData method ID
            header.put("VersionNo", "1.0");
            printRequest.put("Header", header);

            // Detail
            JSONObject detail = new JSONObject();
            detail.put("PrintRefNo", "123456");     // Unique reference number
            detail.put("SavePrintData", true);      // Save print data on device

            // Print Data Array
            JSONArray printData = new JSONArray();

            // Title Line: INSLIP
            JSONObject printTitle = new JSONObject();
            printTitle.put("PrintDataType", 0);        // Text
            printTitle.put("PrinterWidth", 32);
            printTitle.put("IsCenterAligned", true);
            printTitle.put("DataToPrint", "INSLIP");
            printData.put(printTitle);

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleInfoScanActivity.this, "parkinglocation"), 35);

            for (String strLocName : arrLocName) {
                if (null != strLocName && !strLocName.trim().equals("")) {

                    JSONObject bookingNO = new JSONObject();
                    bookingNO.put("PrintDataType", 0);        // Text
                    bookingNO.put("PrinterWidth", 32);
                    bookingNO.put("IsCenterAligned", true);
                    bookingNO.put("DataToPrint", strLocName);
                    printData.put(bookingNO);



                }
            }

            JSONObject newLine = new JSONObject();
            newLine.put("PrintDataType", 0);
            newLine.put("PrinterWidth", 32);
            newLine.put("IsCenterAligned", true);
            newLine.put("DataToPrint", " ");
            printData.put(newLine);


            // Vehicle No Line
            JSONObject printVehicle = new JSONObject();
            printVehicle.put("PrintDataType", 0);
            printVehicle.put("PrinterWidth", 32);
            printVehicle.put("IsCenterAligned", false);
            printVehicle.put("DataToPrint", "Vehicle No: " + vehicle_number);
            printData.put(printVehicle);

            // Check-In Time Line
            JSONObject printCheckin = new JSONObject();
            printCheckin.put("PrintDataType", 0);
            printCheckin.put("PrinterWidth", 32);
            printCheckin.put("IsCenterAligned", false);
            printCheckin.put("DataToPrint", "CheckIn Time : " + checkinTime);
            printData.put(printCheckin);

            // Booking No Line
            JSONObject booking = new JSONObject();
            booking.put("PrintDataType", 0);
            booking.put("PrinterWidth", 45);
            booking.put("IsCenterAligned", false);
            booking.put("DataToPrint", "Booking No. : " + bookingNumber);
            printData.put(booking);

            JSONObject newLine1 = new JSONObject();
            newLine1.put("PrintDataType", 0);
            newLine1.put("PrinterWidth", 32);
            newLine1.put("IsCenterAligned", true);
            newLine1.put("DataToPrint", " ");
            printData.put(newLine1);

            // Example: Print a QR Code

            String strVehicleNo = vehicle_number + "##" + checkinTime + "##" + bookingNumber + "##" + ivehicletype;

                JSONObject printQR = new JSONObject();
                printQR.put("PrintDataType", 4);          // 4 = QR code
                printQR.put("PrinterWidth", 32);
                printQR.put("IsCenterAligned", true);
                printQR.put("DataToPrint", strVehicleNo);
                printData.put(printQR);

            JSONObject newLine2 = new JSONObject();
            newLine2.put("PrintDataType", 0);
            newLine2.put("PrinterWidth", 32);
            newLine2.put("IsCenterAligned", true);
            newLine2.put("DataToPrint", " ");
            printData.put(newLine2);

            JSONObject companyName = new JSONObject();
            companyName.put("PrintDataType", 0);
            companyName.put("PrinterWidth", 32);
            companyName.put("IsCenterAligned", true);
            companyName.put("DataToPrint", "Managed by Rohini Enterprise");
            printData.put(companyName);

            JSONObject ownRisk = new JSONObject();
            ownRisk.put("PrintDataType", 0);
            ownRisk.put("PrinterWidth", 32);
            ownRisk.put("IsCenterAligned", true);
            ownRisk.put("DataToPrint", "Parking At Owner Risks");
            printData.put(ownRisk);

            JSONObject newLine3 = new JSONObject();
            newLine3.put("PrintDataType", 0);
            newLine3.put("PrinterWidth", 24);
            newLine3.put("IsCenterAligned", true);
            newLine3.put("DataToPrint", "\n\n");
            printData.put(newLine3);




            // Add array to detail
            detail.put("Data", printData);
            printRequest.put("Detail", detail);

            // Send request
            data.putString("MASTERAPPREQUEST", printRequest.toString());
            message.setData(data);
            message.replyTo = new Messenger(new IncomingHandler()); // Handle response

            mService.send(message);

        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
    }


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("MASTERAPPRESPONSE");
            Log.i("PrintService", "Received response: " + response);

            try {
                // Handle the JSON response
                JSONObject responseObject = new JSONObject(response);
                JSONObject responseObj = responseObject.getJSONObject("Response");

                int responseCode = responseObj.getInt("ResponseCode");
                String responseMsg = responseObj.getString("ResponseMsg");

                if (responseCode==0) {
                    reset();
                    Log.i("PrintService", "Print successful: " + responseMsg);
                }
                else if (responseCode==1002){
                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleInfoScanActivity.this).create();

                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);
                        msg_txt.setText(responseMsg);
                        btnOk.setText(getResources().getString(R.string.reprint));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printBillUsingPineLab(CheckinTime,vehicle_number);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);

                    Log.e("PrintService", "Print failed: " + responseMsg);
                }
                else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            VehicleInfoScanActivity.this).create();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);
                    msg_txt.setText(responseMsg);
                    btnOk.setText(getResources().getString(R.string.reprint));
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                    Log.e("PrintService", "Print failed: " + responseMsg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /******************* eazyTap printer printing code ****************/
     public static final int PAGE_WIDTH_TWO_INCH = 22;
     public static final int PAGE_WIDTH_TWO_INCH_SMALL = 30;
     public static final int PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM = 40;
     static String paddingLeft(String data, int n) {
         StringBuilder temp = new StringBuilder();
         for (int i = 0; i < n; i++)
             temp.append(" ");
         return (temp + data);
     }

    static String paddingRight(String data, int n) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < n; i++)
            temp.append(" ");
        return (data + temp);

    }
     public static String paddingCenter(String data, int n) {
         String temp = "";
         String t1;
         String t2;
         String t3;

         if (data.length() > n) {
             t1 = String.format("%s\n", data.substring(0, n));
             t2 = data.substring(n, data.length());
             if (t2.length() > n) {
                 t3 = paddingCenter(t2, n);
             } else {
                 t3 = paddingLeft(t2, ((n / 2) - (t2.length() / 2)));
             }
             temp = t1 + t3;
         } else if (data.length() < n) {
             temp = paddingLeft(data, ((n / 2) - (data.length() / 2)));
         } else if (data.length() == n) {
             temp = data;
         }
         return temp;

     }
    private void printEazytapBill() {

        new Thread(new Runnable() {
            public void run() {
                String dateTime[] = getDateTime();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
//                Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.kmc);
//                Bitmap printbitmap = pad(icon,100,0);
//                PrinterTester.getInstance().printBitmap(printbitmap);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("100"));
                StringBuilder print_bill = new StringBuilder();
                print_bill.append(paddingCenter("sParking", PAGE_WIDTH_TWO_INCH)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                PrinterTester.getInstance().step(2);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("70"));
             //   PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_32);
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter("www.s-parking.com", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("60"));
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter(SharedStorage.getValue(VehicleInfoScanActivity.this,"parkinglocation"), PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
//                PrinterTester.getInstance().printStr(SharedStorage.getValue(VehicleInfoScanActivity.this,"parkinglocation")+"\n",null);
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("Vehicle No    : "+scanResults.getText().toString()+"\n",null);
                PrinterTester.getInstance().printStr("CheckIn Time  : "+dateTime[0]+" "+dateTime[1]+"\n",null);
                PrinterTester.getInstance().printStr("Booking No    : "+bookingNumber+"\n",null);
                try {

                    String strVehicleNo = scanResults.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;

                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 200, 200);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }

                        Bitmap bitmap = pad(bmp,100,0);

                        if(bitmap!=null){
                            byte[] command = Utils.decodeBitmap(bitmap);
                            PrinterTester.getInstance().printBitmap(bitmap);

//                            PrinterTester.getInstance().leftIndents(Short.parseShort("5"));

                            print_bill = new StringBuilder();
                            print_bill.append(paddingCenter("Download s-Parking App from Play Store", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                            PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                            PrinterTester.getInstance().printStr("Download s-Parking App from Play Store\n",null);
                            PrinterTester.getInstance().printStr("\n",null);
                            PrinterTester.getInstance().printStr("\n",null);
                            PrinterTester.getInstance().step(60);
                        }else{
                            Log.e("Print Photo error", "the file isn't exists");
                        }
                    } catch (WriterException e) {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                PrinterTester.getInstance().step(2);


                final String status = PrinterTester.getInstance().start();
                scanResults.post(new Runnable() {
                    public void run() {
                        CToast.show(getApplicationContext(),status);
                        try {
                            Thread.sleep(300);
                            reset();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


    }
    private void printEazytapBillUsingSDK(String checkinTime,String vehicle_number) {

        new Thread(new Runnable() {
            public void run() {
                try {

                    PrinterTester.getInstance().init();
                    Bitmap bitmap = null;
                    JSONObject jsonRequest = new JSONObject();
                    JSONObject jsonImageObj = new JSONObject();

                    String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleInfoScanActivity.this, "parkinglocation"), 35);
                    if (arrLocName.length <= 1) {
                        bitmap = Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888);
                    } else {
                        Integer bitmapHeight = 600 + ((arrLocName.length - 1) * 24);
                        bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
                    }

                    bitmap.eraseColor(Color.WHITE);

                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    paint.setTypeface(typeface);
                    paint.setColor(Color.rgb(0, 0, 0));
                    Rect bounds = new Rect();

                    // Set first line in Bitmap
                    paint.setTextSize((int) (26));
                    String strText = "INSLIP";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    int x = (bitmap.getWidth() - bounds.width()) / 2;
                    int y = 30;
                    canvas.drawText(strText, x, y, paint);
// Set first line in Bitmap
//                    paint.setTextSize((int) (24));
//                    strText = "SmartPower";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                     x = (bitmap.getWidth() - bounds.width()) / 2;
//                     y += 24;
//                    canvas.drawText(strText, x, y, paint);

//                    // Set second line in Bitmap
//                    paint.setTextSize((int) (22));
//                    strText = "www.s-parking.com";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width()) / 2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);

                    // Set third line in Bitmap
                    paint.setTextSize((int) (22));
                    //  strText = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation");
                    //  String[] arrLocName = breakStringToLines(strText,35);
                    for (String strLocName : arrLocName) {
                        if (null != strLocName && !strLocName.trim().equals("")) {
                            paint.getTextBounds(strLocName, 0, strLocName.length(), bounds);
                            x = (bitmap.getWidth() - bounds.width()) / 2;
                            y += 24;
                            canvas.drawText(strLocName, x, y, paint);
                        }
                    }
                    String gstNo = SharedStorage.getValue(VehicleInfoScanActivity.this,"AgencyGSTNo");

//                    if(!gstNo.equals("")){
//
//                        // Set fourth line in Bitmap
//                        paint.setTextSize((int) (24));
//                        strText = "GST - : " + gstNo;
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width()) / 2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                    }

                    // Set fourth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Vehicle No       : " + vehicle_number;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 62;
                    canvas.drawText(strText, x, y, paint);

                    // Set fifth line in Bitmap
                    String dateTime[] = getDateTime();
                    String checkInTime = checkinTime.substring(0, checkinTime.length() - 3);

                    paint.setTextSize((int) (24));
                    //strText = "CheckIn Time : "+dateTime[0]+" "+dateTime[1];
                    strText = "CheckIn Time : " + checkInTime;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set sixth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Booking No     : " + bookingNumber;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    Typeface typefaceqr = Typeface.createFromAsset(getAssets(), "fonts/Outfit-Medium.ttf");
                    Log.e("typeface", String.valueOf(typefaceqr));
                    paint.setTypeface(typefaceqr);
                    paint.setColor(Color.rgb(0, 0, 0));
                    // Set qr line in Bitmap
                    //  String strVehicleNo = scanResults.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;
                    String strVehicleNo = vehicle_number + "##" + CheckinTime + "##" + bookingNumber + "##" + ivehicletype;

                    y += 20;
                    QRCodeWriter writer = new QRCodeWriter();
                    BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 250, 250);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    //bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int xCount = 0; xCount < width; xCount++) {
                        for (int yCount = 0; yCount < height; yCount++) {
                            bitmap.setPixel(xCount + 80, yCount + y, bitMatrix.get(xCount, yCount) ? Color.BLACK : Color.WHITE);
                        }
                    }



                    Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    Log.e("typeface", String.valueOf(typeface1));
                    paint.setTypeface(typeface1);
                    paint.setColor(Color.rgb(0, 0, 0));

                    // Set seventh line in Bitmap
                    paint.setTextSize((int) (22));
                    strText = "Managed by Rohini Enterprise";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 274;
                    canvas.drawText(strText, x, y, paint);

                   //  Set seventh line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Parking at Owner's risk";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set seventh line in Bitmap


                    PrinterTester.getInstance().printBitmap(bitmap);

                    PrinterTester.getInstance().step(2);


                    final String status = PrinterTester.getInstance().start();
                    // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());
                    scanResults.post(new Runnable() {
                        public void run() {
                            // CToast.show(getApplicationContext(),status);
                            if (status.equals("Out of paper ")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(
                                        VehicleInfoScanActivity.this).create();

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                                alertDialog.setView(dialogView);
                                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                                Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                                heading.setText(R.string.validation_name);
                                msg_txt.setText(status);
                                btnOk.setText(getResources().getString(R.string.reprint));
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        try {
                                            printEazytapBillUsingSDK(vehicle_number,CheckinTime);

                                        } catch (Exception e) {

                                            e.printStackTrace();

                                        }
                                    }
                                });
                                //Animate alert dialog box
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.animator.fade_in,
                                        android.R.animator.fade_out);
                                // Showing Alert Message
                                alertDialog.show();
                                alertDialog.setCancelable(false);

                            }else{

                                reset();
                            }

                        }
                    });
                }
                catch(Exception e){
                    e.printStackTrace();
                }
//                finally {
//                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this,"Unable to print receipt");
//
//                }

            }
        }).start();



    }


    private void printEazytapBillNew(String CheckinTime,String vehicle_number) {
        try
        {
            Bitmap bitmap = null;
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleInfoScanActivity.this, "parkinglocation"), 35);
            if (arrLocName.length <= 1) {
                bitmap = Bitmap.createBitmap(400, 580, Bitmap.Config.ARGB_8888);
            } else {
                Integer bitmapHeight = 580 + ((arrLocName.length - 1) * 24);
                bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
            }

            bitmap.eraseColor(Color.WHITE);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 0));
            Rect bounds = new Rect();

            // Set first line in Bitmap
            paint.setTextSize((int) (26));
            String strText = "INSLIP";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = 30;
            canvas.drawText(strText, x, y, paint);

            // Set second line in Bitmap


            // Set third line in Bitmap
            paint.setTextSize((int) (22));
            //  strText = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation");
            //  String[] arrLocName = breakStringToLines(strText,35);
            for(String strLocName : arrLocName)
            {
                if(null != strLocName && !strLocName.trim().equals(""))
                {
                    paint.getTextBounds(strLocName, 0, strLocName.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strLocName, x, y, paint);
                }
            }
            String gstNo = SharedStorage.getValue(VehicleInfoScanActivity.this,"GSTN");

//            if(!gstNo.equals("")){
//
//                // Set fourth line in Bitmap
//                paint.setTextSize((int) (24));
//                strText = "GST - : " + gstNo;
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width()) / 2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);
//
//            }
            // Set fourth line in Bitmap
            paint.setTextSize((int) (24));
            strText = "Vehicle No       : "+vehicle_number;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fifth line in Bitmap
            String dateTime[] = getDateTime();
            CheckinTime = CheckinTime.substring(0, CheckinTime.length() - 3);

            paint.setTextSize((int) (22));
            //strText = "CheckIn Time : "+dateTime[0]+" "+dateTime[1];
            strText = "CheckIn Time  : "+CheckinTime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No      : "+bookingNumber;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set qr line in Bitmap
            //  String strVehicleNo = scanResults.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;
            String strVehicleNo = vehicle_number+"##"+CheckinTime+"##"+bookingNumber+"##"+ivehicletype;

            y += 20;
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 250, 250);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            //bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int xCount = 0; xCount < width; xCount++) {
                for (int yCount = 0; yCount < height; yCount++) {
                    bitmap.setPixel(xCount + 80, yCount + y, bitMatrix.get(xCount, yCount) ? Color.BLACK : Color.WHITE);
                }
            }

            // Set seventh line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Download s-Parking from Play Store";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 260;
            canvas.drawText(strText, x, y, paint);

            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            // Building Image Object
            jsonImageObj.put("imageData", encodedImageData);
            jsonImageObj.put("imageType", "JPEG");
            jsonRequest.put("image", jsonImageObj); // Pass this attribute when you have a valid captured signature image
            EzeAPI.printBitmap(VehicleInfoScanActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);

            scanResults.post(new Runnable() {
                public void run() {
                    //      CToast.show(getApplicationContext(),status);
                    try {
                        Thread.sleep(300);
                        reset();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private String getEncoded64ImageStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = bmp;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedDate = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedDate;
    }

    public static String getWhiteSpace(int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }

    /*******************  end of eazyTap printer printing code ****************/

    private void vehicleCheckINForAdvNormal(String veichele_number, String PaymentMode, String alternate_phone) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String userid = SharedStorage.getValue(getApplicationContext(),"UserId");
        /*String start_parking_url = Urls.VehicleCheckIN+"/"+veichele_number;*/
        String start_parking_url = Urls.VehicleCheckINForAdvNormal;

        payment_mode = PaymentMode;
        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.VEHICLECHECKINFORADVNORMAL;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(veichele_number, "UTF-8") +
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&AlternateContactNo=" + URLEncoder.encode(alternate_phone, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
            Crashlytics.log(Log.ERROR,"SParkingAgent_startparking",e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(mScannerView!= null){
//           // mScannerView.setResultHandler(this);
//            mScannerView.startCamera();
//        }

    }

    @Override
    public void onPause() {
        super.onPause();
//        if(mScannerView!= null){
//            mScannerView.stopCamera();
//        }

    }
    @Override
    public void onBackPressed() {
        dataModel.details_shown="1";
        dataModel.about_advanced_dash = 0;

        if(scanlay_main.getVisibility() == View.VISIBLE){
            try {

               // mScannerView.stopCameraPreview();
               // mScannerView.stopCamera();
                scanlay_main.setVisibility(View.GONE);
                scanlay_main.setAnimation(bottomout);
                lay_scan.removeAllViews();

            }catch (Exception e){
                e.printStackTrace();
            }


        }else {
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(VehicleInfoScanActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }

    }
    // this will find a bluetooth printer device
    @SuppressLint("MissingPermission")
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                /*myLabel.setText("No bluetooth adapter available");*/
            }

            if(!mBluetoothAdapter.isEnabled()) {
                if(dataModel.isbluetoothon == 0){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }

            }
            Boolean isnotconnected = true;
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals(SharedStorage.getValue(VehicleInfoScanActivity.this,"printer_name"))) {
                        Log.e("device",device.getName());
                        mmDevice = device;
                        isnotconnected = false;
                        //openBT();
                        break;
                    }

                }
            }



        }catch(Exception e){
            iv_printer.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.e("data",data);
                                               // tv_device_name.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                            iv_printer.setVisibility(View.GONE);
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            iv_printer.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    private String[] getDateTime() {

        //calculation of hours using checkin and checkout time
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[3];
        int day =c.get(Calendar.DAY_OF_MONTH);
        String sday = "";
        if (day < 10) {
            sday = "0"+String.valueOf(day);
        } else {
            sday = String.valueOf(day);
        }
      //  dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DAY_OF_MONTH);
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ sday;
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;

        return dateTime;
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;

            if (mmOutputStream != null){

                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }

            /*myLabel.setText("Bluetooth Closed");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);

            /*Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);*/
            Bitmap bitmap = Bitmap.createBitmap(200, 50, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            float scale = getResources().getDisplayMetrics().density;
            //paint.setTextSize((int) (25 * scale));
            paint.setTextSize(16);

            canvas.drawText(encodedString, 200, 50, paint);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long betweenDates(Date firstDate, Date secondDate) throws IOException
    {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }


    public static long betweenDateslowversion(Date firstDate, Date secondDate) throws IOException
    {

        long msDiff = secondDate.getTime() - firstDate.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        return daysDiff;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == 2) {

            String returnedResult = intent.getData().toString().trim().replaceAll(" ","");
            scanResults.setText(returnedResult);
        }

        if (requestCode == REQUEST_CODE_PRINT_BITMAP) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response", String.valueOf(response));
                        // Initialization of SDK is successful, proceed with your action
                    }
                    else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleInfoScanActivity.this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);
                        msg_txt.setText(errorMessage);
                        btnOk.setText(getResources().getString(R.string.reprint));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printEazytapBillNew(CheckinTime,vehicle_number);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);

                    }
                    else{
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleInfoScanActivity.this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);
                        msg_txt.setText(errorMessage);
                        btnOk.setText(getResources().getString(R.string.reprint));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printEazytapBillNew(CheckinTime,vehicle_number);
                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }
                }
                else{
                    // Show dialog : Text : "Printer Error, Data null from Ezetap : " + resultCode
                    ShowAlertDialog.showAlertDialog(this, "Printer Error, Data null from Ezetap : " + resultCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //  ShowAlertDialog.showAlertDialog(this, e.getMessage());
                // Do your exception handling
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
    // for location line break
    public static String[] breakStringToLines(String str, int maxLength)
    {
        StringBuilder result = new StringBuilder();
        while (str.length() > maxLength)
        {
            // Attempt to break on whitespace first,
            int breakingIndex = lastIndexOfRegex(str, "\\s", maxLength);

            // Then on other non-alphanumeric characters,
            if (breakingIndex == NOT_FOUND) breakingIndex = lastIndexOfRegex(str, "[^a-zA-Z0-9]", maxLength);

            // And if all else fails, break in the middle of the word
            if (breakingIndex == NOT_FOUND) breakingIndex = maxLength;

            // Append each prepared line to the builder
            result.append(str.substring(0, breakingIndex + 1));
            result.append("\n");

            // And start the next line
            str = str.substring(breakingIndex + 1);
        }

        // Check if there are any residual characters left
        if (str.length() > 0)
        {
            result.append(str);
        }

        // Return the resulting string
        return result.toString().split("\n");
    }
    // lastIndexOfRegex
    public static int lastIndexOfRegex(String str, String toFind, int fromIndex)
    {
        // Limit the search by searching on a suitable substring
        return lastIndexOfRegex(str.substring(0, fromIndex), toFind);
    }

    //lastIndexOfRegex
    public static int lastIndexOfRegex(String str, String toFind)
    {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = NOT_FOUND;

        // Search for the given pattern
        while (matcher.find())
        {
            lastIndex = matcher.start();
        }

        return lastIndex;
    }
}
