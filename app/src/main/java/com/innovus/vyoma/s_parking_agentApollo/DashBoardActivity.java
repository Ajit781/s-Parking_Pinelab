package com.innovus.vyoma.s_parking_agentApollo;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.eze.api.EzeAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.OfflineVechileListAdapter;
import adapter.PassStoreListAdapter;
import adapter.VechileListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.BookingBillBean;
import data_objects.bean.DataObject;
import data_objects.bean.GateOpenBoomBarrierBean;
import data_objects.bean.SpclPassStoreBean;
import data_objects.bean.VehicleCheckInBean;
import data_objects.bean.VehicleType;
import db.DatabaseHandler;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ImageFile.MarshMallowPermission;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.SessionManager;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;
import utilities.listnerofRecyclerView.CustomItemClickListener;
import utilities.listnerofRecyclerView.OfflineCheckInListRecyclerItemTouchHelper;
import utilities.listnerofRecyclerView.RecyclerItemTouchHelper;
import utilities.others.CToast;
import utilities.others.ConnectionStatus;
import utilities.printer_utils.Utils;
import utilities.retrofit.NetworkStateChecker;


public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextWatcher, AsyncResponse, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,OfflineCheckInListRecyclerItemTouchHelper.offlineCheckInListRecyclerItemTouchHelper/*, SwipeListener*/ {
    private static final Logger log = LoggerFactory.getLogger(DashBoardActivity.class);
    private Context mContext;
    String strUserName,parking_slot,parkinglocation;
    Integer iID;
    private RecyclerView rv_vehicle_list,rv_vehicle_list_offline;
    private ImageView noitem_in_cart,iv_fourWheeler,iv_twoWheeler;
    private RelativeLayout no_result;
    private LinearLayout ll_vehicle_count;
    private ImageView iv_printer,iv_mode;
    private CoordinatorLayout content_dashoard;
    private EditText input_search;
    OfflineVechileListAdapter offlineVechileListAdapter;
    private CheckBox check_pass;
    private LinearLayout ll_check_pass,ll_pass_main;
    private Spinner sp_store;
    private TextView headerUserName,headerUserPass,header_parking_slot,header_parkinglocation,
            tv_message, tv_two_count,tv_four_count,tv_notifications, it_notification_txt;
    private FloatingActionButton fab_parkingStart,fab_parkingEnd, fab_parking_gate_open;
    String vechile_name="";
    String vehicle_number="";
    int isServerError =0;
    Integer iVehicleType = 0;
    BookingBillBean bookingBillBean;
    private String checkintime = "";
    private String checkouttime = "";
    String payment_mode ="";
    private String parkingfee = "";
    Integer parkingrate = 0;
    public static final int NOT_FOUND = -1;
    private VehicleCheckInBean vehicleCheckInBean_deletedItem= new VehicleCheckInBean();

    private String pending_vehicleNumber = "";
    private String bookingNumber = "";
    private String vehicleNo = "";
    private boolean normal_listfetch=true;
    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();
    VechileListAdapter vechileListAdapter;
    List<DataObject> filteredModelList=new ArrayList<DataObject>();
    boolean presstwice = false;
    private SessionManager session;
    RemoteAsync remoteAsync;
    private boolean ischirp_going_on = true;
    private  int deletedIndex=0;
    private DataObject deletedItem= new DataObject();
    private SpotsDialog progressDialog;
    private AudioManager audiocheck;
    int countfour=0;
    int countTwo =0;
    int channelId =0;
    int counthit =0;
    int countcheck =0;
    String  mode ="";
    private String strStoreid = "0";
    private String passapplied = "0";
    private String ivehicletype = "0";
    private String version_name = "";
    /*private BarcodeDetector detector;*/
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    NavigationView navigationView;

    DataOutputStream mmOutputStream_forboom;
    // needed for communication to bluetooth device / Network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;
    int readBufferPosition;
    boolean checkperm = true;
    DatabaseHandler databaseHandler;
    volatile boolean stopWorker;
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;
    private static final int RESULT_REQUEST_CAMERA = 2;
    ArrayList<String >vehicleList = new ArrayList<String>();
    private BroadcastReceiver broadcastReceiver;
    PassStoreListAdapter passStoreListAdapter;
    private ConstraintLayout listlay;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private ProgressDialog pDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    Button btn_find;

    private String blockCharacterSet = "~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹~";
    private ExecutorService mSingleThreadExecutor;
    private final int REQUEST_CODE_INITIALIZE = 10001;// pax initialization code
    private FloatView floatView;

    public static Messenger mService;
    public static boolean isBound = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

         if(!SharedStorage.getValue(getApplicationContext(),"baseUrl").equals("")){
            dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");
        }
        databaseHandler = new DatabaseHandler(getApplicationContext());
//         try{
//             databaseHandler.getofflinevehiclecheckin();
//             Log.e("Offline Vehicle List", String.valueOf(dataModel.offlinevehicleCheckInBeanArrayList.size()));
//         }
//         catch(Exception e){
//             Log.e("Exception",e.toString());
//         }

       // Bundle bundle = getIntent().getExtras();
        strUserName = SharedStorage.getValue(getApplicationContext(),"Userame");
        parking_slot = SharedStorage.getValue(getApplicationContext(),"parkingslot");
        parkinglocation = SharedStorage.getValue(getApplicationContext(),"parkinglocation");
        iID = Integer.valueOf(SharedStorage.getValue(getApplicationContext(),"UserId"));

        SharedStorage.setValue(DashBoardActivity.this, "agent_mode", "1");// online Mode

        SharedStorage.setValue(DashBoardActivity.this, "printer_name", "eazy_Tap");
       // SharedStorage.setValue(DashBoardActivity.this,"printer_name","verifone");


        // for  drawer open and close
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        it_notification_txt =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.it_notification));

        View header = navigationView.getHeaderView(0);
        headerUserName = (TextView) header.findViewById(R.id.header_UserName);
        headerUserName = (TextView) header.findViewById(R.id.header_UserName);
        headerUserPass = (TextView) header.findViewById(R.id.header_UserPass);
        header_parking_slot = (TextView) header.findViewById(R.id.header_parking_slot);
        iv_mode = (ImageView) header.findViewById(R.id.iv_mode);
        header_parkinglocation = (TextView) header.findViewById(R.id.header_parkinglocation);

        Log.e("header items--->",iID.toString()+strUserName+parking_slot+parkinglocation);

        //Network check
        if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){

            iv_mode.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));

        }else {

            iv_mode.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_notification_overlay));
        }


        headerUserName.setText(strUserName);
        headerUserPass.setText(iID.toString());
        header_parking_slot.setText(parking_slot);
        header_parkinglocation.setText(parkinglocation);

        //This class gives you control of the power state of the device.
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //Wake Lock gives you control over the Android Power- and WifiManager.
        // For example, you can force the PowerManager to keep the screen on or have the CPU ...
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();



        session = new SessionManager(getApplicationContext());
            if (!session.isLoggedIn()) {
            logout();
        }

        //the broadcast receiver to update sync status
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                //loading the names again
//
//
//            }
//        };
        // Register a BroadcastReceiver to be run in the main activity thread.
      //   registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // PAX api initliazation
//        initalizePaxAPI();
        bindPineLabPos();
        initView();

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
                                // App version checking
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

        if (!SharedStorage.getValue(DashBoardActivity.this,"printer_name").equals("")){

            floatView = FloatView.getInstance(DashBoardActivity.this);

        }


    }
    private void bindPineLabPos(){

        Intent intent = new Intent();
        intent.setAction("com.pinelabs.masterapp.SERVER");
        intent.setPackage("com.pinelabs.masterapp");
        bindService(intent, connection, BIND_AUTO_CREATE);

    }

    // PAX api initliazation
    private void initalizePaxAPI() {
        // Live Key

        JSONObject jsonRequest = new JSONObject();
        try {
//            jsonRequest.put("demoAppKey","25e95651-c5aa-4735-903d-c2133d661f7a");
//            jsonRequest.put("prodAppKey","25e95651-c5aa-4735-903d-c2133d661f7a");

            jsonRequest.put("demoAppKey","03ee6ba9-0e26-4cca-9c2d-95bb8358220e");
            jsonRequest.put("prodAppKey","03ee6ba9-0e26-4cca-9c2d-95bb8358220e");

            jsonRequest.put("merchantName","APOLLO_MULTISPECIALITY_HO");
            //jsonRequest.put("userName","1510202410");
            jsonRequest.put("userName","2119592700");
            jsonRequest.put("currencyCode","INR");
            jsonRequest.put("appMode","PROD");
            jsonRequest.put("captureSignature","false");
            jsonRequest.put("prepareDevice","false");

            Log.e("request_init",jsonRequest.toString());
            //          EzeApi initialization
            EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE, jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        JSONObject jsonRequest = new JSONObject();
//        try {
//             jsonRequest.put("demoAppKey","a8b2bcdd-0a39-497e-9a41-73d68a77ffa7");
////
//            //jsonRequest.put("prodAppKey","F6E5395E-E403-4925-A823-E63FF494DBDA");
//            jsonRequest.put("prodAppKey","a8b2bcdd-0a39-497e-9a41-73d68a77ffa7");
////                jsonRequest.put("prodAppKey","");
//            jsonRequest.put("merchantName","VYOMA_INNOVUS_GLOBEL_PVT");
//            //jsonRequest.put("userName",SharedStorage.getValue(getApplicationContext(),"Userame"));
//            jsonRequest.put("userName","9836700645");
//            jsonRequest.put("currencyCode","INR");
//            jsonRequest.put("appMode","PROD");
//            jsonRequest.put("captureSignature","false");
//            jsonRequest.put("prepareDevice","false");
//
//            Log.e("request_init",jsonRequest.toString());
//            //          EzeApi initialization
//            EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE, jsonRequest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    // App version checking
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            remoteAsync.execute(urlParams);
        }
        Log.e("params>>",urlParams);
    }
    // set  Notification count
    public void initializeCountDrawer(String count) {

        it_notification_txt.setGravity(Gravity.CENTER_VERTICAL);
        it_notification_txt.setTypeface(null, Typeface.BOLD);
        it_notification_txt.setTextColor(getResources().getColor(R.color.colorAccent));
        it_notification_txt.setText(count);
        tv_notifications.setText(count);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        }
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initView(){
        rv_vehicle_list = (RecyclerView) findViewById(R.id.rv_vehicle_list);
        rv_vehicle_list_offline = (RecyclerView) findViewById(R.id.rv_vehicle_list_offline);
        content_dashoard = (CoordinatorLayout) findViewById(R.id.content_dashoard);
        listlay = (ConstraintLayout) findViewById(R.id.listlay);
        input_search=(EditText)findViewById(R.id.input_search);
        btn_find = (Button)findViewById(R.id.btn_find);
        noitem_in_cart= (ImageView) findViewById(R.id.noitem_in_cart);
        no_result= (RelativeLayout) findViewById(R.id.no_result);
        tv_message= (TextView) findViewById(R.id.tv_message);
        tv_four_count= (TextView)findViewById(R.id.tv_four_count);
        tv_two_count= (TextView) findViewById(R.id.tv_two_count);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);
        ll_vehicle_count = (LinearLayout) findViewById(R.id.ll_vehicle_count);

       // input_search.setVisibility(View.GONE);

       //input_search.addTextChangedListener(DashBoardActivity.this);//
       //input_search.setFilters(new InputFilter[] { filter });
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
                    if (!input_search.getText().toString().equals("")){
                        getAllCheckedInList(String.valueOf(iID), input_search.getText().toString());

                    }else{
                        input_search.requestFocus();
                        input_search.setError("Please enter vehicle number");
                    }

                }else{
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.NoInternetConnection));

                }
            }
        });
        listlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(DashBoardActivity.this);
            }
        });

        //to hide item from navigation menu
        if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            showItem();
        }else{
            hideItem();
        }
        
        fab_parkingStart = (FloatingActionButton) findViewById(R.id.fab_parkingstart);
        fab_parkingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){

                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        try {
                            closeBT();// this will close bluetooth device connection
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dataModel.about_advanced_dash = 1;
                        Intent i = new Intent(DashBoardActivity.this, OfflineCheckInActivity.class);
                        i.putExtra("myName",strUserName);
                        i.putExtra("myID",iID);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();

                    }else {
                        try {
                            closeBT();// this will close bluetooth device connection
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dataModel.about_advanced_dash = 1;
                        Intent i = new Intent(DashBoardActivity.this, VehicleInfoScanActivity.class);
                        i.putExtra("myName",strUserName);
                        i.putExtra("myID",iID);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    }
                }else {

                    try {
                        closeBT();// this will close bluetooth device connection
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), OfflineCheckInActivity.class);
                    i.putExtra("myName",strUserName);
                    i.putExtra("myID",iID);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                }
            }
        });

        fab_parkingEnd = (FloatingActionButton) findViewById(R.id.fab_parkingend);
        fab_parkingEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    closeBT();// this will close bluetooth device connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dataModel.about_advanced_dash = 1;
                dataModel.vehicleCheckInBean = null;
                if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){
                    Intent i = new Intent(getApplicationContext(), EndParkingActivity.class);
                    i.putExtra("myName",strUserName);
                    i.putExtra("myID",iID);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                }else{
                    Intent i = new Intent(getApplicationContext(), OfflineCheckOutActivity.class);
                    i.putExtra("myName",strUserName);
                    i.putExtra("myID",iID);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                }

            }
        });

        fab_parking_gate_open = (FloatingActionButton) findViewById(R.id.fab_parking_gate_open);
        fab_parking_gate_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //if online call service for checked in list
        if(SharedStorage.getValue(this,"agent_mode").equals("1")){
            if(dataModel.url.equals("")){
                dataModel.url = SharedStorage.getValue(getApplicationContext(),"baseUrl");
            }
            // this method is for getting All Checked in vehicle
            ll_vehicle_count.setVisibility(View.VISIBLE);
            input_search.setVisibility(View.VISIBLE);
            try{
                if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
//                  if (dataModel.dataObjectArrayList.size()>0){
//                      getparkrecyclerview("");
//                  }

                   //getAllCheckedInList(String.valueOf(iID));//service to get checkin list
                      VehicleTypeList();// for vehicle type
                 }else{
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                   // btnyes.setText(getResources().getString(R.string.retry));
                    btnno.setText(getResources().getString(R.string.cancel));
                    btnno.setVisibility(View.GONE);

                    heading.setText(R.string.validation_name);

                    msg_txt.setText(getResources().getString(R.string.no_internet));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            //getOflineVehicleCheckedInList();
                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
                            //    getAllCheckedInList(String.valueOf(iID));//service to get checkin list
                                VehicleTypeList();// for vehicle type
                            }else{
                                 ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.NoInternetConnection));
                                //getOflineVehicleCheckedInList();
                            }
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    alertDialog.show();
                    alertDialog.setCancelable(false);
            }
            }catch(Exception e){
                e.printStackTrace();
            }


        }
//        else {
//            getOflineVehicleCheckedInList();
////            no_result.setVisibility(View.VISIBLE);
//            input_search.setVisibility(View.VISIBLE);
////            //noitem_in_cart.setVisibility(View.GONE);
////            ll_vehicle_count.setVisibility(View.GONE);
////            noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
////            tv_message.setText(getResources().getString(R.string.youareoffline));
//
//        }


    }




    private void VehicleTypeList() {
      //  start_progress_dialog();
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
//        CToast.show(this,login_url+"-------params----->"+urlParams);
    }



    public class clientSock extends Thread {
        public void run () {
            try {
                //CToast.show(getApplicationContext(),"insideoutputstrem to send data");
                mmOutputStream_forboom.writeBytes(getResources().getString(R.string.commandtofire)); // anything you want
                mmOutputStream_forboom.flush();

                //closeBT_forBoom();

            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }


    //hide items from navigation drawer
    private void hideItem() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.e("packageInfo", String.valueOf(packageInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version_code = packageInfo.versionCode;
        final String version_name = packageInfo.versionName;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.it_about).setTitle("v-"+version_name);
       // nav_Menu.findItem(R.id.it_railway_parking).setVisible(false);
      //  nav_Menu.findItem(R.id.it_terms_condition).setVisible(true);
      //  nav_Menu.findItem(R.id.it_privacy).setVisible(true);
    }

    //show item in navigaiton drawer
    private void showItem() {

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.e("packageInfo", String.valueOf(packageInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version_code = packageInfo.versionCode;
        final String version_name = packageInfo.versionName;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.it_about).setTitle("v-"+version_name);

       // nav_Menu.findItem(R.id.it_railway_parking).setVisible(true);
       // nav_Menu.findItem(R.id.it_terms_condition).setVisible(false);
      //  nav_Menu.findItem(R.id.it_privacy).setVisible(false);

    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(DashBoardActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }catch (Exception e){
            e.printStackTrace();
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

    void stop_progress_dialog() {
        if(progressDialog!=null){
            try{
                progressDialog.dismiss();
                progressDialog=null;
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }
    // this method is for getting Special Pass list
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

    // this will get All checked in vehicle list
    private void getAllCheckedInList(String userid,String vehicle_number) {
        if(normal_listfetch){
            start_progress_dialog();
        }
        Urls Urls = new Urls();

      // String login_url = Urls.GetAllCheckedInList;
       String login_url = Urls.GetAllCheckedInListV1;


        remoteAsync = new RemoteAsync(login_url);
       // remoteAsync.type = RemoteAsync.GETALLCHECKEDLIST;
        remoteAsync.type = RemoteAsync.GETALLCHECKEDLISTV1;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams=  "AgentID=" + URLEncoder.encode(userid, "UTF-8")+
                        "&VehicleNo=" + URLEncoder.encode(vehicle_number, "UTF-8");
      //  urlParams=  "AgentID=" + URLEncoder.encode(userid, "UTF-8");


        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    // this method is used for vehicle check out
    private void endParking(String vehicle_number,String userid) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String start_parking_url = Urls.GetCheckoutDetailsV20;
        // String start_parking_url = Urls.SetEndParking + "/" + bookingid;

        if(dataModel.dataObjectArrayList.size()>0){
            for (int i=0;i<dataModel.dataObjectArrayList.size();i++){
                if(dataModel.dataObjectArrayList.get(i).getM_strVehicleNo().equals(vehicle_number)){
                    dataModel.dataObjectArrayList.remove(i);
                }
            }
        }

        dataModel.about_advanced_dash = 1;
        pending_vehicleNumber = vehicle_number;

        remoteAsync = new RemoteAsync(start_parking_url);
       // remoteAsync.type = RemoteAsync.VEHICLECHECKOUT;
        remoteAsync.type = RemoteAsync.VEHICLECHECKOUTDETAILSV20;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "VehicleNumber=" + URLEncoder.encode(vehicle_number, "UTF-8") +
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&AgentID=" + URLEncoder.encode(userid, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    @Override
    public void processFinish(String type, String output) {
       // if (type.equals(RemoteAsync.GETALLCHECKEDLIST)) {
        if (type.equals(RemoteAsync.GETALLCHECKEDLISTV1)) {
            stop_progress_dialog();
            if(normal_listfetch){
                stop_progress_dialog();
            }
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());
                ischirp_going_on = true;

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.about_advanced_dash = 0;

                    JSONArray checkinJsonArrayList = obj.getJSONArray("CheckinList");
                    ArrayList<DataObject> dataObjectArrayList = new ArrayList<DataObject>();
                    if (checkinJsonArrayList.length() > 0) {
                        countfour=0;// fourWheeler Count
                        countTwo=0;// TwoWheeler Count

                        for (int i = 0; i < checkinJsonArrayList.length(); i++) {
                            JSONObject object = checkinJsonArrayList.getJSONObject(i);
                            DataObject dataObject = new DataObject();

                            dataObject.setM_iBookingID(Integer.parseInt(object.getString("booking_id")));
                            dataObject.setParking_area_id(object.getString("parking_area_id"));
                            dataObject.setSlot_id(object.getString("slot_id"));
                            dataObject.setSlot_name(object.getString("slot_name"));
                            dataObject.setPayment_mode_id(object.getString("payment_mode_id"));
                            dataObject.setPayment_mode(object.getString("payment_mode"));
                            dataObject.setVehicle_id(object.getString("vehicle_id"));
                            dataObject.setM_strUserName(object.getString("owner_name"));
                            dataObject.setM_iUID(Integer.parseInt(object.getString("owner_id")));
                            dataObject.setM_strOwnerPhone(object.getString("owner_contact_no"));
                            dataObject.setM_strAlterPhone(object.getString("alternate_mobile_no"));
                            Log.e("alternate_mobile_no",object.getString("alternate_mobile_no"));
                            dataObject.setM_strCheckInTime(object.getString("checkin_time"));
                            dataObject.setM_strVehicleNo(object.getString("vehicle_number"));
                            dataObject.setM_strVehicleType(object.getString("vehicle_type"));
                            if (object.getString("vehicle_type").equals("Four Wheeler")){
                                countfour++;
                                Log.e("countfour", String.valueOf(countfour));
                            }else if (object.getString("vehicle_type").equals("Two Wheeler")){
                                countTwo++;
                                Log.e("countTwo", String.valueOf(countTwo));
                            }
                            dataObject.setVehicle_type_icon(object.getString("vehicle_type_icon"));
                            dataObject.setM_strOwnerName(object.getString("owner_name"));
                            dataObject.setBooking_no(object.getString("booking_no"));

                            dataObject.setViewtype(1);

                            dataObjectArrayList.add(dataObject);

                        }

                        //tv_four_count.setText(String.valueOf(countfour));
                        tv_two_count.setText(String.valueOf(countTwo));
                        dataModel.dataObjectArrayList.removeAll(dataModel.dataObjectArrayList);
                        Collections.reverse(dataObjectArrayList);// this will show the newly checked in vehicle first
                        dataModel.dataObjectArrayList.addAll(dataObjectArrayList);
                        tv_four_count.setText(String.valueOf(dataModel.dataObjectArrayList.size()));
                        getparkrecyclerview("");//show  vehicle to into recycler view
                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){

                    if(dataModel.check_in_remove ==2){
                        dataModel.check_in_remove = 0;
                        dataModel.dataObjectArrayList.remove(dataModel.vehicleCheckInBean);
                        dataModel.vehicleCheckInBean = null;
                        getparkrecyclerview("");//show  vehicle to into recycler view
                    }

                    getAllCheckedInList(String.valueOf(iID),input_search.getText().toString());//service to get checkin list

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                        getAllCheckedInList(String.valueOf(iID),input_search.getText().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    dataModel.dataObjectArrayList.removeAll(dataModel.dataObjectArrayList);
                    getparkrecyclerview(msg.getString("message"));// this method is for vehicle list showin recyclerview
                    if (dataModel.dataObjectArrayList.size()==0){
                        countfour=0;// set Four Wheeler count 0 if dataModel.dataObjectArrayList.size is zero.
                        countTwo=0;//set Two Wheeler count 0 if dataModel.dataObjectArrayList.size is zero
                       // tv_four_count.setText(String.valueOf(countfour));
                        tv_four_count.setText(String.valueOf(dataModel.dataObjectArrayList.size()));
                        tv_two_count.setText(String.valueOf(countTwo));
                    }
                    //ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));
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
                        passStoreListAdapter  = new PassStoreListAdapter(DashBoardActivity.this, dataModel.spclPassStoreBeanArrayList) {
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
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                    btnyes.setText(getResources().getString(R.string.retry));
                    btnno.setText(getResources().getString(R.string.cancel));

                    heading.setText(R.string.validation_name);


                    heading.setText(R.string.validation_name);
                    msg_txt.setText(getResources().getString(R.string.nonetavailable));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            SpecialPassList();
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                        SpecialPassList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else  if (type.equals(RemoteAsync.VEHICLETYPELIST)) {
            //stop_progress_dialog();
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
                            if(!(object.getString("vehicle_type_id").equals("3") || (object.getString("vehicle_type_id").equals("5"))))
                            {
                                vehicleType.setVehicleTypeId(object.getString("vehicle_type_id"));
                                vehicleType.setVehicleTypeName(object.getString("vehicle_type_name"));

                                vehicletypeArrayList.add(vehicleType);
                            }

                         }
                        Log.e("vehicleTypeList",vehicletypeArrayList.toString());
                        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
                        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);

                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)) {

                    VehicleTypeList();// for vehicle type

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
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (type.equals(RemoteAsync.VEHICLECHECKINBYVEHICLEID)) {
            stop_progress_dialog();

            try {
                Log.e("Response-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.details_shown="1";
                    isServerError = 0;
                    bookingNumber =obj.getString("BookingNumber");
                    vehicleNo =obj.getString("VehicleNumber");
                    ivehicletype =obj.getString("VehicleTypeID");

                    //pos- sunmi printer
                    //pos_new- udayma printer

                    if (SharedStorage.getValue(DashBoardActivity.this,"printer_name").equals("")) {

                         ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                    }
                    else if (SharedStorage.getValue(DashBoardActivity.this,"printer_name").equals("eazy_Tap"))
                    {
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
                                    printEazytapBill();
                                } catch (Exception e) {

                                    e.printStackTrace();
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
                        alertDialog.setCancelable(false);

                    }

                  else {
                        // this alert dialog  to select print reciept or not
                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                    this).create();

                            final LayoutInflater inflater = this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                            alertDialog.setView(dialogView);
                            // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
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
                                       //kk
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                            alertDialog.setCancelable(false);
                        }




                }
                else if(obj.getString("status").equals(Constants.REG)){
                    //this dialog  will show if vehicle is not registered
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    // Setting Dialog Title

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                    msg_txt.setText(obj.getString("message"));
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent=new Intent(DashBoardActivity.this, VehicleOwnerRegistrationActivity.class);
                            intent.putExtra("myID",iID);
                            intent.putExtra("myName","");
                            intent.putExtra("myVehicleNumber",vehicle_number);
                            intent.putExtra("alt_mobile","");
                            intent.putExtra("strStoreid","0");
                            intent.putExtra("passapplied","0");
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
                else if (obj.getString("status").equals(Constants.INSUF)){
                    // this dialog will show if vehicle owner has insufficient balance
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
                                // parking will start  with payment mode  cash
                                startparkinng(vehicle_number,"1","");

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
                    //this will show if owner has  any pass
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this,msg.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    //this will show if vehicle check in gets not success with status 2
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.ok));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                     /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            isServerError = 1;
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
                else if (obj.getString("status").equals(Constants.REPEAT_CHECKIN)){
                    if (isServerError==1){

                        dataModel.details_shown="1";
                        isServerError = 0;


                        bookingNumber =obj.getString("BookingNumber");

                        if (SharedStorage.getValue(DashBoardActivity.this,"printer_name").equals("")) {
                            ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                        }
                        else if (SharedStorage.getValue(DashBoardActivity.this,"printer_name").equals("eazy_Tap"))
                        {
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
                                        printEazytapBill();
                                    } catch (Exception e) {

                                        e.printStackTrace();
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
                            alertDialog.setCancelable(false);

                        }
                        else {
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
                                        //=
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                            alertDialog.setCancelable(false);

                        }
                    }else {
                        JSONObject msg = new JSONObject(output);
                        ShowAlertDialog.showAlertDialog(DashBoardActivity.this,msg.getString("message"));
                    }


                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                        endParking(vehicle_number, String.valueOf(iID));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else{
                    isServerError=0;
                    JSONObject msg = new JSONObject(output);

                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this,msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       // else if (type.equals(RemoteAsync.VEHICLECHECKOUT)) {
        else if (type.equals(RemoteAsync.VEHICLECHECKOUTDETAILSV20)) {
            stop_progress_dialog();
            try {
                Log.e("output-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    strStoreid = "0";
                    passapplied = "0";
                    dataModel.vehicle_no="";
                    try {
                        closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String bookingno = obj.getString("BookingNumber");
                    String BookingID = obj.getString("BookingID");
                   // String bookingstatus = obj.getString("bookingstatus");
                    String checkintime = obj.getString("CheckinTime");
                    String checkouttime = obj.getString("CheckoutTime");
                   // String ownername = obj.getString("ownername");
                    String ownerphoneno = obj.getString("VehicleOwnerContactNumber");
                    //String totalamount = obj.getString("totalamount");
                    String VehicleType = obj.getString("VehicleType");
                    if (VehicleType.equals("Two Wheeler")){
                        countTwo--;
                    }else if (VehicleType.equals("Four Wheeler")){
                        countfour--;
                    }
                    String vechile_no = obj.getString("VehicleNumber");
                    String ParkingAreaName = obj.getString("ParkingAreaName");
                    String TotalDuration = obj.getString("TotalDuration");
                    String TotalParkingAmount = obj.getString("TotalParkingAmount");
                    String TotalPaybleAmount = obj.getString("TotalPaybleAmount");
                    String FineAmount = obj.getString("FineAmount");
                    String OfferAmount = obj.getString("OfferAmount");
                    String PaymentMode = obj.getString("PaymentMode");
                    String AgencyName = obj.getString("AgencyName");
                    String message = obj.getString("message");
                    String vehicle_type_icon = obj.getString("vehicle_type_icon");
                    Log.e("vehicle_type_icon",vehicle_type_icon);
//                    String overtime_duration = obj.getString("OverTimeDuration");
//                    String overtime_amount = obj.getString("OverTimeAmount");
                    String overtime_duration = "0";
                    String overtime_amount = "0";

                    SharedStorage.setValue(getApplicationContext(),"BookingID", obj.getString("BookingID"));

                   // SharedStorage.setValue(getApplicationContext(),"billstatus", "1");

                    BookingBillBean bookingBillBean = new BookingBillBean(bookingno,BookingID,checkintime,checkouttime,ownerphoneno,VehicleType,
                            vechile_no,ParkingAreaName,TotalDuration,TotalParkingAmount,TotalPaybleAmount,FineAmount,OfferAmount,PaymentMode,
                            AgencyName,overtime_duration,overtime_amount,message,"0");
                    databaseHandler.addbookingbill(bookingBillBean);

                    Intent intent = new Intent(DashBoardActivity.this,BillGenerateActivity.class);
                    intent.putExtra("bookingno",bookingno);
                    intent.putExtra("BookingID",BookingID);
                    intent.putExtra("checkintime",checkintime);
                    intent.putExtra("checkouttime",checkouttime);
                    intent.putExtra("ownerphoneno",ownerphoneno);
                    //intent.putExtra("totalamount",totalamount);
                    intent.putExtra("ParkingAreaName",ParkingAreaName);
                    intent.putExtra("TotalDuration",TotalDuration);
                    intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                    intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                    intent.putExtra("OfferAmount",OfferAmount);
                    intent.putExtra("FineAmount",FineAmount);
                    intent.putExtra("PaymentMode",PaymentMode);
                    intent.putExtra("VehicleType",VehicleType);
                    //intent.putExtra("bookingstatus",bookingstatus);
                    intent.putExtra("vechile_no",vechile_no);
                    intent.putExtra("AgencyName",AgencyName);
                    intent.putExtra("message",message);
                    intent.putExtra("vehicle_type_icon",vehicle_type_icon);
                    intent.putExtra("overtime_duration",overtime_duration);
                    intent.putExtra("overtime_amount",overtime_amount);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                }
                else if (obj.getString("status").equals(Constants.PASS)){
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    strStoreid = "0";
                    passapplied = "0";
                    dataModel.about_advanced_dash = 0;

                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    dataModel.about_advanced_dash = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                    btnyes.setText(getResources().getString(R.string.retry));
                    btnno.setText(getResources().getString(R.string.cancel));

                    heading.setText(R.string.validation_name);


                    heading.setText(R.string.validation_name);
                    msg_txt.setText(getResources().getString(R.string.no_internet));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            isServerError = 1;
                            vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                            dataModel.vehicleCheckInBean = deletedItem;
                            //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

                            dataModel.about_advanced_dash = 1;
                            dataModel.check_in_remove = 1;

                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            // if network is not available then this api will call after tapping on retry button
                            endParking( dataModel.vehicle_no, String.valueOf(iID));
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    alertDialog.show();
                    alertDialog.setCancelable(false);


                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                        //get all parking details by parking id
                       // endParking(pending_vehicleNumber, String.valueOf(iID));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    dataModel.about_advanced_dash = 0;
                    isServerError = 0;
                    strStoreid = "0";
                    passapplied = "0";
                    pending_vehicleNumber = "";
                    vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                   /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            normal_listfetch = true;
                            getAllCheckedInList(String.valueOf(iID),input_search.getText().toString());//service to get checkin list
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
                vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                dataModel.about_advanced_dash = 0;
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
                    ShowAlertDialog.showAlertDialogFailure(DashBoardActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (type.equals(RemoteAsync.PENDINGBILLGENERATE)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    isServerError = 0;
                    pending_vehicleNumber = "";

                    String bookingno = obj.getString("BookingNumber");
                    String BookingID = obj.getString("BookingID");
                    // String bookingstatus = obj.getString("bookingstatus");
                    String checkintime = obj.getString("CheckinTime");
                    String checkouttime = obj.getString("CheckoutTime");
                    // String ownername = obj.getString("ownername");
                    String ownerphoneno = obj.getString("VehicleOwnerContactNumber");
                    //String totalamount = obj.getString("totalamount");
                    String VehicleType = obj.getString("VehicleType");
                    String vechile_no = obj.getString("VehicleNumber");
                    String ParkingAreaName = obj.getString("ParkingAreaName");
                    String TotalDuration = obj.getString("TotalDuration");
                    String TotalParkingAmount = obj.getString("TotalParkingAmount");
                    String TotalPaybleAmount = obj.getString("TotalPaybleAmount");
                    String FineAmount = obj.getString("FineAmount");
                    String OfferAmount = obj.getString("OfferAmount");
                    String PaymentMode = obj.getString("PaymentMode");
                    String AgencyName = obj.getString("AgencyName");
                    String vehicle_type_icon  = obj.getString("vehicle_type_icon");

                    String message = obj.getString("message");

                    SharedStorage.setValue(getApplicationContext(),"BookingID", obj.getString("BookingID"));

                    SharedStorage.setValue(getApplicationContext(),"billstatus", "1");

                    BookingBillBean bookingBillBean = new BookingBillBean(bookingno,BookingID,checkintime,checkouttime,ownerphoneno,VehicleType,
                            vechile_no,ParkingAreaName,TotalDuration,TotalParkingAmount,TotalPaybleAmount,FineAmount,OfferAmount,PaymentMode,
                            AgencyName,"0","0",message,"0");

                    databaseHandler.addbookingbill(bookingBillBean);

                    Intent intent = new Intent(DashBoardActivity.this,BillGenerateActivity.class);
                    intent.putExtra("bookingno",bookingno);
                    intent.putExtra("BookingID",BookingID);
                    intent.putExtra("checkintime",checkintime);
                    intent.putExtra("checkouttime",checkouttime);
                    intent.putExtra("ownerphoneno",ownerphoneno);
                    //intent.putExtra("totalamount",totalamount);
                    intent.putExtra("ParkingAreaName",ParkingAreaName);
                    intent.putExtra("TotalDuration",TotalDuration);
                    intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                    intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                    intent.putExtra("OfferAmount",OfferAmount);
                    intent.putExtra("FineAmount",FineAmount);
                    intent.putExtra("PaymentMode",PaymentMode);
                    intent.putExtra("VehicleType",VehicleType);
                    //intent.putExtra("bookingstatus",bookingstatus);
                    intent.putExtra("vechile_no",vechile_no);
                    intent.putExtra("AgencyName",AgencyName);
                    intent.putExtra("message",message);
                    intent.putExtra("vehicle_type_icon",vehicle_type_icon);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

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

                            endParking(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

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
                        //get all parking details by parking id
                        //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

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
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));

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

    private void PendingBillGenerate(String vehicle_number,String userid) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String start_parking_url = Urls.PendingBillGenerate;

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.PENDINGBILLGENERATE;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "VehicleNumber=" + URLEncoder.encode(vehicle_number, "UTF-8") +
                    "&AgentID=" + URLEncoder.encode(userid, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }


    // Adapter setting with recyclerView
    public void getparkrecyclerview(String message){
        if (!dataModel.dataObjectArrayList.isEmpty()){
            no_result.setVisibility(View.GONE);
            rv_vehicle_list.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(DashBoardActivity.this, LinearLayoutManager.VERTICAL, false);
            rv_vehicle_list.setLayoutManager(layoutManager);
            rv_vehicle_list.setItemAnimator(new DefaultItemAnimator());
            vechileListAdapter= new VechileListAdapter(dataModel.dataObjectArrayList, DashBoardActivity.this, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String vechile_no=(dataModel.dataObjectArrayList.get(position).getM_strVehicleNo());

                }
            });
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, DashBoardActivity.this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_vehicle_list);
            rv_vehicle_list.setAdapter(vechileListAdapter);
            vechileListAdapter.notifyDataSetChanged();

        }else {

            no_result.setVisibility(View.VISIBLE);
            rv_vehicle_list.setVisibility(View.GONE);
            tv_message.setText(message);
            // for  showing gif file if there is no any vehicle in the list
            Glide.with(getApplicationContext()).load(R.drawable.nobooking).asGif().into(noitem_in_cart);// for  showing gif file if there is no any vehicle in the list
        }

    }

    private boolean validate(){
        boolean result = true;

        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            if(check_pass.isChecked()){
                if(strStoreid.equals("0")){
                    ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.validate_storepass));
                    result = false;
                    return result;
                }
            }
        }

        return result;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof VechileListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            if (vechile_name.equals("")) {
                final String vehicle_number = String.valueOf(dataModel.dataObjectArrayList.get(viewHolder.getAdapterPosition()).getM_strVehicleNo());
                dataModel.vehicle_no = vehicle_number;
                // backup of removed item for undo purpose
                deletedItem = dataModel.dataObjectArrayList.get(viewHolder.getAdapterPosition());
                deletedIndex = viewHolder.getAdapterPosition();
                Log.e("delete position--->", String.valueOf(deletedIndex));

                // remove the item from recycler view
              //  vechileListAdapter.removeItem(viewHolder.getAdapterPosition());

                final AlertDialog alertDialog = new AlertDialog.Builder(
                        this).create();

                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.customdialog_end_parkinglayout, null);
                alertDialog.setView(dialogView);
                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                ll_pass_main = (LinearLayout) dialogView.findViewById(R.id.ll_pass_main);
                ll_check_pass = (LinearLayout) dialogView.findViewById(R.id.ll_check_pass);
                check_pass = (CheckBox) dialogView.findViewById(R.id.check_pass);
                sp_store = (Spinner) dialogView.findViewById(R.id.sp_store);
                heading.setText(R.string.validation_name);

                // Setting Dialog Message
                msg_txt.setText(getResources().getString(R.string.suritychek));

                if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
                    ll_pass_main.setVisibility(View.GONE);// visibility gone for testing for boom gate opening
                }else {
                    ll_pass_main.setVisibility(View.GONE);
                }
                check_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked){
                            passapplied = "1";// means yes pass is Applied
                            sp_store.setVisibility(View.VISIBLE);
                            SpecialPassList();

                        }else {
                            passapplied= "0";// pass not applied
                            strStoreid = "0";
                            sp_store.setVisibility(View.GONE);
                        }
                    }
                });

                btnyes.setOnClickListener(  new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        dataModel.cameFrom="Dashboard";
                        if (validate()){
                            if(passapplied.equals("0")) {
                                // vehicle checked out without any pass
                                endParking(vehicle_number, String.valueOf(iID));

                            }else {
                                // vehicle checked out using pass
                                endParking(vehicle_number, String.valueOf(iID));

                            }
                            input_search.setText("");
                        }else{
                            vechileListAdapter.restoreItem(deletedItem, deletedIndex);

                        }
                    }
                });
                btnno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        vechileListAdapter.restoreItem(deletedItem, deletedIndex);

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
            else {
                if (filteredModelList.size() > 0) {
                    final String vehicle_number = String.valueOf(filteredModelList.get(viewHolder.getAdapterPosition()).getM_strVehicleNo());

                    // backup of removed item for undo purpose
                    deletedItem = filteredModelList.get(viewHolder.getAdapterPosition());
                    deletedIndex = viewHolder.getAdapterPosition();
                    Log.e("delete position--->", String.valueOf(deletedIndex));

                    // remove the item from recycler view
                    vechileListAdapter.removeItem(viewHolder.getAdapterPosition());

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parkinglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                    ll_pass_main = (LinearLayout) dialogView.findViewById(R.id.ll_pass_main);
                    ll_check_pass = (LinearLayout) dialogView.findViewById(R.id.ll_check_pass);
                    check_pass = (CheckBox) dialogView.findViewById(R.id.check_pass);
                    sp_store = (Spinner) dialogView.findViewById(R.id.sp_store);
                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.suritychek));

                    if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
                        ll_pass_main.setVisibility(View.VISIBLE);
                    }else {
                        ll_pass_main.setVisibility(View.GONE);
                    }
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

                    btnyes.setOnClickListener(  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            if (validate()){

                                if(passapplied.equals("0")) {

                                    endParking(vehicle_number, String.valueOf(iID));

                                }else {
                                    endParking(vehicle_number, String.valueOf(iID));

                                }
                                input_search.setText("");
                            }else{
                                vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                            }
                        }
                    });
                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                } else {

                    final String vehicle_number = String.valueOf(dataModel.dataObjectArrayList.get(viewHolder.getAdapterPosition()).getM_strVehicleNo());

                    // backup of removed item for undo purpose
                    deletedItem = dataModel.dataObjectArrayList.get(viewHolder.getAdapterPosition());
                    deletedIndex = viewHolder.getAdapterPosition();
                    Log.e("delete position--->", String.valueOf(deletedIndex));

                    // remove the item from recycler view
                    vechileListAdapter.removeItem(viewHolder.getAdapterPosition());

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parkinglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                    ll_pass_main = (LinearLayout) dialogView.findViewById(R.id.ll_pass_main);
                    ll_check_pass = (LinearLayout) dialogView.findViewById(R.id.ll_check_pass);
                    check_pass = (CheckBox) dialogView.findViewById(R.id.check_pass);
                    sp_store = (Spinner) dialogView.findViewById(R.id.sp_store);
                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.suritychek));

                    if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
                        ll_pass_main.setVisibility(View.VISIBLE);
                    }else {
                        ll_pass_main.setVisibility(View.GONE);
                    }
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

                    btnyes.setOnClickListener(  new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            if (validate()){
                                if(passapplied.equals("0")) {

                                    endParking(vehicle_number, String.valueOf(iID));

                                }else {
                                    endParking(vehicle_number, String.valueOf(iID));

                                }
                                input_search.setText("");
                            }else{
                                vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                            }
                        }
                    });
                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            vechileListAdapter.restoreItem(deletedItem, deletedIndex);
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
        else if (viewHolder instanceof OfflineVechileListAdapter.OfflineViewHolder){
            final String vehicle_number = String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getVehicle_number());
            final String bookingid = String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getBookingid());
            final String ownermobilenumber = String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getMobilenum());
            final String checkinDate = String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getCheckintime());
            //  final String vehicle_number = String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getVehicle_number());
            final int iVehicleTypeNew = Integer.parseInt(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(position).getVehicletype());
            dataModel.vehicle_no = vehicle_number;
            iVehicleType= Integer.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition()).getVehicletype());

            dataModel.vehicle_Type = iVehicleType;

            // backup of removed item for undo purpose
            vehicleCheckInBean_deletedItem = dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(viewHolder.getAdapterPosition());
            deletedIndex = viewHolder.getAdapterPosition();
            Log.e("delete position--->", String.valueOf(deletedIndex));

            // remove the item from recycler view
            offlineVechileListAdapter.removeItem(viewHolder.getAdapterPosition());
            final AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.customdialog_end_parkinglayout, null);
            alertDialog.setView(dialogView);
            TextView heading = (TextView) dialogView.findViewById(R.id.heading);
            TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
            Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
            Button btnno = (Button) dialogView.findViewById(R.id.btnno);
            ll_pass_main = (LinearLayout) dialogView.findViewById(R.id.ll_pass_main);
            ll_check_pass = (LinearLayout) dialogView.findViewById(R.id.ll_check_pass);
            check_pass = (CheckBox) dialogView.findViewById(R.id.check_pass);
            sp_store = (Spinner) dialogView.findViewById(R.id.sp_store);
            heading.setText(R.string.validation_name);
            ll_pass_main.setVisibility(View.GONE);
            ll_check_pass.setVisibility(View.GONE);
            // Setting Dialog Message

            msg_txt.setText(getResources().getString(R.string.suritychek));


            btnyes.setOnClickListener(  new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    if (validate()){
                        oflineOnlineCheckOut(checkinDate,bookingid,ownermobilenumber,deletedIndex,vehicle_number,iVehicleTypeNew);
                        input_search.setText("");
                    }else{
                        offlineVechileListAdapter.restoreItem(vehicleCheckInBean_deletedItem, deletedIndex);
                    }
                }
            });
            btnno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    offlineVechileListAdapter.restoreItem(vehicleCheckInBean_deletedItem, deletedIndex);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (presstwice == true) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                System.exit(0);
            }
            this.presstwice = true;
            showMsg(getResources().getString(R.string.doubletapmsg));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    presstwice = false;
                }
            }, 2000);
        }
}

    private void showMsg(String msg) {
        Snackbar snackbar = Snackbar
                .make(content_dashoard, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = menuItem.getActionView();
        tv_notifications = (TextView) actionView.findViewById(R.id.tv_notifications);

        if(SharedStorage.getValue(this,"agent_mode").equals("1")){
            tv_notifications.setText(dataModel.advbookingcount);
            initializeCountDrawer(dataModel.advbookingcount);

        }else {
            tv_notifications.setText("0");
            initializeCountDrawer("0");
        }

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            if(SharedStorage.getValue(this,"agent_mode").equals("1")){

                if(ConnectionStatus.checkConnectionStatus(getApplicationContext())){
                    countfour=0;
                    countTwo=0;
                    normal_listfetch = true;
                    dataModel.about_advanced_dash = 1;
                    getAllCheckedInList(String.valueOf(iID),input_search.getText().toString());//service to get checkin list
                }else {
                    ShowAlertDialog.showAlertDialogFailure(DashBoardActivity.this,getResources().getString(R.string.nonetavailable));
                }

            }else {
                no_result.setVisibility(View.VISIBLE);
                //noitem_in_cart.setVisibility(View.GONE);
                noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
                tv_message.setText(getResources().getString(R.string.youareoffline));
            }
            return true;
        }

        if (id == R.id.action_notifications){
            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(getApplicationContext(), AdvBookingListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // parking start from drawer
        if (id == R.id.it_start_parking) {
            if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){

                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                    try {
                        closeBT();// this will close bluetooth device connection
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), OfflineCheckInActivity.class);
                    i.putExtra("myName",strUserName);
                    i.putExtra("myID",iID);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                }else {
                    try {
                        closeBT();// this will close bluetooth device connection
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), VehicleInfoScanActivity.class);
                    i.putExtra("myName",strUserName);
                    i.putExtra("myID",iID);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                }


            }else {

                try {
                    closeBT();// this will close bluetooth device connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getApplicationContext(), OfflineCheckInActivity.class);
                i.putExtra("myName",strUserName);
                i.putExtra("myID",iID);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();

            }

            // PArking end
        }

        if (id == R.id.it_end_parking) {


            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }

            dataModel.about_advanced_dash = 1;
            dataModel.vehicleCheckInBean = null;
            if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){
                Intent i = new Intent(getApplicationContext(), EndParkingActivity.class);
                i.putExtra("myName",strUserName);
                i.putExtra("myID",iID);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            }else{
                Intent i = new Intent(getApplicationContext(), OfflineCheckOutActivity.class);
                i.putExtra("myName",strUserName);
                i.putExtra("myID",iID);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            }

        }
        if (id == R.id.it_booking_summary) {


            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(getApplicationContext(), BookingSummaryActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();

        }

        // Term and Conditions
        if (id == R.id.it_terms_condition) {
            dataModel.about_web_view=1;
            String web_view_string="http://www.s-parking.com/termscondition.html";
            String heading_about="Terms and Conditions";
            Intent intent=new Intent(DashBoardActivity.this,WebViewAboutUsActivity.class);
            intent.putExtra("web_view_string",web_view_string);
            intent.putExtra("heading_webview",heading_about);
            startActivity(intent);
            finish();
        }

        // privacy policy
        if (id == R.id.it_privacy) {
            dataModel.about_web_view=1;
            String web_view_string="http://www.s-parking.com/privacypolicy.html";
            String heading_about="Privacy Policy";
            Intent intent=new Intent(DashBoardActivity.this,WebViewAboutUsActivity.class);
            intent.putExtra("web_view_string",web_view_string);
            intent.putExtra("heading_webview",heading_about);
            startActivity(intent);
            finish();
        }

        if (id == R.id.it_logout) {
            logout();// Agent Logout

        }

        if (id == R.id.it_boombarrier) {
            final AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.customdialogboomgatelayout, null);
            alertDialog.setView(dialogView);
            TextView heading = (TextView) dialogView.findViewById(R.id.heading);
            EditText msg_txt = (EditText) dialogView.findViewById(R.id.msg_txt);
            EditText msg_txt_gate = (EditText) dialogView.findViewById(R.id.msg_txt_gate);
            Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

            heading.setText(R.string.validation_name);

            if(!SharedStorage.getValue(getApplicationContext(),"ip").equals("")){
                msg_txt.setText(SharedStorage.getValue(getApplicationContext(),"ip"));
            }

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    if(!msg_txt.getText().toString().trim().equals("")){
                    if(!msg_txt_gate.getText().toString().trim().equals("")){
                        SharedStorage.setValue(getApplicationContext(),"ip",msg_txt.getText().toString().trim());
                        SharedStorage.setValue(getApplicationContext(),"gate",msg_txt_gate.getText().toString().trim());
                    }
                    else {
                        ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.validate_gate));

                    }
                    }
                    else {
                        ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.validate_ip));
                    }
                }
            });
            //Animate alert dialog box
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out);
            // Showing Alert Message
            alertDialog.show();

        }

        if (id == R.id. syncnow){

            SharedStorage.setValue(DashBoardActivity.this, "sync_mode", "1");

        }

        if (id == R.id. it_railway_parking){

            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        if (id == R.id.it_net_mode){

            final AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

            final LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.networkconfiglayout, null);
            alertDialog.setView(dialogView);
            // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
            Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
            Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
            RadioGroup radioGroup_Network = (RadioGroup) dialogView.findViewById(R.id.radioGroup_Network);
            final RadioButton radioButton_offline = (RadioButton) dialogView.findViewById(R.id.radioButton_offline);
            final RadioButton radioButton_online = (RadioButton) dialogView.findViewById(R.id.radioButton_online);


            final String[] radiocheck = {SharedStorage.getValue(getApplicationContext(),"agent_mode")};

            if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){

                radioButton_online.setChecked(true);
                radioButton_offline.setChecked(false);

            }else {

                radioButton_online.setChecked(false);
                radioButton_offline.setChecked(true);

            }

            radioGroup_Network.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        if(checkedId == R.id.radioButton_offline){

                            radiocheck[0] = "0";
                            SharedStorage.setValue(DashBoardActivity.this, "agent_mode", "0");
                           // noitem_in_cart.setVisibility(View.GONE);
                            ll_vehicle_count.setVisibility(View.GONE);
                            noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
                            tv_message.setText(getResources().getString(R.string.youareoffline));
                            dataModel.about_advanced_dash = 1;

                        }else if(checkedId == R.id.radioButton_online) {

                            radiocheck[0] = "1";
                            ll_vehicle_count.setVisibility(View.VISIBLE);
                            SharedStorage.setValue(DashBoardActivity.this, "agent_mode", "1");
                            dataModel.about_advanced_dash = 0;
                        }
                    }
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(radiocheck[0].equals("")){

                        ShowAlertDialog.showAlertDialog(DashBoardActivity.this,getResources().getString(R.string.select_net_mode));

                    }else{

                        if(SharedStorage.getValue(getApplicationContext(),"agent_mode").equals("1")){

                            input_search.setVisibility(View.VISIBLE);
                            iv_mode.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
                            no_result.setVisibility(View.GONE);
                            /*rv_vehicle_list.setVisibility(View.VISIBLE);*/
                            getAllCheckedInList(String.valueOf(iID),input_search.getText().toString());//service to get checkin list

                        }else {

                            iv_mode.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_notification_overlay));
                            input_search.setVisibility(View.GONE);
                            no_result.setVisibility(View.VISIBLE);
                            rv_vehicle_list.setVisibility(View.GONE);

                        }
                        alertDialog.dismiss();

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
            alertDialog.setCancelable(false);

        }
        if (id == R.id.it_printer_setting) {

            final AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

            final LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.printer_configurationdialoglayout, null);
            alertDialog.setView(dialogView);
            // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
            LinearLayout ll_mainlay= (LinearLayout) dialogView.findViewById(R.id.ll_mainlay);
            final EditText et_printer_name =(EditText) dialogView.findViewById(R.id.et_printer_name);
            Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
            Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
            RadioGroup radioGroup_printer = (RadioGroup) dialogView.findViewById(R.id.radioGroup_printer);
            RadioButton radioButton_none = (RadioButton) dialogView.findViewById(R.id.radioButton_none);
            RadioButton radioButton_eazy_tap = (RadioButton) dialogView.findViewById(R.id.radioButton_eazy_tap);
            RadioButton radioButton_pos = (RadioButton) dialogView.findViewById(R.id.radioButton_pos);
            RadioButton radioButton_bluetooth = (RadioButton) dialogView.findViewById(R.id.radioButton_bluetooth);
            RadioButton radioButton_pos_9210 = (RadioButton) dialogView.findViewById(R.id.radioButton_pos_9210);

            RadioButton radioButton_pos_new = (RadioButton) dialogView.findViewById(R.id.radioButton_pos_new);
            RadioButton radioButton_pos_park = (RadioButton) dialogView.findViewById(R.id.radioButton_pos_park);
            radioGroup_printer.clearCheck();

            //pos- sunmi printer
            //pos_new- udayma printer

            if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("")){
                radioButton_none.setChecked(true);
                radioButton_eazy_tap.setChecked(false);
                radioButton_pos.setChecked(false);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(false);
                radioButton_pos_new.setChecked(false);
                radioButton_pos_park.setChecked(false);

            } if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("eazy_Tap")){

                radioButton_eazy_tap.setChecked(true);
                radioButton_none.setChecked(false);
                radioButton_pos.setChecked(false);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(false);
                radioButton_pos_new.setChecked(false);
                radioButton_pos_park.setChecked(false);

            }

            else  if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("pos")){

                radioButton_none.setChecked(false);
                radioButton_pos.setChecked(true);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(false);
                radioButton_pos_new.setChecked(false);
                radioButton_pos_park.setChecked(false);

            }
            else  if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("pos_new")){

                radioButton_none.setChecked(false);
                radioButton_pos.setChecked(false);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(false);
                radioButton_pos_new.setChecked(true);
                radioButton_pos_park.setChecked(false);

            }
            else  if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("pos_new_park")){

                radioButton_none.setChecked(false);
                radioButton_pos.setChecked(false);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(false);
                radioButton_pos_new.setChecked(false);
                radioButton_pos_park.setChecked(true);

            }
            else  if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("pos_new_jaipur")){

                radioButton_none.setChecked(false);
                radioButton_pos.setChecked(false);
                radioButton_bluetooth.setChecked(false);
                radioButton_pos_9210.setChecked(true);
                radioButton_pos_new.setChecked(false);
                radioButton_pos_park.setChecked(false);

            }else {
//                radioButton_eazy_tap.setChecked(false);
//                radioButton_pos.setChecked(false);
//                radioButton_bluetooth.setChecked(true);
//                radioButton_pos_new.setChecked(false);
//                radioButton_pos_park.setChecked(false);
//                radioButton_pos_9210.setChecked(false);
            }

            final int[] radiocheck = {0};

            radioGroup_printer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
                    if (null != rb && checkedId > -1) {
                        if(checkedId == R.id.radioButton_none){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 1;

                        }else if(checkedId == R.id.radioButton_pos){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "pos");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 2;

                        }else if(checkedId == R.id.radioButton_pos_new){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "pos_new");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 4;

                        }else if(checkedId == R.id.radioButton_pos_park){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "pos_new_park");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 5;
                        }else if(checkedId == R.id.radioButton_eazy_tap){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "eazy_Tap");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 2;
                        }

                        else if(checkedId == R.id.radioButton_pos_9210){

                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", "pos_new_jaipur");
                            et_printer_name.setVisibility(View.GONE);
                            radiocheck[0] = 6;

                        }else if(checkedId == R.id.radioButton_bluetooth){

                            et_printer_name.setVisibility(View.VISIBLE);
                            radiocheck[0] = 3;

                        }
                    }

                }
            });

            ll_mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(DashBoardActivity.this);
                }
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
//                    if(radiocheck[0] ==3){
//                        if (et_printer_name.getText().toString().trim().equals("")) {
//                            et_printer_name.setError(getResources().getString(R.string.printer_name_required));
//                        } else{
//                            SharedStorage.setValue(DashBoardActivity.this, "printer_name", et_printer_name.getText().toString().trim());
//                            iv_printer.setVisibility(View.VISIBLE);
//                            alertDialog.dismiss();
//                        }
//                    }else {
//                        radiocheck[0] = 0;
//                        CToast.show(getApplicationContext(),"connection requested");
//                        // commented because of bluetooth function
////                        findBT_forboombarier_new();
//                        alertDialog.dismiss();
//                    }

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
            alertDialog.setCancelable(false);

        }

        if (id == R.id.it_notification){

            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataModel.about_advanced_dash = 1;
            Intent i = new Intent(getApplicationContext(), AdvBookingListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        }

        if (id == R.id.it_adv_booking_list){

            dataModel.about_advanced_dash = 1;
            try {
                closeBT();// this will close bluetooth device connection
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(getApplicationContext(), AcceptedBookingListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // agent logout
    private void logout() {

        databaseHandler.deletecheckinlist();
        databaseHandler.deleteparkingPrice();
        databaseHandler.deleteofflinecheckintlist();
        databaseHandler.deletecheckoutlist();
        databaseHandler.deleterailwaycheckintlist();
        databaseHandler.deletefreepricelist();
        databaseHandler.deleterailwaycheckouttlist();
        databaseHandler.deletparkingpricelist();

        try {
            closeBT();//  Bluetooth device connection close

        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedStorage.setValue(getApplicationContext(),"UserId", "");
        SharedStorage.setValue(getApplicationContext(),"Userame","");
        SharedStorage.setValue(getApplicationContext(),"parkingslot","");
        SharedStorage.setValue(getApplicationContext(),"parkinglocation", "");
        SharedStorage.setValue(getApplicationContext(),"parking_area_id", "");
        SharedStorage.setValue(getApplicationContext(),"is_special_pass_available", "");
        SharedStorage.setValue(getApplicationContext(),"printer_name", "");
        SharedStorage.setValue(getApplicationContext(),"FourWheelerRate", "");
        SharedStorage.setValue(getApplicationContext(),"TwoWheelerRate", "");
        SharedStorage.setValue(getApplicationContext(),"agent_mode", "");
        SharedStorage.setValue(getApplicationContext(),"TwoWheelerSPRate", "");
        SharedStorage.setValue(getApplicationContext(),"FourWheelerSPRate", "");
        SharedStorage.setValue(getApplicationContext(),"FreeParkingFacility", "");
        session.setLogin(false);
        //Redirecting to dashboard screen
        startActivity(new Intent(DashBoardActivity.this,  LoginActivity.class));
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    try{
        vechile_name=String.valueOf(charSequence);

        if(dataModel.dataObjectArrayList.size()>0){
            no_result.setVisibility(View.GONE);
            rv_vehicle_list.setVisibility(View.VISIBLE);
            if(vechile_name.equals("")){
                vechileListAdapter = new VechileListAdapter(dataModel.dataObjectArrayList, DashBoardActivity.this, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        String vechile_no=(dataModel.dataObjectArrayList.get(position).getM_strVehicleNo());

                    }
                });
                rv_vehicle_list.setAdapter(vechileListAdapter);
                vechileListAdapter.notifyDataSetChanged();
            }else{
                filteredModelList.removeAll(filteredModelList);
                if(filtermodel(dataModel.dataObjectArrayList, vechile_name).size()>0){
                    filteredModelList = filtermodel(dataModel.dataObjectArrayList, vechile_name);
                    vechileListAdapter.setFilter(filteredModelList);
                }else {
                    //ShowAlertDialog.showAlertDialog(this,getResources().getString(R.string.novehiclefound));
                    no_result.setVisibility(View.VISIBLE);
                    rv_vehicle_list.setVisibility(View.GONE);
                    tv_message.setText(getResources().getString(R.string.novehiclefound));
                    Glide.with(getApplicationContext()).load(R.drawable.nobooking).asGif().into(noitem_in_cart);
                    /*vechileListAdapter.setFilter(filteredModelList);*/
                }
            }
        }

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private List<DataObject> filtermodel(List<DataObject> models, String query) {
        query = query.toLowerCase();

        final List<DataObject> filteredModelList = new ArrayList<DataObject>();
        try{
            for (DataObject model : models) {
                final String text = model.getM_strVehicleNo().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return filteredModelList;
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(databaseHandler==null){
            databaseHandler = new DatabaseHandler(getApplicationContext());
        }

        if (checkperm) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                MarshMallowPermission marshMallowPermission = new MarshMallowPermission(DashBoardActivity.this);

                if (marshMallowPermission.checkPermissionForCamera() == false) {
                    checkperm = true;
                    marshMallowPermission.requestPermissionForCamera();
                } else {
                    checkperm = false;
                }

            }
        }

        }
  //  }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private void startparkinng(String veichele_number, String PaymentMode,String alternate_phone) {
       start_progress_dialog();
        Urls Urls = new Urls();
        String userid = SharedStorage.getValue(getApplicationContext(),"UserId");
        /*String start_parking_url = Urls.VehicleCheckIN+"/"+veichele_number;*/
        String start_parking_url = Urls.VehicleCheckINByVehicleID;

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.VEHICLECHECKINBYVEHICLEID;
        remoteAsync.delegate = this;
        payment_mode =PaymentMode;
        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleID=" + URLEncoder.encode(veichele_number, "UTF-8") +
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&IsSpecialPassApplied=" + URLEncoder.encode("0", "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode("0", "UTF-8")+
                    "&AlternateContactNo=" + URLEncoder.encode(alternate_phone, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
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
        final Calendar c = Calendar.getInstance();
        String dateTime[] = new String[3];
        dateTime[0] = c.get(Calendar.YEAR) + "-" + String.valueOf(c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;
        return dateTime;
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
    private void printEazytapBill() {

        new Thread(new Runnable() {
            public void run() {
                String dateTime[] = getDateTime();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
                PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
                PrinterTester.getInstance().printStr("sParking\n",null);
                PrinterTester.getInstance().step(2);
                PrinterTester.getInstance().leftIndents(Short.parseShort("78"));
               // PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("www.s-parking.com\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("75"));
                PrinterTester.getInstance().printStr(SharedStorage.getValue(DashBoardActivity.this,"parkinglocation")+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("Vehicle No    : "+vehicleNo+"\n",null);
                PrinterTester.getInstance().printStr("CheckIn Time  : "+dateTime[0]+" "+dateTime[1]+"\n",null);
                PrinterTester.getInstance().printStr("Booking No    : "+bookingNumber+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                try {

                    String strVehicleNo = vehicleNo+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;

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

                            PrinterTester.getInstance().leftIndents(Short.parseShort("5"));

                            PrinterTester.getInstance().printStr("Download s-Parking App from Play Store\n",null);
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
                input_search.post(new Runnable() {
                    public void run() {
                        CToast.show(getApplicationContext(),status);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }

    /*******************  end of eazyTap printer printing code ****************/


    // EzeAPI initialization request code  checking
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INITIALIZE) {
            try {
                if (data != null && data.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response",response.toString());
                        String resp = response.toString();
                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response",errorMessage);
                        // Show the error to user as a pop-up informing the details
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Do your exception handling
            }
        }

    }
    private void getOflineVehicleCheckedInList() {
        try{
            databaseHandler.getofflinevehiclecheckinNotSync();

            if(dataModel.offlinevehicleCheckInBeanArrayListNotSync.size()>0){
                //Collections.reverse(dataModel.offlinevehicleCheckInBeanArrayListNotSync);// this will show the newly checked in vehicle first
                tv_four_count.setText(String.valueOf(dataModel.offlinevehicleCheckInBeanArrayListNotSync.size()));
                Log.e("vehicle number",dataModel.offlinevehicleCheckInBeanArrayListNotSync.get(0).getVehicle_number());
                no_result.setVisibility(View.GONE);
                rv_vehicle_list.setVisibility(View.GONE);
                rv_vehicle_list_offline.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(DashBoardActivity.this, LinearLayoutManager.VERTICAL, false);
                rv_vehicle_list_offline.setLayoutManager(layoutManager);
                rv_vehicle_list_offline.setItemAnimator(new DefaultItemAnimator());
                offlineVechileListAdapter= new OfflineVechileListAdapter(dataModel.offlinevehicleCheckInBeanArrayListNotSync, DashBoardActivity.this, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        // String vechile_no=(dataModel.offlinevehicleCheckInBeanArrayList.get(position).getVehicle_number());

                    }
                });
                ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new OfflineCheckInListRecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, DashBoardActivity.this);
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_vehicle_list_offline);
                rv_vehicle_list_offline.setAdapter(offlineVechileListAdapter);
                offlineVechileListAdapter.notifyDataSetChanged();
            }



            else{
                no_result.setVisibility(View.VISIBLE);
                tv_message.setText(getResources().getString(R.string.no_vehicle_for_offlinelist));
                input_search.setVisibility(View.GONE);
                //noitem_in_cart.setVisibility(View.GONE);
                ll_vehicle_count.setVisibility(View.GONE);
                //  noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
            }
        }
        catch(Exception e){
            Log.e("Exception",e.toString());
        }
    }
    private void oflineOnlineCheckOut(String checkInTime,String booking_no,String ownerMobileNumber,int position,String vehicle_number,int iVehicleTypeNew) {
        try{
            if (isExistInOfflineAndOnlineTable(vehicle_number)||isAlreadyCheckedOutorNot(vehicle_number)){
            int bookingNo= Integer.parseInt(booking_no);

                if (isexistindatabase(position)) {//if database is exist

                    bookingBillBean = null;

                    BookingBillBean newbookingbillbean = new BookingBillBean();

                    newbookingbillbean =
                            filtermodel_bookingtableobject(dataModel.offlinebookingBillBeansnArrayList, booking_no);

                    Log.e("vehicle_num", newbookingbillbean.getVechile_no());
                    checkintime = newbookingbillbean.getCheckintime();
                    checkouttime = newbookingbillbean.getCheckouttime();
                    int parkingarea_fee = 0;
                    int total_parkingarea_fee =0;

                    if (payment_mode.equals("4")){//for special pass
                        total_parkingarea_fee=0;
                        if (iVehicleType == 1) {

                            parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                        } else if (iVehicleType == 2) {
                            parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                        } else if (iVehicleType == 3) {
                            parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"HeavyVehicleRate"));
                        }

                    }
                    else if (payment_mode.equals("3")){//for monthly pass
                        total_parkingarea_fee=0;
                        parkingarea_fee= 0;

                    }else{
                        parkingarea_fee= parseInt(newbookingbillbean.getTotalPaybleAmount());
                        total_parkingarea_fee=parkingarea_fee;
                    }

                    //new values store to bean for future use
                    bookingBillBean = new BookingBillBean(newbookingbillbean.getBookingno(), newbookingbillbean.getBookingID(),
                            checkintime, checkouttime, newbookingbillbean.getOwnerphoneno(), String.valueOf(iVehicleType),
                            vehicle_number, newbookingbillbean.getParkingAreaName(),
                            newbookingbillbean.getTotalDuration(), String.valueOf(parkingarea_fee), String.valueOf(total_parkingarea_fee),
                            newbookingbillbean.getFineAmount(), newbookingbillbean.getOfferAmount(), payment_mode,
                            newbookingbillbean.getAgencyName(),newbookingbillbean.getOverTimeDuration(),
                            newbookingbillbean.getOverTimeAmount(), newbookingbillbean.getMessage(),
                            newbookingbillbean.getAdvbookingid());

                }

                else {

                    //calculation of hours using checkin and checkout time
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    String dateTime[] = getDateTime();
                    checkouttime = dateTime[0] + " " + dateTime[1];
                    checkintime = checkInTime;


                    Date date1 = null;
                    try {
                        date1 = simpleDateFormat.parse(checkInTime);
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

                        if (payment_mode.equals("4") ){//for special pass
                            total_parkingarea_fee=0;

                            if (iVehicleType == 1) {

                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                            } else if (iVehicleType == 2) {
                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                            }else if (iVehicleType == 3) {
                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"HeavyVehicleRate"));
                            }
                        }else if(payment_mode.equals("3")){// for monthly pass
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
                        if (payment_mode.equals("4")){//for special pass
                            total_parkingarea_fee=0;

                            if (iVehicleType == 1) {

                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"TwoWheelerSPRate"));

                            } else if (iVehicleType == 2) {
                                parkingarea_fee = parseInt(SharedStorage.getValue(getApplicationContext(),"FourWheelerSPRate"));
                            }
                        }
                        else if (payment_mode.equals("3")){//for monthly pass
                            total_parkingarea_fee=0;

                            parkingarea_fee= 0;

                        }
                        else{
                            if (iVehicleType == 1) {//two wheeler

                                parkingrate = Integer.valueOf(SharedStorage.getValue(DashBoardActivity.this, "TwoWheelerRate"));

                                if ((hours!=0)&&(min>0)){
                                    hours++;
                                    parkingarea_fee = hours * parkingrate;
                                }else{
                                    parkingarea_fee = parkingrate;
                                }

                                total_parkingarea_fee =parkingarea_fee;

                            }
                            else if (iVehicleType == 2) {//four wheeler
                                parkingrate = Integer.valueOf(SharedStorage.getValue(DashBoardActivity.this, "FourWheelerRate"));

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
                                parkingrate = Integer.valueOf(SharedStorage.getValue(DashBoardActivity.this, "HeavyVehicleRate"));

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

//                        int accesscontrolamnt = 0;
//                        accesscontrolamnt = Integer.parseInt(et_acess_control.getText().toString().trim());
//                        total_parkingarea_fee = accesscontrolamnt + parkingarea_fee;
//
//                        parkingfee = String.valueOf(parkingarea_fee);

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

                        bookingBillBean = new BookingBillBean(booking_no,
                                bookingNumber, checkintime, checkouttime,ownerMobileNumber
                                , String.valueOf(iVehicleType), vehicle_number,
                                SharedStorage.getValue(getApplicationContext(), "parkingslot"),
                                Total_duration, parkingfee, String.valueOf(total_parkingarea_fee),
                                "0", "0", payment_mode,
                                SharedStorage.getValue(getApplicationContext(), "AgencyName"),
                                "0","0","Sucessfully checked out", "0");
                    }

                    //set updated parking fee values to bean class for normal agent
                    else {
                        bookingBillBean = new BookingBillBean(booking_no,bookingNumber,
                                checkintime, checkouttime,
                                ownerMobileNumber, String.valueOf(iVehicleType),vehicle_number,
                                SharedStorage.getValue(getApplicationContext(), "parkingslot"), Total_duration,
                                parkingfee, String.valueOf(total_parkingarea_fee), "0", "0", payment_mode,
                                SharedStorage.getValue(getApplicationContext(), "AgencyName"),
                                "0","0","Sucessfully checked out", "0");
                    }

                }

                Log.e("duration", bookingBillBean.getTotalDuration());
                Log.e("payble_amount", bookingBillBean.getTotalPaybleAmount());

                Log.e("parking fee", parkingfee);
                Log.e("parking vehicletype", String.valueOf(iVehicleType));
                Log.e("parking mode", String.valueOf(payment_mode));

                if (SharedStorage.getValue(DashBoardActivity.this, "printer_name").equals("eazy_Tap")) {

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

                                if (isexistindatabase(position)) {

                                    //if exist then update New Vehicle Check out offline details in databse
                                    databaseHandler.updatevehiclecheckofflinecheckoutvechicle(checkintime, String.valueOf(iVehicleType),
                                            vehicle_number, bookingBillBean.getBookingID());
                                    databaseHandler.updateofflinevehiclecheckinNotsync("2",vehicle_number);
                                    deleteofflinefile("2");
                                    databaseHandler.updateOnlineOfflineTable("2",vehicle_number);
                                    deleteofflineAndOnlinefile(vehicle_number);
                                }else {
                                    //add New Vehicle Check out offline details
                                    databaseHandler.addvehiclecheckout(bookingBillBean);

                                    databaseHandler.updateofflinevehiclecheckinNotsync("2",vehicle_number);
                                    deleteofflinefile("2");
                                    databaseHandler.updateOnlineOfflineTable("2",vehicle_number);
                                    deleteofflineAndOnlinefile(vehicle_number);

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
                                        Intent intent = new Intent(DashBoardActivity.this, DashBoardActivity.class);
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
                                        Intent intent = new Intent(DashBoardActivity.this, DashBoardActivity.class);
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
                                Intent intent = new Intent(DashBoardActivity.this, DashBoardActivity.class);
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


            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
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
    //check databse is exist or not
    private boolean isexistindatabase(int position) {

        boolean result = true;

        databaseHandler.getvehiclecheckout();

        if (dataModel.offlinebookingBillBeansnArrayList.size() > 0) {
            if (filtermodel_bookingtable(dataModel.offlinebookingBillBeansnArrayList, String.valueOf(dataModel.offlinebookingBillBeansnArrayList.get(position).getBookingno()))){
                result = false;

            } else {
                result = true;
            }

        } else {
            result = false;
        }

        return result;
    }

    private boolean filtermodel_bookingtable(List<BookingBillBean> models, String query) {
        query = query.toLowerCase();
        boolean result = true;
        final List<BookingBillBean> filteredModelList = new ArrayList<BookingBillBean>();
        try {
            for (BookingBillBean model : models) {
                final String text = model.getBookingID().toLowerCase();
                if (text.equals(query)) {
                    filteredModelList.add(model);
                    result = true;
                }else{
                    result = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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

    private void deleteofflinefile(String exetype){
        databaseHandler.deletevehiclecheckinNotSync(exetype);

    }
    private void deleteofflineAndOnlinefile(String vehicle_number){
        databaseHandler.deletevehiclecheckinOnlineOffline(vehicle_number);

    }
    private void printEazytapBillUsingSDK() {

        new Thread(new Runnable() {
            public void run() {

                PrinterTester.getInstance().init();
                Bitmap bitmap = null;
                JSONObject jsonRequest = new JSONObject();
                JSONObject jsonImageObj = new JSONObject();

                Integer iBitmapBaseHeight = 680;
                String gstNo = SharedStorage.getValue(DashBoardActivity.this,"AgencyGSTNo");
                if(null == gstNo || gstNo.trim().equals(""))
                {
                    iBitmapBaseHeight -= 24;
                }

                String[] arrLocName = breakStringToLines(SharedStorage.getValue(DashBoardActivity.this,"parkinglocation"),35);
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
                    strText = "GSTIN : "+SharedStorage.getValue(DashBoardActivity.this,"AgencyGSTNo");
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

                input_search.post(new Runnable() {
                    public void run() {
                        if (status.equals("Out of paper ")){
                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                    DashBoardActivity.this).create();

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

                        }
                    }
                });
            }
        }).start();


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
    public static int lastIndexOfRegex(String str, String toFind) {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = NOT_FOUND;

        // Search for the given pattern
        while (matcher.find()) {
            lastIndex = matcher.start();
        }

        return lastIndex;


    }

    /// for sending the request to the payment app

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isBound = true;
            Log.e("tag", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBound = false;
        }
    };

    private void printReciept() {

    }


//get the responce form the payment app->


//for handler the responce of pinlab--

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            String value = bundle.getString("MASTERAPPRESPONSE"); // process the response Json as required.

            Log.e("Tagresponse",value);

            Toast.makeText(DashBoardActivity.this,value,Toast.LENGTH_LONG).show();

        }

    }





}
