package com.innovus.vyoma.s_parking_agentApollo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import adapter.AcceptedBookingListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.AcceptedBookingBean;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;
import utilities.listnerofRecyclerView.CustomItemClickListener;
import utilities.listnerofRecyclerView.RecyclerItemAccpetenceTouchHelper;
import utilities.others.CToast;
import utilities.others.ConnectionStatus;
import utilities.printer_utils.Utils;


public class AcceptedBookingListActivity extends AppCompatActivity implements TextWatcher/*,
        SwipeListener*/, AsyncResponse, RecyclerItemAccpetenceTouchHelper.RecyclerItemTouchHelperListener {

    SParkingAgentModel datamodel = SParkingAgentModel.getInstance();
    private EditText input_search;
    private RecyclerView rv_advbook_vehicle_list;
    private RelativeLayout no_result;
    private ImageView noitem_in_cart,iv_printer;
    private LinearLayout ll_vehicle_count;
    private TextView tv_message,tv_four_count,tv_two_count;
    private ConstraintLayout listlay;
    AcceptedBookingListAdapter acceptedBookingListAdapter;
    private String blockCharacterSet = "~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹~";
    String vechile_name="";
    List<AcceptedBookingBean> filteredModelList=new ArrayList<AcceptedBookingBean>();
    private  int deletedIndex=0;
    private AcceptedBookingBean deletedItem= new AcceptedBookingBean();
    RemoteAsync remoteAsync;
    private SpotsDialog progressDialog;
    int countfour=0;
    int countTwo =0;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    String vehiclenumber="", bookingNumber="", ivehicletype="", bookingid="", startingtime="";
    private String AdvPayAmt = "";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;
    volatile boolean stopWorker;
    private Context mContext;
    private FloatView floatView;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Toast.makeText(AcceptedBookingListActivity.this,
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
        setContentView(R.layout.activity_accepted_booking_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+getResources().getString(R.string.app_name)+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mContext = this;
        initviews();
    }

    private void initviews() {

        datamodel.about_advanced_dash = 1;

        input_search = (EditText)findViewById(R.id.input_search);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_four_count = (TextView) findViewById(R.id.tv_four_count);
        tv_two_count = (TextView) findViewById(R.id.tv_two_count);
        rv_advbook_vehicle_list = (RecyclerView) findViewById(R.id.rv_advbook_vehicle_list);
        no_result = (RelativeLayout) findViewById(R.id.no_result);
        noitem_in_cart = (ImageView) findViewById(R.id.noitem_in_cart);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);
        listlay = (ConstraintLayout) findViewById(R.id.listlay);
        ll_vehicle_count = (LinearLayout) findViewById(R.id.ll_vehicle_count);

        input_search.addTextChangedListener(AcceptedBookingListActivity.this);
        input_search.setFilters(new InputFilter[] { filter });
        listlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AcceptedBookingListActivity.this);
            }
        });

        //check Network mode
        if(SharedStorage.getValue(this,"agent_mode").equals("1")){
            ll_vehicle_count.setVisibility(View.VISIBLE);
            input_search.setVisibility(View.VISIBLE);
            //service for accepted advance booking list
            getAllAcceptedList(SharedStorage.getValue(getApplicationContext(),"parking_area_id"));
        }else{
            ll_vehicle_count.setVisibility(View.GONE);
            no_result.setVisibility(View.VISIBLE);
            input_search.setVisibility(View.GONE);
            noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
            tv_message.setText(getResources().getString(R.string.youareoffline));
        }

        if (!SharedStorage.getValue(AcceptedBookingListActivity.this,"printer_name").equals("")){


            floatView = FloatView.getInstance(AcceptedBookingListActivity.this);
//            floatView.createFloatView(20, 20);
//            floatView.release();

        }

    }

    //service for accepted advance booking list
    private void getAllAcceptedList(String parking_area_id) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String login_url = Urls.GetAdvBookingAcceptByAgentDetails;

        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GETADVBOOKINGACCEPTEDBYAGENT;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams=  "ParkingAreaID=" + URLEncoder.encode(parking_area_id, "UTF-8");

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // it is used for input_search  Edit text to filter text
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (source != null && blockCharacterSet.contains(("" + source.charAt(i)))) {
                    return source.toString().substring(start,source.toString().length()-1);
                }
            }
            return null;
        }
    };

    // Adapter setting with recyclerView
    private void getparkrecyclerview(String message){
        if (datamodel.acceptedBookingBeanArrayList.size()>0){
            no_result.setVisibility(View.GONE);
            rv_advbook_vehicle_list.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(AcceptedBookingListActivity.this,
                    LinearLayoutManager.VERTICAL, false);
            rv_advbook_vehicle_list.setLayoutManager(layoutManager);
            rv_advbook_vehicle_list.setItemAnimator(new DefaultItemAnimator());
            acceptedBookingListAdapter= new AcceptedBookingListAdapter(datamodel.acceptedBookingBeanArrayList,
                    AcceptedBookingListActivity.this, new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                }
            });
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemAccpetenceTouchHelper(0, ItemTouchHelper.LEFT, AcceptedBookingListActivity.this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_advbook_vehicle_list);
           rv_advbook_vehicle_list.setAdapter(acceptedBookingListAdapter);
           acceptedBookingListAdapter.notifyDataSetChanged();

        }else {

            no_result.setVisibility(View.VISIBLE);
            rv_advbook_vehicle_list.setVisibility(View.GONE);
            tv_message.setText(message);
            // for  showing gif file if there is no any vehicle in the list
            Glide.with(getApplicationContext()).load(R.drawable.nobooking).asGif().into(noitem_in_cart);// for  showing gif file if there is no any vehicle in the list
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = menuItem.getActionView();

        menuItem.setVisible(false);//to hide notification icon
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        //refresh
        if (item.getItemId() == R.id.action_settings) {

            //Network checking
            if(SharedStorage.getValue(this,"agent_mode").equals("1")){

                if(ConnectionStatus.checkConnectionStatus(getApplicationContext())){
                    countfour=0;
                    countTwo=0;
                    //service for accepted advance booking list
                    getAllAcceptedList(SharedStorage.getValue(getApplicationContext(),"parking_area_id"));
                }else {
                    ShowAlertDialog.showAlertDialog(AcceptedBookingListActivity.this,getResources().getString(R.string.no_internet));
                }

            }else {
                no_result.setVisibility(View.VISIBLE);
                //noitem_in_cart.setVisibility(View.GONE);
                noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
                tv_message.setText(getResources().getString(R.string.youareoffline));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        datamodel.about_advanced_dash = 0;
        super.onBackPressed();
        Intent intent = new Intent(AcceptedBookingListActivity.this, DashBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AcceptedBookingListAdapter.MyViewHolder) {

            if (vechile_name.equals("")){
                final String vehicle_number = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicleno();
                String advancebookingid = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingid();
                String agentid = SharedStorage.getValue(getApplicationContext(),"UserId");
                ivehicletype = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicletypeid();
                vehiclenumber = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicleno();
                startingtime =  datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingstarttime();

                // backup of removed item for undo purpose
                deletedItem = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition());
                deletedIndex = viewHolder.getAdapterPosition();
                Log.e("delete position--->", String.valueOf(deletedIndex));

                // remove the item from recycler view
                acceptedBookingListAdapter.removeItem(viewHolder.getAdapterPosition());

                //checkinservice
                startparkinng(advancebookingid,agentid,vehicle_number);

            }else {
                if (filteredModelList.size() > 0) {
                    final String vehicle_number = filteredModelList.get(viewHolder.getAdapterPosition()).getVehicleno();
                    String advancebookingid = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingid();
                    String agentid = SharedStorage.getValue(getApplicationContext(),"UserId");
                    vehiclenumber = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicleno();
                    ivehicletype = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicletypeid();
                    startingtime =  datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingstarttime();

                    // backup of removed item for undo purpose
                    deletedItem = filteredModelList.get(viewHolder.getAdapterPosition());
                    deletedIndex = viewHolder.getAdapterPosition();
                    Log.e("delete position--->", String.valueOf(deletedIndex));

                    // remove the item from recycler view
                    acceptedBookingListAdapter.removeItem(viewHolder.getAdapterPosition());

                    //checkinservice
                    startparkinng(advancebookingid,agentid,vehicle_number);

                }else {
                    final String vehicle_number = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicleno();
                    String advancebookingid = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingid();
                    String agentid = SharedStorage.getValue(getApplicationContext(),"UserId");
                    vehiclenumber = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicleno();
                    ivehicletype = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getVehicletypeid();
                    startingtime =  datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition()).getAdvbookingstarttime();

                    // backup of removed item for undo purpose
                    deletedItem = datamodel.acceptedBookingBeanArrayList.get(viewHolder.getAdapterPosition());
                    deletedIndex = viewHolder.getAdapterPosition();
                    Log.e("delete position--->", String.valueOf(deletedIndex));

                    // remove the item from recycler view
                    acceptedBookingListAdapter.removeItem(viewHolder.getAdapterPosition());

                    //checkinservice
                    startparkinng(advancebookingid,agentid,vehicle_number);

                }
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
    private void startparkinng(String advancebookingid, String agentid,String vehicle_number ) {
        start_progress_dialog();

        Urls Urls = new Urls();
        String start_parking_url = Urls.AdvanceBookingVehicleCheckin;

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.ADVANCEBOOKINGVEHICLECHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AdvBookingID=" + URLEncoder.encode(advancebookingid, "UTF-8") +
                    "&AgentID=" + URLEncoder.encode(agentid, "UTF-8");

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
            Crashlytics.log(Log.ERROR,"SParkingAgent_startparking",e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    @Override
    public void processFinish(String type, String output) {

        //response for accepted adv booking
        if (type.equals(RemoteAsync.GETADVBOOKINGACCEPTEDBYAGENT)){
            stop_progress_dialog();
            try{
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)){

                    JSONArray checkinJsonArrayList = obj.getJSONArray("AdvBookingAcceptByAgentList");
                    ArrayList<AcceptedBookingBean> dataObjectArrayList = new ArrayList<AcceptedBookingBean>();
                    if (checkinJsonArrayList.length() > 0) {
                        countfour=0;// fourWheeler Count
                        countTwo=0;// TwoWheeler Count

                        for (int i = 0; i < checkinJsonArrayList.length(); i++) {
                            JSONObject object = checkinJsonArrayList.getJSONObject(i);
                            AcceptedBookingBean acceptedBookingBean = new AcceptedBookingBean();

                            acceptedBookingBean.setAdvbookingid(String.valueOf(object.getString("Adv_booking_id")));
                            acceptedBookingBean.setVehicleid(String.valueOf(object.getString("Vehicle_id")));
                            acceptedBookingBean.setVehicleno(object.getString("Vehicle_no"));
                            acceptedBookingBean.setAdvbookingstarttime(object.getString("Adv_booking_start_time"));
                            acceptedBookingBean.setAdvbookingendtime(object.getString("Adv_booking_end_time"));
                            acceptedBookingBean.setTotaltime(object.getString("Total_time"));
                            acceptedBookingBean.setTotalamount(String.valueOf(object.getString("Total_amount")));
                            acceptedBookingBean.setRate(String.valueOf(object.getString("Rate")));
                            acceptedBookingBean.setVehicletypeid(String.valueOf(object.getString("Vehicle_type_id")));
                            acceptedBookingBean.setVehicletypename(object.getString("Vehicle_type_name"));
                            acceptedBookingBean.setVehicleownerfullname(object.getString("Vehicle_owner_full_name"));
                            acceptedBookingBean.setVehicleownermobile(object.getString("Vehicle_owner_mobile_no"));
                            acceptedBookingBean.setAdvbookingstatusid(String.valueOf(object.getString("Adv_booking_status_id")));
                            if (String.valueOf(object.getString("Vehicle_type_id")).equals("1")){
                                countTwo++;
                                Log.e("countTwo", String.valueOf(countTwo));
                            }else if (String.valueOf(object.getString("Vehicle_type_id")).equals("2")){
                                countfour++;
                                Log.e("countfour", String.valueOf(countfour));

                            }
                            acceptedBookingBean.setViewtype(1);
                            dataObjectArrayList.add(acceptedBookingBean);
                        }

                        tv_four_count.setText(String.valueOf(countfour));
                        tv_two_count.setText(String.valueOf(countTwo));
                        datamodel.acceptedBookingBeanArrayList.removeAll(datamodel.acceptedBookingBeanArrayList);
                        Collections.reverse(dataObjectArrayList);// this will show the newly checked in vehicle first
                        datamodel.acceptedBookingBeanArrayList.addAll(dataObjectArrayList);
                        getparkrecyclerview("");//show  vehicle to into recycler view
                    }
                    else {
                        datamodel.acceptedBookingBeanArrayList.removeAll(datamodel.acceptedBookingBeanArrayList);
                        getparkrecyclerview(getResources().getString(R.string.no_item_found));
                        if ( datamodel.acceptedBookingBeanArrayList.size()==0){
                            countfour=0;// set Four Wheeler count 0 if dataModel.dataObjectArrayList.size is zero.
                            countTwo=0;//set Two Wheeler count 0 if dataModel.dataObjectArrayList.size is zero
                            tv_four_count.setText(String.valueOf(countfour));
                            tv_two_count.setText(String.valueOf(countTwo));
                        }
                    }
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                        getAllAcceptedList(SharedStorage.getValue(getApplicationContext(),"parking_area_id"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    datamodel.acceptedBookingBeanArrayList.removeAll( datamodel.acceptedBookingBeanArrayList);
                    getparkrecyclerview(msg.getString("message"));// this method is for vehicle list showin recyclerview
                    if ( datamodel.acceptedBookingBeanArrayList.size()==0){
                        countfour=0;// set Four Wheeler count 0 if dataModel.dataObjectArrayList.size is zero.
                        countTwo=0;//set Two Wheeler count 0 if dataModel.dataObjectArrayList.size is zero
                        tv_four_count.setText(String.valueOf(countfour));
                        tv_two_count.setText(String.valueOf(countTwo));
                    }
                    //ShowAlertDialog.showAlertDialog(DashBoardActivity.this, msg.getString("message"));
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
                    ShowAlertDialog.showAlertDialogFailure(AcceptedBookingListActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //response for adv booking vheicle which are checked in
        else if (type.equals(RemoteAsync.ADVANCEBOOKINGVEHICLECHECKIN)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    bookingNumber = obj.getString("BookingNumber");
                    bookingid = obj.getString("BookingID");
                    if (SharedStorage.getValue(AcceptedBookingListActivity.this,"printer_name").equals(""))
                    {
                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                    }
                    else if (SharedStorage.getValue(AcceptedBookingListActivity.this,"printer_name").equals("eazy_Tap"))
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
                            @SuppressLint("NewApi")
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printEazytapBill();//call this method for printing bill

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
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            acceptedBookingListAdapter.restoreItem(deletedItem, deletedIndex);
                            //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

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

                    GenerateAuthToken();//
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                       // startparkinng(advancebookingid,agentid,vehicle_number);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else{

                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);

                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            //restor list data
                            acceptedBookingListAdapter.restoreItem(deletedItem, deletedIndex);
                            //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[3];
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DAY_OF_MONTH);
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;
        return dateTime;
    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        try{

            //search vehicle
            vechile_name=String.valueOf(charSequence);

            if(datamodel.acceptedBookingBeanArrayList.size()>0){
                no_result.setVisibility(View.GONE);
                rv_advbook_vehicle_list.setVisibility(View.VISIBLE);

                if(vechile_name.equals("")){
                    acceptedBookingListAdapter = new AcceptedBookingListAdapter(datamodel.acceptedBookingBeanArrayList, AcceptedBookingListActivity.this, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                            String vechile_no=(datamodel.acceptedBookingBeanArrayList.get(position).getVehicleno());

                        }
                    });
                    rv_advbook_vehicle_list.setAdapter(acceptedBookingListAdapter);
                    acceptedBookingListAdapter.notifyDataSetChanged();
                }else {
                    filteredModelList.removeAll(filteredModelList);
                    // add  search filter for lowercase vehicle number in list
                    if(filtermodel(datamodel.acceptedBookingBeanArrayList, vechile_name).size()>0){
                        filteredModelList = filtermodel(datamodel.acceptedBookingBeanArrayList, vechile_name);
                        acceptedBookingListAdapter.setFilter(filteredModelList);
                    }else {
                        no_result.setVisibility(View.VISIBLE);
                        rv_advbook_vehicle_list.setVisibility(View.GONE);
                        tv_message.setText(getResources().getString(R.string.novehiclefound));
                        Glide.with(getApplicationContext()).load(R.drawable.nobooking).asGif().into(noitem_in_cart);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // search filter for vehicle number in list
    private List<AcceptedBookingBean> filtermodel(List<AcceptedBookingBean> models, String query) {
        query = query.toLowerCase();

        final List<AcceptedBookingBean> filteredModelList = new ArrayList<AcceptedBookingBean>();
        try{
            for (AcceptedBookingBean model : models) {
                final String text = model.getVehicleno().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return filteredModelList;
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(AcceptedBookingListActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop_progress_dialog() {
        try{
            if(progressDialog!=null){

                progressDialog.dismiss();
                progressDialog=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
//                PrinterTester.getInstance().printStr("sParking\n",null);
                StringBuilder print_bill = new StringBuilder();
                print_bill.append(paddingCenter("sParking", PAGE_WIDTH_TWO_INCH)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                PrinterTester.getInstance().step(2);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("78"));
               // PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_32);
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter("www.s-parking.com", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().printStr("www.s-parking.com\n",null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("75"));
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter(SharedStorage.getValue(AcceptedBookingListActivity.this,"parkinglocation"), PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().printStr(SharedStorage.getValue(AcceptedBookingListActivity.this,"parkinglocation")+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("Vehicle No    : "+vehiclenumber+"\n",null);
                PrinterTester.getInstance().printStr("CheckIn Time  : "+dateTime[0]+" "+dateTime[1]+"\n",null);
                PrinterTester.getInstance().printStr("Booking No    : "+bookingNumber+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                try {

                    String strVehicleNo = vehiclenumber+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;

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
                input_search.post(new Runnable() {
                    public void run() {
                        CToast.show(getApplicationContext(),status);
                    }
                });
            }
        }).start();


    }

    /*******************  end of eazyTap printer printing code ****************/


}
