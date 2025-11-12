package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import data_objects.SParkingAgentModel;
import data_objects.bean.BookingBillBean;
import data_objects.bean.DataObject;
import data_objects.bean.FreeParkingPriceType;
import data_objects.bean.HourMinParkingPrice;
import data_objects.bean.ParkingPrice;
import data_objects.bean.RailwayCheckInBean;
import data_objects.bean.RailwayCheckOutBean;
import data_objects.bean.VehicleCheckInBean;
import data_objects.bean.VehicleTypePrice;

import static android.icu.text.ListFormatter.Type.AND;
import static java.lang.Math.ceil;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "sparking_agent_table";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BILL_AMOUNT = "booking_bill";
    private static final String TABLE_VEHICLE_CHECKOUT = "vehicle_checkout";
    private static final String TABLE_RAILWAY_CHECKIN = "railway_checkin";
    private static final String TABLE_RAILWAY_CHECKOUT = "railway_checkout";
    private static final String TABLE_VIHICLE_CHECKIN = "Offile_vehicle_checkin";
    private static final String TABLE_OFFLINEVIHICLE_CHECKIN = "Newvehicle_offile_checkin";
    private static final String TABLE_FREE_PARKING_LIST = "FreeParkingPriceList";
    private static final String TABLE_PARKING_PRICE = "parking_Price";
    private static final String TABLE_TRAFFIC_PARKING_PRICE_LIST = "TariffParkingAreaPriceList";
    private static final String TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC = "Newvehicle_offile_checkin_but_not_sync";
    private static final String TABLE_OFFLINE_ONLINE_LIST = "OfflineOnlineSyncList";
    private static final String TABLE_SEARCHEDADDRESS = "searchedaddress";
    private static final String TABLE_CHECK_FAV = "check_fav";
    private static final String TABLE_BRANDS = "brands";
    private static final String TABLE_FUEL = "fuel";
    private static final String TABLE_BODY_TYPE = "bodytype";
    private static final String TABLE_VIDEO_TYPE = "Offile_video_list";
    private static final String TABLE_ASSIGNED_DRIVER = "assigned_driver";

    private static final String KEY_MSTRUSERNAME = "m_strUserName";
    private static final String KEY_PARKINGAREAID = "parking_area_id";
    private static final String KEY_SLOTID = "slot_id";
    private static final String KEY_SLOTNAME = "slot_name";
    private static final String KEY_BOOKINGNO = "booking_no";
    private static final String KEY_PAYMENTMODEID = "payment_mode_id";
    private static final String KEY_PAYMENTMODE= "payment_mode";
    private static final String KEY_VEHICLEID= "vehicle_id";
    private static final String KEY_MIUID= "m_iUID";
    private static final String KEY_MSTRVEHICLENO= "m_strVehicleNo";
    private static final String KEY_MIBOOKINGID= "m_iBookingID";
    private static final String KEY_MSTROWNERNAME= "m_strOwnerName";
    private static final String KEY_MSTROWNERPHONE= "m_strOwnerPhone";
    private static final String KEY_MSTRALTERPHONE= "m_strAlterPhone";
    private static final String KEY_MSTRVEHICLETYPE= "m_strVehicleType";
    private static final String KEY_MSTRCHECKINTIME= "m_strCheckInTime";
    private static final String KEY_VEHICLETYPEICON= "vehicle_type_icon";
    private static final String KEY_VEHICLETYPEID= "vehicle_type_id";

    private static DatabaseHandler sInstance;

    private static final String KEY_UID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PROMO = "promo";


    private static final String KEY_BRAND_ID = "id";
    private static final String KEY_BRAND_IMAGE = "image";
    private static final String KEY_BRAND_NAME = "brand_name";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_VIEWTYPE_BRANDS = "viewtype";

    private static final String KEY_PLACEID = "placeid";
    private static final String KEY_PLACEADDRESS = "placeaddress";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_SECONDARY_TEXT = "secondary_text";
    //FOR PARKING PRICE
    private static final String KEY_PP_ID = "pp_id";
    private static final String KEY_PP_PARKING_AREA = "pp_parking_area";
    private static final String KEY_PP_POLICY = "pp_policy";
    private static final String KEY_PP_VEHICLE_TYPE ="pp_vehicle_type" ;
    private static final String KEY_PP_SHIFT_TYPE = "pp_shift_type";
    private static final String KEY_PP_HOUR ="pp_hour" ;
    private static final String KEY_PP_MIN = "pp_min";
    private static final String KEY_PP_PRICE = "pp_price";
    private static final String KEY_PP_LASTUPDATEON = "pp_lastupdateon";


    private static final String KEY_CARID = "id";
    private static final String KEY_CARIMAGE = "car_img";
    private static final String KEY_CARAME = "car_name";
    private static final String KEY_CARPRICELOW = "car_price_low";
    private static final String KEY_CARPRICEHIGH = "car_price_high";
    private static final String KEY_CARMODEL = "car_model";
    private static final String KEY_CARENGINECAPACITY = "car_enginecapacity";
    private static final String KEY_CARHORSEPOWER = "car_enginehorsepower";
    private static final String KEY_CARMILLEAGE = "car_milleage";
    private static final String KEY_STATUS = "status";
    private static final String KEY_RELEASEDATE = "release_date";
    private static final String KEY_TYPE = "type";
    private static final String KEY_FAVOURITE = "favourite";
    private static final String KEY_VIEWTYPE = "viewtype";
    private static final String KEY_EXECUTION_TYPE = "executiontype";
    private static final String KEY_ADVANCE_BOOKING_ID = "AdvanceBookingID";

    private static final String KEY_FUEL_ID = "ID";
    private static final String KEY_FUEL_NAME = "fuel_name";
    private static final String KEY_FUEL_IMAGE = "image_path";
    private static final String KEY_FUEL_CHECK = "fuel_check";
    private static final String KEY_FUEL_VIEW_TYPE = "fuel_view_type";

    private static final String KEY_BODY_ID = "id";
    private static final String KEY_BODY_NAME = "body_type";
    private static final String KEY_BODY_IMAGE = "image_path";
    private static final String KEY_BODY_CHECK = "body_check";
    private static final String KEY_BODY_VIEW_TYPE = "body_view_type";


    private static final String KEY_VIDEO_ID = "video_id";
    private static final String KEY_VIDEO_CAPTION = "video_caption";
    private static final String KEY_VIDEO_LENGTH = "video_length";
    private static final String KEY_VIDEO_THUMB = "video_thumb";
    private static final String KEY_VIDEO_URL = "video_url";
    private static final String KEY_FREE_VERSION = "free_version";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_FILE_KEY = "file_key";
    private static final String KEY_IMAGE_OFFLINE= "image_offline";
    private static final String KEY_DOWNLOAD_FLAG= "download_flag";
    private static final String KEY_SERVER_PATH= "server_path";
    private static final String KEY_YOUTUBE_CODE= "youtube_code";
    private static final String KEY_DESCRIPTION= "description";
    private static final String KEY_VIDEO_VIEWTYPE = "viewtype";


    private static final String KEY_BOOKING_NUMBER = "BookingNumber";
    private static final String KEY_BOOKING_ID = "BookingID";
    private static final String KEY_CHECKIN_TIME = "CheckinTime";
    private static final String KEY_CHECKOUT_TIME = "CheckoutTime";
    private static final String KEY_VEHICLE_OWNER_CONTACT_NUMBER = "VehicleOwnerContactNumber";
    private static final String KEY_VEHICLE_TYPE = "VehicleType";
    private static final String KEY_VEHICLE_NUMBER = "VehicleNumber";
    private static final String KEY_PARKINGAREA_NAME = "ParkingAreaName";
    private static final String KEY_TOTAL_DURATION = "TotalDuration";
    private static final String KEY_TOTAL_PARKING_AMOUNT = "TotalParkingAmount";
    private static final String KEYTOTAL_PAYABLE_AMOUNT = "TotalPaybleAmount";
    private static final String KEYFINE_AMOUNT = "FineAmount";
    private static final String KEYOFFER_AMOUNT = "OfferAmount";
    private static final String KEYPAYMENT_MODE = "PaymentMode";
    private static final String KEYAGENCY_NAME = "AgencyName";
    private static final String KEYMESSAGE = "message";
    private static final String KEY_PASS_APPLIED = "pass_applied";
    private static final String KEY_PASS_ID = "pass_id";
    private static final String KEY_STARTTIME = "StartTime";
    private static final String KEY_ENDTIME = "EndTime";
    private static final String KEY_PRICE = "Price";
    private static final String KEY_OVERTIME_DURATION = "OverTimeDuration";
    private static final String KEY_OVERTIME_AMOUNT = "OverTimeAmount";

    private static final String KEYVEHICLETYPEID = "VehicleTypeID";
    private static final String KEY_VEHICLETYPE = "VehicleType";
    private static final String KEY_FIRSTCHARGE = "FirstCharge";
    private static final String KEY_HOURLYCHARGE = "HourlyCharge";
    private static final String KEY_MINDURATION = "MinDuration";
    private static final String KEY_RECURSIVEDURATION = "RecursiveDuration";





    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    public static DatabaseHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_Bill_TABLE = "CREATE TABLE " + TABLE_BILL_AMOUNT + "("
                +  KEY_BOOKING_NUMBER + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_CHECKOUT_TIME + " TEXT,"
                +  KEY_VEHICLE_OWNER_CONTACT_NUMBER + " TEXT,"
                +  KEY_VEHICLE_TYPE + " TEXT,"
                +  KEY_VEHICLE_NUMBER + " TEXT,"
                +  KEY_PARKINGAREA_NAME + " TEXT,"
                +  KEY_TOTAL_DURATION + " TEXT,"
                +  KEY_TOTAL_PARKING_AMOUNT + " TEXT,"
                +  KEYTOTAL_PAYABLE_AMOUNT + " TEXT,"
                +  KEYOFFER_AMOUNT + " TEXT,"
                +  KEYFINE_AMOUNT + " TEXT,"
                +  KEYPAYMENT_MODE + " TEXT,"
                +  KEYAGENCY_NAME + " TEXT,"
                +  KEYMESSAGE + " TEXT"+ ")";

        String CREATE_VEHICLECHECKOUT_TABLE = "CREATE TABLE " + TABLE_VEHICLE_CHECKOUT + "("
                +  KEY_BOOKING_NUMBER + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_CHECKOUT_TIME + " TEXT,"
                +  KEY_VEHICLE_OWNER_CONTACT_NUMBER + " TEXT,"
                +  KEY_VEHICLE_TYPE + " TEXT,"
                +  KEY_VEHICLE_NUMBER + " TEXT,"
                +  KEY_PARKINGAREA_NAME + " TEXT,"
                +  KEY_TOTAL_DURATION + " TEXT,"
                +  KEY_TOTAL_PARKING_AMOUNT + " TEXT,"
                +  KEYTOTAL_PAYABLE_AMOUNT + " TEXT,"
                +  KEYOFFER_AMOUNT + " TEXT,"
                +  KEYFINE_AMOUNT + " TEXT,"
                +  KEYPAYMENT_MODE + " TEXT,"
                +  KEYAGENCY_NAME + " TEXT,"
                +  KEY_OVERTIME_DURATION + " TEXT,"
                +  KEY_OVERTIME_AMOUNT + " TEXT,"
                +  KEYMESSAGE + " TEXT,"
                +  KEY_ADVANCE_BOOKING_ID + " TEXT"+ ")";


        String CREATE_CHECKIN_TABLE = "CREATE TABLE " + TABLE_VIHICLE_CHECKIN + "("
                +  KEY_VEHICLE_NUMBER + " TEXT,"
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_VEHICLE_TYPE + " TEXT,"
                +  KEY_MOBILE + " TEXT,"
                +  KEY_PASS_APPLIED + " TEXT,"
                +  KEY_PASS_ID + " TEXT,"
                +  KEY_EXECUTION_TYPE + " TEXT"+ ")";

        String CREATE_PARKING_LIST_TABLE = "CREATE TABLE " + TABLE_TRAFFIC_PARKING_PRICE_LIST + "("
                +  KEYVEHICLETYPEID + " TEXT,"
                +  KEY_VEHICLETYPE + " TEXT,"
                +  KEY_FIRSTCHARGE + " TEXT,"
                +  KEY_HOURLYCHARGE + " TEXT,"
                +  KEY_MINDURATION + " TEXT,"
                +  KEY_RECURSIVEDURATION + " TEXT"+ ")";

        String CREATE_OFFLINE_CHECKIN_TABLE = "CREATE TABLE " + TABLE_OFFLINEVIHICLE_CHECKIN + "("
                +  KEY_VEHICLE_NUMBER + " TEXT,"
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_VEHICLE_TYPE + " TEXT,"
                +  KEY_MOBILE + " TEXT,"
                +  KEY_PASS_APPLIED + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_PASS_ID + " TEXT,"
                +  KEY_EXECUTION_TYPE + " TEXT,"
                +  KEY_ADVANCE_BOOKING_ID + " TEXT"+ ")";

        String CREATE_OFFLINE_CHECKIN_TABLE_BUT_NOT_SYNC = "CREATE TABLE " + TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC + "("
                +  KEY_VEHICLE_NUMBER + " TEXT,"
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_VEHICLE_TYPE + " TEXT,"
                +  KEY_MOBILE + " TEXT,"
                +  KEY_PASS_APPLIED + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_PASS_ID + " TEXT,"
                +  KEY_EXECUTION_TYPE + " TEXT,"
                +  KEY_ADVANCE_BOOKING_ID + " TEXT"+ ")";



        String CREATE_RAILWAY_CHECKIN_TABLE = "CREATE TABLE " + TABLE_RAILWAY_CHECKIN + "("
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_PASS_APPLIED + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_PASS_ID + " TEXT,"
                +  KEY_EXECUTION_TYPE + " TEXT"+ ")";

        String CREATE_FREE_PARKING_LIST = "CREATE TABLE " + TABLE_FREE_PARKING_LIST + "("
                +  KEY_STARTTIME + " TEXT,"
                +  KEY_ENDTIME + " TEXT,"
                +  KEY_PRICE + " TEXT"+ ")";

        String CREATE_RAILWAY_CHECKOUT_TABLE = "CREATE TABLE " + TABLE_RAILWAY_CHECKOUT + "("
                +  KEY_CHECKIN_TIME + " TEXT,"
                +  KEY_CHECKOUT_TIME + " TEXT,"
                +  KEY_PASS_APPLIED + " TEXT,"
                +  KEY_PASS_ID + " TEXT,"
                +  KEY_BOOKING_ID + " TEXT,"
                +  KEY_TOTAL_DURATION + " TEXT,"
                +  KEY_TOTAL_PARKING_AMOUNT + " TEXT,"
                +  KEYTOTAL_PAYABLE_AMOUNT + " TEXT,"
                +  KEYFINE_AMOUNT + " TEXT,"
                +  KEYOFFER_AMOUNT + " TEXT,"
                +  KEYPAYMENT_MODE + " TEXT,"
                +  KEYAGENCY_NAME + " TEXT,"
                +  KEY_EXECUTION_TYPE + " TEXT"+ ")";

        String CREATE_TABLE_PARKING_PRICE = "CREATE TABLE " + TABLE_PARKING_PRICE + "("
                +  KEY_PP_ID + " Integer,"
                +  KEY_PP_PARKING_AREA + " TEXT,"
                +  KEY_PP_POLICY + " TEXT,"
                +  KEY_PP_VEHICLE_TYPE + " TEXT,"
                +  KEY_PP_SHIFT_TYPE + " TEXT,"
                +  KEY_PP_HOUR + " TEXT,"
                +  KEY_PP_MIN + " TEXT,"
                +  KEY_PP_PRICE + " TEXT,"
                +  KEY_PP_LASTUPDATEON + " TEXT"+ ")";

        String CREATE_TABLE_OFFLINE_LIST_WITH_ONLINE = "CREATE TABLE " + TABLE_OFFLINE_ONLINE_LIST + "("
                +  KEY_MSTRUSERNAME + " TEXT,"
                +  KEY_PARKINGAREAID + " TEXT,"
                +  KEY_SLOTID + " TEXT,"
                +  KEY_SLOTNAME + " TEXT,"
                +  KEY_BOOKINGNO + " TEXT,"
                +  KEY_PAYMENTMODEID + " TEXT,"
                +  KEY_PAYMENTMODE+ " TEXT,"
                +  KEY_VEHICLEID + " TEXT,"
                +  KEY_MIUID  + " Integer,"
                +  KEY_MSTRVEHICLENO + " TEXT,"
                +  KEY_MIBOOKINGID + " Integer,"
                +  KEY_MSTROWNERNAME+ " TEXT,"
                +  KEY_MSTROWNERPHONE + " TEXT,"
                +  KEY_MSTRALTERPHONE+ " TEXT,"
                +  KEY_MSTRVEHICLETYPE+ " TEXT,"
                +  KEY_MSTRCHECKINTIME+ " TEXT,"
                +  KEY_VIEWTYPE + " TEXT"+ ")";

        db.execSQL(CREATE_Bill_TABLE);
        db.execSQL(CREATE_CHECKIN_TABLE);
        db.execSQL(CREATE_OFFLINE_CHECKIN_TABLE);
        db.execSQL(CREATE_VEHICLECHECKOUT_TABLE);
        db.execSQL(CREATE_RAILWAY_CHECKIN_TABLE);
        db.execSQL(CREATE_RAILWAY_CHECKOUT_TABLE);
        db.execSQL(CREATE_FREE_PARKING_LIST);
        db.execSQL(CREATE_PARKING_LIST_TABLE);
        db.execSQL(CREATE_TABLE_PARKING_PRICE);
        db.execSQL(CREATE_OFFLINE_CHECKIN_TABLE_BUT_NOT_SYNC);
        db.execSQL(CREATE_TABLE_OFFLINE_LIST_WITH_ONLINE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL_AMOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIHICLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINEVIHICLE_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE_CHECKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FREE_PARKING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAILWAY_CHECKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAILWAY_CHECKIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAFFIC_PARKING_PRICE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARKING_PRICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_ONLINE_LIST);
        // Create tables again
        onCreate(db);
    }

    /**
     * This method is for inserting the Favouritecar details
     *
     /** @param id
     * @param car_img
     * @param car_name
     * @param car_price_low
     * @param car_price_high
     * @param car_model
     * @param car_enginecapacity
     * @param car_enginehorsepower
     * @param car_milleage
     * @param car_milleage
     * @param status
     * @param type
     * @param favourite
     * @param viewtype
     */
    public void addFavourite(String id, String car_img, String car_name, String car_price_low,
                             String car_price_high, String car_model, String car_enginecapacity,
                             String car_enginehorsepower, String car_milleage, String status,
                             String release_date, String type, int favourite, int viewtype){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CARID,id);
        values.put(KEY_CARIMAGE,car_img);
        values.put(KEY_CARAME,car_name);
        values.put(KEY_CARPRICELOW,car_price_low);
        values.put(KEY_CARPRICEHIGH,car_price_high);
        values.put(KEY_CARMODEL,car_model);
        values.put(KEY_CARENGINECAPACITY,car_enginecapacity);
        values.put(KEY_CARHORSEPOWER,car_enginehorsepower);
        values.put(KEY_CARMILLEAGE,car_milleage);
        values.put(KEY_STATUS,status);
        values.put(KEY_RELEASEDATE,release_date);
        values.put(KEY_TYPE,type);
        values.put(KEY_FAVOURITE,favourite);
        values.put(KEY_VIEWTYPE,viewtype);


        // Inserting Row
        db.insert(TABLE_CHECK_FAV, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    /**
     * This method is for inserting the user details
     *
     * @param user_id
     * @param name
     * @param email
     * @param mobile
     * @param image
     * @param promo
     */
    public void addUser(String user_id, String name, String email, String mobile,
                        String image,String promo){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_UID,user_id);
        values.put(KEY_NAME,name);
        values.put(KEY_EMAIL,email);
        values.put(KEY_MOBILE,mobile);
        values.put(KEY_IMAGE,image);
        values.put(KEY_PROMO,promo);

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    /**
     * This method is for inserting the user details
     *
     * @param vehicleCheckInBean

     */
    public void addVehicleCheckIn(VehicleCheckInBean vehicleCheckInBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_VEHICLE_NUMBER,vehicleCheckInBean.getVehicle_number());
        values.put(KEY_CHECKIN_TIME,vehicleCheckInBean.getCheckintime());
        values.put(KEY_VEHICLE_TYPE,vehicleCheckInBean.getVehicletype());
        values.put(KEY_MOBILE,vehicleCheckInBean.getMobilenum());
        values.put(KEY_PASS_APPLIED,vehicleCheckInBean.getPass_applied());
        values.put(KEY_PASS_ID,vehicleCheckInBean.getPass_id());
        values.put(KEY_EXECUTION_TYPE,vehicleCheckInBean.getExetype());


        // Inserting Row
        db.insert(TABLE_VIHICLE_CHECKIN, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    /**
     * This method is for inserting the user details
     *
     * @param vehicleTypePrice

     */
    public void addVehicleTypePrice(VehicleTypePrice vehicleTypePrice){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEYVEHICLETYPEID,vehicleTypePrice.getVehicleTypeID());
        values.put(KEY_VEHICLETYPE,vehicleTypePrice.getVehicleType());
        values.put(KEY_FIRSTCHARGE,vehicleTypePrice.getFirstCharge());
        values.put(KEY_HOURLYCHARGE,vehicleTypePrice.getHourlyCharge());
        values.put(KEY_MINDURATION,vehicleTypePrice.getMinDuration());
        values.put(KEY_RECURSIVEDURATION,vehicleTypePrice.getRecursiveDuration());


        // Inserting Row
        db.insert(TABLE_TRAFFIC_PARKING_PRICE_LIST, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public SQLiteDatabase m_db = null;
    public void iniDB(){
        if (null==m_db){
            m_db = this.getWritableDatabase();
        }
    }

    public void releaseDB(){
        if (m_db !=null){
            m_db.close();
        }
    }
    /**
     * This method is for inserting the user details
     *
     * @param parkingPrice

     */
  public void addParkingPrice(ParkingPrice parkingPrice){


        ContentValues values = new ContentValues();

        values.put(KEY_PP_ID,parkingPrice.getPp_id());
        values.put(KEY_PP_PARKING_AREA,parkingPrice.getPp_parking_area());
        values.put(KEY_PP_POLICY,parkingPrice.getPp_policy());
        values.put(KEY_PP_VEHICLE_TYPE,parkingPrice.getPp_vehicle_type());
        values.put(KEY_PP_SHIFT_TYPE,parkingPrice.getPp_shift_type());
        values.put(KEY_PP_HOUR,parkingPrice.getPp_hour());
        values.put(KEY_PP_MIN,parkingPrice.getPp_min());
        values.put(KEY_PP_PRICE,parkingPrice.getPp_price());
        values.put(KEY_PP_LASTUPDATEON,parkingPrice.getPp_lastupdateon());


        // Inserting Row
        m_db.insert(TABLE_PARKING_PRICE, null, values);


    }

    /**
     * This method is for inserting the user details
     *
     * @param railwayCheckInBean

     */
    public void addRailwayCheckIn(RailwayCheckInBean railwayCheckInBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CHECKIN_TIME,railwayCheckInBean.getCheckintime());
        values.put(KEY_PASS_APPLIED,railwayCheckInBean.getPass_applied());
        values.put(KEY_BOOKING_ID,railwayCheckInBean.getBookingID());
        values.put(KEY_PASS_ID,railwayCheckInBean.getPass_id());
        values.put(KEY_EXECUTION_TYPE,railwayCheckInBean.getExetype());


        // Inserting Row
        db.insert(TABLE_RAILWAY_CHECKIN, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    /**
     * This method is for inserting the user details
     *
     * @param freeParkingPriceType

     */
    public void addFreeParkingPrice(FreeParkingPriceType freeParkingPriceType){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STARTTIME,freeParkingPriceType.getStartTime());
        values.put(KEY_ENDTIME,freeParkingPriceType.getEndTime());
        values.put(KEY_PRICE,freeParkingPriceType.getPrice());


        // Inserting Row
        db.insert(TABLE_FREE_PARKING_LIST, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    /**
     * This method is for inserting the user details
     *
     * @param railwayCheckOutBean

     */
    public void addRailwayCheckOut(RailwayCheckOutBean railwayCheckOutBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CHECKIN_TIME,railwayCheckOutBean.getCheckintime());
        values.put(KEY_CHECKOUT_TIME,railwayCheckOutBean.getCheckouttime());
        values.put(KEY_PASS_APPLIED,railwayCheckOutBean.getPass_applied());
        values.put(KEY_PASS_ID,railwayCheckOutBean.getPass_id());
        values.put(KEY_BOOKING_ID,railwayCheckOutBean.getBookingID());
        values.put(KEY_TOTAL_DURATION,railwayCheckOutBean.getTotalDuration());
        values.put(KEY_TOTAL_PARKING_AMOUNT,railwayCheckOutBean.getTotalParkingAmount());
        values.put(KEYTOTAL_PAYABLE_AMOUNT,railwayCheckOutBean.getTotalPaybleAmount());
        values.put(KEYFINE_AMOUNT,railwayCheckOutBean.getFineAmount());
        values.put(KEYOFFER_AMOUNT,railwayCheckOutBean.getOfferAmount());
        values.put(KEYPAYMENT_MODE,railwayCheckOutBean.getPaymentMode());
        values.put(KEYAGENCY_NAME,railwayCheckOutBean.getAgencyName());
        values.put(KEY_EXECUTION_TYPE,railwayCheckOutBean.getExetype());


        // Inserting Row
        db.insert(TABLE_RAILWAY_CHECKOUT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    /**
     * This method is for inserting the Offline checkin vehicles details
     *
     * @param vehicleCheckInBean

     */
    public void addofflineVehicleCheckIn(VehicleCheckInBean vehicleCheckInBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_VEHICLE_NUMBER,vehicleCheckInBean.getVehicle_number());
        values.put(KEY_CHECKIN_TIME,vehicleCheckInBean.getCheckintime());
        values.put(KEY_VEHICLE_TYPE,vehicleCheckInBean.getVehicletype());
        values.put(KEY_MOBILE,vehicleCheckInBean.getMobilenum());
        values.put(KEY_PASS_APPLIED,vehicleCheckInBean.getPass_applied());
        values.put(KEY_BOOKING_ID,vehicleCheckInBean.getBookingid());
        values.put(KEY_PASS_ID,vehicleCheckInBean.getPass_id());
        values.put(KEY_EXECUTION_TYPE,vehicleCheckInBean.getExetype());
        values.put(KEY_ADVANCE_BOOKING_ID,vehicleCheckInBean.getAdvanceBookingID());


        // Inserting Row
        db.insert(TABLE_OFFLINEVIHICLE_CHECKIN, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    public void addofflineVehicleCheckInNotSync(VehicleCheckInBean vehicleCheckInBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_VEHICLE_NUMBER,vehicleCheckInBean.getVehicle_number());
        values.put(KEY_CHECKIN_TIME,vehicleCheckInBean.getCheckintime());
        values.put(KEY_VEHICLE_TYPE,vehicleCheckInBean.getVehicletype());
        values.put(KEY_MOBILE,vehicleCheckInBean.getMobilenum());
        values.put(KEY_PASS_APPLIED,vehicleCheckInBean.getPass_applied());
        values.put(KEY_BOOKING_ID,vehicleCheckInBean.getBookingid());
        values.put(KEY_PASS_ID,vehicleCheckInBean.getPass_id());
        values.put(KEY_EXECUTION_TYPE,vehicleCheckInBean.getExetype());
        values.put(KEY_ADVANCE_BOOKING_ID,vehicleCheckInBean.getAdvanceBookingID());


        // Inserting Row
        db.insert(TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    public void addAllCheckinVehicle(DataObject dataObject){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MSTRUSERNAME,dataObject.getM_strUserName());
        values.put(KEY_PARKINGAREAID,dataObject.getParking_area_id());
        values.put(KEY_SLOTID,dataObject.getSlot_id());
        values.put(KEY_SLOTNAME,dataObject.getSlot_name());
        values.put(KEY_BOOKINGNO,dataObject.getBooking_no());
        values.put(KEY_PAYMENTMODEID,dataObject.getPayment_mode_id());
        values.put(KEY_PAYMENTMODE,dataObject.getPayment_mode());
        values.put(KEY_VEHICLEID,dataObject.getVehicle_id());
        values.put(KEY_MIUID,dataObject.getM_iUID());
        values.put(KEY_MSTRVEHICLENO,dataObject.getM_strVehicleNo());
        values.put(KEY_MIBOOKINGID,dataObject.getM_iBookingID());
        values.put(KEY_MSTROWNERNAME,dataObject.getM_strOwnerName());
        values.put(KEY_MSTROWNERPHONE,dataObject.getM_strOwnerPhone());
        values.put(KEY_MSTRVEHICLETYPE,dataObject.getM_strVehicleType());
        values.put(KEY_MSTRCHECKINTIME,dataObject.getM_strCheckInTime());
        values.put(KEY_VIEWTYPE,dataObject.getViewtype());



        // Inserting Row
        db.insert(TABLE_OFFLINE_ONLINE_LIST, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    public void clearDataFromonlineOfflineSync() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_OFFLINE_ONLINE_LIST, null, null);
        db.close();
    }

    public ArrayList<VehicleCheckInBean> getofflinevehiclecheckinNotSync(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC, null);

        ArrayList<VehicleCheckInBean> vehicleCheckInBeanArrayList=new ArrayList<VehicleCheckInBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        VehicleCheckInBean vehicleCheckInBean = new VehicleCheckInBean();
                        vehicleCheckInBean.setVehicle_number(cursor.getString(0));
                        vehicleCheckInBean.setCheckintime(cursor.getString(1));
                        vehicleCheckInBean.setVehicletype(cursor.getString(2));
                        vehicleCheckInBean.setMobilenum(cursor.getString(3));
                        vehicleCheckInBean.setPass_applied(cursor.getString(4));
                        vehicleCheckInBean.setBookingid(cursor.getString(5));
                        vehicleCheckInBean.setPass_id(cursor.getString(6));
                        vehicleCheckInBean.setExetype(cursor.getString(7));
                        vehicleCheckInBean.setAdvanceBookingID(cursor.getString(8));

                        vehicleCheckInBeanArrayList.add(vehicleCheckInBean);

                        cursor.moveToNext();
                    }
                }
                Collections.reverse(vehicleCheckInBeanArrayList);// this will show the newly checked in vehicle first

                dataModel.offlinevehicleCheckInBeanArrayListNotSync.removeAll(dataModel.offlinevehicleCheckInBeanArrayListNotSync);

                dataModel.offlinevehicleCheckInBeanArrayListNotSync.addAll(vehicleCheckInBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.offlinevehicleCheckInBeanArrayListNotSync;
    }
    public ArrayList<DataObject> getAllofflineandOnlineVehilceList(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_OFFLINE_ONLINE_LIST, null);

        ArrayList<DataObject> vehicleCheckInBeanArrayList=new ArrayList<DataObject>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        DataObject dataObject = new DataObject();
                        dataObject.setM_strUserName(cursor.getString(0));
                        dataObject.setParking_area_id(cursor.getString(1));
                        dataObject.setSlot_id(cursor.getString(2));
                        dataObject.setSlot_name(cursor.getString(3));
                        dataObject.setBooking_no(cursor.getString(4));
                        dataObject.setPayment_mode_id(cursor.getString(5));
                        dataObject.setPayment_mode(cursor.getString(6));
                        dataObject.setVehicle_id(cursor.getString(7));
                        dataObject.setM_iUID(Integer.parseInt(cursor.getString(8)));
                        dataObject.setM_strVehicleNo(cursor.getString(9));
                        dataObject.setM_iBookingID(Integer.parseInt(cursor.getString(10)));
                        dataObject.setM_strOwnerName(cursor.getString(11));
                        dataObject.setM_strOwnerPhone(cursor.getString(12));
                        dataObject.setM_strAlterPhone(cursor.getString(13));
                        dataObject.setM_strVehicleType(cursor.getString(14));
                        dataObject.setM_strCheckInTime(cursor.getString(15));
                        dataObject.setViewtype(Integer.parseInt(cursor.getString(20)));


                        vehicleCheckInBeanArrayList.add(dataObject);
                        cursor.moveToNext();
                    }
                }
                Collections.reverse(vehicleCheckInBeanArrayList);
                dataModel.dataObjectArrayList.removeAll(dataModel.dataObjectArrayList);
                dataModel.dataObjectArrayList.addAll(vehicleCheckInBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.dataObjectArrayList;
    }
    /**
     * This method is for inserting the Bill details
     *
     * @param bookingBillBean
     */
    public void addbookingbill(BookingBillBean bookingBillBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_BOOKING_NUMBER,bookingBillBean.getBookingno());
        values.put(KEY_BOOKING_ID,bookingBillBean.getBookingID());
        values.put(KEY_CHECKIN_TIME,bookingBillBean.getCheckintime());
        values.put(KEY_CHECKOUT_TIME,bookingBillBean.getCheckouttime());
        values.put(KEY_VEHICLE_OWNER_CONTACT_NUMBER,bookingBillBean.getOwnerphoneno());
        values.put(KEY_VEHICLE_TYPE,bookingBillBean.getVehicleType());
        values.put(KEY_VEHICLE_NUMBER,bookingBillBean.getVechile_no());
        values.put(KEY_PARKINGAREA_NAME,bookingBillBean.getParkingAreaName());
        values.put(KEY_TOTAL_DURATION,bookingBillBean.getTotalDuration());
        values.put(KEY_TOTAL_PARKING_AMOUNT,bookingBillBean.getTotalParkingAmount());
        values.put(KEYTOTAL_PAYABLE_AMOUNT,bookingBillBean.getTotalPaybleAmount());
        values.put(KEYFINE_AMOUNT,bookingBillBean.getFineAmount());
        values.put(KEYOFFER_AMOUNT,bookingBillBean.getOfferAmount());
        values.put(KEYPAYMENT_MODE,bookingBillBean.getPaymentMode());
        values.put(KEYAGENCY_NAME,bookingBillBean.getAgencyName());
        values.put(KEYMESSAGE,bookingBillBean.getMessage());
        Log.e("payment",bookingBillBean.getPaymentMode());
        Log.e("bookingNo",bookingBillBean.getBookingno());
        // Inserting Row
        db.insert(TABLE_BILL_AMOUNT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }



    /**
     * This method is for inserting the check out type details
     *
     * @param bookingBillBean
     */
    public void addvehiclecheckout(BookingBillBean bookingBillBean){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_BOOKING_NUMBER,bookingBillBean.getBookingno());
        values.put(KEY_BOOKING_ID,bookingBillBean.getBookingID());
        values.put(KEY_CHECKIN_TIME,bookingBillBean.getCheckintime());
        values.put(KEY_CHECKOUT_TIME,bookingBillBean.getCheckouttime());
        values.put(KEY_VEHICLE_OWNER_CONTACT_NUMBER,bookingBillBean.getOwnerphoneno());
        values.put(KEY_VEHICLE_TYPE,bookingBillBean.getVehicleType());
        values.put(KEY_VEHICLE_NUMBER,bookingBillBean.getVechile_no());
        values.put(KEY_PARKINGAREA_NAME,bookingBillBean.getParkingAreaName());
        values.put(KEY_TOTAL_DURATION,bookingBillBean.getTotalDuration());
        values.put(KEY_TOTAL_PARKING_AMOUNT,bookingBillBean.getTotalParkingAmount());
        values.put(KEYTOTAL_PAYABLE_AMOUNT,bookingBillBean.getTotalPaybleAmount());
        values.put(KEYFINE_AMOUNT,bookingBillBean.getFineAmount());
        values.put(KEYOFFER_AMOUNT,bookingBillBean.getOfferAmount());
        values.put(KEYPAYMENT_MODE,bookingBillBean.getPaymentMode());
        values.put(KEYAGENCY_NAME,bookingBillBean.getAgencyName());
        values.put(KEY_OVERTIME_DURATION,bookingBillBean.getOverTimeDuration());
        values.put(KEY_OVERTIME_AMOUNT,bookingBillBean.getOverTimeAmount());
        values.put(KEYMESSAGE,bookingBillBean.getMessage());
        values.put(KEY_ADVANCE_BOOKING_ID,bookingBillBean.getAdvbookingid());
        Log.e("payment",bookingBillBean.getPaymentMode());
        Log.e("bookingNo",bookingBillBean.getBookingno());
        // Inserting Row
        db.insert(TABLE_VEHICLE_CHECKOUT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    /**
     * This method is for getting the user details
     *
     * @param bookingid
     */
    public void getbill(String bookingid){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BILL_AMOUNT, new String[] { KEY_BOOKING_NUMBER,KEY_BOOKING_ID,
                        KEY_CHECKIN_TIME,
                        KEY_CHECKOUT_TIME , KEY_VEHICLE_OWNER_CONTACT_NUMBER,
                        KEY_VEHICLE_TYPE , KEY_VEHICLE_NUMBER,
                        KEY_PARKINGAREA_NAME , KEY_TOTAL_DURATION,
                        KEY_TOTAL_PARKING_AMOUNT,
                        KEYTOTAL_PAYABLE_AMOUNT , KEYFINE_AMOUNT,
                        KEYOFFER_AMOUNT ,
                        KEYPAYMENT_MODE,KEYAGENCY_NAME, KEYMESSAGE}, KEY_BOOKING_ID + "=?",
                new String[] { bookingid }, null, null, null, null);


        try {
            if (cursor != null)
                cursor.moveToFirst();

            BookingBillBean bookingBillBean = new BookingBillBean();
            bookingBillBean.setBookingno(cursor.getString(0));
            bookingBillBean.setBookingID(cursor.getString(1));
            bookingBillBean.setCheckintime(cursor.getString(2));
            bookingBillBean.setCheckouttime(cursor.getString(3));
            bookingBillBean.setOwnerphoneno(cursor.getString(4));
            bookingBillBean.setVehicleType(cursor.getString(5));
            bookingBillBean.setVechile_no(cursor.getString(6));
            bookingBillBean.setParkingAreaName(cursor.getString(7));
            bookingBillBean.setTotalDuration(cursor.getString(8));
            bookingBillBean.setTotalParkingAmount(cursor.getString(9));
            bookingBillBean.setTotalPaybleAmount(cursor.getString(10));
            bookingBillBean.setFineAmount(cursor.getString(11));
            bookingBillBean.setOfferAmount(cursor.getString(12));
            bookingBillBean.setPaymentMode(cursor.getString(13));
            bookingBillBean.setAgencyName(cursor.getString(14));
            bookingBillBean.setMessage(cursor.getString(15));
            Log.e("payment",cursor.getString(14));
            Log.e("bookingNO",cursor.getString(2));


            dataModel.bookingBillBean = null;
            dataModel.bookingBillBean=bookingBillBean;

        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        db.close();
    }

    /**
     * This method is for getting the vehicle checkin car list
     *
     */
    public ArrayList<VehicleCheckInBean> getvehiclecheckin(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_VIHICLE_CHECKIN, null);

        ArrayList<VehicleCheckInBean> vehicleCheckInBeanArrayList=new ArrayList<VehicleCheckInBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        VehicleCheckInBean vehicleCheckInBean = new VehicleCheckInBean();
                        vehicleCheckInBean.setVehicle_number(cursor.getString(0));
                        vehicleCheckInBean.setCheckintime(cursor.getString(1));
                        vehicleCheckInBean.setVehicletype(cursor.getString(2));
                        vehicleCheckInBean.setMobilenum(cursor.getString(3));
                        vehicleCheckInBean.setPass_applied(cursor.getString(4));
                        vehicleCheckInBean.setPass_id(cursor.getString(5));
                        vehicleCheckInBean.setExetype(cursor.getString(6));

                        vehicleCheckInBeanArrayList.add(vehicleCheckInBean);
                        cursor.moveToNext();
                    }
                }
                dataModel.vehicleCheckInBeanArrayList.removeAll(dataModel.vehicleCheckInBeanArrayList);
                dataModel.vehicleCheckInBeanArrayList.addAll(vehicleCheckInBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.vehicleCheckInBeanArrayList;
    }


    /**
     * This method is for getting the vehicle checkin car list
     *
     */
    public ArrayList<VehicleTypePrice> getvehiclepricelist(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_TRAFFIC_PARKING_PRICE_LIST, null);

        ArrayList<VehicleTypePrice> vehicleTypePriceArrayList=new ArrayList<VehicleTypePrice>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        VehicleTypePrice vehicleTypePrice = new VehicleTypePrice();
                        vehicleTypePrice.setVehicleTypeID(cursor.getString(0));
                        vehicleTypePrice.setVehicleType(cursor.getString(1));
                        vehicleTypePrice.setFirstCharge(cursor.getString(2));
                        vehicleTypePrice.setHourlyCharge(cursor.getString(3));
                        vehicleTypePrice.setMinDuration(cursor.getString(4));
                        vehicleTypePrice.setRecursiveDuration(cursor.getString(5));

                        vehicleTypePriceArrayList.add(vehicleTypePrice);
                        cursor.moveToNext();
                    }
                }
                dataModel.vehicleTypePriceArrayList.removeAll(dataModel.vehicleTypePriceArrayList);
                dataModel.vehicleTypePriceArrayList.addAll(vehicleTypePriceArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.vehicleTypePriceArrayList;
    }


    //for Parking Price list
    public ParkingPrice getvehicleParkingPrice(Integer vehicleType,Integer total_hour,Integer total_min){
        SQLiteDatabase db = this.getReadableDatabase();
        ParkingPrice  parkingPrice = new ParkingPrice();
        Double ceiling = ceil(total_min.doubleValue()/5);
        Integer minuteVal = ceiling.intValue() * 5;

        Log.e("vehicleType", String.valueOf(vehicleType));
        Log.e("total_hour", String.valueOf(total_hour));
        Log.e("total_min", String.valueOf(total_min));
        Log.e("ceiling", String.valueOf(ceiling));
        Log.e("minuteVal", String.valueOf(minuteVal));


        Cursor cursor1 = db.rawQuery("select * from " + TABLE_PARKING_PRICE,null);
        try {
            if (cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    while (!cursor1.isAfterLast()) {
                        Log.e("parking price", String.valueOf(cursor1.getInt(0)) + "|" +(cursor1.getString(1)) + "|" +(cursor1.getString(2)) + "|" +(cursor1.getString(3)) + "|" +(cursor1.getString(4)) + "|" +(cursor1.getString(5)) + "|" +(cursor1.getString(6)) + "|" +(cursor1.getString(8)));
                        cursor1.moveToNext();
                    }
                }
                //dataModel.parkingPriceArrayList.removeAll(dataModel.parkingPriceArrayList);
                // dataModel.parkingPriceArrayList.addAll(parkingPriceArrayList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    if (minuteVal>55){
        minuteVal=0;
        total_hour++;
    }

     //   Cursor cursor = db.rawQuery("select pp_price from " + TABLE_PARKING_PRICE +" WHERE "+KEY_PP_VEHICLE_TYPE+"='"+1+"' AND "+KEY_PP_HOUR+"='"+0+"' AND "+KEY_PP_MIN+"='"+15+"'",null);
        Cursor cursor = db.rawQuery("select pp_price from " + TABLE_PARKING_PRICE +" WHERE "+KEY_PP_VEHICLE_TYPE+"='"+vehicleType+"' AND "+KEY_PP_HOUR+"='"+total_hour+"' AND "+KEY_PP_MIN+"='"+minuteVal+"'",null);

        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {

                        parkingPrice.setPp_price(cursor.getString(0));
                        Log.e("parking price",cursor.getString(0));
                        cursor.moveToNext();
                    }
                }
               //dataModel.parkingPriceArrayList.removeAll(dataModel.parkingPriceArrayList);
               // dataModel.parkingPriceArrayList.addAll(parkingPriceArrayList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return parkingPrice;
    }

    /**
     * This method is for getting the railway checkin car list
     *
     */
    public ArrayList<RailwayCheckInBean> getrailwaycheckin(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_RAILWAY_CHECKIN, null);

        ArrayList<RailwayCheckInBean> railwayCheckInBeanArrayList=new ArrayList<RailwayCheckInBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        RailwayCheckInBean railwayCheckInBean = new RailwayCheckInBean();
                        railwayCheckInBean.setCheckintime(cursor.getString(0));
                        railwayCheckInBean.setPass_applied(cursor.getString(1));
                        railwayCheckInBean.setBookingID(cursor.getString(2));
                        railwayCheckInBean.setPass_id(cursor.getString(3));
                        railwayCheckInBean.setExetype(cursor.getString(4));

                        railwayCheckInBeanArrayList.add(railwayCheckInBean);
                        cursor.moveToNext();
                    }
                }
                dataModel.railwayCheckInBeansArrayList.removeAll(dataModel.railwayCheckInBeansArrayList);
                dataModel.railwayCheckInBeansArrayList.addAll(railwayCheckInBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.railwayCheckInBeansArrayList;
    }


    /**
     * This method is for getting the railway checkin car list
     *
     */
    public ArrayList<FreeParkingPriceType> getfreeparkingpricelist(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_FREE_PARKING_LIST, null);

        ArrayList<FreeParkingPriceType> freeParkingPriceTypeArrayList=new ArrayList<FreeParkingPriceType>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        FreeParkingPriceType freeParkingPriceType = new FreeParkingPriceType();
                        freeParkingPriceType.setStartTime(cursor.getString(0));
                        freeParkingPriceType.setEndTime(cursor.getString(1));
                        freeParkingPriceType.setPrice(cursor.getString(2));

                        freeParkingPriceTypeArrayList.add(freeParkingPriceType);
                        cursor.moveToNext();
                    }
                }
                dataModel.freeParkingPriceTypeArrayList.removeAll(dataModel.freeParkingPriceTypeArrayList);
                dataModel.freeParkingPriceTypeArrayList.addAll(freeParkingPriceTypeArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.freeParkingPriceTypeArrayList;
    }
    /**
     * This method is for getting the railway checkin car list
     *
     */
    public ArrayList<RailwayCheckOutBean> getrailwaycheckout(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_RAILWAY_CHECKOUT, null);

        ArrayList<RailwayCheckOutBean> railwayCheckOutBeanArrayList=new ArrayList<RailwayCheckOutBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        RailwayCheckOutBean railwayCheckOutBean = new RailwayCheckOutBean();
                        railwayCheckOutBean.setCheckintime(cursor.getString(0));
                        railwayCheckOutBean.setCheckouttime(cursor.getString(1));
                        railwayCheckOutBean.setPass_applied(cursor.getString(2));
                        railwayCheckOutBean.setPass_id(cursor.getString(3));
                        railwayCheckOutBean.setBookingID(cursor.getString(4));
                        railwayCheckOutBean.setTotalDuration(cursor.getString(5));
                        railwayCheckOutBean.setTotalParkingAmount(cursor.getString(6));
                        railwayCheckOutBean.setTotalPaybleAmount(cursor.getString(7));
                        railwayCheckOutBean.setFineAmount(cursor.getString(8));
                        railwayCheckOutBean.setOfferAmount(cursor.getString(9));
                        railwayCheckOutBean.setPaymentMode(cursor.getString(10));
                        railwayCheckOutBean.setAgencyName(cursor.getString(11));
                        railwayCheckOutBean.setExetype(cursor.getString(12));


                        railwayCheckOutBeanArrayList.add(railwayCheckOutBean);
                        cursor.moveToNext();
                    }
                }
                dataModel.railwayCheckOutBeansArrayList.removeAll(dataModel.railwayCheckOutBeansArrayList);
                dataModel.railwayCheckOutBeansArrayList.addAll(railwayCheckOutBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.railwayCheckOutBeansArrayList;
    }


    /**
     * This method is for getting the vehicle checkout car list
     *
     */
    public ArrayList<BookingBillBean> getvehiclecheckout(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_VEHICLE_CHECKOUT, null);

        ArrayList<BookingBillBean> bookingBillBeansArrayList=new ArrayList<BookingBillBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        BookingBillBean bookingBillBean = new BookingBillBean();
                        bookingBillBean.setBookingno(cursor.getString(0));
                        bookingBillBean.setBookingID(cursor.getString(1));
                        bookingBillBean.setCheckintime(cursor.getString(2));
                        bookingBillBean.setCheckouttime(cursor.getString(3));
                        bookingBillBean.setOwnerphoneno(cursor.getString(4));
                        bookingBillBean.setVehicleType(cursor.getString(5));
                        bookingBillBean.setVechile_no(cursor.getString(6));
                        bookingBillBean.setParkingAreaName(cursor.getString(7));
                        bookingBillBean.setTotalDuration(cursor.getString(8));
                        bookingBillBean.setTotalParkingAmount(cursor.getString(9));
                        bookingBillBean.setTotalPaybleAmount(cursor.getString(10));
                        bookingBillBean.setFineAmount(cursor.getString(11));
                        bookingBillBean.setOfferAmount(cursor.getString(12));
                        bookingBillBean.setPaymentMode(cursor.getString(13));
                        bookingBillBean.setAgencyName(cursor.getString(14));
                        bookingBillBean.setOverTimeDuration(cursor.getString(15));
                        bookingBillBean.setOverTimeAmount(cursor.getString(16));
                        bookingBillBean.setMessage(cursor.getString(17));
                        bookingBillBean.setAdvbookingid(cursor.getString(18));

                        bookingBillBeansArrayList.add(bookingBillBean);
                        cursor.moveToNext();
                    }
                }
                dataModel.offlinebookingBillBeansnArrayList.removeAll(dataModel.offlinebookingBillBeansnArrayList);
                dataModel.offlinebookingBillBeansnArrayList.addAll(bookingBillBeansArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.offlinebookingBillBeansnArrayList;
    }


    /**
     * This method is for getting the vehicle checkin car list
     *
     */
    public ArrayList<VehicleCheckInBean> getofflinevehiclecheckin(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_OFFLINEVIHICLE_CHECKIN, null);

        ArrayList<VehicleCheckInBean> vehicleCheckInBeanArrayList=new ArrayList<VehicleCheckInBean>();
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        VehicleCheckInBean vehicleCheckInBean = new VehicleCheckInBean();
                        vehicleCheckInBean.setVehicle_number(cursor.getString(0));
                        vehicleCheckInBean.setCheckintime(cursor.getString(1));
                        vehicleCheckInBean.setVehicletype(cursor.getString(2));
                        vehicleCheckInBean.setMobilenum(cursor.getString(3));
                        vehicleCheckInBean.setPass_applied(cursor.getString(4));
                        vehicleCheckInBean.setBookingid(cursor.getString(5));
                        vehicleCheckInBean.setPass_id(cursor.getString(6));
                        vehicleCheckInBean.setExetype(cursor.getString(7));
                        vehicleCheckInBean.setAdvanceBookingID(cursor.getString(8));

                        vehicleCheckInBeanArrayList.add(vehicleCheckInBean);
                        cursor.moveToNext();
                    }
                }
                dataModel.offlinevehicleCheckInBeanArrayList.removeAll(dataModel.offlinevehicleCheckInBeanArrayList);
                dataModel.offlinevehicleCheckInBeanArrayList.addAll(vehicleCheckInBeanArrayList);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        db.close();
        return dataModel.offlinevehicleCheckInBeanArrayList;
    }


    /*********
     * update Vehicle Check in details
     * */
    public int updatevehiclecheckin(String exetype, String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXECUTION_TYPE, exetype);

        // updating row
        return db.update(TABLE_VIHICLE_CHECKIN, values, KEY_VEHICLE_NUMBER + " = ?",
                new String[] { String.valueOf(vehicle_number) });
    }

    /*********
     * update railway Check in details
     * */
    public int updaterailwayheckin(String exetype, String booking_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXECUTION_TYPE, exetype);

        // updating row
        return db.update(TABLE_RAILWAY_CHECKIN, values, KEY_BOOKING_ID + " = ?",
                new String[] { String.valueOf(booking_id) });
    }

    /*********
     * update railway Check out details
     * */
    public int updaterailwayheckout(String exetype, String booking_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXECUTION_TYPE, exetype);

        // updating row
        return db.update(TABLE_RAILWAY_CHECKOUT, values, KEY_BOOKING_ID + " = ?",
                new String[] { String.valueOf(booking_id) });
    }

    /*********
     * update New Vehicle Check in details
     * */
    public int updateofflinevehiclecheckin(String exetype, String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXECUTION_TYPE, exetype);

        // updating row
        return db.update(TABLE_OFFLINEVIHICLE_CHECKIN, values, KEY_VEHICLE_NUMBER + " = ?",
                new String[] { String.valueOf(vehicle_number) });
    }

    /*********
     * update New Vehicle Check out details
     * */
    public int updateofflinevehiclecheckout(String exetype, String bookingid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEYMESSAGE, exetype);

        // updating row
        return db.update(TABLE_VEHICLE_CHECKOUT, values, KEY_BOOKING_ID + " = ?",
                new String[] { String.valueOf(bookingid) });
    }

    /*********
     * update New Vehicle Check out offline details
     * */
   public int updatevehiclecheckofflinecheckoutvechicle(String checkintime,String vehicletype,String vehiclenumber,String bookingid){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHECKIN_TIME,checkintime);
        values.put(KEY_VEHICLE_TYPE,vehicletype);
        values.put(KEY_VEHICLE_NUMBER,vehiclenumber);

        // updating row
        return db.update(TABLE_VEHICLE_CHECKOUT, values, KEY_BOOKING_ID + " = ?",
                new String[] { String.valueOf(bookingid) });
    }

    /*********
     * update New railway Vehicle Check out offline details
     * */
    public int updaterailwaycheckofflinecheckoutvechicle(String checkintime,String bookingid){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHECKIN_TIME,checkintime);

        // updating row
        return db.update(TABLE_RAILWAY_CHECKOUT, values, KEY_BOOKING_ID + " = ?",
                new String[] { String.valueOf(bookingid) });
    }

    /*********
     * update favourite car details
     * */
    public int updatecardetails(String id, String car_img, String car_name, String car_price_low,
                                String car_price_high, String car_model, String car_enginecapacity,
                                String car_enginehorsepower, String car_milleage, String status,
                                String release_date, String type, int favourite, int viewtype) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CARIMAGE,car_img);
        values.put(KEY_CARAME,car_name);
        values.put(KEY_CARPRICELOW,car_price_low);
        values.put(KEY_CARPRICEHIGH,car_price_high);
        values.put(KEY_CARMODEL,car_model);
        values.put(KEY_CARENGINECAPACITY,car_enginecapacity);
        values.put(KEY_CARHORSEPOWER,car_enginehorsepower);
        values.put(KEY_CARMILLEAGE,car_milleage);
        values.put(KEY_STATUS,status);
        values.put(KEY_RELEASEDATE,release_date);
        values.put(KEY_TYPE,type);
        values.put(KEY_FAVOURITE,favourite);
        values.put(KEY_VIEWTYPE,viewtype);

        // updating row
        return db.update(TABLE_CHECK_FAV, values, KEY_CARID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
    * delete address table completely
    *
    * @param bookingid
    *
    */
    public void deletebill(String bookingid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_BILL_AMOUNT+ " WHERE "+KEY_BOOKING_ID+"='"+bookingid+"'");
        db.close();

    }

    /*
   * delete address table completely
   *
   * @param bookingid
   *
   */
    public void deletevehiclecheckout(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_VEHICLE_CHECKOUT+ " WHERE "+KEYMESSAGE+"='"+exetype+"'");
        db.close();

    }

    /*
     * delete railway check in access table completely
     *
     * @param bookingid
     *
     */
    public void deleterailwaycheckin(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_RAILWAY_CHECKIN+ " WHERE "+KEY_EXECUTION_TYPE+"='"+exetype+"'");
        db.close();

    }

    /*
     * delete railway check out access table completely
     *
     * @param bookingid
     *
     */
    public void deleterailwaycheckout(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_RAILWAY_CHECKOUT+ " WHERE "+KEY_EXECUTION_TYPE+"='"+exetype+"'");
        db.close();

    }

    /*
   * delete address table completely
   *
   * @param bookingid
   *
   */
    public void deletevehiclecheckin(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_OFFLINEVIHICLE_CHECKIN+ " WHERE "+KEY_EXECUTION_TYPE+"='"+exetype+"'");
        db.close();

    }

    /*
   * delete address table completely
   *
   * @param bookingid
   *
   */
    public void deletecheckin(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_VIHICLE_CHECKIN+ " WHERE "+KEY_EXECUTION_TYPE+"='"+exetype+"'");
        db.close();

    }



    /*
  * delete checkin table completely
  * */
    public void deletecheckinlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_VIHICLE_CHECKIN, null, null);
        db.close();

    }
/*
  * delete checkin table completely
  * */
    public void deleteparkingPrice() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PARKING_PRICE, null, null);
        db.close();

    }

    /*
  * delete checkout table completely
  * */
    public void deletecheckoutlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_VEHICLE_CHECKOUT, null, null);
        db.close();

    }

    /*
  * delete offlinecheck in table completely
  * */
    public void deleteofflinecheckintlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_OFFLINEVIHICLE_CHECKIN, null, null);
        db.close();

    }

    /*
     * delete railway check in table completely
     * */
    public void deleterailwaycheckintlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_RAILWAY_CHECKIN, null, null);
        db.close();

    }

    /*
     * delete railway check out table completely
     * */
    public void deleterailwaycheckouttlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_RAILWAY_CHECKOUT, null, null);
        db.close();

    }


    /*
     * delete railway freepricelist table completely
     * */
    public void deletefreepricelist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_FREE_PARKING_LIST, null, null);
        db.close();

    }


    /*
     * delete freepricelist table completely
     * */
    public void deletparkingpricelist() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_TRAFFIC_PARKING_PRICE_LIST, null, null);
        db.close();

    }


    public void deletevehiclecheckinNotSync(String exetype) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC+ " WHERE "+KEY_EXECUTION_TYPE+"='"+exetype+"'");
        db.close();

    }
    public void deletevehiclecheckinOnlineOffline(String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_OFFLINE_ONLINE_LIST+ " WHERE "+KEY_MSTRVEHICLENO+"='"+vehicle_number+"'");
        db.close();
    }
    // By Booking No.
    public void deletevehiclecheckinOnlineOfflineByBookingNo(String booking_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.execSQL("DELETE FROM " + TABLE_OFFLINE_ONLINE_LIST+ " WHERE "+KEY_BOOKINGNO+"='"+booking_no+"'");
        db.close();
    }
    public int updateofflinevehiclecheckinNotsync(String exetype, String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXECUTION_TYPE, exetype);

        // updating row
        return db.update(TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC, values, KEY_VEHICLE_NUMBER + " = ?",
                new String[] { String.valueOf(vehicle_number) });
    }
    public int updateOnlineOfflineTable(String exetype, String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_BOOKING_ID,vehicle_number);
        values.put(KEY_MSTRVEHICLENO,vehicle_number);

        // updating row
//        return db.update(TABLE_OFFLINE_ONLINE_LIST, values, KEY_BOOKING_ID + " = ?",
//                new String[] { String.valueOf(vehicle_number) });
        return db.update(TABLE_OFFLINE_ONLINE_LIST, values, KEY_MSTRVEHICLENO + " = ?",
                new String[] { String.valueOf(vehicle_number) });
    }
    public boolean isVehicleCheckedInorNot(String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from  "+TABLE_OFFLINE_ONLINE_LIST+" WHERE "+KEY_MSTRVEHICLENO+"='"+vehicle_number+"'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public boolean isAlreadyCheckedOutorNot(String vehicle_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from  "+TABLE_OFFLINEVIHICLE_CHECKIN_BUT_NOT_SYNC+" WHERE "+KEY_VEHICLE_NUMBER+"='"+vehicle_number+"'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
