

package utilities.async_tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import data_objects.bean.ParkingPrice;
import db.DatabaseHandler;
import utilities.constants.SessionManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PriceAsync extends AsyncTask<PriceAsyncBean, Void, String> {
    private String string_JSON;
    Context context;
    /**
     * Creating an instance of the {@link AsyncResponse} interface to receive
     * {@code processFinish()}
     */
    public AsyncResponse delegate = null;
    private String url;
    public String type = "";
    DatabaseHandler databaseHandler;
   SessionManager session ;
    /**
     * Pass the Url of the web service as a String
     * <p/>
     * url
     */
    public PriceAsync() {

    }
    public PriceAsync(Context context) {
        this.context = context.getApplicationContext();

    }

    @Override
    protected String doInBackground(PriceAsyncBean... priceAsyncBeans) {
        PriceAsyncBean priceBean = priceAsyncBeans[0];

        try {
                databaseHandler = priceBean.getDatabaseHandler();
                databaseHandler.iniDB();


                JSONArray HourMinParkingPriceList = priceBean.getJsonArray();
                if (HourMinParkingPriceList.length() > 0) {

                Integer ppID = 0;
                for (int i = 0; i < HourMinParkingPriceList.length(); i++) {

                    JSONObject object = HourMinParkingPriceList.getJSONObject(i);

//                                    ParkingPrice parkingPrice = new ParkingPrice();
                    String dateTime[] = getDateTime();

                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"1",object.getString("TwoWPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"2",object.getString("FourWPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"3",object.getString("HeavyWPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"4",object.getString("CyclePrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"5",object.getString("PremiumCarPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"6",object.getString("CommercialTaxiVanPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"7",object.getString("AmbulanceArmyVVIPAuthPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"8",object.getString("MVCStaffPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"9",object.getString("SettlementPassPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"10",object.getString("OlaUberPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"11",object.getString("RailwayEmployeesPrice"));
                    addParkingPrice(++ppID,"786","1","1",dateTime[0]+" "+dateTime[1],object.getString("Hour"),object.getString("Minutes"),"12",object.getString("Helmet2WPrice"));

                    Log.e("loop count", String.valueOf(i));
                }

                Log.e("loop count", "After Loop");

            }
            //TODO your background code
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Success";
    }

    @Override
    protected void onPostExecute(String jsonString) {
        databaseHandler.releaseDB();
        Log.e("loop count", "Done");
        Log.e("jsonString", jsonString);
    //       ( new LoginActivity()).pageChange(jsonString);
        //  Log.e("loop count", "Done");
       // delegate.processFinish("",jsonString);
        context.startActivity(new Intent(context, DashBoardActivity.class).setFlags(FLAG_ACTIVITY_NEW_TASK));

    }


    private void addParkingPrice(int id, String parking_area, String policy, String shift_type, String date_time, String hour, String minutes,String vehicle_type,String parking_price) {
        ParkingPrice parkingPrice = new ParkingPrice();
        String dateTime[] = getDateTime();

        parkingPrice.setPp_id(id);
        parkingPrice.setPp_parking_area(parking_area);
        parkingPrice.setPp_policy(policy);
        parkingPrice.setPp_shift_type(shift_type);
        parkingPrice.setPp_lastupdateon(date_time);

        parkingPrice.setPp_hour(hour);
        parkingPrice.setPp_min(minutes);
        // set price of Two Wheeler
        parkingPrice.setPp_vehicle_type(vehicle_type);
        parkingPrice.setPp_price(parking_price);
        databaseHandler.addParkingPrice(parkingPrice);
    }


    // get current date and time
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

}
