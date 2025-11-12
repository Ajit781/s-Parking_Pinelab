package data_objects.bean;

public class VehicleCheckInBean {
    private String vehicle_number = "";
    private String checkintime = "";
    private String vehicletype = "";
    private String mobilenum = "";
    private String pass_applied = "";
    private String pass_id = "";
    private String exetype = "";
    private String BookingID = "";
    private String AdvanceBookingID = "";
    private int viewtype=0;



    public VehicleCheckInBean() {
    }

    public VehicleCheckInBean(String vehicle_number, String checkintime, String vehicletype,
                              String mobilenum, String pass_applied, String pass_id, String exetype,int viewtype) {
        this.vehicle_number = vehicle_number;
        this.checkintime = checkintime;
        this.vehicletype = vehicletype;
        this.mobilenum = mobilenum;
        this.pass_applied = pass_applied;
        this.pass_id = pass_id;
        this.exetype = exetype;
        this.viewtype = viewtype;
    }

    public VehicleCheckInBean(String vehicle_number, String checkintime, String vehicletype,
                              String mobilenum, String pass_applied, String pass_id, String exetype, String bookingid,
                              String advanceBookingID,int viewtype) {
        this.vehicle_number = vehicle_number;
        this.checkintime = checkintime;
        this.vehicletype = vehicletype;
        this.mobilenum = mobilenum;
        this.pass_applied = pass_applied;
        this.pass_id = pass_id;
        this.exetype = exetype;
        this.BookingID = bookingid;
        this.AdvanceBookingID = advanceBookingID;
        this.viewtype = viewtype;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getCheckintime() {
        return checkintime;
    }

    public void setCheckintime(String checkintime) {
        this.checkintime = checkintime;
    }

    public String getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    public String getMobilenum() {
        return mobilenum;
    }

    public void setMobilenum(String mobilenum) {
        this.mobilenum = mobilenum;
    }

    public String getExetype() {
        return exetype;
    }

    public void setExetype(String exetype) {
        this.exetype = exetype;
    }

    public String getPass_applied() {
        return pass_applied;
    }

    public void setPass_applied(String pass_applied) {
        this.pass_applied = pass_applied;
    }

    public String getPass_id() {
        return pass_id;
    }

    public void setPass_id(String pass_id) {
        this.pass_id = pass_id;
    }

    public String getBookingid() {
        return BookingID;
    }

    public void setBookingid(String bookingid) {
        this.BookingID = bookingid;
    }

    public String getAdvanceBookingID() {
        return AdvanceBookingID;
    }

    public void setAdvanceBookingID(String advanceBookingID) {
        AdvanceBookingID = advanceBookingID;
    }

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}
