package utilities.retrofit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data_objects.SParkingAgentModel;
import data_objects.bean.AdvBookingReqBean;
import data_objects.bean.DataObject;
import db.DatabaseHandler;
import shared_pref.SharedStorage;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;

/**
 * Created by server1 on 10/8/2018.
 */

public class NetworkStateChecker extends BroadcastReceiver implements AsyncResponse {

    //context and database helper object
    private Context context;
    RemoteAsync remoteAsync;
    SParkingAgentModel datamodel = SParkingAgentModel.getInstance();
    ScheduledExecutorService scheduler;
    private boolean isactive = true;
    private boolean isactivecheckout = true;
    private boolean isactivecheckin = true;
    DatabaseHandler db1;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();



    @Override
    public void onReceive(final Context context, Intent intent) {

        this.context = context;


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a Network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                /*new Handler().postDelayed(new Runnable() {
                    public void run() {
                        getvehiclecheckin();
                    }
                }, 2000);*/
                db1 = new DatabaseHandler(context);

                scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleWithFixedDelay
                        (new Runnable() {
                            public void run() {
                                //at the time of VEHICLECHECKIN service call
                                //get list of offline checkin list
                                if(datamodel.isofflinecheckin){

                                    getvehiclecheckin();//offline vehicle checkin

                                    /*if(SharedStorage.getValue(context.getApplicationContext(), "sync_mode").equals("1")){
                                        getsyncoff();

                                    }*/
                                    //offline vehicle checkin
                                    getvehicleofflinecheckin();
                                    getVehiclecheckout();//offline vehicle checkout
                                    getAllCheckedInList(String.valueOf(SharedStorage.getValue(context,"UserId")));//service to get checkin list
                                    getRailwayCheckout();//railway check out

                                    getrailwaycheckin();//railway check in


                                    if(SharedStorage.getValue(context,"agent_mode").equals("1")){//online checkin

                                        if (datamodel.about_advanced_dash == 0){
                                           GetAdvBookingInProcessDetails();//adv booking details
                                        }
                                    }
                                }

                            }
                        }, 0, 15, TimeUnit.SECONDS);
            }
        }
    }

    //adv booking details
    private void GetAdvBookingInProcessDetails() {
        Urls Urls = new Urls();
        String login_url = Urls.GetAdvBookingInProcessDetails;

        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GETADVBOOKINGINPROCESSDETAILS;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "ParkingAreaID=" + URLEncoder.encode(SharedStorage.getValue(context,"parking_area_id"), "UTF-8") ;

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    //railway check out/access control checkout
    private void getRailwayCheckout() {
        try {
            db1.getrailwaycheckout();
            if (datamodel.railwayCheckOutBeansArrayList.size()>0){
                for (int i = 0; i<datamodel.railwayCheckOutBeansArrayList.size();i++){
                    if(!datamodel.railwayCheckOutBeansArrayList.get(i).getExetype().equals("2")){

                        Log.e("railwaycheckout---->",datamodel.railwayCheckOutBeansArrayList.get(i).getBookingID());
                        db1.updaterailwayheckout("1",datamodel.railwayCheckOutBeansArrayList.get(i).getBookingID());
                      /*  Log.e("vehiclenumber---->",datamodel.railwayCheckOutBeansArrayList.get(i).getVechile_no());
                        Log.e("vehiclenumbersize---->", String.valueOf(datamodel.railwayCheckOutBeansArrayList.size()));*/
                        Log.e("railwaycheckoutexe---->",datamodel.railwayCheckOutBeansArrayList.get(i).getExetype());

                        if(isactivecheckout){
                            if(remoteAsync == null || remoteAsync.getStatus() != AsyncTask.Status.RUNNING){
                                isactivecheckout = false;
                              //  Log.e("vehiclenumberin---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                                OfflineModeAccessControlCheckOUT(datamodel.railwayCheckOutBeansArrayList.get(i).getCheckintime(),
                                        datamodel.railwayCheckOutBeansArrayList.get(i).getBookingID(),
                                        datamodel.railwayCheckOutBeansArrayList.get(i).getCheckouttime(),
                                        datamodel.railwayCheckOutBeansArrayList.get(i).getTotalPaybleAmount());
                            }else{
                                Log.e("railwaycheckout---->",datamodel.railwayCheckOutBeansArrayList.get(i).getBookingID());
                            }
                        }else{
                            Log.e("else_railwaycheckout",datamodel.railwayCheckOutBeansArrayList.get(i).getBookingID());
                        }
                    }else{
                        deleterailwaychechout("2");
                     //   Log.e("vehiclenumber---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                    }
                }
            }else{
                db1.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleterailwaychechout(String exetype) {

        /*DatabaseHandler db1 = new DatabaseHandler(context);*/
        db1.deleterailwaycheckout(exetype);
        isactive = true;
    }

    private void getsyncoff() {

        if(datamodel.offlinebookingBillBeansnArrayList.size()==0){
            if(datamodel.offlinevehicleCheckInBeanArrayList.size()==0){

                SharedStorage.setValue(context.getApplicationContext(), "sync_mode", "0");
            }
        }
    }

    //offline vehicle checkout
    private void getVehiclecheckout() {
        try {
            db1.getvehiclecheckout();
            if (datamodel.offlinebookingBillBeansnArrayList.size()>0){
                for (int i = 0; i<datamodel.offlinebookingBillBeansnArrayList.size();i++){
                    if(!datamodel.offlinebookingBillBeansnArrayList.get(i).getMessage().equals("2")){

                        //Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        db1.updateofflinevehiclecheckout("1",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                        Log.e("vehiclenumber---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                        Log.e("vehiclenumbersize---->", String.valueOf(datamodel.offlinebookingBillBeansnArrayList.size()));
                        Log.e("exetype---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getMessage());

                        if(isactivecheckout){
                            if(remoteAsync == null || remoteAsync.getStatus() != AsyncTask.Status.RUNNING){
                                isactivecheckout = false;
                                Log.e("vehiclenumberin---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                                OfflineModeVehicleCheckOut(datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getCheckintime(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getBookingID(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getPaymentMode(),
                                        "0",datamodel.offlinebookingBillBeansnArrayList.get(i).getVehicleType(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getCheckouttime(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getTotalPaybleAmount(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getOwnerphoneno(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getAdvbookingid(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getOverTimeDuration(),
                                        datamodel.offlinebookingBillBeansnArrayList.get(i).getOverTimeAmount());
                            }else{
                                Log.e("vehiclenumber---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                            }
                        }else{
                            Log.e("else_vehiclenumber",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                        }
                    }else{
                        deleteofflinecheckout("2");
                        Log.e("vehiclenumber---->",datamodel.offlinebookingBillBeansnArrayList.get(i).getVechile_no());
                    }
                }
            }else{
                db1.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getvehicleofflinecheckin() {
        try{
            db1.getofflinevehiclecheckin();
            if (datamodel.offlinevehicleCheckInBeanArrayList.size()>0){
                for (int i = 0; i<datamodel.offlinevehicleCheckInBeanArrayList.size();i++){
                    if(!datamodel.offlinevehicleCheckInBeanArrayList.get(i).getExetype().equals("2")){

                        //Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        db1.updateofflinevehiclecheckin("1",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        Log.e("vehiclenumber---->",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        Log.e("vehiclenumbersize---->", String.valueOf(datamodel.offlinevehicleCheckInBeanArrayList.size()));
                        Log.e("exetype---->",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getExetype());

                        if(isactivecheckin){
                            if(remoteAsync == null || remoteAsync.getStatus() != AsyncTask.Status.RUNNING){
                                isactivecheckin = false;
                                Log.e("vehiclenumberin---->",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                                OffVehicleCheckIN(datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number(),
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getCheckintime(),
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getBookingid(),
                                        "1",
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getPass_id(),
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicletype(),
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getMobilenum(),
                                        datamodel.offlinevehicleCheckInBeanArrayList.get(i).getAdvanceBookingID());
                            }else{
                                Log.e("vehiclenumber---->",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                            }
                        }else{
                            Log.e("else_vehiclenumber",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        }
                    }else{
                        deleteofflinefile("2");
                        Log.e("vehiclenumber---->",datamodel.offlinevehicleCheckInBeanArrayList.get(i).getVehicle_number());
                    }
                }
            }else{
                db1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //railway check in/ acess control checkin
    private void getrailwaycheckin() {
        try{
            db1.getrailwaycheckin();
            if (datamodel.railwayCheckInBeansArrayList.size()>0){
                for (int i = 0; i<datamodel.railwayCheckInBeansArrayList.size();i++){
                    if(!datamodel.railwayCheckInBeansArrayList.get(i).getExetype().equals("2")){

                        //Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        db1.updaterailwayheckin("1",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                        Log.e("railwaycheckin---->",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                        Log.e("railwaycheckin---->", String.valueOf(datamodel.railwayCheckInBeansArrayList.size()));
                        Log.e("railwaycheckin exe---->",datamodel.railwayCheckInBeansArrayList.get(i).getExetype());

                        if(isactivecheckin){
                            if(remoteAsync == null || remoteAsync.getStatus() != AsyncTask.Status.RUNNING){
                                isactivecheckin = false;
                                Log.e("railwaycheckin---->",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                                OfflineModeAccessControlCheckIN(datamodel.railwayCheckInBeansArrayList.get(i).getCheckintime(),
                                        datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                            }else{
                                Log.e("railwaycheckin---->",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                            }
                        }else{
                            Log.e("else_railwaycheckin",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                        }
                    }else{
                        deleterailwayfile("2");
                        Log.e("railwaycheckin---->",datamodel.railwayCheckInBeansArrayList.get(i).getBookingID());
                    }

                }
            }else{
                db1.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //offline vehicle checkin
    private void getvehiclecheckin(){
        try{

            db1.getvehiclecheckin();
            if (datamodel.vehicleCheckInBeanArrayList.size()>0){
                for (int i = 0; i<datamodel.vehicleCheckInBeanArrayList.size();i++){
                    if(!datamodel.vehicleCheckInBeanArrayList.get(i).getExetype().equals("2")){

                        //Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        db1.updatevehiclecheckin("1",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        Log.e("vehiclenumbersize---->", String.valueOf(datamodel.vehicleCheckInBeanArrayList.size()));
                        Log.e("exetype---->",datamodel.vehicleCheckInBeanArrayList.get(i).getExetype());

                        if(isactive){
                            if(remoteAsync == null || remoteAsync.getStatus() != AsyncTask.Status.RUNNING){
                                isactive = false;
                                Log.e("vehiclenumberin---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                                VehicleCheckIN(datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number(),
                                        datamodel.vehicleCheckInBeanArrayList.get(i).getCheckintime(),
                                        datamodel.vehicleCheckInBeanArrayList.get(i).getVehicletype(),
                                        datamodel.vehicleCheckInBeanArrayList.get(i).getMobilenum());
                            }else{
                                Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                            }
                        }else{
                            Log.e("else_vehiclenumber",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                        }
                    }else{
                        deletefile("2");
                        Log.e("vehiclenumber---->",datamodel.vehicleCheckInBeanArrayList.get(i).getVehicle_number());
                    }
                }
            }else{
                db1.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deletefile(String exetype){

        /*DatabaseHandler db1 = new DatabaseHandler(context);*/
        db1.deletecheckin(exetype);
        isactive = true;

    }

    private void deleteofflinefile(String exetype){

        /*DatabaseHandler db1 = new DatabaseHandler(context);*/
        db1.deletevehiclecheckin(exetype);
        isactivecheckin = true;

    }

    private void deleterailwayfile(String exetype){

        /*DatabaseHandler db1 = new DatabaseHandler(context);*/
        db1.deleterailwaycheckin(exetype);
        isactivecheckin = true;

    }

    private void deleteofflinecheckout(String exetype){

        /*DatabaseHandler db1 = new DatabaseHandler(context);*/
        db1.deletevehiclecheckout(exetype);
        isactivecheckout = true;

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
    //offline checkout
    private void OfflineModeVehicleCheckOut(String vehiclenumber, String Checkintime,String BookingNumber,
                                            String PaymentMode,String SpecialPassID,
                                   String vehivletype, String CheckoutTime, String TotalAmount, String mobile,
                                            String advbookingid, String overtimeamount, String overtimeduration) {

        String start_parking_url ="";
        Urls Urls = new Urls();
        start_parking_url = Urls.OfflineModeVehicleCheckOut;

        // String login_url = Urls.alephLogin;
        String userid = SharedStorage.getValue(context,"UserId");

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.OFFLINEMODEVEHICLECHECKOUT;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(vehiclenumber, "UTF-8") +
                    "&VehicleTypeID=" + URLEncoder.encode(vehivletype, "UTF-8") +
                    "&ContactNumber=" + URLEncoder.encode(mobile, "UTF-8") +
                    "&CheckinTime=" + URLEncoder.encode(Checkintime, "UTF-8")+
                    "&CheckoutTime=" + URLEncoder.encode(CheckoutTime, "UTF-8")+
                    "&BookingNumber=" + URLEncoder.encode(BookingNumber, "UTF-8")+
                    "&TotalAmount=" + URLEncoder.encode(TotalAmount, "UTF-8")+
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(SpecialPassID, "UTF-8")+
                    "&AdvanceBookingID=" + URLEncoder.encode(advbookingid, "UTF-8")+
                    "&OverTimeDuration=" + URLEncoder.encode(overtimeduration, "UTF-8")+
                    "&OverTimeAmount=" + URLEncoder.encode(overtimeamount, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    //acess control checkin
    private void OfflineModeAccessControlCheckIN(String Checkintime,String BookingNumber) {

        String start_parking_url ="";
        Urls Urls = new Urls();
        start_parking_url = Urls.OfflineModeAccessControlCheckIN;

        // String login_url = Urls.alephLogin;
        String userid = SharedStorage.getValue(context,"UserId");

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.OFFLINEMODEACCESSCONTROLCHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&CheckinTime=" + URLEncoder.encode(Checkintime, "UTF-8") +
                    "&BookingNumber=" + URLEncoder.encode(BookingNumber, "UTF-8");

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    //access control checkout offline
    private void OfflineModeAccessControlCheckOUT(String Checkintime,String BookingNumber,String CheckoutTime,String TotalAmount) {

        String start_parking_url ="";
        Urls Urls = new Urls();
        start_parking_url = Urls.OfflineModeAccessControlCheckOUT;

        // String login_url = Urls.alephLogin;
        String userid = SharedStorage.getValue(context,"UserId");

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.OFFLINEMODEACCESSCONTROLCHECKOUT;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&CheckinTime=" + URLEncoder.encode(Checkintime, "UTF-8") +
                    "&CheckoutTime=" + URLEncoder.encode(CheckoutTime, "UTF-8") +
                    "&TotalAmount=" + URLEncoder.encode(TotalAmount, "UTF-8") +
                    "&BookingNumber=" + URLEncoder.encode(BookingNumber, "UTF-8");

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    private void OffVehicleCheckIN(String vehiclenumber, String Checkintime,String BookingNumber,
                                   String PaymentMode,String SpecialPassID,
                                   String vehivletype, String mobile, String advancebookingid) {

        String start_parking_url ="";
        Urls Urls = new Urls();
        start_parking_url = Urls.OfflineModeVehicleCheckIN;

        // String login_url = Urls.alephLogin;
        String userid = SharedStorage.getValue(context,"UserId");

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.OFFLINEMODEVEHICLECHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(vehiclenumber, "UTF-8") +
                    "&VehicleTypeID=" + URLEncoder.encode(vehivletype, "UTF-8") +
                    "&ContactNumber=" + URLEncoder.encode(mobile, "UTF-8") +
                    "&CheckinTime=" + URLEncoder.encode(Checkintime, "UTF-8")+
                    "&BookingNumber=" + URLEncoder.encode(BookingNumber, "UTF-8")+
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(SpecialPassID, "UTF-8")+
                    "&AdvanceBookingID=" + URLEncoder.encode(advancebookingid, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }


    private void VehicleCheckIN(String vehiclenumber, String Checkintime, String vehivletype, String mobile) {

        String start_parking_url ="";
        Urls Urls = new Urls();
        start_parking_url = Urls.OfflineVehicleCheckIN;

       // String login_url = Urls.alephLogin;
        String userid = SharedStorage.getValue(context,"UserId");

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.OFFLINEVEHICLECHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(vehiclenumber, "UTF-8") +
                    "&CheckinTime=" + URLEncoder.encode(Checkintime, "UTF-8")+
                    "&VehicleTypeID=" + URLEncoder.encode(vehivletype, "UTF-8")+
                    "&ContactNumber=" + URLEncoder.encode(mobile, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.OFFLINEVEHICLECHECKIN)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    /*SharedStorage.setValue(this,"DeviceId",obj.getString("deviceNo"));
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    DatabaseHandler db1 = new DatabaseHandler(context);
                    db1.updatevehiclecheckin("2",obj.getString("VehicleNumber"));
                    deletefile("2");
                }else {
                    isactive = true;
                    getvehiclecheckin();//offline vehicle checkin
                    /*SharedStorage.setValue(this,"DeviceId",device_id);
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    //ShowAlertDialog.showAlertDialog(LoginActivity.this,"Incorrect Login credentials");
                }

            } catch (Exception e) {
                isactive = true;
                e.printStackTrace();
            }

        }

        else if (type.equals(RemoteAsync.OFFLINEMODEVEHICLECHECKIN)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    /*SharedStorage.setValue(this,"DeviceId",obj.getString("deviceNo"));
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    DatabaseHandler db1 = new DatabaseHandler(context);
                    db1.updateofflinevehiclecheckin("2",obj.getString("VehicleNumber"));
                    deleteofflinefile("2");
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try{
                        Thread.sleep(300);
                        isactivecheckin = true;
                        getvehicleofflinecheckin();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                else {
                    isactivecheckin = true;
                    getvehicleofflinecheckin();
                    /*SharedStorage.setValue(this,"DeviceId",device_id);
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    //ShowAlertDialog.showAlertDialog(LoginActivity.this,"Incorrect Login credentials");
                }

            } catch (Exception e) {
                isactivecheckin = true;
                e.printStackTrace();
            }

        }

        else if (type.equals(RemoteAsync.OFFLINEMODEACCESSCONTROLCHECKIN)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    /*SharedStorage.setValue(this,"DeviceId",obj.getString("deviceNo"));
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    DatabaseHandler db1 = new DatabaseHandler(context);
                    db1.updaterailwayheckin("2",obj.getString("BookingNumber"));
                    deleterailwayfile("2");
                }else {
                    isactivecheckin = true;
                    getrailwaycheckin();//railway check in
                    /*SharedStorage.setValue(this,"DeviceId",device_id);
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    //ShowAlertDialog.showAlertDialog(LoginActivity.this,"Incorrect Login credentials");
                }

            } catch (Exception e) {
                isactivecheckin = true;
                e.printStackTrace();
            }

        }

        else if (type.equals(RemoteAsync.OFFLINEMODEACCESSCONTROLCHECKOUT)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    /*SharedStorage.setValue(this,"DeviceId",obj.getString("deviceNo"));
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    DatabaseHandler db1 = new DatabaseHandler(context);
                    db1.updaterailwayheckout("2",obj.getString("BookingNumber"));
                    deleterailwaychechout("2");
                }else {
                    isactivecheckin = true;
                    getRailwayCheckout();//railway check out
                    /*SharedStorage.setValue(this,"DeviceId",device_id);
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    //ShowAlertDialog.showAlertDialog(LoginActivity.this,"Incorrect Login credentials");
                }

            } catch (Exception e) {
                isactivecheckin = true;
                e.printStackTrace();
            }

        }

        else if (type.equals(RemoteAsync.OFFLINEMODEVEHICLECHECKOUT)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    /*SharedStorage.setValue(this,"DeviceId",obj.getString("deviceNo"));
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    DatabaseHandler db1 = new DatabaseHandler(context);
                    db1.updateofflinevehiclecheckout("2",obj.getString("BookingNumber"));
                    deleteofflinecheckout("2");
                    deleteofflineAndOnlinefile(obj.getString("BookingNumber"));

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try{
                        Thread.sleep(300);
                        isactivecheckout = true;
                        getVehiclecheckout();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                else {
                    isactivecheckout = true;
                    getVehiclecheckout();//offline vehicle checkout
                    /*SharedStorage.setValue(this,"DeviceId",device_id);
                    startActivity(new Intent(LoginActivity.this, AlephDashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
                    //ShowAlertDialog.showAlertDialog(LoginActivity.this,"Incorrect Login credentials");
                }

            } catch (Exception e) {
                isactivecheckout = true;
                e.printStackTrace();
            }

        }
        else if (type.equals(RemoteAsync.GETADVBOOKINGINPROCESSDETAILS)){
            try{

                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString()); // Response from server
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    JSONArray AdvBookingInProcessList = obj.getJSONArray("AdvBookingInProcessList");
                    ArrayList<AdvBookingReqBean> advBookingReqBeanArrayList = new ArrayList<AdvBookingReqBean>();
                    if (AdvBookingInProcessList.length() > 0) {
                        for (int i = 0; i < AdvBookingInProcessList.length(); i++) {
                            JSONObject object = AdvBookingInProcessList.getJSONObject(i);
                            AdvBookingReqBean advBookingReqBean = new AdvBookingReqBean();

                            advBookingReqBean.setAdvbookingid(String.valueOf(object.getString("Adv_booking_id")));
                            advBookingReqBean.setVehicle_id(String.valueOf(object.getString("Vehicle_id")));
                            advBookingReqBean.setVehicle_no(object.getString("Vehicle_no"));
                           // advBookingReqBean.setAdvbooking_starttime(object.getString("Adv_booking_start_time"));

                            String[] starttime = object.getString("Adv_booking_start_time").split(" ");

                            advBookingReqBean.setStartTime(starttime[1]);
                            advBookingReqBean.setBooking_date(starttime[0]);

                            //advBookingReqBean.setAdvbooking_endtime(object.getString("Adv_booking_end_time"));

                            String[] endtime = object.getString("Adv_booking_end_time").split(" ");

                            advBookingReqBean.setEndTime(endtime[1]);
                            advBookingReqBean.setBooking_dateend(endtime[0]);

                            advBookingReqBean.setTotaltime(object.getString("Total_time"));
                            advBookingReqBean.setTotalamount(String.valueOf(object.getString("Total_amount")));
                            advBookingReqBean.setRate(String.valueOf(object.getString("Rate")));
                            advBookingReqBean.setVehicletype_id(String.valueOf(object.getString("Vehicle_type_id")));
                            advBookingReqBean.setVehicletype_name(object.getString("Vehicle_type_name"));
                            advBookingReqBean.setVehicleowner_fullname(object.getString("Vehicle_owner_full_name"));
                            advBookingReqBean.setVehicleowner_mobilenumber(object.getString("Vehicle_owner_mobile_no"));

                            advBookingReqBean.setViewtype(1);
                            advBookingReqBeanArrayList.add(advBookingReqBean);
                        }

                        datamodel.advBookingReqBeanArrayList.removeAll(datamodel.advBookingReqBeanArrayList);
                        datamodel.advBookingReqBeanArrayList.addAll(advBookingReqBeanArrayList);
                        datamodel.advbookingcount = String.valueOf(datamodel.advBookingReqBeanArrayList.size());

                        if(datamodel.about_advanced_dash==0){

                            ((DashBoardActivity)context).initializeCountDrawer(datamodel.advbookingcount);//set total booking number
                        }
                    }else {
                        datamodel.advBookingReqBeanArrayList.removeAll(datamodel.advBookingReqBeanArrayList);
                        datamodel.advbookingcount = String.valueOf(datamodel.advBookingReqBeanArrayList.size());
                        if(datamodel.about_advanced_dash==0){

                            ((DashBoardActivity)context).initializeCountDrawer(datamodel.advbookingcount);
                        }
                    }
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try{
                        Thread.sleep(300);
                        GetAdvBookingInProcessDetails();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                else {
                    Log.e("Error----->", obj.getString("message"));
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
                    //ShowAlertDialog.showAlertDialogFailure(VehicleInfoScanActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.GETALLCHECKEDLIST)) {

            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());


                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.about_advanced_dash = 0;

                    JSONArray checkinJsonArrayList = obj.getJSONArray("CheckinList");
                    ArrayList<DataObject> dataObjectArrayList = new ArrayList<DataObject>();
                    if (checkinJsonArrayList.length() > 0) {
                        Log.e("ArrayList from server", String.valueOf(checkinJsonArrayList.length()));
                        db1.clearDataFromonlineOfflineSync();
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


                            dataObject.setVehicle_type_icon(object.getString("vehicle_type_icon"));
                            dataObject.setM_strOwnerName(object.getString("owner_name"));
                            dataObject.setBooking_no(object.getString("booking_no"));
//                          dataObject.setTransactionID(object.getInt("transaction_id"));
//                          dataObject.setAdvPayAmount(object.getInt("adv_pay_amt"));
                            dataObject.setViewtype(1);

                            dataObjectArrayList.add(dataObject);

                            db1.addAllCheckinVehicle(dataObject);

                        }

                        //tv_four_count.setText(String.valueOf(countfour));

                        dataModel.dataObjectArrayList.removeAll(dataModel.dataObjectArrayList);
                        Collections.reverse(dataObjectArrayList);// this will show the newly checked in vehicle first
                        dataModel.dataObjectArrayList.addAll(dataObjectArrayList);

                    }

                }

                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        //get all parking details by parking id
                        getAllCheckedInList(String.valueOf(SharedStorage.getValue(context,"UserId")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }else{
                    db1.clearDataFromonlineOfflineSync();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private void getAllCheckedInList(String userid) {



        Urls Urls = new Urls();
        //String login_url = Urls.GetCarParkingList+"/"+userid;
        String login_url = Urls.GetAllCheckedInList;


        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GETALLCHECKEDLIST;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams=  "AgentID=" + URLEncoder.encode(userid, "UTF-8");

        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    private void deleteofflineAndOnlinefile(String booking_No){
        db1.deletevehiclecheckinOnlineOfflineByBookingNo(booking_No);

    }
}