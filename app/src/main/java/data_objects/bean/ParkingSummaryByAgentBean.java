package data_objects.bean;

public class ParkingSummaryByAgentBean {
    private Integer VehicleTypeID;
    private String VehicleTypeName;
    private Integer AgentID;
    private String AgentName;
    private Integer ParkingCount;
    private Integer CollectionAmount;
    private int viewtype=0;

    public ParkingSummaryByAgentBean() {
    }

    public ParkingSummaryByAgentBean(Integer vehicleTypeID, String vehicleTypeName, Integer agentID, String agentName, Integer parkingCount, Integer collectionAmount,int viewtype) {
        VehicleTypeID = vehicleTypeID;
        VehicleTypeName = vehicleTypeName;
        AgentID = agentID;
        AgentName = agentName;
        ParkingCount = parkingCount;
        CollectionAmount = collectionAmount;
        this.viewtype = viewtype;
    }

    public Integer getVehicleTypeID() {
        return VehicleTypeID;
    }

    public void setVehicleTypeID(Integer vehicleTypeID) {
        VehicleTypeID = vehicleTypeID;
    }

    public String getVehicleTypeName() {
        return VehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        VehicleTypeName = vehicleTypeName;
    }

    public Integer getAgentID() {
        return AgentID;
    }

    public void setAgentID(Integer agentID) {
        AgentID = agentID;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public Integer getParkingCount() {
        return ParkingCount;
    }

    public void setParkingCount(Integer parkingCount) {
        ParkingCount = parkingCount;
    }

    public Integer getCollectionAmount() {
        return CollectionAmount;
    }

    public void setCollectionAmount(Integer collectionAmount) {
        CollectionAmount = collectionAmount;
    }
    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}
