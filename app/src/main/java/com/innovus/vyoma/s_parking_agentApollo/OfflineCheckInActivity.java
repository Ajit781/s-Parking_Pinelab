package com.innovus.vyoma.s_parking_agentApollo;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Message;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crashlytics.android.Crashlytics;

import com.eze.api.EzeAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_objects.SParkingAgentModel;
import data_objects.bean.VehicleCheckInBean;
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
import utilities.printer_utils.Utils;



public class OfflineCheckInActivity extends AppCompatActivity implements View.OnClickListener/*, SwipeListener*/, AsyncResponse, AdapterView.OnItemSelectedListener {

    EditText scanResults,et_owner_ph_no,et_checkinDate,et_checkinTime,et_bookingid;
    private CheckBox check_pass;
    private Button btn_checkin,btn_ScanQR,default_number_btn;
    private Spinner sp_store,spinner_Vehicle_Type;
    private TextView m_TxtVStartParkingMessage,tv_vehicle_type;
    private LinearLayout freeparking_lay;
    private RadioGroup radioGroup_vehicletype;
    private ImageView iv_printer;
    private RadioButton radioButton_twowheel,radioButton_fourwheel;
    private String bookingNumber = "";
    String parkingareaid = "";
    String strVehicleType = "";
    private String[] str_checking;
    private String[] str_qrdata;
    private LinearLayout ll_check_pass,ll_pass_main;
    private Uri imageUri;
    /******* for custom qr scan **************/
    private LinearLayout lay_scan;
    private RelativeLayout scan_linear;
    private ImageView disable_scan_qr;
   // private ZXingScannerView mScannerView;
    Animation bottomin,bottomout;

    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;
    String strMobileNumber = "";
    String strVehicleNo = "";
    String actualaccesscontrlamount = "";
    public static final int NOT_FOUND = -1;
    int readBufferPosition;
    volatile boolean stopWorker;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private String m_strUserName;
    private Integer m_iUId;
    private String blockCharacterSet = "@~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹~ ";
   // private IntentIntegrator m_qrScan = null;
    private String passapplied = "0";
    private String strStoreid = "0";
    RemoteAsync remoteAsync;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    private SpotsDialog progressDialog;
    String payment_mode ="";
    int isServerError =0;
    private String version_name = "";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    DatabaseHandler databaseHandler;
    String strVehicleNumber = "";
    Integer iVehicleType = 0;
    VehicleCheckInBean vehicleCheckInBean;
    final String[] radiocheck = {""};

    DataOutputStream mmOutputStream_forboom;
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
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private FloatView floatView;
    private final int REQUEST_CODE_PRINT_BITMAP = 10029;// for APi print for pax
    private Context mContext;
    private ExecutorService mSingleThreadExecutor;
    ArrayList<VehicleType> vehicletypeArrayList = new ArrayList<VehicleType>();

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Toast.makeText(OfflineCheckInActivity.this,
                            "Printing now,pls wait for a moment", Toast.LENGTH_LONG)
                            .show();
                    break;

                default:
                    break;
            }
        };
    };

    // Service Response
    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    resetallfield();
                    //Redirecting to dashboard screen
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));


                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {


                }
                else {

                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, msg.getString("message"));

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
                                // check App version service request
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
                            // check App version service request
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
                            // check App version service request
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_check_in);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+getResources().getString(R.string.app_name)+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;

        //show and hide keyboard
        if(getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

            databaseHandler = new DatabaseHandler(getApplicationContext());
        }


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
                                // check App version service request
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
    // print Bill using EzeTap
    private void printEazytapBill(String vehicle_number) {

        new Thread(new Runnable() {
            public void run() {

                String dateTime[] = getDateTime();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
                StringBuilder print_bill = new StringBuilder();
                if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    print_bill.append(paddingCenter(SharedStorage.getValue(getApplicationContext(),"AgencyName"), PAGE_WIDTH_TWO_INCH)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                    PrinterTester.getInstance().printStr(SharedStorage.getValue(getApplicationContext(),"AgencyName")+"\n",null);
                }else{
//                    PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
                    print_bill.append(paddingCenter("sParking", PAGE_WIDTH_TWO_INCH)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                    PrinterTester.getInstance().printStr("sParking\n",null);
                    PrinterTester.getInstance().step(2);
                    print_bill = new StringBuilder();
//                    PrinterTester.getInstance().leftIndents(Short.parseShort("78"));
                  //  PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_32);
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_16);
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Welcome", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("60"));
                }

                String str = SharedStorage.getValue(OfflineCheckInActivity.this,"parkinglocation");
                String[] arrOfStr = str.split("-");

                for (String str_location : arrOfStr){
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter(str_location, PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                }


                PrinterTester.getInstance().leftIndents(Short.parseShort("1"));
                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                    PrinterTester.getInstance().printStr("Access amt : "+actualaccesscontrlamount+"\n",null);
                }

                PrinterTester.getInstance().printStr("\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().printStr("Vehicle No : "+vehicle_number+"\n",null);
                PrinterTester.getInstance().printStr("Vehicle Type: "+strVehicleType+"\n",null);
                PrinterTester.getInstance().printStr("CheckIn Time: "+dateTime[0]+" "+dateTime[1]+"\n",null);
                PrinterTester.getInstance().printStr("Booking No  : "+bookingNumber+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                try {

                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        //data that will be stored in qr image
                        strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                                vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype()
                                +"##"+actualaccesscontrlamount;


                    }else {
                        strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                                vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype();
                    }
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 250, 250);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }

                        Bitmap bitmap = pad(bmp,50,0);

                        if(bitmap!=null){
                            byte[] command = Utils.decodeBitmap(bitmap);
                            PrinterTester.getInstance().printBitmap(bitmap);

//                            PrinterTester.getInstance().leftIndents(Short.parseShort("5"));

                            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                print_bill = new StringBuilder();
                                print_bill.append(paddingCenter("Loss of Ticket = 500", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                                PrinterTester.getInstance().printStr("Loss of Ticket = 500\n",null);

                            }else {
                                print_bill = new StringBuilder();
                                print_bill.append(paddingCenter("www.smartpower.co.in", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                                PrinterTester.getInstance().printStr("Download s-Parking App from Play Store\n",null);


                            }
                            PrinterTester.getInstance().printStr("\n",null);
                            PrinterTester.getInstance().printStr("\n",null);
                            PrinterTester.getInstance().step(60);
                        }else{
                            Log.e("Print Photo error", "the file isn't exists");
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                PrinterTester.getInstance().step(2);
                final String status = PrinterTester.getInstance().start();
                // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());
                scanResults.post(new Runnable() {
                    public void run() {
                        // CToast.show(getApplicationContext(),status);
                        if (status.equals("Out of paper ")){
                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                    OfflineCheckInActivity.this).create();

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
                                        printEazytapBill(vehicle_number);
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

                            btn_checkin.setClickable(true);
                            btn_checkin.setEnabled(true);
                            try {
                                resetallfield();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

                        }

                    }
                });
            }
        }).start();


    }
    private void printEazytapBillUsingSDK(String Vehicle_Number) {

        new Thread(new Runnable() {
            public void run() {

                PrinterTester.getInstance().init();
                Bitmap bitmap = null;
                JSONObject jsonRequest = new JSONObject();
                JSONObject jsonImageObj = new JSONObject();

                String[] arrLocName = breakStringToLines(SharedStorage.getValue(OfflineCheckInActivity.this, "parkinglocation"), 35);
                if (arrLocName.length <= 1) {
                    bitmap = Bitmap.createBitmap(400, 580, Bitmap.Config.ARGB_8888);
                } else {
                    Integer bitmapHeight = 580 + ((arrLocName.length - 1) * 24);
                    bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
                }

                bitmap.eraseColor(Color.WHITE);

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                paint.setTypeface(typeface);
                paint.setColor(Color.rgb(0, 0, 0));
                Rect bounds = new Rect();
                String strText="";
                int x=0;
                int y=0;
                if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    // Set first line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = SharedStorage.getValue(getApplicationContext(),"AgencyName");
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y = 30;
                    canvas.drawText(strText, x, y, paint);

                }
                else{
                    // Set first line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "INSLIP";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y = 30;
                    canvas.drawText(strText, x, y, paint);

                    // Set second line in Bitmap
                    paint.setTextSize((int) (22));
                    strText = "www.s-parking.com";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

//                    paint.setTextSize((int) (26));
//                    strText = "SmartPower";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width()) / 2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);

                }

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
                // Set fourth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Vehicle No      : "+Vehicle_Number;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                x = 20;
                y += 62;
                canvas.drawText(strText, x, y, paint);

                // Set fourth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Vehicle Type   : "+strVehicleType;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                x = 20;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                String dateTime[] = getDateTime();
                // Set fifth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "CheckIn Time : "+vehicleCheckInBean.getCheckintime();
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                x = 20;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set sixth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Booking No     : "+bookingNumber;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                x = 20;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                //Set seven line in Bitmap
                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    paint.setTextSize((int) (22));
                    strText = "Access amt : "+actualaccesscontrlamount;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
                }
                // Set qr line in Bitmap

                y += 10;

                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    //data that will be stored in qr image
                    strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                            vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype()
                            +"##"+actualaccesscontrlamount;


                }else {
                    strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                            vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype();
                }
                try{
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
                }catch(Exception e){
                    e.printStackTrace();
                }
                paint.setTextSize((int) (22));
                strText = "Thank You. Please visit again!";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 274;
                canvas.drawText(strText, x, y, paint);

                // Set seventh line in Bitmap
//                paint.setTextSize((int) (24));
//                strText = "VISIT AGAIN!";
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width()) / 2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);


                if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    // Set seventh line in Bitmap
                    paint.setTextSize((int) (22));
                    strText = "Loss of Ticket = 500";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 264;
                    canvas.drawText(strText, x, y, paint);


                }else {
//                    // Set seventh line in Bitmap
//                    paint.setTextSize((int) (24));
//                    strText = "www.smartpower.co.in";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width())/2;
//                    y += 260;
//                    canvas.drawText(strText, x, y, paint);

                }
                PrinterTester.getInstance().printBitmap(bitmap);

                PrinterTester.getInstance().step(2);
                final String status = PrinterTester.getInstance().start();
                // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());
                scanResults.post(new Runnable() {
                    public void run() {
                        // CToast.show(getApplicationContext(),status);
                        if (status.equals("Out of paper ")){
                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                    OfflineCheckInActivity.this).create();

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
                                        printEazytapBillUsingSDK(Vehicle_Number);

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

                            try {
                                resetallfield();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

                            btn_checkin.setClickable(true);
                            btn_checkin.setEnabled(true);
                        }

                    }
                });
            }
        }).start();


    }
    // print Bill using Verifone device
    private void printEazytapBillNew(String Vehicle_Number) {
        try
        {
           // PrinterTester.getInstance().init();
            Bitmap bitmap = null;
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(OfflineCheckInActivity.this, "parkinglocation"), 35);
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

            String strText="";
            int x=0;
            int y=0;
            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                // Set first line in Bitmap
                paint.setTextSize((int) (24));
                strText = SharedStorage.getValue(getApplicationContext(),"AgencyName");
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y = 30;
                canvas.drawText(strText, x, y, paint);

            }
            else{
                // Set first line in Bitmap
                paint.setTextSize((int) (26));
                strText = "INSLIP";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y = 30;
                canvas.drawText(strText, x, y, paint);
            }

            paint.setTextSize((int) (22));

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
            String gstNo = SharedStorage.getValue(OfflineCheckInActivity.this,"AgencyGSTNo");

            if(!gstNo.equals("")){

                // Set fourth line in Bitmap
                paint.setTextSize((int) (24));
                strText = "GST - : " + gstNo;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width()) / 2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

            }
            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle No      : "+Vehicle_Number;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle Type   : "+strVehicleType;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            String dateTime[] = getDateTime();
            // Set fifth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "CheckIn Time : "+vehicleCheckInBean.getCheckintime();
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No     : "+bookingNumber;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            //Set seven line in Bitmap
            if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                paint.setTextSize((int) (22));
                strText = "Access amt : "+actualaccesscontrlamount;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);
            }
            // Set qr line in Bitmap

            y += 10;

            if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                //data that will be stored in qr image
                strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                        vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype()
                        +"##"+actualaccesscontrlamount;


            }else {
                strVehicleNo = vehicleCheckInBean.getVehicle_number()+"##"+vehicleCheckInBean.getCheckintime()+"##"+
                        vehicleCheckInBean.getBookingid()+"##"+vehicleCheckInBean.getVehicletype();
            }
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

            if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                // Set seventh line in Bitmap
                paint.setTextSize((int) (22));
                strText = "Loss of Ticket = 500";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 264;
                canvas.drawText(strText, x, y, paint);


            }else {
                // Set seventh line in Bitmap
                paint.setTextSize((int) (24));
                strText = "Download s-Parking from Play Store";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 260;
                canvas.drawText(strText, x, y, paint);

            }


            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            // Building Image Object
            jsonImageObj.put("imageData", encodedImageData);
            jsonImageObj.put("imageType", "JPEG");
            jsonRequest.put("image", jsonImageObj); // Pass this attribute when you have a valid captured signature image
            EzeAPI.printBitmap(OfflineCheckInActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);

            scanResults.post(new Runnable() {
                public void run() {
                    //CToast.show(getApplicationContext(),status);
                    try {
                        Thread.sleep(300);
                        resetallfield();
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


    /*******************  end of eazyTap printer printing code ****************/
    //CheckAppsVersion Request
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(mScannerView!= null){
//           // mScannerView.stopCamera();
//        }

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
//                else {
//                    Log.e("result", result.getText().toString().toUpperCase());
//
//                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        str_checking = str_qrdata[0].split(" ");
//                        et_checkinDate.setText(str_checking[0]);
//                        et_checkinTime.setText(str_checking[1]);
//                        et_bookingid.setText(str_qrdata[1]);
//                    }else {
//                        if (result.getText().contains("#")) {
//
//                            ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,getResources().getString(R.string.proper_qr_read));
//
//                        } else {
//                            scanResults.setText(result.getText().toString().toUpperCase());
//                        }
//                    }
//
//                }
//            }
//            else
//            {
//                m_TxtVStartParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        mScannerView.stopCameraPreview();
//        mScannerView.stopCamera();
//        scan_linear.setVisibility(View.GONE);
//        scan_linear.setAnimation(bottomout);
//        lay_scan.removeAllViews();
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        strVehicleType=spinner_Vehicle_Type.getSelectedItem().toString();
        iVehicleType = Integer.valueOf(dataModel.vehicletypeArrayList.get(position).getVehicleTypeId());
        tv_vehicle_type.setText(dataModel.vehicletypeArrayList.get(position).getVehicleTypeName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class clientSock extends Thread {
        public void run () {
            try {
                mmOutputStream_forboom.writeBytes(getResources().getString(R.string.commandtofire)); // anything you want
                mmOutputStream_forboom.flush();

                //closeBT_forBoom();
                resetallfield();// Reset Fields after checking

            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }

    // close the connection to bluetooth printer.
    void closeBT_forBoom() throws IOException {
        try {
            stopWorker = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initviews() {

        dataModel.about_advanced_dash = 1;

        scanResults=(EditText) findViewById(R.id.et_Vehicleno);
        et_owner_ph_no=(EditText)findViewById(R.id.et_owner_ph_no);
        check_pass = (CheckBox) findViewById(R.id.check_pass);
        btn_checkin=(Button) findViewById(R.id.btn_checkin);
        btn_ScanQR=(Button)findViewById(R.id.btn_ScanQR);
        radioGroup_vehicletype=(RadioGroup) findViewById(R.id.radioGroup_vehicletype);
        radioButton_twowheel=(RadioButton)findViewById(R.id.radioButton_twowheel);
        radioButton_fourwheel=(RadioButton)findViewById(R.id.radioButton_fourwheel);
        m_TxtVStartParkingMessage=(TextView)findViewById(R.id.TEXTVIEW_startparkingmessage);
        tv_vehicle_type=(TextView)findViewById(R.id.tv_vehicle_type);
        ll_pass_main = (LinearLayout) findViewById(R.id.ll_pass_main);
        ll_check_pass = (LinearLayout) findViewById(R.id.ll_check_pass);
        spinner_Vehicle_Type=(Spinner)findViewById(R.id.spinner_Vehicle_Type);
        sp_store=(Spinner)findViewById(R.id.sp_store);
        iv_printer=(ImageView)findViewById(R.id.iv_printer);
        default_number_btn= (Button) findViewById(R.id.default_number_btn);

        /********** for railway check in *************/

        freeparking_lay = (LinearLayout) findViewById(R.id.freeparking_lay);
        et_checkinDate = (EditText) findViewById(R.id.et_checkinDate);
        et_checkinTime = (EditText) findViewById(R.id.et_checkinTime);
        et_bookingid = (EditText) findViewById(R.id.et_bookingid);


        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

            freeparking_lay.setVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            et_checkinDate.setText(dateFormat.format(new Date()));
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
                        }else {
                            Log.e("in else part", String.valueOf(keyCode));
                        }
                    }catch (Exception e){
                        Log.e("inside catch","inside catch");
                        e.printStackTrace();
                        Log.e("exception",e.getMessage());
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

            //to maintain 3digits for parking area id
            if (parkingareaid.length() == 1){
                parkingareaid = "00" + parkingareaid;
            }else if (parkingareaid.length() == 2){
                parkingareaid = "0" + parkingareaid;
            }

            String createbooking_id = parkingareaid + "-" + String.valueOf(seconds);

            Log.e("seconds_booking", createbooking_id);

            et_bookingid.setText(createbooking_id.substring(0, createbooking_id.length() - 5) + "-");
            et_bookingid.setSelection(et_bookingid.getText().toString().length());

        }
        else{
            freeparking_lay.setVisibility(View.GONE);
        }


        /********* in that page scan ***************/
        lay_scan= (LinearLayout) findViewById(R.id.lay_scan);
        scan_linear= (RelativeLayout) findViewById(R.id.scan_linear);
        disable_scan_qr= (ImageView) findViewById(R.id.disable_scan_qr);
        bottomin = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_in);
        bottomout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_out);

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
        et_owner_ph_no.requestFocus();

        default_number_btn.setOnClickListener(this);
        btn_checkin.setOnClickListener(this);
        btn_ScanQR.setOnClickListener(this);
        disable_scan_qr.setOnClickListener(this);

        strVehicleNumber = scanResults.getText().toString().trim();

        radioGroup_vehicletype.clearCheck();

        radioGroup_vehicletype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if(checkedId == R.id.radioButton_twowheel){

                        radiocheck[0] = "1";
                        iVehicleType=1;

                    }else if(checkedId == R.id.radioButton_fourwheel){

                        radiocheck[0] = "2";
                        iVehicleType=2;
                    }
                }
            }
        });

        scanResults.setFilters(new InputFilter[] { filter });
        if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            scanResults.setText(getResources().getString(R.string.statecode_ts));
        }else {
            scanResults.setText(getResources().getString(R.string.statecode));
        }

        scanResults.setSelection(scanResults.getText().toString().length());

        m_TxtVStartParkingMessage = (TextView)findViewById(R.id.TEXTVIEW_startparkingmessage);

        check_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    passapplied = "1";
                    sp_store.setVisibility(View.VISIBLE);

                }else {
                    passapplied= "0";
                    strStoreid = "0";
                    sp_store.setVisibility(View.GONE);
                }
            }
        });

        if (!SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("")){

            floatView = FloatView.getInstance(OfflineCheckInActivity.this);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //to find bluetooth device
    @SuppressLint("MissingPermission")
    private void findBT() {

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
                    if (device.getName().equals(SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name"))) {
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

    public static String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        return newString;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_checkin) {

            if (validate()) {

                btn_checkin.setClickable(false);
                btn_checkin.setEnabled(false);
                if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){//check in for access control parking

                    if (et_owner_ph_no.getText().toString().trim().equals("")){

                        StringBuilder defaultnumber = new StringBuilder();

                        for(int i = 0;i<=getResources().getString(R.string.defaultmobilenumber).length();i++){
                            if(i>=5){
                                defaultnumber.append("X");
                            }else{
                                defaultnumber.append(getResources().getString(R.string.defaultmobilenumber).charAt(i));
                            }
                        }

                        et_owner_ph_no.setText(defaultnumber);

                        et_owner_ph_no.setSelection(et_owner_ph_no.getText().toString().length());
                    }

                }

                if (isValid(scanResults.getText().toString().trim())) {
                    strVehicleNumber = scanResults.getText().toString().trim();

                   if(et_owner_ph_no.getText().toString().contains("X")){

                       strMobileNumber = getResources().getString(R.string.defaultmobilenumber);

                       String dateTime[] = getDateTime();
                       String checkintime = dateTime[0]+" "+dateTime[1];

                       if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                           bookingNumber = et_bookingid.getText().toString().trim();

                           actualaccesscontrlamount = accesscontrolamount(checkintime);

                       }else{
                           long millis = System.currentTimeMillis();

                           //Divide millis by 1000 to get the number of seconds.
                           long seconds = millis / 1000;

                           String parkingareaid = SharedStorage.getValue(getApplicationContext(), "parking_area_id");

                           Log.e("parkingareaid",parkingareaid);

                           if (parkingareaid.length() == 1){
                               parkingareaid = "00" + parkingareaid;
                           }else if (parkingareaid.length() == 2){
                               parkingareaid = "0" + parkingareaid;
                           }

                           Log.e("booking_number", String.valueOf(seconds));
                           bookingNumber = parkingareaid+"-"+insertString(String.valueOf(seconds),
                                   "-",4);
                       }

                       //long currentTimeMillis ()-Returns the current time in milliseconds.


                       Log.e("bookingNumber",bookingNumber);

                       vehicleCheckInBean = null;
                       //store values in bean class to save database
                       vehicleCheckInBean = new VehicleCheckInBean(strVehicleNumber,checkintime,
                               String.valueOf(iVehicleType),strMobileNumber,passapplied,strStoreid,"0",bookingNumber,"0",0);

                       if (SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("")) {
                           et_owner_ph_no.setText("");
                           radioGroup_vehicletype.clearCheck();
                           spinner_Vehicle_Type.setSelection(iVehicleType);
                           if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                               scanResults.setText("TS");
                           }else {
                               scanResults.setText("UP32");
                           }

                           scanResults.setSelection(scanResults.getText().toString().length());
                           final AlertDialog alertDialog = new AlertDialog.Builder(
                                   this).create();

                           final LayoutInflater inflater = this.getLayoutInflater();
                           View dialogView = inflater.inflate(R.layout.custom_bill_dilog_new_simple, null);
                           alertDialog.setView(dialogView);

                           TextView msg_txt = (TextView) dialogView.findViewById(R.id.messageid);
                           TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                           TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                           TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                           Button bt_cash = (Button) dialogView.findViewById(R.id.btnOk);
                           Button bt_cancel = (Button) dialogView.findViewById(R.id.btnCancel);
                           TextView title = (TextView) dialogView.findViewById(R.id.title);
                           title.setText(getResources().getString(R.string.app_name_title));
                           txt_booking_no.setText(vehicleCheckInBean.getBookingid());
                           txt_vechile_no.setText(vehicleCheckInBean.getVehicle_number());
                           txt_check_in_time.setText(vehicleCheckInBean.getCheckintime());
                           bt_cash.setText(getResources().getString(R.string.ok));
                           msg_txt.setText(getResources().getString(R.string.parking_succesfully));

                           bt_cash.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   alertDialog.dismiss();

                                   //add bean values to local database for offline use
                                   databaseHandler.addofflineVehicleCheckIn(vehicleCheckInBean);
                                   try {
                                       if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                           et_checkinTime.setText("");
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
                                       }
                                       et_owner_ph_no.setText("");
                                       radioGroup_vehicletype.clearCheck();
                                       spinner_Vehicle_Type.setSelection(iVehicleType);

                                       if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                           scanResults.setText(getResources().getString(R.string.statecode_ts));
                                       }else {
                                           scanResults.setText(getResources().getString(R.string.statecode));
                                       }

                                       scanResults.setSelection(scanResults.getText().toString().length());

                                       resetallfield();
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                           });

                           bt_cancel.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   alertDialog.dismiss();
                                   resetallfield();
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
                       else if (SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("eazy_Tap")) {

                           try {
                               //add bean values to local database for offline use
                               databaseHandler.addofflineVehicleCheckIn(vehicleCheckInBean);
                               databaseHandler.addofflineVehicleCheckInNotSync(vehicleCheckInBean);

                               final AlertDialog alertDialog = new AlertDialog.Builder(
                                       this).create();

                               final LayoutInflater inflater = this.getLayoutInflater();
                               View dialogView = inflater.inflate(R.layout.custom_bill_dilog_new_simple, null);
                               alertDialog.setView(dialogView);

                               TextView msg_txt = (TextView) dialogView.findViewById(R.id.messageid);
                               TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                               TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                               TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                               Button bt_cash = (Button) dialogView.findViewById(R.id.btnOk);
                               Button bt_cancel = (Button) dialogView.findViewById(R.id.btnCancel);
                               TextView title = (TextView) dialogView.findViewById(R.id.title);
                               title.setText(getResources().getString(R.string.app_name_title));
                               txt_booking_no.setText(vehicleCheckInBean.getBookingid());
                               txt_vechile_no.setText(vehicleCheckInBean.getVehicle_number());
                               txt_check_in_time.setText(vehicleCheckInBean.getCheckintime());
                               bt_cash.setText(getResources().getString(R.string.ok));
                               msg_txt.setText(getResources().getString(R.string.parking_succesfully));

                               bt_cash.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       alertDialog.dismiss();

                                       //sendData();
                                       //printBill();
                                       printEazytapBillUsingSDK(vehicleCheckInBean.getVehicle_number());

                                       if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                           //print slip
                                           try {
                                               // printEazytapBill(vehicleCheckInBean.getVehicle_number());
                                               // printEazytapBillNew(vehicleCheckInBean.getVehicle_number());

                                               btn_checkin.setClickable(false);
                                               btn_checkin.setEnabled(false);
                                           } catch (Exception e) {

                                               e.printStackTrace();
                                               resetallfield();
                                           }
                                       }
                                       else {
                                           //print slip
                                           try {
                                               // printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
                                               printEazytapBillUsingSDK(vehicleCheckInBean.getVehicle_number());
                                               // printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
                                               btn_checkin.setClickable(true);
                                               btn_checkin.setEnabled(true);
                                           } catch (Exception e) {
                                               ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,"printer not found");
                                               e.printStackTrace();
                                               resetallfield();
                                           }

                                       }
                                    /*Thread.sleep(1000);
                                    closeBT();*/
                                       //printPhoto();
                                   }
                               });

                               bt_cancel.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       alertDialog.dismiss();
                                       resetallfield();
                                       btn_checkin.setClickable(true);
                                       btn_checkin.setEnabled(true);
                                   }
                               });
                               //Animate alert dialog box
                               FragmentTransaction ft = getFragmentManager().beginTransaction();
                               ft.setCustomAnimations(android.R.animator.fade_in,
                                       android.R.animator.fade_out);
                               // Showing Alert Message
                               alertDialog.show();
                               alertDialog.setCancelable(false);



                           } catch (Exception e) {
                               resetallfield();
                               btn_checkin.setClickable(true);
                               btn_checkin.setEnabled(true);
                               e.printStackTrace();
                           }


                       }
                       else if (SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("verifone")) {

                           try {
                               //add bean values to local database for offline use
                               databaseHandler.addofflineVehicleCheckIn(vehicleCheckInBean);


                               //sendData();
                               //printBill();
                               if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                   //print slip
                                   try {
                                       printEazytapBillNew(vehicleCheckInBean.getVehicle_number());

                                       btn_checkin.setClickable(false);
                                       btn_checkin.setEnabled(false);
                                   } catch (Exception e) {

                                       e.printStackTrace();
                                       resetallfield();
                                   }
                               }else {
                                   //print slip
                                   try {
                                       printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
                                       btn_checkin.setClickable(false);
                                       btn_checkin.setEnabled(false);
                                   } catch (Exception e) {
                                       ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,"printer not found");
                                       e.printStackTrace();
                                       resetallfield();
                                   }

                               }
                                    /*Thread.sleep(1000);
                                    closeBT();*/
                               //printPhoto();
                           } catch (Exception e) {
                               resetallfield();
                               btn_checkin.setClickable(true);
                               btn_checkin.setEnabled(true);
                               e.printStackTrace();
                           }


                       }



                   }
                   //when mobile number is not default number
                   else  if(isValidMobile(et_owner_ph_no.getText().toString().trim())){

                       strMobileNumber = et_owner_ph_no.getText().toString().trim();

                       String dateTime[] = getDateTime();
                       String checkintime = dateTime[0]+" "+dateTime[1];

                       if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){

                           bookingNumber = et_bookingid.getText().toString().trim();
                           actualaccesscontrlamount = accesscontrolamount(checkintime);

                       }else {
                           //long currentTimeMillis ()-Returns the current time in milliseconds.
                           long millis = System.currentTimeMillis();

                            //Divide millis by 1000 to get the number of seconds.
                           long seconds = millis / 1000;

                           Log.e("booking_number", String.valueOf(seconds));

                           /*bookingNumber = String.valueOf(seconds);*/

                           String parkingareaid = SharedStorage.getValue(getApplicationContext(), "parking_area_id");

                           Log.e("parkingareaid",parkingareaid);

                           if (parkingareaid.length() == 1){
                               parkingareaid = "00" + parkingareaid;
                           }else if (parkingareaid.length() == 2){
                               parkingareaid = "0" + parkingareaid;
                           }

                           bookingNumber = parkingareaid+"-"+insertString(String.valueOf(seconds),
                                   "-",4);
                       }

                       Log.e("bookingNumber",bookingNumber);

                       vehicleCheckInBean = null;
                       //save values for offline use
                       vehicleCheckInBean = new VehicleCheckInBean(strVehicleNumber,checkintime,
                               String.valueOf(iVehicleType),strMobileNumber,passapplied,strStoreid,"0",bookingNumber,"0",0);


                       if (SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("")) {
                           et_owner_ph_no.setText("");
                           radioGroup_vehicletype.clearCheck();
                           spinner_Vehicle_Type.setSelection(iVehicleType);
                           if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                               scanResults.setText("TS");
                           }else {
                               scanResults.setText("WB");
                           }
                           scanResults.setSelection(scanResults.getText().toString().length());

                           /*ShowAlertDialog.showAlertDialog(this,getResources().getString(R.string.parking_succesfully));*/

                           final AlertDialog alertDialog = new AlertDialog.Builder(
                                   this).create();

                           final LayoutInflater inflater = this.getLayoutInflater();
                           View dialogView = inflater.inflate(R.layout.custom_bill_dilog_new_simple, null);
                           alertDialog.setView(dialogView);

                           TextView msg_txt = (TextView) dialogView.findViewById(R.id.messageid);
                           TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                           TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                           TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                           Button bt_cash = (Button) dialogView.findViewById(R.id.btnOk);
                           Button bt_cancel = (Button) dialogView.findViewById(R.id.btnCancel);
                           TextView title = (TextView) dialogView.findViewById(R.id.title);
                           title.setText(getResources().getString(R.string.app_name_title));
                           txt_booking_no.setText(vehicleCheckInBean.getBookingid());
                           txt_vechile_no.setText(vehicleCheckInBean.getVehicle_number());
                           txt_check_in_time.setText(vehicleCheckInBean.getCheckintime());
                           bt_cash.setText(getResources().getString(R.string.ok));
                           msg_txt.setText(getResources().getString(R.string.parking_succesfully));

                           bt_cash.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   alertDialog.dismiss();
                                   //add bean values to local database for offline use
                                   databaseHandler.addofflineVehicleCheckIn(vehicleCheckInBean);

                               }
                           });

                           bt_cancel.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   alertDialog.dismiss();
                                 resetallfield();

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
                       else if (SharedStorage.getValue(OfflineCheckInActivity.this,"printer_name").equals("eazy_Tap")) {


                           final AlertDialog alertDialog = new AlertDialog.Builder(
                                   this).create();

                           final LayoutInflater inflater = this.getLayoutInflater();
                           View dialogView = inflater.inflate(R.layout.custom_bill_dialog_new, null);
                           alertDialog.setView(dialogView);

                           TextView msg_txt = (TextView) dialogView.findViewById(R.id.messageid);
                           TextView txt_booking_no = (TextView) dialogView.findViewById(R.id.txt_booking_no);
                           TextView txt_vechile_no = (TextView) dialogView.findViewById(R.id.txt_vechile_no);
                           TextView txt_check_in_time = (TextView) dialogView.findViewById(R.id.txt_check_in_time);
                           Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash1);
                           Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel1);
                           TextView title = (TextView) dialogView.findViewById(R.id.title);
                           title.setText(getResources().getString(R.string.app_name_title));
                           txt_booking_no.setText(vehicleCheckInBean.getBookingid());
                           txt_vechile_no.setText(vehicleCheckInBean.getVehicle_number());
                           txt_check_in_time.setText(vehicleCheckInBean.getCheckintime());
                           bt_cash.setText(getResources().getString(R.string.print));
                           msg_txt.setText(getResources().getString(R.string.parking_succesfully));

                           bt_cash.setOnClickListener(new View.OnClickListener() {
                               @SuppressLint("NewApi")
                               @Override
                               public void onClick(View view) {
                                   alertDialog.dismiss();
                                   try {
                                       //add bean values to local database for offline use
                                       databaseHandler.addofflineVehicleCheckIn(vehicleCheckInBean);
                                       //sendData();
                                       //printBill();
                                       if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                           //print slip
                                           try {
                                              // printEazytapBill();
                                               //printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
                                               printEazytapBillUsingSDK(vehicleCheckInBean.getVehicle_number());
                                               btn_checkin.setClickable(true);
                                               btn_checkin.setEnabled(true);
                                           } catch (Exception e) {

                                               e.printStackTrace();
                                               resetallfield();
                                           }
                                       }else {
                                           //print slip
                                           try {
                                               //printEazytapBill();
                                              // printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
                                               printEazytapBillUsingSDK(vehicleCheckInBean.getVehicle_number());
                                               btn_checkin.setClickable(true);
                                               btn_checkin.setEnabled(true);
                                           } catch (Exception e) {

                                               e.printStackTrace();
                                               resetallfield();
                                               btn_checkin.setClickable(true);
                                               btn_checkin.setEnabled(true);
                                           }

                                       }
                                    /*Thread.sleep(1000);
                                    closeBT();*/
                                       //printPhoto();
                                   } catch (Exception e) {
                                       btn_checkin.setClickable(true);
                                       btn_checkin.setEnabled(true);
                                       resetallfield();
                                       e.printStackTrace();
                                   }

                               }
                           });
                           bt_cancel.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   alertDialog.dismiss();
                                   resetallfield();

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
                }
                else {
                    btn_checkin.setClickable(true);
                    btn_checkin.setEnabled(true);
                    ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_vehicle_msg));
                }

            }
        }
//        else if (view.getId() == R.id.btn_ScanQR) {
//            try {
//
//                /*m_qrScan.initiateScan();*/
//
//                hideKeyboard(this);
//                scan_linear.setVisibility(View.VISIBLE);
//                scan_linear.setAnimation(bottomin);
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
//                    mScannerView.setFlash(false);
//                }else {
//                    mScannerView.setFlash(true);
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//        else if (view.getId() == R.id.disable_scan_qr) {
//            try {
//
//                mScannerView.stopCameraPreview();
//                mScannerView.stopCamera();
//                scan_linear.setVisibility(View.GONE);
//                scan_linear.setAnimation(bottomout);
//                lay_scan.removeAllViews();
//
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
        else if(view.getId() == R.id.default_number_btn){
            StringBuilder defaultnumber = new StringBuilder();

            for(int i = 0;i<=getResources().getString(R.string.defaultmobilenumber).length();i++){
                if(i>=5){
                    defaultnumber.append("X");
                }else{
                    defaultnumber.append(getResources().getString(R.string.defaultmobilenumber).charAt(i));
                }
            }

            et_owner_ph_no.setText(defaultnumber);

            et_owner_ph_no.setSelection(et_owner_ph_no.getText().toString().length());

        }
    }

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

    private void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //for amount calculation depending on checkin time
    private String accesscontrolamount(String checkin_time){
        String amount = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String dateTime[] = getDateTime();
        String actualcheckintime ="";
        String accesscheckintime = "";
        accesscheckintime = et_checkinDate.getText().toString().trim() + " " + et_checkinTime.getText().toString().trim();
        actualcheckintime = checkin_time;
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(et_checkinDate.getText().toString().trim() + " " + et_checkinTime.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(actualcheckintime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("date1", String.valueOf(date1.getTime()));
        Log.e("date2", String.valueOf(date2.getTime()));

        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);


        if ((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60) != 0.0) {

            min++;
        }

        Log.e("mins---->", String.valueOf(min));

        databaseHandler.getfreeparkingpricelist();
        int firstaccessendtime = 0;
        int secondaccessendtime = 0;
        int thirdaccessendtime = 0;
        /*int fourthaccessendtime = 0;*/

        firstaccessendtime = Integer.parseInt(dataModel.freeParkingPriceTypeArrayList.get(0).getEndTime());
        secondaccessendtime = Integer.parseInt(dataModel.freeParkingPriceTypeArrayList.get(1).getEndTime());
        thirdaccessendtime = Integer.parseInt(dataModel.freeParkingPriceTypeArrayList.get(2).getEndTime());
        /*fourthaccessendtime = Integer.parseInt(dataModel.freeParkingPriceTypeArrayList.get(3).getEndTime());*/
        if(min > firstaccessendtime){

            if(min > secondaccessendtime){
                if(min > thirdaccessendtime){

                    amount = dataModel.freeParkingPriceTypeArrayList.get(3).getPrice();
                }else {
                    amount = dataModel.freeParkingPriceTypeArrayList.get(2).getPrice();
                }

            }else {
                amount = dataModel.freeParkingPriceTypeArrayList.get(1).getPrice();
            }

        }else {
            amount = dataModel.freeParkingPriceTypeArrayList.get(0).getPrice();
        }

        return amount;
    }

    private void resetallfield() {

        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            et_checkinTime.setText("");
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
        }
        et_owner_ph_no.setText("");
       // radioGroup_vehicletype.clearCheck();
        spinner_Vehicle_Type.setSelection(iVehicleType);
        if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
            scanResults.setText(getResources().getString(R.string.statecode_ts));
        } else {
            scanResults.setText(getResources().getString(R.string.statecode));
        }

        scanResults.setSelection(scanResults.getText().toString().length());
    }
    private boolean validate() {

        boolean result = true;


        if (!SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            if (et_owner_ph_no.getText().toString().equals("")) {
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.phone_vnumber));
                result = false;
                return result;
            }
        }


        if (!SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")) {

            if (et_owner_ph_no.getText().toString().length() != 10) {
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.phone_vnumber));
                result = false;
                return result;
            }
        }
        if(iVehicleType == 0){
            ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.vehicle_type_vld));
            result = false;
            return result;
        }

//        if ( radiocheck[0].equals("") ){
//
//            ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,getResources().getString(R.string.vehicle_type));
//            result = false;
//            return result;
//        }

        if (/*scanResults.getText().toString().equals("") ||*/ scanResults.getText().toString().length()<6){
            ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,getResources().getString(R.string.vehicle_num_vld));
            result = false;
            return result;
        }
        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            if(check_pass.isChecked()){
                if(strStoreid.equals("0")){
                    ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this,getResources().getString(R.string.validate_storepass));
                    result = false;
                    return result;
                }
            }
        }

        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            if (et_bookingid.getText().toString().equals("")) {
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.booking_validid));
                result = false;
                return result;
            }

            if (et_bookingid.getText().toString().trim().length() < 15){
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.booking_validid));
                result = false;
                return result;
            }

            if (et_checkinDate.getText().toString().equals("")) {
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.checking_vdate));
                result = false;
                return result;
            }
            if (et_checkinTime.getText().toString().equals("")) {
                ShowAlertDialog.showAlertDialog(OfflineCheckInActivity.this, getResources().getString(R.string.checking_vtime));
                result = false;
                return result;
            }
        }
        return result;
    }

    public static boolean isValid(String str){
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

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(OfflineCheckInActivity.this, R.style.CustomWaitDialog);
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

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[3];
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
        String curTimeSec = String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;
        return dateTime;
    }


    @Override
    public void onBackPressed() {
        dataModel.details_shown="1";
        dataModel.about_advanced_dash = 0;

        if(scan_linear.getVisibility() == View.VISIBLE){
            try {

               // mScannerView.stopCameraPreview();
                //mScannerView.stopCamera();
                scan_linear.setVisibility(View.GONE);
                scan_linear.setAnimation(bottomout);
                lay_scan.removeAllViews();

            }catch (Exception e){
                e.printStackTrace();
            }


        }else {
            try {
                closeBT();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(OfflineCheckInActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }
    }

    private void closeBT() throws IOException {
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

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);

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

    private boolean isValidMobile(String phone) {
        String expression = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }

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
                    //checkintim = et_checkinTime.getText().toString().trim();
                    checkintim += ":";

                    Log.e("checkintim3", checkintim);

                    et_checkinTime.setText(checkintim);
                    et_checkinTime.setSelection(et_checkinTime.getText().toString().length());
                }else if (charSequence.length()== 1){

                    Log.e("checkintim4", checkintim);

                    // checkintim = et_checkinTime.getText().toString().trim();
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
    // for verifone printer
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        if (resultCode == 2) {
//
//            String returnedResult = intent.getData().toString().trim().replaceAll(" ", "");
//            scanResults.setText(returnedResult);
//        }
        if (requestCode == REQUEST_CODE_PRINT_BITMAP) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response", String.valueOf(response));
                        try {
                            resetallfield();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                OfflineCheckInActivity.this).create();

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
                                    printEazytapBillNew(vehicleCheckInBean.getVehicle_number());
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
                                OfflineCheckInActivity.this).create();

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
                                    printEazytapBillNew(vehicleCheckInBean.getVehicle_number());

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



}
