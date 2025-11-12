package data_objects.bean;

/**
 * Created by Juhi on 28-Jun-17.
 */
public class DataObject {

    private String m_strUserName;
    private String parking_area_id;
    private String slot_id;
    private String slot_name;
    private String booking_no;
    private String payment_mode_id;
    private String payment_mode;
    private String vehicle_id;
    private int m_iUID;
    private String m_strVehicleNo;
    private int m_iBookingID;
    private String m_strOwnerName;
    private String m_strOwnerPhone;
    private String m_strAlterPhone;
    private String m_strVehicleType;
    private String m_strCheckInTime;
    private String vehicle_type_icon;
    private int viewtype=0;

    public DataObject(String m_strUserName, String parking_area_id, String slot_id, String slot_name, String booking_no, String payment_mode_id, String payment_mode, String vehicle_id, int m_iUID, String m_strVehicleNo, int m_iBookingID, String m_strOwnerName,
                      String m_strOwnerPhone, String m_strAlterPhone, String m_strVehicleType, String m_strCheckInTime, String vehicle_type_icon, int viewtype) {
        this.m_strUserName = m_strUserName;
        this.parking_area_id = parking_area_id;
        this.slot_id = slot_id;
        this.slot_name = slot_name;
        this.booking_no = booking_no;
        this.payment_mode_id = payment_mode_id;
        this.payment_mode = payment_mode;
        this.vehicle_id = vehicle_id;
        this.m_iUID = m_iUID;
        this.m_strVehicleNo = m_strVehicleNo;
        this.m_iBookingID = m_iBookingID;
        this.m_strOwnerName = m_strOwnerName;
        this.m_strOwnerPhone = m_strOwnerPhone;
        this.m_strAlterPhone = m_strAlterPhone;
        this.m_strVehicleType = m_strVehicleType;
        this.m_strCheckInTime = m_strCheckInTime;
        this.vehicle_type_icon = vehicle_type_icon;
        this.viewtype = viewtype;
    }



    public DataObject()
    {
        m_strUserName = "";
        parking_area_id = "";
        slot_id = "";
        slot_name = "";
        booking_no = "";
        payment_mode_id = "";
        payment_mode = "";
        vehicle_id = "";
        m_iUID = 0;
        m_strVehicleNo = "";
        m_iBookingID = 0;
        m_strOwnerName = "";
        m_strOwnerPhone = "";
        m_strAlterPhone = "";
        m_strVehicleType = "";
        m_strCheckInTime = "";
        vehicle_type_icon = "";
        viewtype = 0;

    }

    public String getM_strUserName() {
        return m_strUserName;
    }

    public void setM_strUserName(String m_strUserName) {
        this.m_strUserName = m_strUserName;
    }

    public int getM_iUID() {
        return m_iUID;
    }

    public void setM_iUID(int m_iUID) {
        this.m_iUID = m_iUID;
    }

    public String getM_strVehicleNo() {
        return m_strVehicleNo;
    }

    public void setM_strVehicleNo(String m_strVehicleNo) {
        this.m_strVehicleNo = m_strVehicleNo;
    }

    public int getM_iBookingID() {
        return m_iBookingID;
    }

    public void setM_iBookingID(int m_iBookingID) {
        this.m_iBookingID = m_iBookingID;
    }

    public String getM_strOwnerName() {
        return m_strOwnerName;
    }

    public void setM_strOwnerName(String m_strOwnerName) {
        this.m_strOwnerName = m_strOwnerName;
    }

    public String getM_strOwnerPhone() {
        return m_strOwnerPhone;
    }

    public void setM_strOwnerPhone(String m_strOwnerPhone) {
        this.m_strOwnerPhone = m_strOwnerPhone;
    }

    public String getM_strAlterPhone() {
        return m_strAlterPhone;
    }

    public void setM_strAlterPhone(String m_strAlterPhone) {
        this.m_strAlterPhone = m_strAlterPhone;
    }

    public String getM_strVehicleType() {
        return m_strVehicleType;
    }

    public void setM_strVehicleType(String m_strVehicleType) {
        this.m_strVehicleType = m_strVehicleType;
    }

    public String getM_strCheckInTime() {
        return m_strCheckInTime;
    }

    public void setM_strCheckInTime(String m_strCheckInTime) {
        this.m_strCheckInTime = m_strCheckInTime;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }

    public String getParking_area_id() {
        return parking_area_id;
    }

    public void setParking_area_id(String parking_area_id) {
        this.parking_area_id = parking_area_id;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getSlot_name() {
        return slot_name;
    }

    public void setSlot_name(String slot_name) {
        this.slot_name = slot_name;
    }

    public String getBooking_no() {
        return booking_no;
    }

    public void setBooking_no(String booking_no) {
        this.booking_no = booking_no;
    }

    public String getPayment_mode_id() {
        return payment_mode_id;
    }

    public void setPayment_mode_id(String payment_mode_id) {
        this.payment_mode_id = payment_mode_id;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }
    public String getVehicle_type_icon() {
        return vehicle_type_icon;
    }

    public void setVehicle_type_icon(String vehicle_type_icon) {
        this.vehicle_type_icon = vehicle_type_icon;
    }
}