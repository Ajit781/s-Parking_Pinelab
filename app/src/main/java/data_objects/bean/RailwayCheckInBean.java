package data_objects.bean;

public class RailwayCheckInBean {
    private String checkintime = "";
    private String pass_applied = "";
    private String pass_id = "";
    private String exetype = "";
    private String BookingID = "";



    public RailwayCheckInBean() {
    }

    public RailwayCheckInBean(String checkintime, String pass_applied, String pass_id, String exetype, String bookingID) {
        this.checkintime = checkintime;
        this.pass_applied = pass_applied;
        this.pass_id = pass_id;
        this.exetype = exetype;
        BookingID = bookingID;
    }

    public String getCheckintime() {
        return checkintime;
    }

    public void setCheckintime(String checkintime) {
        this.checkintime = checkintime;
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

    public String getExetype() {
        return exetype;
    }

    public void setExetype(String exetype) {
        this.exetype = exetype;
    }

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
    }
}
