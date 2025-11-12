package data_objects.bean;

public class AdvBookingReqBean {

    private String advbookingid;
    private String vehicle_id ;
    private String vehicle_no;
    private String advbooking_starttime;
    private String advbooking_endtime;
    private String totaltime;
    private String totalamount;
    private String rate;
    private String vehicletype_id;
    private String vehicletype_name;
    private String vehicleowner_fullname;
    private String vehicleowner_mobilenumber;
    private String StartTime;
    private String EndTime;
    private String booking_date;
    private String booking_dateend;
    private int viewtype=0;

    public AdvBookingReqBean() {
    }

    public AdvBookingReqBean(String advbookingid, String vehicle_id, String vehicle_no, String advbooking_starttime,
                             String advbooking_endtime, String totaltime, String totalamount, String rate, String vehicletype_id,
                             String vehicletype_name, String vehicleowner_fullname, String vehicleowner_mobilenumber,
                             String startTime, String endTime, String booking_date, String booking_dateend, int viewtype) {
        this.advbookingid = advbookingid;
        this.vehicle_id = vehicle_id;
        this.vehicle_no = vehicle_no;
        this.advbooking_starttime = advbooking_starttime;
        this.advbooking_endtime = advbooking_endtime;
        this.totaltime = totaltime;
        this.totalamount = totalamount;
        this.rate = rate;
        this.vehicletype_id = vehicletype_id;
        this.vehicletype_name = vehicletype_name;
        this.vehicleowner_fullname = vehicleowner_fullname;
        this.vehicleowner_mobilenumber = vehicleowner_mobilenumber;
        StartTime = startTime;
        EndTime = endTime;
        this.booking_date = booking_date;
        this.booking_dateend = booking_dateend;
        this.viewtype = viewtype;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_dateend() {
        return booking_dateend;
    }

    public void setBooking_dateend(String booking_dateend) {
        this.booking_dateend = booking_dateend;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }

    public String getAdvbookingid() {
        return advbookingid;
    }

    public void setAdvbookingid(String advbookingid) {
        this.advbookingid = advbookingid;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getAdvbooking_starttime() {
        return advbooking_starttime;
    }

    public void setAdvbooking_starttime(String advbooking_starttime) {
        this.advbooking_starttime = advbooking_starttime;
    }

    public String getAdvbooking_endtime() {
        return advbooking_endtime;
    }

    public void setAdvbooking_endtime(String advbooking_endtime) {
        this.advbooking_endtime = advbooking_endtime;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getVehicletype_id() {
        return vehicletype_id;
    }

    public void setVehicletype_id(String vehicletype_id) {
        this.vehicletype_id = vehicletype_id;
    }

    public String getVehicletype_name() {
        return vehicletype_name;
    }

    public void setVehicletype_name(String vehicletype_name) {
        this.vehicletype_name = vehicletype_name;
    }

    public String getVehicleowner_fullname() {
        return vehicleowner_fullname;
    }

    public void setVehicleowner_fullname(String vehicleowner_fullname) {
        this.vehicleowner_fullname = vehicleowner_fullname;
    }

    public String getVehicleowner_mobilenumber() {
        return vehicleowner_mobilenumber;
    }

    public void setVehicleowner_mobilenumber(String vehicleowner_mobilenumber) {
        this.vehicleowner_mobilenumber = vehicleowner_mobilenumber;
    }
}
