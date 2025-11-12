package data_objects.bean;

/**
 * Created by Juhi on 28-Jun-17.
 */
public class AcceptedBookingBean {

    private String advbookingid;
    private String vehicleid;
    private String vehicleno;
    private String advbookingstarttime;
    private String advbookingendtime;
    private String totaltime;
    private String totalamount;
    private String rate;
    private String vehicletypeid;
    private String vehicletypename;
    private String vehicleownerfullname;
    private String vehicleownermobile;
    private String advbookingstatusid;

    private int viewtype=0;

    public AcceptedBookingBean() {
    }

    public AcceptedBookingBean(String advbookingid, String vehicleid, String vehicleno, String advbookingstarttime,
                               String advbookingendtime, String totaltime, String totalamount, String rate, String vehicletypeid,
                               String vehicletypename, String vehicleownerfullname, String vehicleownermobile,
                               String advbookingstatusid, int viewtype) {
        this.advbookingid = advbookingid;
        this.vehicleid = vehicleid;
        this.vehicleno = vehicleno;
        this.advbookingstarttime = advbookingstarttime;
        this.advbookingendtime = advbookingendtime;
        this.totaltime = totaltime;
        this.totalamount = totalamount;
        this.rate = rate;
        this.vehicletypeid = vehicletypeid;
        this.vehicletypename = vehicletypename;
        this.vehicleownerfullname = vehicleownerfullname;
        this.vehicleownermobile = vehicleownermobile;
        this.advbookingstatusid = advbookingstatusid;
        this.viewtype = viewtype;
    }

    public String getAdvbookingid() {
        return advbookingid;
    }

    public void setAdvbookingid(String advbookingid) {
        this.advbookingid = advbookingid;
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

    public String getAdvbookingstarttime() {
        return advbookingstarttime;
    }

    public void setAdvbookingstarttime(String advbookingstarttime) {
        this.advbookingstarttime = advbookingstarttime;
    }

    public String getAdvbookingendtime() {
        return advbookingendtime;
    }

    public void setAdvbookingendtime(String advbookingendtime) {
        this.advbookingendtime = advbookingendtime;
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

    public String getVehicletypeid() {
        return vehicletypeid;
    }

    public void setVehicletypeid(String vehicletypeid) {
        this.vehicletypeid = vehicletypeid;
    }

    public String getVehicletypename() {
        return vehicletypename;
    }

    public void setVehicletypename(String vehicletypename) {
        this.vehicletypename = vehicletypename;
    }

    public String getVehicleownerfullname() {
        return vehicleownerfullname;
    }

    public void setVehicleownerfullname(String vehicleownerfullname) {
        this.vehicleownerfullname = vehicleownerfullname;
    }

    public String getVehicleownermobile() {
        return vehicleownermobile;
    }

    public void setVehicleownermobile(String vehicleownermobile) {
        this.vehicleownermobile = vehicleownermobile;
    }

    public String getAdvbookingstatusid() {
        return advbookingstatusid;
    }

    public void setAdvbookingstatusid(String advbookingstatusid) {
        this.advbookingstatusid = advbookingstatusid;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}