package com.innovus.vyoma.s_parking_agentApollo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.FragmentTransaction;
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
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eze.api.EzeAPI;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_objects.SParkingAgentModel;
import data_objects.bean.BookingBillBean;
import data_objects.bean.VehicleType;
import db.DatabaseHandler;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;

import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.hideKeyboard;
import static java.lang.Integer.parseInt;

public class OfflineCheckOutActivity extends AppCompatActivity implements View.OnClickListener,/* SwipeListener,*//* DecoratedBarcodeView.TorchListener,*/ AsyncResponse, AdapterView.OnItemSelectedListener {

    private EditText et_bookingid, et_checkinDate, m_EditText_VehicleNumber, et_checkinTime, et_acess_control;
    private Button btn_EndParkingScanQR = null;
    private Button btn_endParking;
    private RelativeLayout rl_scan;
    private TextView tv_vehicle_type;
    private Spinner spinner_Vehicle_Type;
    private ImageView iv_datepicker, iv_timepicker;
    private RadioGroup radioGroup_pass, radioGroup_vehicletype;
    private FrameLayout fl_asess_amount;
    private RadioButton radioButton_specialpass, radioButton_monthlypass, radioButton_twowheel, radioButton_fourwheel, radioButton_none;
    private RelativeLayout endlay;
    private String blockCharacterSet = "~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹";
    private String m_strUserName;
    private Integer m_iUId;
    String strVehicleType = "";
    boolean isBackCamera = true;
    private CheckBox check_pass;
    //private IntentIntegrator m_qrScan = null;
    private TextView m_TxtVEndParkingMessage = null;
    private SpotsDialog progressDialog;
    private Integer paymode = 1;
    Integer iVehicleType = 0;
    private int year1;
    private int day;
    private int month;
    private String version_name = "";
    private int access_control_amount = 0;
    public static final int NOT_FOUND = -1;
    Animation animbounce, trans_left_in,trans_right_in,bottom_in,bottom_out;
    String advbookingid = "";

    /******* for custom qr scan **************/
//    private DecoratedBarcodeView barcodeScannerView;
    //private CaptureManager capture;
    private LinearLayout lay_scan;
    private View lay_scan_view;
    //private ZXingScannerView mScannerView;
    private int timeOut = 120;
    private Handler resultHandler;
    private TranslateAnimation translateAnimation;


    Calendar myCalendar = Calendar.getInstance();
    static final int DATE_PICKER_ID = 1111;
    static final int DATE_PICKER_ID_CASE = 1112;
    private String todate_str = "";
    private LinearLayout ll_pass_main;


    DatePickerDialog.OnDateSetListener arivalfrag, departurefrag;
    DatePickerDialog datePickerDialog1, datePickerDialog;
    TimePickerDialog.OnTimeSetListener timefrag;
    TimePickerDialog timePickerDialog;

    Integer parkingrate = 0;
    String parkingareaid = "";
    private String checkintime = "";
    private String checkouttime = "";
    private String ownermobilenumber = "";
    private String parkingfee = "";
    private String[] str_checking;
    private String[] str_qrdata;
    DatabaseHandler databaseHandler;
    BookingBillBean bookingBillBean;

    // needed for communication to bluetooth device / Network
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;

    private ImageView iv_printer,iv_close;
    int readBufferPosition;
    volatile boolean stopWorker;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    Calendar mcurrentTime = Calendar.getInstance();
    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    int minute = mcurrentTime.get(Calendar.MINUTE);

    DataOutputStream mmOutputStream_forboom;

    private Context mContext;
    //for park24X7 POS
    private String printVersion;
    private final int NOPAPER = 3;
    private final int RESET = 16;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int NOBLACKBLOCK = 15;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog pDialog;
    RemoteAsync remoteAsync;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private ExecutorService mSingleThreadExecutor;
    ArrayList<VehicleType>vehicletypeArrayList = new ArrayList<VehicleType>();
    private FloatView floatView;
    private final int REQUEST_CODE_PRINT_BITMAP = 10029;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_check_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>" + getResources().getString(R.string.app_name) + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if(getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        m_strUserName = SharedStorage.getValue(getApplicationContext(), "Userame");

        m_iUId = Integer.valueOf(SharedStorage.getValue(getApplicationContext(), "UserId"));
        databaseHandler = new DatabaseHandler(getApplicationContext());
        bottom_in = AnimationUtils.loadAnimation(OfflineCheckOutActivity.this.getApplicationContext(),
                R.anim.trans_bottom_in);
        bottom_out = AnimationUtils.loadAnimation(OfflineCheckOutActivity.this.getApplicationContext(),
                R.anim.trans_bottom_out);



        initviews();

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
                                // check App version calling Request
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
    private void initviews() {

        dataModel.about_advanced_dash = 1;

        spinner_Vehicle_Type = (Spinner) findViewById(R.id.spinner_Vehicle_Type);
        et_bookingid = (EditText) findViewById(R.id.et_bookingid);
        et_checkinDate = (EditText) findViewById(R.id.et_checkinDate);
        et_checkinTime = (EditText) findViewById(R.id.et_checkinTime);
        et_acess_control = (EditText) findViewById(R.id.et_acess_control);
        m_EditText_VehicleNumber = (EditText) findViewById(R.id.et_VehiclenoEndParking);
        m_TxtVEndParkingMessage = (TextView) findViewById(R.id.TEXTVIEW_endparkingmessage);
        tv_vehicle_type = (TextView) findViewById(R.id.tv_vehicle_type);
        endlay = (RelativeLayout) findViewById(R.id.endlay);
        check_pass = (CheckBox) findViewById(R.id.check_pass);
        btn_EndParkingScanQR = (Button) findViewById(R.id.btn_EndParkingScanQR);
        btn_endParking = (Button) findViewById(R.id.btn_endParking);
        radioGroup_pass = (RadioGroup) findViewById(R.id.radioGroup_pass);
        radioGroup_vehicletype = (RadioGroup) findViewById(R.id.radioGroup_vehicletype);
        radioButton_specialpass = (RadioButton) findViewById(R.id.radioButton_specialpass);
        radioButton_monthlypass = (RadioButton) findViewById(R.id.radioButton_monthlypass);
        radioButton_twowheel = (RadioButton) findViewById(R.id.radioButton_twowheel);
        radioButton_fourwheel = (RadioButton) findViewById(R.id.radioButton_fourwheel);
        radioButton_none = (RadioButton) findViewById(R.id.radioButton_none);
        iv_datepicker = (ImageView) findViewById(R.id.iv_datepicker);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_timepicker = (ImageView) findViewById(R.id.iv_timepicker);
        ll_pass_main = (LinearLayout) findViewById(R.id.ll_pass_main);
        rl_scan = (RelativeLayout) findViewById(R.id.rl_scan);
        fl_asess_amount = (FrameLayout) findViewById(R.id.fl_asess_amount);

        /********* in that page scan ***************/
        //mScannerView = new ZXingScannerView(this);
        lay_scan= (LinearLayout) findViewById(R.id.lay_scan);

        //checkin for access control
        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            fl_asess_amount.setVisibility(View.VISIBLE);
        }else{
            fl_asess_amount.setVisibility(View.GONE);
        }
        spinner_Vehicle_Type.setOnItemSelectedListener(this);

        if (dataModel.vehicletypeArrayList.size()>0){
            vehicletypeArrayList = dataModel.vehicletypeArrayList;
        }else{
            vehicletypeArrayList.add(new VehicleType("0", getResources().getString(R.string.selectvtype)));
            vehicletypeArrayList.add(new VehicleType("1", "Two Wheeler"));
            vehicletypeArrayList.add(new VehicleType("2", "Four Wheeler"));
            vehicletypeArrayList.add(new VehicleType("3", "Heavy Vehicle"));

            dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
            dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);
        }
        // Spinner value settings
        // spinner_Vehicle_Type.setSelection(0);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, vehicletypeArrayList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Vehicle_Type.setAdapter(aa);
        aa.notifyDataSetChanged();

        if(iVehicleType!= 0){
            spinner_Vehicle_Type.setSelection(iVehicleType);

        }

        m_EditText_VehicleNumber.requestFocus();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        et_checkinDate.setText(dateFormat.format(new Date()));//set current date
        et_checkinDate.setSelection(et_checkinDate.getText().toString().length());

        et_checkinTime.setSelection(et_checkinTime.getText().toString().length());
        et_checkinTime.addTextChangedListener(new MyTextWatcher(et_checkinTime));

        //remove colon from et_checkinTime
        et_checkinTime.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {

                Log.e("getKeyCode", String.valueOf(event.getKeyCode()));
                Log.e("keyCode", String.valueOf(keyCode));
                try{
                    if(event.getKeyCode()== event.KEYCODE_DEL)
                    {
                        if(et_checkinTime.getText().toString().length()>0) {

                            if (et_checkinTime.getText().toString().length() == 3) {

                                try {

                                    int pos = et_checkinTime.getSelectionStart();
                                    Log.i("pos", String.valueOf(pos));
                                    String time = et_checkinTime.getText().toString().trim();
                                    Log.i("newtime", time.substring(0, time.length() - 2));
                                    et_checkinTime.setText(time.substring(0, time.length() - 2));
                                    et_checkinTime.setSelection(et_checkinTime.getText().toString().length());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        });

        //long currentTimeMillis ()-Returns the current time in milliseconds.
        long millis = System.currentTimeMillis();

        //Divide millis by 1000 to get the number of seconds.
        long seconds = millis / 1000;

        Log.e("seconds_booking", String.valueOf(seconds));

        parkingareaid = SharedStorage.getValue(getApplicationContext(), "parking_area_id");

        Log.e("parkingareaid",parkingareaid);

        if (parkingareaid.length() == 1){
            parkingareaid = "00" + parkingareaid;
        }else if (parkingareaid.length() == 2){
            parkingareaid = "0" + parkingareaid;
        }

        String createbooking_id = parkingareaid + "-" + String.valueOf(seconds);

        Log.e("seconds_booking", createbooking_id);

        et_bookingid.setText(createbooking_id.substring(0, createbooking_id.length() - 5) + "-");
        et_bookingid.setSelection(et_bookingid.getText().toString().length());

        final int[] radiocheck = {0};


        radioGroup_vehicletype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (checkedId == R.id.radioButton_twowheel) {

                        radiocheck[0] = 1;
                        iVehicleType = 1;

                    } else if (checkedId == R.id.radioButton_fourwheel) {

                        radiocheck[0] = 2;
                        iVehicleType = 2;
                    }
                }
            }
        });

        radioGroup_pass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (checkedId == R.id.radioButton_specialpass) {

                        radiocheck[0] = 1;
                        paymode = 4;

                    } else if (checkedId == R.id.radioButton_monthlypass) {

                        radiocheck[0] = 2;
                        paymode = 3;

                    } else if (checkedId == R.id.radioButton_none) {

                        radiocheck[0] = 2;
                        paymode = 1;

                    }
                }
            }
        });

        endlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(OfflineCheckOutActivity.this);
            }
        });

        m_EditText_VehicleNumber.setFilters(new InputFilter[]{filter});

        if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            m_EditText_VehicleNumber.setText(getResources().getString(R.string.statecode_ts));
        }else {
            m_EditText_VehicleNumber.setText(getResources().getString(R.string.statecode));
        }

        m_EditText_VehicleNumber.setSelection(m_EditText_VehicleNumber.getText().toString().length());

        // m_qrScan = new IntentIntegrator(this);

        btn_endParking.setOnClickListener(this);
        btn_EndParkingScanQR.setOnClickListener(this);
        iv_datepicker.setOnClickListener(this);
        iv_timepicker.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        arivalfrag = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                /*updateLabel();*/
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                Log.e("currentdate---->", sdf.format(System.currentTimeMillis()));
                Log.e("dateprinted---->", sdf.format(myCalendar.getTime()));
                et_checkinDate.setText(sdf.format(myCalendar.getTime()));

            }
        };

        if(dataModel.vehicleCheckInBean != null){

            setthevalues();
        }

        if (!SharedStorage.getValue(OfflineCheckOutActivity.this,"printer_name").equals("")){

            floatView = FloatView.getInstance(OfflineCheckOutActivity.this);

        }


    }
    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                    Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
                    finish();


                } else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
//                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//                    LayoutInflater inflater = this.getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
//                    alertDialog.setView(dialogView);
//                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
//                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
//                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
//                    btnOk.setText(getResources().getString(R.string.retry));
//
//                    heading.setText(R.string.validation_name);
//
//                    // Setting Dialog Message
//                    /*alertDialog.setMessage(message);*/
//                    msg_txt.setText(obj.getString("message"));
//
//                    btnOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//
//                        }
//                    });
//                    //Animate alert dialog box
//                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
//                    ft.setCustomAnimations(android.R.animator.fade_in,
//                            android.R.animator.fade_out);
//                    // Showing Alert Message
//                    alertDialog.show();
//                    alertDialog.setCancelable(false);

                }
                else {

                    JSONObject msg = new JSONObject(output);
                   // ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, msg.getString("message"));

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
                                // check App version calling Request
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
                            // check App version calling Request
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
                            // check App version calling Request
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    // check App version calling Request
    private void CheckAppsVersion(String packagename,String versionname) {
        //String login_url = Urls.CheckAppsVersion+"/1/"+packagename+"/"+versionname;
        String login_url = Urls.CheckAppVersion;


        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.CHECKAPPVERSION;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            //_PTMoradabad
            urlParams = "Package=" + URLEncoder.encode(packagename+"_LUCKNOW", "UTF-8") +
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



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        strVehicleType=spinner_Vehicle_Type.getSelectedItem().toString();
        iVehicleType = Integer.valueOf(dataModel.vehicletypeArrayList.get(position).getVehicleTypeId());
        tv_vehicle_type.setText(dataModel.vehicletypeArrayList.get(position).getVehicleTypeName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void setthevalues() {

        m_EditText_VehicleNumber.setText(dataModel.vehicleCheckInBean.getM_strVehicleNo());
        String strdata_frombean = dataModel.vehicleCheckInBean.getM_strCheckInTime();
        Log.e("strdata_frombean","time-->"+strdata_frombean);
        str_checking = strdata_frombean.split(" ");
        et_checkinDate.setText(str_checking[0]);
        String checkintime = str_checking[1];
        Log.e("strdata_frombean","checkintime-->"+checkintime);
        String str_checkingtime = checkintime.substring(0,checkintime.length()-3);
        et_checkinTime.setText(str_checkingtime);
        et_bookingid.setText(dataModel.vehicleCheckInBean.getBooking_no());

        if(dataModel.vehicleCheckInBean.getM_strVehicleType().equals("Two Wheeler")){

            iVehicleType = 1;

        }else if (dataModel.vehicleCheckInBean.getM_strVehicleType().equals("Four Wheeler")){
            iVehicleType = 2;

        }

        if (iVehicleType==1) {

            radioButton_twowheel.setChecked(true);

        } else {

            radioButton_fourwheel.setChecked(true);

        }
    }

//    @Override
//    public void handleResult(Result result) {
//        try{
//            if (result != null) {
//                String rawresult = result.getText();
//
//                Log.e("result", rawresult);
//                if (result.getText() == null) {
//                    m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                    btn_endParking.setVisibility(View.VISIBLE);
//                } else {
//
//                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
//                        Log.e("result--->", result.getText().toString().toUpperCase());
//                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
//                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
//                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
//                    str_checking=checkinobj.getString("checkintime").split(" ");
//                    et_checkinDate.setText(str_checking[0]);
//                    et_checkinTime.setText(str_checking[1]);
//                    et_bookingid.setText(checkinobj.getString("BookingID"));
//                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/
//
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        m_EditText_VehicleNumber.setText(str_qrdata[0]);
//                        str_checking = str_qrdata[1].split(" ");
//                        et_checkinDate.setText(str_checking[0]);
//                        et_checkinTime.setText(str_checking[1]);
//                        et_bookingid.setText(str_qrdata[2]);
//                        iVehicleType = Integer.valueOf(str_qrdata[3]);
//                        spinner_Vehicle_Type.setSelection(iVehicleType);
//                        //ownermobilenumber = checkinobj.getString("mobilenum");
//
//
//                        et_acess_control.setText(str_qrdata[4]);
//
//                    }else {
//                        Log.e("result--->", result.getText().toString().toUpperCase());
//                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
//                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
//                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
//                    str_checking=checkinobj.getString("checkintime").split(" ");
//                    et_checkinDate.setText(str_checking[0]);
//                    et_checkinTime.setText(str_checking[1]);
//                    et_bookingid.setText(checkinobj.getString("BookingID"));
//                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/
//
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        m_EditText_VehicleNumber.setText(str_qrdata[0]);
//                        str_checking = str_qrdata[1].split(" ");
//                        et_checkinDate.setText(str_checking[0]);
//                        et_checkinTime.setText(str_checking[1]);
//                        et_bookingid.setText(str_qrdata[2]);
//                        iVehicleType = Integer.valueOf(str_qrdata[3]);
//                        spinner_Vehicle_Type.setSelection(iVehicleType);
//                        //ownermobilenumber = checkinobj.getString("mobilenum");
//
//                    }
//
//                }
//            }else {
//                m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                btn_endParking.setVisibility(View.VISIBLE);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//       /* mScannerView.stopCameraPreview();
//
//        mScannerView.resumeCameraPreview(this);*/
//        //mScannerView.stopCameraPreview();
//        mScannerView.stopCamera();
//        rl_scan.setVisibility(View.GONE);
//        rl_scan.setAnimation(bottom_out);
//        btn_endParking.setVisibility(View.VISIBLE);
//        lay_scan.removeAllViews();
//
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public class clientSock extends Thread {
        public void run () {
            try {
                mmOutputStream_forboom.writeBytes(getResources().getString(R.string.commandtofire)); // anything you want
                mmOutputStream_forboom.flush();

                //closeBT_forBoom();
                page_change();



            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }


    void page_change(){

        Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
        finish();
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



    private void callBT_forboom() {
        start_progress_dialog();
        String login_url = "http://"+SharedStorage.getValue(getApplicationContext(),"ip")+"/bbctrl";;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.BBCTRL;
        remoteAsync.delegate = this;

//        GateOpenBoomBarrierBean gateOpenBoomBarrierBean= new GateOpenBoomBarrierBean(Integer.valueOf(status));
//
//        Gson gson = new Gson();

        String urlParams = "";
        try {

            /*********convert bean class values from Json to Gson ******/
            //  urlParams = gson.toJson(gateOpenBoomBarrierBean);
            //  urlParams = "status=" + URLEncoder.encode(status, "UTF-8") ;

        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams----->",urlParams);
        remoteAsync.execute(urlParams);
    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_endParking) {

            if (validate()) {
                btn_endParking.setClickable(false);
                btn_endParking.setEnabled(false);
                try{
                    if (isExistInOfflineAndOnlineTable(m_EditText_VehicleNumber.getText().toString())||isAlreadyCheckedOutorNot(m_EditText_VehicleNumber.getText().toString())){


                    if (isexistindatabase()) {//if database is exist

                        bookingBillBean = null;

                        BookingBillBean newbookingbillbean = new BookingBillBean();

                        newbookingbillbean =
                                filtermodel_bookingtableobject(dataModel.offlinebookingBillBeansnArrayList, et_bookingid.getText().toString().trim());

                        Log.e("vehicle_num", newbookingbillbean.getVechile_no());
                        checkintime = newbookingbillbean.getCheckintime();
                        checkouttime = newbookingbillbean.getCheckouttime();
                        int parkingarea_fee = 0;
                        int total_parkingarea_fee =0;

                        if (paymode.equals("4")){//for special pass
                            total_parkingarea_fee=0;
                            if (iVehicleType == 1) {

                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                            } else if (iVehicleType == 2) {
                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                            } else if (iVehicleType == 3) {
                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"HeavyVehicleRate"));
                            }

                        }
                        else if (paymode.equals("3")){//for monthly pass
                            total_parkingarea_fee=0;
                            parkingarea_fee= 0;

                        }else{
                            parkingarea_fee= parseInt(newbookingbillbean.getTotalPaybleAmount());
                            total_parkingarea_fee=parkingarea_fee;
                        }

                        //new values store to bean for future use
                        bookingBillBean = new BookingBillBean(newbookingbillbean.getBookingno(), newbookingbillbean.getBookingID(),
                                checkintime, checkouttime, newbookingbillbean.getOwnerphoneno(), String.valueOf(iVehicleType),
                                m_EditText_VehicleNumber.getText().toString().trim(), newbookingbillbean.getParkingAreaName(),
                                newbookingbillbean.getTotalDuration(), String.valueOf(parkingarea_fee), String.valueOf(total_parkingarea_fee),
                                newbookingbillbean.getFineAmount(), newbookingbillbean.getOfferAmount(), String.valueOf(paymode),
                                newbookingbillbean.getAgencyName(),newbookingbillbean.getOverTimeDuration(),
                                newbookingbillbean.getOverTimeAmount(), newbookingbillbean.getMessage(),
                                newbookingbillbean.getAdvbookingid());

                    }

                    else {

                        //calculation of hours using checkin and checkout time
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        String dateTime[] = getDateTime();
                            checkouttime = dateTime[0] + " " + dateTime[1];
                            checkintime = et_checkinDate.getText().toString().trim() + " " + et_checkinTime.getText().toString().trim();


                        Date date1 = null;
                        try {
                            date1 = simpleDateFormat.parse(et_checkinDate.getText().toString().trim() + " " + et_checkinTime.getText().toString().trim());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = null;
                        try {
                            date2 = simpleDateFormat.parse(checkouttime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.e("date1", String.valueOf(date1.getTime()));
                        Log.e("date2", String.valueOf(date2.getTime()));
                        Log.e("date2_new", String.valueOf(System.currentTimeMillis()));

                        long difference = date2.getTime() - date1.getTime();

                        Log.e("difference", String.valueOf(difference));
                        int days = (int) (difference / (1000 * 60 * 60 * 24));
                        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                        hours = (hours < 0 ? -hours : hours);
                        if (min==0){
                            min=1;
                        }

//                    if (difference % (1000 * 60 * 60 * 24) != 0.0) {
//
//                        hours++;
//                    }

                        hours = (days * 24)+hours;

                        Log.i("======= Hours", " :: " + hours);
                        Log.i("======= Days", " :: " + days);
                        Log.i("======= min", " :: " + min);
                        /*Log.i("======= diff"," :: "+difference);*/
//
//                        ParkingPrice parkingPrice = databaseHandler.getvehicleParkingPrice(iVehicleType,hours,min);
//                        if (null==parkingPrice ||null==parkingPrice.getPp_price()){
//                            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this,getResources().getString(R.string.couldnotfetch));
//                        }

                        int parkingarea_fee = 0;
                        int total_parkingarea_fee = 0;
                        String Total_duration = hours+":"+min;

                        //set parkingarea_fee for access control situation
                        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                            if (paymode == 4){//for special pass
                                total_parkingarea_fee=0;

                                if (iVehicleType == 1) {

                                    parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                                } else if (iVehicleType == 2) {
                                    parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                                }else if (iVehicleType == 3) {
                                    parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"HeavyVehicleRate"));
                                }
                            }else if (paymode == 3){// for monthly pass
                                total_parkingarea_fee=0;

                                parkingarea_fee= 0;

                            }else{

                                //get list of price from local database
                                databaseHandler.getvehiclepricelist();

                            if (iVehicleType == 1) {//two wheeler

                                //parking price calculation depending on hour for two wheeler
                                // databaseHandler.getvehicleParkingPrice(iVehicleType);

                                int vehicleminduration = 0;
                                int vehicleminduration_inhour = 0;
                                vehicleminduration = Integer.parseInt(dataModel.vehicleTypePriceArrayList.get(0).getMinDuration());
                                vehicleminduration_inhour = (vehicleminduration /60);

                                if(vehicleminduration_inhour != 1){
                                    if (vehicleminduration % (60) != 0.0) {

                                        vehicleminduration_inhour++;
                                    }
                                }
                                Log.e("mindurationinhour---->", String.valueOf(vehicleminduration_inhour));

                                if(hours > vehicleminduration_inhour){
                                    int firstcahnrge = 0;
                                    int recursive = 0;
                                    int RecursiveDuration = 0;
                                    recursive = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(0).getRecursiveDuration());
                                    RecursiveDuration = (recursive /60);
                                    if(RecursiveDuration != 1){
                                        if (RecursiveDuration % (60) != 0.0) {

                                            RecursiveDuration++;
                                        }
                                    }

                                    firstcahnrge = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(0).getFirstCharge());
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(0).getHourlyCharge());
                                    parkingarea_fee = firstcahnrge + (((hours - vehicleminduration_inhour)/RecursiveDuration)* parkingrate);

                                }else {
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(0).getFirstCharge());
                                    parkingarea_fee = parkingrate;
                                }


                            }
                            else if (iVehicleType == 2) {//four wheeler

                                //parking price calculation depending on hour for four wheeler
                                int vehicleminduration = 0;
                                int vehicleminduration_inhour = 0;
                                vehicleminduration = Integer.parseInt(dataModel.vehicleTypePriceArrayList.get(1).getMinDuration());
                                vehicleminduration_inhour = (vehicleminduration /60);

                                if(vehicleminduration_inhour != 1){
                                    if (vehicleminduration % (60) != 0.0) {

                                        vehicleminduration_inhour++;
                                    }
                                }

                                if(hours > vehicleminduration_inhour){
                                    int firstcahnrge = 0;
                                    int recursive = 0;
                                    int RecursiveDuration = 0;
                                    recursive = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(1).getRecursiveDuration());
                                    RecursiveDuration = (recursive /60);
                                    if(RecursiveDuration != 1){
                                        if (RecursiveDuration % (60) != 0.0) {

                                            RecursiveDuration++;
                                        }
                                    }

                                    Log.e("RecursiveDuration", String.valueOf(RecursiveDuration));

                                    firstcahnrge = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(1).getFirstCharge());

                                    Log.e("firstcahnrge", String.valueOf(firstcahnrge));
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(1).getHourlyCharge());
                                    parkingarea_fee = firstcahnrge + (((hours - vehicleminduration_inhour)/RecursiveDuration)* parkingrate);

                                    Log.e("parkingarea_fee", String.valueOf(parkingarea_fee));

                                }else {
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(1).getFirstCharge());
                                    parkingarea_fee = parkingrate;
                                }


                            }
                            else if (iVehicleType == 4) {//for cycle

                                //parking price calculation depending on hour for cycle
                                int vehicleminduration = 0;
                                int vehicleminduration_inhour = 0;
                                vehicleminduration = Integer.parseInt(dataModel.vehicleTypePriceArrayList.get(2).getMinDuration());
                                vehicleminduration_inhour = (vehicleminduration /60);

                                if(vehicleminduration_inhour != 1){
                                    if (vehicleminduration % (60) != 0.0) {

                                        vehicleminduration_inhour++;
                                    }
                                }

                                if(hours > vehicleminduration_inhour){
                                    int firstcahnrge = 0;
                                    int recursive = 0;
                                    int RecursiveDuration = 0;
                                    recursive = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(2).getRecursiveDuration());
                                    RecursiveDuration = (recursive /60);

                                    if(RecursiveDuration != 1){
                                        if (RecursiveDuration % (60) != 0.0) {

                                            RecursiveDuration++;
                                        }
                                    }

                                    firstcahnrge = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(2).getFirstCharge());
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(2).getHourlyCharge());
                                    parkingarea_fee = firstcahnrge + (((hours - vehicleminduration_inhour)/RecursiveDuration)* parkingrate);

                                }else {
                                    parkingrate = Integer.valueOf(dataModel.vehicleTypePriceArrayList.get(2).getFirstCharge());
                                    parkingarea_fee = parkingrate;
                                }

                            }
                            }

                        }

                        //set parkingarea_fee for normal agent
                        else {
                            if (paymode == 4){//for special pass
                                total_parkingarea_fee=0;

                                if (iVehicleType == 1) {

                                    parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                                } else if (iVehicleType == 2) {
                                    parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                                }
                            }
                            else if (paymode == 3){//for monthly pass
                                total_parkingarea_fee=0;

                                parkingarea_fee= 0;

                            }
                            else{
                                if (iVehicleType == 1) {//two wheeler

                                    parkingrate = Integer.valueOf(SharedStorage.getValue(OfflineCheckOutActivity.this, "TwoWheelerRate"));

                                    if ((hours!=0)&&(min>0)){
                                        hours++;
                                        parkingarea_fee = hours * parkingrate;
                                    }else{
                                        parkingarea_fee = parkingrate;
                                    }

                                    total_parkingarea_fee =parkingarea_fee;

                                }
                                else if (iVehicleType == 2) {//four wheeler
                                    parkingrate = Integer.valueOf(SharedStorage.getValue(OfflineCheckOutActivity.this, "FourWheelerRate"));

                                    if ((hours!=0)&&(min>0)){
                                        hours++;
                                        parkingarea_fee = hours * parkingrate;
                                    }else{
                                        parkingarea_fee = parkingrate;
                                    }
                                    total_parkingarea_fee =parkingarea_fee;
                                    Log.e("parking rate", String.valueOf(parkingrate));
                                    Log.e("parking fee", String.valueOf(parkingarea_fee));
                                }
                                else if (iVehicleType == 3) {//four wheeler
                                    parkingrate = Integer.valueOf(SharedStorage.getValue(OfflineCheckOutActivity.this, "HeavyVehicleRate"));

                                    if ((hours!=0)&&(min>0)){
                                        hours++;
                                        parkingarea_fee = hours * parkingrate;
                                    }else{
                                        parkingarea_fee = parkingrate;
                                    }
                                    total_parkingarea_fee =parkingarea_fee;
                                    Log.e("parking rate", String.valueOf(parkingrate));
                                    Log.e("parking fee", String.valueOf(parkingarea_fee));
                                }
                            }
                        }

                        //set parkingfee for access control situation
                        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                            int accesscontrolamnt = 0;
                            accesscontrolamnt = Integer.parseInt(et_acess_control.getText().toString().trim());
                            total_parkingarea_fee = accesscontrolamnt + parkingarea_fee;

                            parkingfee = String.valueOf(parkingarea_fee);

                        }

                        //set parkingfee for normal agent
                        else {
                            // parkingfee = String.valueOf(parkingarea_fee);
                            parkingfee = String.valueOf(total_parkingarea_fee);
                        }

                        Log.e("parking rateoutside", String.valueOf(parkingrate));
                        Log.e("parking feeoutside", String.valueOf(parkingarea_fee));
                        Log.e("total_parkingarea_fee", String.valueOf(total_parkingarea_fee));

                        bookingBillBean = null;

                        //set updated parking fee values to bean class for access control situation
                        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                            bookingBillBean = new BookingBillBean(et_bookingid.getText().toString(),
                                    et_bookingid.getText().toString(), checkintime, checkouttime,
                                    ownermobilenumber, String.valueOf(iVehicleType), m_EditText_VehicleNumber.getText().toString(),
                                    SharedStorage.getValue(getApplicationContext(), "parkingslot"),
                                    Total_duration, parkingfee, String.valueOf(total_parkingarea_fee),
                                    et_acess_control.getText().toString().trim(), "0", String.valueOf(paymode),
                                    SharedStorage.getValue(getApplicationContext(), "AgencyName"),
                                    "0","0","Sucessfully checked out", advbookingid);
                        }

                        //set updated parking fee values to bean class for normal agent
                        else {
                            bookingBillBean = new BookingBillBean(et_bookingid.getText().toString(), et_bookingid.getText().toString(),
                                    checkintime, checkouttime,
                                    ownermobilenumber, String.valueOf(iVehicleType), m_EditText_VehicleNumber.getText().toString(),
                                    SharedStorage.getValue(getApplicationContext(), "parkingslot"), Total_duration,
                                    parkingfee, String.valueOf(total_parkingarea_fee), "0", "0", String.valueOf(paymode),
                                    SharedStorage.getValue(getApplicationContext(), "AgencyName"),
                                    "0","0","Sucessfully checked out", advbookingid);
                        }

                    }

                    Log.e("duration", bookingBillBean.getTotalDuration());
                    Log.e("payble_amount", bookingBillBean.getTotalPaybleAmount());

                    Log.e("parking fee", parkingfee);
                    Log.e("parking vehicletype", String.valueOf(iVehicleType));
                    Log.e("parking mode", String.valueOf(paymode));

                    if (SharedStorage.getValue(OfflineCheckOutActivity.this, "printer_name").equals("")) {
                        //ShowAlertDialog.showAlertDialog(this, obj.getString("message"));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.custom_dialog_bill_popup, null);
                        alertDialog.setView(dialogView);

                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.tv_showmsg);
                        TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                        TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                        TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                        TextView txt_check_out_time = (TextView) dialogView.findViewById(R.id.txt_check_out_time);
                        TextView txt_owner_fee = (TextView) dialogView.findViewById(R.id.txt_owner_fee);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.btnOk);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.btnCancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name_title));
                        txt_booking_no.setText(bookingBillBean.getBookingID());
                        txt_vechile_no.setText(bookingBillBean.getVechile_no());
                        txt_check_in_time.setText(bookingBillBean.getCheckintime());
                        txt_check_out_time.setText(bookingBillBean.getCheckouttime());
                        txt_owner_fee.setText(bookingBillBean.getTotalPaybleAmount());
                        bt_cash.setText(getResources().getString(R.string.ok));
                        msg_txt.setText(getResources().getString(R.string.checkedout));

                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                if (isexistindatabase()) {

                                    //if exist then update New Vehicle Check out offline details in database
                                    databaseHandler.updatevehiclecheckofflinecheckoutvechicle(checkintime,
                                            String.valueOf(iVehicleType),
                                            m_EditText_VehicleNumber.getText().toString().trim(),
                                            bookingBillBean.getBookingID());
                                }else {

                                    //add Vehicle Check out offline details in database
                                    databaseHandler.addvehiclecheckout(bookingBillBean);
                                }

                                if(dataModel.check_in_remove==1){
                                    dataModel.check_in_remove = 2;
                                }



                                page_change();

                            }
                        });

                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                    finish();
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

                    }
                    if (SharedStorage.getValue(OfflineCheckOutActivity.this, "printer_name").equals("eazy_Tap")) {

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.custom_dialog_print_bill_popup, null);
                        alertDialog.setView(dialogView);

                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.tv_showmsg);
                        TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                        TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                        TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                        TextView txt_check_out_time = (TextView) dialogView.findViewById(R.id.txt_check_out_time);
                        TextView txt_owner_fee = (TextView) dialogView.findViewById(R.id.txt_owner_fee);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.btn_ok);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.btn_cncl);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        txt_booking_no.setText(bookingBillBean.getBookingID());
                        txt_vechile_no.setText(bookingBillBean.getVechile_no());
                        txt_check_in_time.setText(bookingBillBean.getCheckintime());
                        txt_check_out_time.setText(bookingBillBean.getCheckouttime());
                        txt_owner_fee.setText(bookingBillBean.getTotalPaybleAmount());
                        title.setText(getResources().getString(R.string.app_name_title));
                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.checkedout));

                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {

                                    if (isexistindatabase()) {

                                        //if exist then update New Vehicle Check out offline details in databse
                                        databaseHandler.updatevehiclecheckofflinecheckoutvechicle(checkintime, String.valueOf(iVehicleType),
                                                m_EditText_VehicleNumber.getText().toString().trim(), bookingBillBean.getBookingID());
                                        databaseHandler.updateofflinevehiclecheckinNotsync("2",m_EditText_VehicleNumber.getText().toString().trim());
                                        deleteofflinefile("2");
                                        databaseHandler.updateOnlineOfflineTable("2",m_EditText_VehicleNumber.getText().toString().trim());
                                        deleteofflineAndOnlinefile(m_EditText_VehicleNumber.getText().toString().trim());
                                    }else {
                                        //add New Vehicle Check out offline details
                                        databaseHandler.addvehiclecheckout(bookingBillBean);

                                        databaseHandler.updateofflinevehiclecheckinNotsync("2",m_EditText_VehicleNumber.getText().toString().trim());
                                        deleteofflinefile("2");
                                        databaseHandler.updateOnlineOfflineTable("2",m_EditText_VehicleNumber.getText().toString().trim());
                                        deleteofflineAndOnlinefile(m_EditText_VehicleNumber.getText().toString().trim());

                                    }

                                    if(dataModel.check_in_remove==1){
                                        dataModel.check_in_remove = 2;
                                    }

                                    if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                        //print slip
                                        try {
                                           // printEazytapBill();
                                          //  printEazytapBillNew();
                                            printEazytapBillUsingSDK();
                                        } catch (Exception e) {

                                            e.printStackTrace();
                                            Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
                                            finish();
                                        }
                                    }
                                    else {
                                        //print slip
                                        try {
                                            //printEazytapBill();
                                           // printEazytapBillNew();
                                            printEazytapBillUsingSDK();
                                        } catch (Exception e) {

                                            e.printStackTrace();
                                            Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
                                            finish();
                                        }

                                    }

                                    /*Thread.sleep(1000);
                                    closeBT();*/
                                    //printPhoto();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                    finish();
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

                    }
                    else if (SharedStorage.getValue(OfflineCheckOutActivity.this, "printer_name").equals("verifone")) {

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.custom_dialog_print_bill_popup, null);
                        alertDialog.setView(dialogView);

                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.tv_showmsg);
                        TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                        TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                        TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                        TextView txt_check_out_time = (TextView) dialogView.findViewById(R.id.txt_check_out_time);
                        TextView txt_owner_fee = (TextView) dialogView.findViewById(R.id.txt_owner_fee);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.btn_ok);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.btn_cncl);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        txt_booking_no.setText(bookingBillBean.getBookingID());
                        txt_vechile_no.setText(bookingBillBean.getVechile_no());
                        txt_check_in_time.setText(bookingBillBean.getCheckintime());
                        txt_check_out_time.setText(bookingBillBean.getCheckouttime());
                        txt_owner_fee.setText(bookingBillBean.getTotalPaybleAmount());
                        title.setText(getResources().getString(R.string.app_name_title));
                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.checkedout));


                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {

                                    if (isexistindatabase()) {

                                        //if exist then update New Vehicle Check out offline details in databse
                                        databaseHandler.updatevehiclecheckofflinecheckoutvechicle(checkintime, String.valueOf(iVehicleType),
                                                m_EditText_VehicleNumber.getText().toString().trim(), bookingBillBean.getBookingID());

                                    }else {
                                        //add New Vehicle Check out offline details
                                        databaseHandler.addvehiclecheckout(bookingBillBean);

                                    }

                                    if(dataModel.check_in_remove==1){
                                        dataModel.check_in_remove = 2;
                                    }

                                    if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                        //print slip
                                        try {
                                            printEazytapBillNew();

                                        } catch (Exception e) {

                                            e.printStackTrace();
                                            Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
                                            finish();
                                        }
                                    }
                                    else {
                                        //print slip
                                        try {
                                           printEazytapBillNew();

                                        } catch (Exception e) {

                                            e.printStackTrace();
//                                        Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.trans_stay_back, R.anim.trans_stay_back);
//                                        finish();
                                        }

                                    }

                                    /*Thread.sleep(1000);
                                    closeBT();*/
                                    //printPhoto();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    Intent intent = new Intent(OfflineCheckOutActivity.this, OfflineCheckOutActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                    finish();
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

                    }
                    }
                    else{
                        ShowAlertDialog.showAlertDialog(this,"This Vehicle is Currently not checked In in this parking Area.");
                        btn_endParking.setClickable(true);
                        btn_endParking.setEnabled(true);

                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
        else if (view.getId() == R.id.btn_EndParkingScanQR) {
            btn_endParking.setVisibility(View.GONE);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 5);
            } else {
                // qr SCAN initialization
                //m_qrScan.initiateScan();
//                try{
//                    hideKeyboard(this);
//                    rl_scan.setVisibility(View.VISIBLE);
//                    rl_scan.setAnimation(bottom_in);
//
//                    mScannerView = new ZXingScannerView(this);
//                    lay_scan.addView(mScannerView);
//                    mScannerView.setResultHandler(this);
//                    mScannerView.setSoundEffectsEnabled(true);
//                    mScannerView.setAutoFocus(true);
//                    mScannerView.startCamera();
//
//                    //turn on flash light after 5pm
//                    String from = "6:00:00";
//                    String to = "16:59:59";
//                    String dateTime[] = getDateTime();
//                    String n = dateTime[2];
//                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                    Date date_from = formatter.parse(from);
//                    Date date_to = formatter.parse(to);
//                    Date dateNow = formatter.parse(n);
//                    if (date_from.before(dateNow) && date_to.after(dateNow)) {
//                        Log.e("Yes time between","Yes time between");
//                        mScannerView.setFlash(false);
//                    }else {
//                        mScannerView.setFlash(true);
//                        Log.e("Yes time else between","Yes time else between");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                try{
                    hideKeyboard(this);
                    // Start the scan activity
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("Place a QR code inside the rectangle");
                    options.setBeepEnabled(true);
                    options.setBarcodeImageEnabled(true);

                    barcodeLauncher.launch(options);
                }catch(Exception e){
                    Log.e("Exception",e.toString());
                }
            }

        }
//        else if (view.getId() == R.id.iv_close) {
//            mScannerView.stopCameraPreview();
//            mScannerView.stopCamera();
//            btn_endParking.setVisibility(View.VISIBLE);
//            rl_scan.setVisibility(View.GONE);
//            rl_scan.setAnimation(bottom_out);
//            lay_scan.removeAllViews();
//        }
        else if (view.getId() == R.id.iv_datepicker) {

            day = myCalendar.get(Calendar.DAY_OF_MONTH);
            month = myCalendar.get(Calendar.MONTH);
            year1 = myCalendar.get(Calendar.YEAR);
            showDialog(DATE_PICKER_ID);
            todate_str = "";

        }
        else if (view.getId() == R.id.iv_timepicker) {

            showDialog(DATE_PICKER_ID_CASE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // qr SCAN initialization
                //m_qrScan.initiateScan();
//                try{
//                    hideKeyboard(this);
//                    rl_scan.setVisibility(View.VISIBLE);
//                    rl_scan.setAnimation(bottom_in);
//
//                    mScannerView = new ZXingScannerView(this);
//                    lay_scan.addView(mScannerView);
//                    mScannerView.setResultHandler(this);
//                    mScannerView.setSoundEffectsEnabled(true);
//                    mScannerView.setAutoFocus(true);
//                    mScannerView.startCamera();
//
//                    //turn on flash light after 5pm
//                    String from = "6:00:00";
//                    String to = "16:59:59";
//                    String dateTime[] = getDateTime();
//                    String n = dateTime[2];
//                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                    Date date_from = formatter.parse(from);
//                    Date date_to = formatter.parse(to);
//                    Date dateNow = formatter.parse(n);
//                    if (date_from.before(dateNow) && date_to.after(dateNow)) {
//                        Log.e("Yes time between","Yes time between");
//                        mScannerView.setFlash(false);
//                    }else {
//                        mScannerView.setFlash(true);
//                        Log.e("Yes time else between","Yes time else between");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                try{
                    hideKeyboard(this);
                    // Start the scan activity
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("Place a QR code inside the rectangle");
                    options.setBeepEnabled(true);
                    options.setBarcodeImageEnabled(true);

                    barcodeLauncher.launch(options);
                }catch(Exception e){
                    Log.e("Exception",e.toString());
                }
            }

        }
    }
    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime[] = new String[3];
        int day =c.get(Calendar.DAY_OF_MONTH);
        int month =c.get(Calendar.MONTH)+1;
        String sday ="";
        String sMonth ="";

        if (month<10){
            sMonth="0"+String.valueOf(month);
        }else{
            sMonth=String.valueOf(month);
        }
        if (day < 10) {
            sday = "0"+String.valueOf(day);
        } else {
            sday = String.valueOf(day);
        }
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ sMonth +"-"+ sday;
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;
        return dateTime;
    }


    public static boolean isValid(String str) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z][a-z_A-Z0-9]*[0-9]$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    void start_progress_dialog() {
        try {
            progressDialog = new SpotsDialog(OfflineCheckOutActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop_progress_dialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean validate() {

        boolean result = true;

        if (et_bookingid.getText().toString().equals("")) {

            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.booking_vid));
            result = false;
            return result;
        } else if (et_checkinDate.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.checking_vdate));
            result = false;
            return result;
        } else if (et_checkinTime.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.checking_vtime));
            result = false;
            et_checkinTime.requestFocus();
            return result;
        } else if (et_bookingid.getText().toString().trim().length() < 15){
            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.booking_validid));
            result = false;
            et_bookingid.requestFocus();
            return result;
        }else if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            if (et_acess_control.getText().toString().equals("")){
                ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.acess_vamount));
                result = false;
                et_acess_control.requestFocus();
            }
        }
        else if(iVehicleType == 0){
            ShowAlertDialog.showAlertDialog(OfflineCheckOutActivity.this, getResources().getString(R.string.vehicle_type_vld));
            result = false;
            return result;
        }
        return result;
    }

    //check databse is exist or not
    private boolean isexistindatabase() {

        boolean result = true;

        databaseHandler.getvehiclecheckout();

        if (dataModel.offlinebookingBillBeansnArrayList.size() > 0) {
            if (filtermodel_bookingtable(dataModel.offlinebookingBillBeansnArrayList, et_bookingid.getText().toString()).size() == 0) {
                result = false;

            } else {
                result = true;
            }

        } else {
            result = false;
        }

        return result;
    }

    private List<BookingBillBean> filtermodel_bookingtable(List<BookingBillBean> models, String query) {
        query = query.toLowerCase();

        final List<BookingBillBean> filteredModelList = new ArrayList<BookingBillBean>();
        try {
            for (BookingBillBean model : models) {
                final String text = model.getBookingID().toLowerCase();
                if (text.equals(query)) {
                    filteredModelList.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteredModelList;
    }

    //to fetch details against booking number if app is clear from background
    private BookingBillBean filtermodel_bookingtableobject(List<BookingBillBean> models, String query) {
        query = query.toLowerCase();

        final List<BookingBillBean> filteredModelList = new ArrayList<BookingBillBean>();
        BookingBillBean filtermodel_obj = new BookingBillBean();
        try {
            for (BookingBillBean model : models) {
                final String text = model.getBookingID().toLowerCase();
                if (text.equals(query)) {

                    filtermodel_obj = model;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filtermodel_obj;
    }

    @Override
    public void onBackPressed() {
        dataModel.about_advanced_dash = 0;
        if(dataModel.check_in_remove!=2){

            dataModel.vehicleCheckInBean = null;

        }

        if(rl_scan.getVisibility() == View.VISIBLE){
            try {
                rl_scan.setVisibility(View.GONE);
                rl_scan.setAnimation(bottom_out);
                btn_endParking.setVisibility(View.VISIBLE);
                lay_scan.removeAllViews();

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            startActivity(new Intent(OfflineCheckOutActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                // create a new DatePickerDialog with values you want to show

                datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, arivalfrag, year1, month, day);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                return datePickerDialog;

            case DATE_PICKER_ID_CASE:
                // create a new DatePickerDialog with values you want to show

                timePickerDialog = new TimePickerDialog(OfflineCheckOutActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String ride_later_time = hourOfDay + ":" + minute;
                        et_checkinTime.setText(ride_later_time);
                    }
                }, hour, minute, true);

                return timePickerDialog;

        }
        return null;
    }

    // this will find a bluetooth printer device
    @SuppressLint("MissingPermission")
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                /*myLabel.setText("No bluetooth adapter available");*/
            }
            if (!mBluetoothAdapter.isEnabled()) {
                if (dataModel.isbluetoothon == 0) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            }

            Boolean isnotconnected = true;
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals(SharedStorage.getValue(OfflineCheckOutActivity.this, "printer_name"))) {
                        Log.e("device", device.getName());
                        mmDevice = device;
                        isnotconnected = false;
                        //openBT();
                        break;
                    }

                }
            }


        } catch (Exception e) {
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
                                                Log.e("data", data);
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



    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x, Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF, 0xFF, 0xFF, 0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }


    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);

            Bitmap bitmap = Bitmap.createBitmap(200, 50, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            float scale = getResources().getDisplayMetrics().density;
            //paint.setTextSize((int) (25 * scale));
            paint.setTextSize(16);

            canvas.drawText(encodedString, 200, 50, paint);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(mScannerView!= null){
//            mScannerView.stopCamera();
//        }

    }

    /*************** ends here *****************/

    public class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String checkintim = et_checkinTime.getText().toString().trim();

            //set : automatic after 2 digits
            if (checkintim.contains(":")){

                Log.e("charSequence", String.valueOf(charSequence));
                Log.e("checkintim1", checkintim);

                if (charSequence.length() == 3){
                    et_checkinTime.setSelection(et_checkinTime.getText().toString().length());
                }

            }else{
                if (charSequence.length() == 2){
                    Log.e("charSequence", String.valueOf(charSequence));
                    Log.e("checkintim2", checkintim);
                    checkintim += ":";

                    Log.e("checkintim3", checkintim);

                    et_checkinTime.setText(checkintim);
                    et_checkinTime.setSelection(et_checkinTime.getText().toString().length());
                }else if (charSequence.length()== 1){

                    Log.e("checkintim4", checkintim);

                    if (checkintim.contains(":")){
                        Log.e("checkintim5", checkintim);

                        et_checkinTime.setText(checkintim.replaceAll(":",""));
                        et_checkinTime.setSelection(et_checkinTime.getText().toString().length());
                    }
                }
            }
        }

        public void afterTextChanged(Editable editable) {

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
                // Bill Formate
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
                StringBuilder print_bill = new StringBuilder();
                print_bill.append(paddingCenter("sParking Receipt", PAGE_WIDTH_TWO_INCH)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().printStr("Tax Invoice\n",null);
                PrinterTester.getInstance().step(2);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("30"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_16);
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter("Parking Maintenance Charge", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().printStr("Parking Maintenance Charge\n",null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("70"));
                String str = SharedStorage.getValue(OfflineCheckOutActivity.this,"parkinglocation");
                String[] arrOfStr = str.split("-");

                for (String str_location : arrOfStr){
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter(str_location, PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                }
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr("\n",null);
                PrinterTester.getInstance().printStr("Vehicle No  : " + bookingBillBean.getVechile_no() +"\n",null);
              //  PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("Vehicle Type : " + strVehicleType+"\n",null);
                PrinterTester.getInstance().printStr("Booking No  : " + bookingBillBean.getBookingID() +"\n",null);
                PrinterTester.getInstance().printStr("In Time      : " + checkintime+"\n",null);
                PrinterTester.getInstance().printStr("Out Time     : " + checkouttime+"\n",null);

                PrinterTester.getInstance().printStr("Duration     : " +bookingBillBean.getTotalDuration()+"\n",null);
//                PrinterTester.getInstance().printStr("Amount       : " + "Rs. "+bookingBillBean.getTotalParkingAmount()+"\n",null);
//
//                PrinterTester.getInstance().printStr("Fine Amount  : " + "Rs. "+bookingBillBean.getFineAmount()+"\n",null);
//                PrinterTester.getInstance().printStr("Discount     : " + "Rs. "+bookingBillBean.getOfferAmount()+"\n",null);
                PrinterTester.getInstance().printStr("--------------------------------"+"\n",null);

                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr("Pay "+"CASH"+"    " + "Rs. "+bookingBillBean.getTotalPaybleAmount()+"\n",null);


//                if (bookingBillBean.getPaymentMode().equals("1")) {
//                    PrinterTester.getInstance().printStr("Pay "+"Cash"+"    " + "Rs. "+bookingBillBean.getTotalPaybleAmount()+"\n",null);
//
//                } else if (bookingBillBean.getPaymentMode().equals("4")) {
//                    PrinterTester.getInstance().printStr("Pay "+getResources().getString(R.string.special_pass)+"    " + "Rs. "+bookingBillBean.getTotalPaybleAmount()+"\n",null);
//
//                } else if (bookingBillBean.getPaymentMode().equals("3")) {
//                    PrinterTester.getInstance().printStr("Pay "+getResources().getString(R.string.normal_pass)+"    " + "Rs. "+bookingBillBean.getTotalPaybleAmount()+"\n",null);
//
//                }
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter("Inclusive of GST @ 18%", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().printStr("Inclusive of GST @ 18%"+"\n",null);
                if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Maa Vaishno Company", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("GSTIN:"+SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo"), PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                    PrinterTester.getInstance().printStr("GSTIN:"+"36AAJCA9683RIZK"+"\n",null);
                    //printCustomnew("Download s-Parking App from Play Store\n", 20, 1, true);
                }else {
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Maa Vaishno Company", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");                    print_bill.append(paddingCenter("GSTIN:"+SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo"), PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("Thank You. Please visit again!", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("www.smartpower.co.in", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                    PrinterTester.getInstance().printStr("GSTIN:"+"19AABCB0977F1ZP"+"\n",null);
//                    PrinterTester.getInstance().printStr("Download s-Parking from Play Store"+"\n",null);
                }

                PrinterTester.getInstance().printStr("\n",null);
                PrinterTester.getInstance().printStr("\n",null);
                PrinterTester.getInstance().printStr("\n",null);

                PrinterTester.getInstance().step(2);


                final String status = PrinterTester.getInstance().start();
                m_EditText_VehicleNumber.post(new Runnable() {
                    public void run() {
                        if (status.equals("Out of paper ")){
                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                    OfflineCheckOutActivity.this).create();

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
                                       // printEazytapBill();
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

                            // callBT_forboom api call on separate thread
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try{
                                        callBT_forboom();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            // btn enable after printing
                            btn_endParking.setClickable(true);
                            btn_endParking.setEnabled(true);
                            try {
                                Thread.sleep(300);
                                page_change();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();


    }
    private void printEazytapBillUsingSDK() {

        new Thread(new Runnable() {
            public void run() {

                PrinterTester.getInstance().init();
                Bitmap bitmap = null;
                JSONObject jsonRequest = new JSONObject();
                JSONObject jsonImageObj = new JSONObject();

                Integer iBitmapBaseHeight = 680;
                String gstNo = SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo");
                if(null == gstNo || gstNo.trim().equals(""))
                {
                    iBitmapBaseHeight -= 24;
                }

                String[] arrLocName = breakStringToLines(SharedStorage.getValue(OfflineCheckOutActivity.this,"parkinglocation"),35);
                if(arrLocName.length <= 1)
                {
                    bitmap = Bitmap.createBitmap(400, iBitmapBaseHeight, Bitmap.Config.ARGB_8888);
                }
                else
                {
                    Integer bitmapHeight = iBitmapBaseHeight + ((arrLocName.length - 1) * 24);
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
                paint.setTextSize((int) (30));
                String strText = "OUTSLIP";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                int x = (bitmap.getWidth() - bounds.width())/2;
                int y = 30;
                canvas.drawText(strText, x, y, paint);
//
//                paint.setTextSize((int) (30));
//                strText = "Smartpower";
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width())/2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);

                // Set second line in Bitmap
                paint.setTextSize((int) (26));
                strText = "Parking Maintenance Charge";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set third line in Bitmap
                paint.setTextSize((int) (22));
                // strText = SharedStorage.getValue(OfflineCheckOutActivity.this,"parkinglocation");
                //String[] arrLocName = breakStringToLines(strText,35);
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

                // Set fourth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Vehicle No   : "+ bookingBillBean.getVechile_no();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 62;
                canvas.drawText(strText, x, y, paint);

                // Set fifth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Booking No : "+ bookingBillBean.getBookingID();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set sixth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "In Time          : "+checkintime;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set seventh line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Out Time      : "+checkouttime;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set eighth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Duration       : "+bookingBillBean.getTotalDuration();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);

//            // Set ninth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Amount         : "+ "Rs. "+bookingBillBean.getTotalParkingAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set tenth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Fine Amount : "+"Rs. "+bookingBillBean.getFineAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set eleventh line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Discount       : "+"Rs. "+bookingBillBean.getOfferAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);

                // Set twelfth line in Bitmap
                Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                paint.setTypeface(typeface2);
                paint.setTextSize((int) (22));
                y += 24;
                strText = "-------------------------------------------------------";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);
                double fare= 0.00;
                double gst_fare= 0.00;
                double sgst_calc=0.00;
                double cgst_calc=0.00;
                int TotalAmt= Integer.parseInt(bookingBillBean.getTotalPaybleAmount());
                fare = TotalAmt*100;
                gst_fare= Double.parseDouble(String.format("%.2f", fare/118));

                sgst_calc= Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));
                cgst_calc=  Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));

                // Set twelfth line in Bitmap
                paint.setTextSize((int) (24));
                y += 24;
                strText = "-------------------------------------------------------";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set thirteenth line in Bitmap
                paint.setTextSize((int) (26));
                strText = "Total Amt." + "   : "+ "Rs. "+gst_fare;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);
//// Set thirteenth line in Bitmap
//                paint.setTextSize((int) (26));
//                strText = "GST   "+"18%";
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = 25;
//                y += 24;
//                canvas.drawText(strText, x, y, paint); paint.setTextSize((int) (26));
//
                paint.setTextSize((int) (26));
                strText = "CGST @9%: "+"Rs. "+cgst_calc;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint); paint.setTextSize((int) (26));
                strText = "SGST @9%: "+"Rs. "+sgst_calc;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);
//// Set thirteenth line in Bitmap
//                paint.setTextSize((int) (26));
//                strText = "Pay "+PaymentMode+"   : "+ "Rs. "+bookingBillBean.getTotalPaybleAmount();
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = 25;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);


                if (bookingBillBean.getPaymentMode().equals("1")) {
                    // Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Pay CASH      : "+ "Rs. "+bookingBillBean.getTotalPaybleAmount();
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                }else if (bookingBillBean.getPaymentMode().equals("6")) {
                    // Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Pay CARD       : "+ "Rs. "+bookingBillBean.getTotalPaybleAmount();
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 35;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
                }else {
                    // Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Pay CASH        : " + "Rs. " + bookingBillBean.getTotalPaybleAmount();
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                }

                Typeface typeface3 = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                paint.setTypeface(typeface3);
                paint.setTextSize((int) (22));

                strText = "-------------------------------------------------------";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 25;
                y += 24;
                canvas.drawText(strText, x, y, paint);
//                if(!(null == gstNo || gstNo.trim().equals("")))
                {


//                    // Set fourteenth line in Bitmap
//                    paint.setTextSize((int) (24));
//                    strText = "Smartpower";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width())/2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);

                    // Set fifteenth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "GSTIN : "+SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo");
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    Typeface typeface4 = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    paint.setTypeface(typeface4);
                    paint.setTextSize((int) (24));

                    strText = "-------------------------------------------------------";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
                }




                // Set sixteenth line in Bitmap
//                paint.setTextSize((int) (24));
//                y +=24;
//                strText = "Thank You. Please visit again!";
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width())/2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);
//
//                // Set sixteenth line in Bitmap
//                paint.setTextSize((int) (24));
//                y +=24;
//                strText = "www.smartpower.co.in";
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width())/2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);
                paint.setTextSize((int) (24));
                y +=24;
                strText = "\n" +
                        "Thank You. Please visit again!";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);
                // Set sixteenth line in Bitmap

                paint.setTextSize((int) (24));
                y +=24;
                strText = "www.s-parking.com";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);


                PrinterTester.getInstance().printBitmap(bitmap);

                PrinterTester.getInstance().step(2);
                final String status = PrinterTester.getInstance().start();

                m_EditText_VehicleNumber.post(new Runnable() {
                    public void run() {
                        if (status.equals("Out of paper ")){
                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                    OfflineCheckOutActivity.this).create();

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
                                        printEazytapBillUsingSDK();
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

//                            // callBT_forboom api call on separate thread
//                            new Thread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    try{
//                                        callBT_forboom();
//                                    }catch(Exception e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();
                            // btn enable after printing
                            btn_endParking.setClickable(true);
                            btn_endParking.setEnabled(true);
                            try {
                                Thread.sleep(300);
                                page_change();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();


    }

    /*******************  end of eazyTap printer printing code ****************/
    private void printEazytapBillNew() {
        try
        {
            Bitmap bitmap = null;
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            Integer iBitmapBaseHeight = 570;
            String gstNo = SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo");
            if(null == gstNo || gstNo.trim().equals(""))
            {
                iBitmapBaseHeight -= 24;
            }

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(OfflineCheckOutActivity.this,"parkinglocation"),35);
            if(arrLocName.length <= 1)
            {
                bitmap = Bitmap.createBitmap(400, iBitmapBaseHeight, Bitmap.Config.ARGB_8888);
            }
            else
            {
                Integer bitmapHeight = iBitmapBaseHeight + ((arrLocName.length - 1) * 24);
                bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
            }
            bitmap.eraseColor(Color.WHITE);


            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 0));
            Rect bounds = new Rect();

            // Set first line in Bitmap
            paint.setTextSize((int) (26));
            String strText = "OUTSLIP";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = 30;
            canvas.drawText(strText, x, y, paint);

            // Set second line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Parking Maintenance Charge";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set third line in Bitmap
            paint.setTextSize((int) (22));
            // strText = SharedStorage.getValue(OfflineCheckOutActivity.this,"parkinglocation");
            //String[] arrLocName = breakStringToLines(strText,35);
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

            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle No   : "+ bookingBillBean.getVechile_no();
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fifth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No : "+ bookingBillBean.getBookingID();
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "In Time         : "+checkintime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set seventh line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Out Time       : "+checkouttime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set eighth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Duration    : "+bookingBillBean.getTotalDuration();
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

//            // Set ninth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Amount         : "+ "Rs. "+bookingBillBean.getTotalParkingAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set tenth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Fine Amount : "+"Rs. "+bookingBillBean.getFineAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set eleventh line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Discount       : "+"Rs. "+bookingBillBean.getOfferAmount();
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);

            // Set twelfth line in Bitmap
            paint.setTextSize((int) (20));
            y += 10;
            strText = "-------------------------------------------------------";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            if (bookingBillBean.getPaymentMode().equals("1")) {
                // Set thirteenth line in Bitmap
                paint.setTextSize((int) (26));
                strText = "Pay CASH       : "+ "Rs. "+bookingBillBean.getTotalPaybleAmount();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 35;
                y += 24;
                canvas.drawText(strText, x, y, paint);

            }else if (bookingBillBean.getPaymentMode().equals("6")) {
                // Set thirteenth line in Bitmap
                paint.setTextSize((int) (26));
                strText = "Pay CARD      : "+ "Rs. "+bookingBillBean.getTotalPaybleAmount();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 35;
                y += 24;
                canvas.drawText(strText, x, y, paint);
            }else {
                // Set thirteenth line in Bitmap
                paint.setTextSize((int) (26));
                strText = "Pay CASH       : " + "Rs. " + bookingBillBean.getTotalPaybleAmount();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = 35;
                y += 24;
                canvas.drawText(strText, x, y, paint);

            }
            paint.setTextSize((int) (20));
            strText = "-------------------------------------------------------";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);


            if(!(null == gstNo || gstNo.trim().equals(""))) {


// Set fifteenth line in Bitmap
                paint.setTextSize((int) (22));
                strText = "GSTIN : "+SharedStorage.getValue(OfflineCheckOutActivity.this,"AgencyGSTNo");
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);
            }

            // Set sixteenth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Managed by Rohini Enterpise";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 24;
            canvas.drawText(strText, x, y, paint);


            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            // Building Image Object
            jsonImageObj.put("imageData", encodedImageData);
            jsonImageObj.put("imageType", "JPEG");
            jsonRequest.put("image", jsonImageObj); // Pass this attribute when you have a valid captured signature image
            EzeAPI.printBitmap(OfflineCheckOutActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);
//            m_EditText_VehicleNumber.post(new Runnable() {
//                public void run() {
//                    //    CToast.show(getApplicationContext(),status);
//                    try {
//                        Thread.sleep(300);
//                        page_change();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        }
        catch(Exception ex)
        {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 2) {

            String returnedResult = intent.getData().toString().trim().replaceAll(" ", "");
            m_EditText_VehicleNumber.setText(returnedResult);
        }
        if (requestCode == REQUEST_CODE_PRINT_BITMAP) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response", String.valueOf(response));

                        // callBT_forboom api call on separate thread
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try{
                                    callBT_forboom();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        try {
                            Thread.sleep(300);
                            page_change();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                OfflineCheckOutActivity.this).create();

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
                                    printEazytapBillNew();
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
                                OfflineCheckOutActivity.this).create();

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
                                    printEazytapBillNew();
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
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(OfflineCheckOutActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(OfflineCheckOutActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(OfflineCheckOutActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    try{
                        if (result != null) {
                            String rawresult =  result.getContents();

                            Log.e("result", rawresult);
                            if (result.getContents() == null) {
                                m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
                                btn_endParking.setVisibility(View.VISIBLE);
                            } else {

                                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                    Log.e("result--->", result.getContents().toString().toUpperCase());
                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
                    str_checking=checkinobj.getString("checkintime").split(" ");
                    et_checkinDate.setText(str_checking[0]);
                    et_checkinTime.setText(str_checking[1]);
                    et_bookingid.setText(checkinobj.getString("BookingID"));
                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/

                                    String qr_result = result.getContents().toString().toUpperCase();
                                    str_qrdata = qr_result.split("##");
                                    m_EditText_VehicleNumber.setText(str_qrdata[0]);
                                    str_checking = str_qrdata[1].split(" ");
                                    et_checkinDate.setText(str_checking[0]);
                                    et_checkinTime.setText(str_checking[1]);
                                    et_bookingid.setText(str_qrdata[2]);
                                    iVehicleType = Integer.valueOf(str_qrdata[3]);

                                    //ownermobilenumber = checkinobj.getString("mobilenum");
                                    spinner_Vehicle_Type.setSelection(iVehicleType);
                                    et_acess_control.setText(str_qrdata[4]);

                                }else {
                                    Log.e("result--->", result.getContents().toString().toUpperCase());
                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
                    str_checking=checkinobj.getString("checkintime").split(" ");
                    et_checkinDate.setText(str_checking[0]);
                    et_checkinTime.setText(str_checking[1]);
                    et_bookingid.setText(checkinobj.getString("BookingID"));
                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/

                                    String qr_result = result.getContents().toString().toUpperCase();
                                    str_qrdata = qr_result.split("##");
                                    m_EditText_VehicleNumber.setText(str_qrdata[0]);
                                    str_checking = str_qrdata[1].split(" ");
                                    et_checkinDate.setText(str_checking[0]);
                                    et_checkinTime.setText(str_checking[1]);
                                    et_bookingid.setText(str_qrdata[2]);
                                    iVehicleType = Integer.valueOf(str_qrdata[3]);
                                    spinner_Vehicle_Type.setSelection(iVehicleType);
                                    //ownermobilenumber = checkinobj.getString("mobilenum");


                                }

                            }
                        }else {
                            m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
                            btn_endParking.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    ;
                    rl_scan.setVisibility(View.GONE);
                    btn_endParking.setVisibility(View.VISIBLE);

                }
            });

    private void deleteofflinefile(String exetype){
        databaseHandler.deletevehiclecheckinNotSync(exetype);

    }
    private void deleteofflineAndOnlinefile(String vehicle_number){
        databaseHandler.deletevehiclecheckinOnlineOffline(vehicle_number);

    }
    private boolean isExistInOfflineAndOnlineTable(String Vehicle_Number) {
        boolean result = false;
        result= databaseHandler.isVehicleCheckedInorNot(Vehicle_Number);
        return result;
    }
    private boolean isAlreadyCheckedOutorNot(String Vehicle_Number) {
        boolean result = false;
        result= databaseHandler.isAlreadyCheckedOutorNot(Vehicle_Number);
        return result;
    }

}