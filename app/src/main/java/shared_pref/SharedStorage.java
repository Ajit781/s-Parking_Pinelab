/*
 * Copyright (c) 2016.
 * Soham Ghosh
 */

package shared_pref;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStorage {
    static SharedPreferences preference;

    private static String prefData="ExampleStructApp";

    // public static String UserId="in.neardeliveries.user_id"; // example of keys
    public static String UserId= "UserId";
    public static String Userame= "Userame";
    public static String CurrentBookingStatus = "CurrentBookingStatus";
    public static String parkingslot= "parkingslot";
    public static String parkinglocation= "parkinglocation";
    public static String DriverDeviceId = "DriverDeviceId";
    public static String Password = "Password";
    public static String uniq_id = "uniq_id";
    public static String driver_id = "driver_id";
    public static String driverpage_current_lat = "driverpage_current_lat";
    public static String driverpage_current_lng = "driverpage_current_lng";
    public static String driverpage_pickup_lat = "driverpage_pickup_lat";
    public static String driverpage_pickup_lng = "driverpage_pickup_lng";
    public static String driverpage_drop_lat = "driverpage_drop_lat";
    public static String driverpage_drop_lng = "driverpage_drop_lng";
    public static String driverpage_pickup = "driverpage_pickup";
    public static String driverpage_drop = "driverpage_drop";
    public static String driverpage_vicheletype_type = "driverpage_vicheletype_type";
    public static String booking_uniq_id = "booking_uniq_id";
    public static String driver_image = "driver_image";
    public static String driver_mobile = "driver_mobile";
    public static String carNo = "carNo";
    public static String carModel = "carModel";
    public static String driver_name = "driver_name";
    public static String drop_selection = "drop_selection";
    public static String pickup_selection = "pickup_selection";
    public static String OnTrip = "0";
    public static String BookDateTime = "0";
    public static String payment = "payment";
    public static String payment_type = "payment";
    public static String vehicle_id = "vehicle_id";
    public static String bookingRequestid = "bookingRequestid";
    public static String boomprice = "boomprice";
    public static String versionname = "versionname";

    public static void setValue(Context context,String key,String data){
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(key,data);
        editor.commit();
    }

    public static String getValue(Context context,String key){
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        String id = preference.getString(key,"");
        return id;
    }

    public static void resetValue(Context context){
        preference = context.getSharedPreferences(prefData, Context.MODE_PRIVATE);
        preference.edit().clear().commit();
    }


    public static void setAssignedDriverId(Context mContext, String status) {
        SharedPreferences preferences = mContext.getSharedPreferences("FourgcabsDriver", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("assigned_driver_id", status);
        editor.commit();
    }

    public static String getAssignedDriverId(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("FourgcabsDriver", 0);
        String latitude = preferences.getString("assigned_driver_id", "");
        return latitude;
    }

    public static void setDriverid(Context mContext, String cat) {
        SharedPreferences preferences = mContext.getSharedPreferences("FourgcabsDriver", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("driver_id", cat);
        editor.commit();
    }

    public static String getDriverid(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("FourgcabsDriver", 0); // 0 - for private mode
        String name = preferences.getString("driver_id", "");
        return name;
    }

    public static void setBookid(Context mContext, String cat) {
        SharedPreferences preferences = mContext.getSharedPreferences("KBikeDriver", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("book_id", cat);
        editor.commit();
    }

    public static String getBookid(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("KBikeDriver", 0); // 0 - for private mode
        String name = preferences.getString("book_id", "");
        return name;
    }

    public static void setDriverUserId(Context mContext, String cat) {
        SharedPreferences preferences = mContext.getSharedPreferences("KBikeDriver", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("driver_user_id", cat);
        editor.commit();
    }

    public static String getDriverUserId(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences("KBikeDriver", 0); // 0 - for private mode
        String name = preferences.getString("driver_user_id", "");
        return name;
    }
}
