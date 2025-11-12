package data_objects.bean;

public class ParkingPrice {
    private Integer pp_id;
    private String pp_parking_area;
    private String pp_policy;
    private String pp_vehicle_type;
    private String pp_shift_type;
    private String pp_hour;
    private String pp_min;
    private String pp_price;
    private String pp_lastupdateon;

    public ParkingPrice() {
    }

    public ParkingPrice(Integer pp_id, String pp_parking_area,String pp_policy, String pp_vehicle_type, String pp_shift_type, String pp_hour, String pp_min, String pp_price, String pp_lastupdateon) {
        this.pp_id = pp_id;
        this.pp_parking_area = pp_parking_area;
        this.pp_policy = pp_policy;
        this.pp_vehicle_type = pp_vehicle_type;
        this.pp_shift_type = pp_shift_type;
        this.pp_hour = pp_hour;
        this.pp_min = pp_min;
        this.pp_price = pp_price;
        this.pp_lastupdateon = pp_lastupdateon;
    }

    public Integer getPp_id() {
        return pp_id;
    }

    public void setPp_id(Integer pp_id) {
        this.pp_id = pp_id;
    }

    public String getPp_parking_area() {
        return pp_parking_area;
    }

    public void setPp_parking_area(String pp_parking_area) {
        this.pp_parking_area = pp_parking_area;
    }

    public String getPp_policy() {
        return pp_policy;
    }

    public void setPp_policy(String pp_policy) {
        this.pp_policy = pp_policy;
    }

    public String getPp_vehicle_type() {
        return pp_vehicle_type;
    }

    public void setPp_vehicle_type(String pp_vehicle_type) {
        this.pp_vehicle_type = pp_vehicle_type;
    }

    public String getPp_shift_type() {
        return pp_shift_type;
    }

    public void setPp_shift_type(String pp_shift_type) {
        this.pp_shift_type = pp_shift_type;
    }

    public String getPp_hour() {
        return pp_hour;
    }

    public void setPp_hour(String pp_hour) {
        this.pp_hour = pp_hour;
    }

    public String getPp_min() {
        return pp_min;
    }

    public void setPp_min(String pp_min) {
        this.pp_min = pp_min;
    }

    public String getPp_price() {
        return pp_price;
    }

    public void setPp_price(String pp_price) {
        this.pp_price = pp_price;
    }

    public String getPp_lastupdateon() {
        return pp_lastupdateon;
    }

    public void setPp_lastupdateon(String pp_lastupdateon) {
        this.pp_lastupdateon = pp_lastupdateon;
    }
}
