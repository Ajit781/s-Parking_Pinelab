

package data_objects;


import java.util.ArrayList;

import data_objects.bean.AcceptedBookingBean;
import data_objects.bean.AdvBookingReqBean;
import data_objects.bean.BookingBillBean;
import data_objects.bean.DataObject;
import data_objects.bean.FreeParkingPriceType;
import data_objects.bean.ParkingPrice;
import data_objects.bean.ParkingSummaryByAgentBean;
import data_objects.bean.RailwayCheckInBean;
import data_objects.bean.RailwayCheckOutBean;
import data_objects.bean.SpclPassStoreBean;
import data_objects.bean.UserBean;
import data_objects.bean.VehicleCheckInBean;
import data_objects.bean.VehicleType;
import data_objects.bean.VehicleTypePrice;

public class SParkingAgentModel {
    private static SParkingAgentModel ourInstance = new SParkingAgentModel();



    public static SParkingAgentModel getInstance() {
        return ourInstance;
    }

    private SParkingAgentModel() {

    }

    // Instances of the bean classes are following
    public String details_shown = "";
    public String mobile_no = "";
    public String cameFrom="";
    public String vehicle_name="";
    public String vehicle_no="";
    public String advbookingcount = "";
    public int about_web_view = 0;
    public int about_advanced_dash = 0;
    public int isbluetoothon = 0;
    public int check_in = 0;
    public int check_in_remove = 0;
    public String url = "";
    public String base_token = "";
    public int check_from = 0;
    public boolean scanVehicle = false;
    public String scanVehicleNumber = "";
    public boolean isofflinecheckin = true;
    public Integer vehicle_Type=0;


    public UserBean userBean = new UserBean();
    public ParkingSummaryByAgentBean parkingSummaryByAgentBean = new ParkingSummaryByAgentBean();
    public DataObject vehicleCheckInBean = new DataObject();
    public BookingBillBean bookingBillBean = new BookingBillBean();
    public ArrayList<DataObject> dataObjectArrayList = new ArrayList<DataObject>();
    public ArrayList<VehicleType> vehicletypeArrayList = new ArrayList<VehicleType>();
    public ArrayList<SpclPassStoreBean> spclPassStoreBeanArrayList = new ArrayList<SpclPassStoreBean>();
    public ArrayList<VehicleCheckInBean> vehicleCheckInBeanArrayList = new ArrayList<VehicleCheckInBean>();
    public ArrayList<VehicleCheckInBean> offlinevehicleCheckInBeanArrayList = new ArrayList<VehicleCheckInBean>();
    public ArrayList<BookingBillBean> offlinebookingBillBeansnArrayList = new ArrayList<BookingBillBean>();
    public ArrayList<RailwayCheckInBean> railwayCheckInBeansArrayList = new ArrayList<RailwayCheckInBean>();
    public ArrayList<RailwayCheckOutBean> railwayCheckOutBeansArrayList = new ArrayList<RailwayCheckOutBean>();
    public ArrayList<FreeParkingPriceType> freeParkingPriceTypeArrayList = new ArrayList<FreeParkingPriceType>();
    public ArrayList<VehicleTypePrice> vehicleTypePriceArrayList = new ArrayList<VehicleTypePrice>();
    public ArrayList<AdvBookingReqBean> advBookingReqBeanArrayList = new ArrayList<AdvBookingReqBean>();
    public ArrayList<AcceptedBookingBean> acceptedBookingBeanArrayList = new ArrayList<AcceptedBookingBean>();
    public ArrayList<ParkingPrice> parkingPriceArrayList = new ArrayList<ParkingPrice>();
    public ArrayList<ParkingSummaryByAgentBean> parkingSummaryByAgentBeanArrayList  = new ArrayList<ParkingSummaryByAgentBean>();
    public ArrayList<VehicleCheckInBean> offlinevehicleCheckInBeanArrayListNotSync = new ArrayList<VehicleCheckInBean>();

}
