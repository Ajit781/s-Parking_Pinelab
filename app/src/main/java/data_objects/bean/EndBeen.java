package data_objects.bean;

/**
 * Created by jumin on 11/21/2017.
 */

public class EndBeen {

    private String PropertyChanged = "";
    private String bookingid = "";
    private String bookingno = "";
    private String bookingstatus = "";
    private String checkintime = "";
    private String checkouttime = "";
    private String message = "";
    private String ownerid = "";
    private String ownername = "";
    private String ownerphoneno = "";
    private String parkid = "";
    private String parkingarea = "";
    private String parkingownerward = "";
    private String parkingtype = "";
    private String privateparkingownerid = "";
    private String rate = "";
    private String status = "";
    private String totalamount = "";
    private String totaltime = "";
    private String vehicleid = "";
    private String vehicleno = "";
    private String wardid = "";

    public EndBeen(String propertyChanged, String bookingid, String bookingno, String bookingstatus, String checkintime,
                   String checkouttime, String message, String ownerid, String ownername, String ownerphoneno,
                   String parkid, String parkingarea, String parkingownerward, String parkingtype, String privateparkingownerid,
                   String rate, String status, String totalamount, String totaltime, String vehicleid, String vehicleno,
                   String wardid) {
        PropertyChanged = propertyChanged;
        this.bookingid = bookingid;
        this.bookingno = bookingno;
        this.bookingstatus = bookingstatus;
        this.checkintime = checkintime;
        this.checkouttime = checkouttime;
        this.message = message;
        this.ownerid = ownerid;
        this.ownername = ownername;
        this.ownerphoneno = ownerphoneno;
        this.parkid = parkid;
        this.parkingarea = parkingarea;
        this.parkingownerward = parkingownerward;
        this.parkingtype = parkingtype;
        this.privateparkingownerid = privateparkingownerid;
        this.rate = rate;
        this.status = status;
        this.totalamount = totalamount;
        this.totaltime = totaltime;
        this.vehicleid = vehicleid;
        this.vehicleno = vehicleno;
        this.wardid = wardid;
    }

    public EndBeen() {
    }

    public String getPropertyChanged() {
        return PropertyChanged;
    }

    public void setPropertyChanged(String propertyChanged) {
        PropertyChanged = propertyChanged;
    }

    public String getBookingid() {
        return bookingid;
    }

    public void setBookingid(String bookingid) {
        this.bookingid = bookingid;
    }

    public String getBookingno() {
        return bookingno;
    }

    public void setBookingno(String bookingno) {
        this.bookingno = bookingno;
    }

    public String getBookingstatus() {
        return bookingstatus;
    }

    public void setBookingstatus(String bookingstatus) {
        this.bookingstatus = bookingstatus;
    }

    public String getCheckintime() {
        return checkintime;
    }

    public void setCheckintime(String checkintime) {
        this.checkintime = checkintime;
    }

    public String getCheckouttime() {
        return checkouttime;
    }

    public void setCheckouttime(String checkouttime) {
        this.checkouttime = checkouttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnerphoneno() {
        return ownerphoneno;
    }

    public void setOwnerphoneno(String ownerphoneno) {
        this.ownerphoneno = ownerphoneno;
    }

    public String getParkid() {
        return parkid;
    }

    public void setParkid(String parkid) {
        this.parkid = parkid;
    }

    public String getParkingarea() {
        return parkingarea;
    }

    public void setParkingarea(String parkingarea) {
        this.parkingarea = parkingarea;
    }

    public String getParkingownerward() {
        return parkingownerward;
    }

    public void setParkingownerward(String parkingownerward) {
        this.parkingownerward = parkingownerward;
    }

    public String getParkingtype() {
        return parkingtype;
    }

    public void setParkingtype(String parkingtype) {
        this.parkingtype = parkingtype;
    }

    public String getPrivateparkingownerid() {
        return privateparkingownerid;
    }

    public void setPrivateparkingownerid(String privateparkingownerid) {
        this.privateparkingownerid = privateparkingownerid;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getVehicleid() {
        return vehicleid;
    }

    public void setVehicleid(String vehicleid) {
        this.vehicleid = vehicleid;
    }

    public String getVehicleno() {
        return vehicleno;
    }

    public void setVehicleno(String vehicleno) {
        this.vehicleno = vehicleno;
    }

    public String getWardid() {
        return wardid;
    }

    public void setWardid(String wardid) {
        this.wardid = wardid;
    }
}
