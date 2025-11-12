package data_objects.bean;

public class RailwayCheckOutBean {
    private String checkintime = "";
    private String checkouttime = "";
    private String pass_applied = "";
    private String pass_id = "";
    private String exetype = "";
    private String BookingID = "";
    private String TotalDuration = "";
    private String TotalParkingAmount = "";
    private String TotalPaybleAmount = "";
    private String FineAmount = "";
    private String OfferAmount = "";
    private String PaymentMode = "";
    private String AgencyName = "";



    public RailwayCheckOutBean() {
    }

    public RailwayCheckOutBean(String checkintime, String checkouttime, String pass_applied, String pass_id, String exetype,
                               String bookingID, String totalDuration, String totalParkingAmount,
                               String totalPaybleAmount, String fineAmount, String offerAmount, String paymentMode, String agencyName) {
        this.checkintime = checkintime;
        this.checkouttime = checkouttime;
        this.pass_applied = pass_applied;
        this.pass_id = pass_id;
        this.exetype = exetype;
        BookingID = bookingID;
        TotalDuration = totalDuration;
        TotalParkingAmount = totalParkingAmount;
        TotalPaybleAmount = totalPaybleAmount;
        FineAmount = fineAmount;
        OfferAmount = offerAmount;
        PaymentMode = paymentMode;
        AgencyName = agencyName;
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

    public String getCheckouttime() {
        return checkouttime;
    }

    public void setCheckouttime(String checkouttime) {
        this.checkouttime = checkouttime;
    }

    public String getTotalDuration() {
        return TotalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        TotalDuration = totalDuration;
    }

    public String getTotalParkingAmount() {
        return TotalParkingAmount;
    }

    public void setTotalParkingAmount(String totalParkingAmount) {
        TotalParkingAmount = totalParkingAmount;
    }

    public String getTotalPaybleAmount() {
        return TotalPaybleAmount;
    }

    public void setTotalPaybleAmount(String totalPaybleAmount) {
        TotalPaybleAmount = totalPaybleAmount;
    }

    public String getFineAmount() {
        return FineAmount;
    }

    public void setFineAmount(String fineAmount) {
        FineAmount = fineAmount;
    }

    public String getOfferAmount() {
        return OfferAmount;
    }

    public void setOfferAmount(String offerAmount) {
        OfferAmount = offerAmount;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getAgencyName() {
        return AgencyName;
    }

    public void setAgencyName(String agencyName) {
        AgencyName = agencyName;
    }
}
