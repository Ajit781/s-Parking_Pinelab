
package utilities.async_tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class RemoteAsync extends AsyncTask<String, Void, String> {
    private String string_JSON;

    /**
     * Creating an instance of the {@link AsyncResponse} interface to receive
     * {@code processFinish()}
     */
    public AsyncResponse delegate = null;
    private String url;
    public String type = "";

    // Constants
    public static final String GOOGLE_ADDRESS_PICKER = "googleAddrPicker";
    public static final String GOOGLE_LATLNG_PICKER = "googleLatlngPicker";
    public static final String GETAUTHENTICATION = "GetAuthentication";
    public static final String SETOTP = "GenerateOTPForVehicleReg";
    public static final String VEHICLECHECKIN = "VehicleCheckIN";
    public static final String AGENTWISEPARKINGSUMMARY = "AgentWiseParkingSummary";
    public static final String VEHICLECHECKINV10 = "VehicleCheckINV10";
    public static final String VEHICLECHECKINFORADVNORMAL = "VehicleCheckINForAdvNormal";
    public static final String OFFLINEVEHICLECHECKIN = "OfflineVehicleCheckIN";
    public static final String GETMOBILEAUTHENTICATION = "GetMobileAuthentication";
    public static final String SETVEHICLEUSERREGISTRATION = "SetVehicleUserRegistration";
    public static final String GETALLCHECKEDLIST = "GetAllCheckedInList";
    public static final String GETALLCHECKEDLISTV1 = "GetAllCheckedInListV1";
    public static final String SPECIALPASSLIST = "SpecialPassList";
    public static final String NEWVEHICLEREGISTRATION = "NewVehicleRegistration";
    public static final String VEHICLECHECKOUT = "VehicleCheckOut";
    public static final String VEHICLECHECKOUTDETAILSV20 = "GetCheckoutDetailsV20";
    public static final String PENDINGBILLGENERATE = "PendingBillGenerate";
    public static final String PAYMENTCOLLECTION = "PaymentCollection";
    public static final String VEHICLECHECKOUTV10 = "VehicleCheckOutV10";
    public static final String GETLOGINUSER = "GetLoginUser";
    public static final String GETLOGINUSERV10 = "GetLoginUserV10";
    public static final String VEHICLETYPELIST = "VehicleTypeList";
    public static final String GENERATEAUTHTOKEN = "GenerateAuthToken";
    public static final String CHECKAPPVERSION = "CheckAppVersion";
    public static final String VEHICLECHECKINBYVEHICLEID = "VehicleCheckINByVehicleID";
    public static final String OFFLINEMODEVEHICLECHECKIN = "OfflineModeVehicleCheckIN";
    public static final String OFFLINEMODEVEHICLECHECKOUT = "OfflineModeVehicleCheckOut";
    public static final String OFFLINEMODEACCESSCONTROLCHECKIN = "OfflineModeAccessControlCheckIN";
    public static final String OFFLINEMODEACCESSCONTROLCHECKOUT = "OfflineModeAccessControlCheckOUT";
    public static final String GETADVBOOKINGINPROCESSDETAILS = "GetAdvBookingInProcessDetails";
    public static final String ADVBOOKINGACCEPTORDECLINE = "AdvBookingAcceptOrDeclineByAgent";
    public static final String GETADVBOOKINGACCEPTEDBYAGENT = "GetAdvBookingAcceptByAgentDetails";
    public static final String ADVANCEBOOKINGVEHICLECHECKIN = "AdvanceBookingVehicleCheckin";
    public static final String SETEZETAPTRANSACTIONLOG = "SetEzetapTransactionLog";
    public static final String BBCTRL = "bbctrl";

    /**
     * Pass the Url of the web service as a String
     * <p/>
     * url
     */
    public RemoteAsync(String url) {
        Log.e("SERVICE URL # ", url);
        this.url = url;
    }

    @Override
    protected String doInBackground(String... params) {


       if (type.equals(GOOGLE_ADDRESS_PICKER)) {
            String s=params[0];
           int p = s.indexOf("=");
            String s1 = s.substring(p);
            Log.e("loc?>>",s1);
            string_JSON= AutoCompleteConnection.autocomplete(s1);
        }
       else if (type.equals(GOOGLE_LATLNG_PICKER)) {
            HttpUrlconnection httpUrlconnection = new HttpUrlconnection();
            string_JSON = httpUrlconnection.getGetresponse(url , params[0]);

       }
//       else if (type.equals(CHECKAPPVERSION)) {
//            HttpsUrlconnection httpUrlconnection = new HttpsUrlconnection();
//            string_JSON = httpUrlconnection.getPostresponse(url , params[0]);
//
//       }
       else if (type.equals(BBCTRL)) {
           HttpUrlconnectionWithJsonParsing httpUrlconnection = new HttpUrlconnectionWithJsonParsing();
           string_JSON = httpUrlconnection.getPostresponse(url , params[0]);

       } else{
            /*HttpConnection connection = new HttpConnection();
           string_JSON = connection.getPostRespoonse(url, pairs[0]);*/
           // for Http
//           HttpUrlconnection httpUrlconnection = new HttpUrlconnection();
//           string_JSON = httpUrlconnection.getPostresponse(url , params[0]);
           // for Https
           HttpsUrlconnection httpUrlconnection = new HttpsUrlconnection();
           string_JSON = httpUrlconnection.getPostresponse(url , params[0]);
       }
        return string_JSON;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        delegate.processFinish(type, jsonString);
    }


}

