package data_objects.bean;

/**
 * Created by server1 on 6/14/2019.
 */

public class VehicleTypePrice {
    private String VehicleTypeID;
    private String VehicleType;
    private String FirstCharge;
    private String HourlyCharge;
    private String MinDuration;
    private String RecursiveDuration;

    public VehicleTypePrice() {
    }

    public VehicleTypePrice(String vehicleTypeID, String vehicleType, String firstCharge,
                            String hourlyCharge, String minDuration, String recursiveDuration) {
        VehicleTypeID = vehicleTypeID;
        VehicleType = vehicleType;
        FirstCharge = firstCharge;
        HourlyCharge = hourlyCharge;
        MinDuration = minDuration;
        RecursiveDuration = recursiveDuration;
    }

    public String getVehicleTypeID() {
        return VehicleTypeID;
    }

    public void setVehicleTypeID(String vehicleTypeID) {
        VehicleTypeID = vehicleTypeID;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getFirstCharge() {
        return FirstCharge;
    }

    public void setFirstCharge(String firstCharge) {
        FirstCharge = firstCharge;
    }

    public String getHourlyCharge() {
        return HourlyCharge;
    }

    public void setHourlyCharge(String hourlyCharge) {
        HourlyCharge = hourlyCharge;
    }

    public String getMinDuration() {
        return MinDuration;
    }

    public void setMinDuration(String minDuration) {
        MinDuration = minDuration;
    }

    public String getRecursiveDuration() {
        return RecursiveDuration;
    }

    public void setRecursiveDuration(String recursiveDuration) {
        RecursiveDuration = recursiveDuration;
    }
}
