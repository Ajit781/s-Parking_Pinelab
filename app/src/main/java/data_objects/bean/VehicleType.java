package data_objects.bean;

/**
 * Created by server1 on 6/14/2019.
 */

public class VehicleType {
    private String vehicleTypeId;
    private String vehicleTypeName;

    public VehicleType() {
    }

    public VehicleType(String vehicleTypeId, String vehicleTypeName) {
        this.vehicleTypeId = vehicleTypeId;
        this.vehicleTypeName = vehicleTypeName;
    }

    public String getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }
    @Override
    public String toString() {
        return vehicleTypeName;
    }
}
