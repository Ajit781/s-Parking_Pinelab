
package utilities.constants;


import data_objects.SParkingAgentModel;

public class Urls {

    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    /*********** production url ********************/

    public static final String url_product="https://www.s-parking.com/sParkingAppVersion/";
    public String url=dataModel.url;



    public  String GetAuthentication = url+"GetAuthentication";
    public  String GetLoginUser = url+"GetLoginUser";
    public  String GetLoginUserV10 = url+"GetLoginUserV10";
    public  String SetOTP = url+"GenerateOTPForVehicleReg";
    public  String GetMobileAuthentication = url+"GetMobileAuthentication";
    public  String SetVehicleUserRegistration = url+"SetVehicleUserRegistration";
    public  String VehicleCheckIN = url+"VehicleCheckIN";
    public  String AgentWiseParkingSummary = url+"AgentWiseParkingSummary";
    public  String VehicleCheckINV10 = url+"VehicleCheckINV10";
    public  String VehicleCheckINForAdvNormal = url+"VehicleCheckINForAdvNormal";
    public  String OfflineVehicleCheckIN = url+"OfflineVehicleCheckIN";
    public  String GetAllCheckedInList = url+"GetAllCheckedInList";
    public  String GetAllCheckedInListV1 = url+"GetAllCheckedInListV1";
    public  String NewVehicleRegistration = url+"NewVehicleRegistration";
    public  String GetCheckoutDetailsV20 = url+"GetCheckoutDetailsV20";
    public  String VehicleCheckOutV10 = url+"VehicleCheckOutV10";
    public  String PendingBillGenerate = url+"PendingBillGenerate";
    public  String PaymentCollection = url+"PaymentCollection";
    public  String VehicleTypeList = url+"VehicleTypeList";
    public  String SetEzetapTransactionLog = url+"SetEzetapTransactionLog";
    //public  String CheckAppVersion = url + "CheckAppVersion";
    public  String SpecialPassList = url + "SpecialPassList";
    public  static final String CheckAppVersion = url_product + "CheckAppVersion.php";
    public  String GenerateAuthToken = url + "GenerateAuthToken";
    public  String VehicleCheckINByVehicleID = url + "VehicleCheckINByVehicleID";
    public  String OfflineModeVehicleCheckIN = url + "OfflineModeVehicleCheckIN";
    public  String OfflineModeVehicleCheckOut = url + "OfflineModeVehicleCheckOut";
    public  String OfflineModeAccessControlCheckIN = url + "OfflineModeAccessControlCheckIN";
    public  String OfflineModeAccessControlCheckOUT = url + "OfflineModeAccessControlCheckOUT";
    public  String GetAdvBookingInProcessDetails = url + "GetAdvBookingInProcessDetails";
    public  String AdvBookingAcceptOrDeclineByAgent = url + "AdvBookingAcceptOrDeclineByAgent";
    public  String GetAdvBookingAcceptByAgentDetails = url + "GetAdvBookingAcceptByAgentDetails";
    public  String AdvanceBookingVehicleCheckin = url + "AdvanceBookingVehicleCheckin";
}
