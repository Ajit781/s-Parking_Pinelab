package data_objects.bean;

/**
 * Created by vyomainnovus on 17/06/19.
 */

public class BookingBillBean {

    private String bookingno = "";
    private String BookingID = "";
    private String checkintime = "";
    private String checkouttime = "";
    private String ownerphoneno = "";
    private String VehicleType = "";
    private String vechile_no = "";
    private String ParkingAreaName = "";
    private String TotalDuration = "";
    private String TotalParkingAmount = "";
    private String TotalPaybleAmount = "";
    private String FineAmount = "";
    private String OfferAmount = "";
    private String PaymentMode = "";
    private String AgencyName = "";
    private String OverTimeDuration = "";
    private String OverTimeAmount = "";
    private String message = "";
    private String advbookingid = "";

    public BookingBillBean(String bookingno, String bookingID, String checkintime, String checkouttime, String ownerphoneno,
                           String vehicleType, String vechile_no, String parkingAreaName, String totalDuration,
                           String totalParkingAmount, String totalPaybleAmount, String fineAmount, String offerAmount,
                           String paymentMode, String agencyName, String overTimeDuration, String overTimeAmount,
                           String message, String advbookingid) {
        this.bookingno = bookingno;
        BookingID = bookingID;
        this.checkintime = checkintime;
        this.checkouttime = checkouttime;
        this.ownerphoneno = ownerphoneno;
        VehicleType = vehicleType;
        this.vechile_no = vechile_no;
        ParkingAreaName = parkingAreaName;
        TotalDuration = totalDuration;
        TotalParkingAmount = totalParkingAmount;
        TotalPaybleAmount = totalPaybleAmount;
        FineAmount = fineAmount;
        OfferAmount = offerAmount;
        PaymentMode = paymentMode;
        AgencyName = agencyName;
        OverTimeDuration = overTimeDuration;
        OverTimeAmount = overTimeAmount;
        this.message = message;
        this.advbookingid = advbookingid;
    }

    public BookingBillBean() {
    }

    public String getAdvbookingid() {
        return advbookingid;
    }

    public void setAdvbookingid(String advbookingid) {
        this.advbookingid = advbookingid;
    }

    public String getBookingno() {
        return bookingno;
    }

    public void setBookingno(String bookingno) {
        this.bookingno = bookingno;
    }

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
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

    public String getOwnerphoneno() {
        return ownerphoneno;
    }

    public void setOwnerphoneno(String ownerphoneno) {
        this.ownerphoneno = ownerphoneno;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getVechile_no() {
        return vechile_no;
    }

    public void setVechile_no(String vechile_no) {
        this.vechile_no = vechile_no;
    }

    public String getParkingAreaName() {
        return ParkingAreaName;
    }

    public void setParkingAreaName(String parkingAreaName) {
        ParkingAreaName = parkingAreaName;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOverTimeDuration() {
        return OverTimeDuration;
    }

    public void setOverTimeDuration(String overTimeDuration) {
        OverTimeDuration = overTimeDuration;
    }

    public String getOverTimeAmount() {
        return OverTimeAmount;
    }

    public void setOverTimeAmount(String overTimeAmount) {
        OverTimeAmount = overTimeAmount;
    }
}
