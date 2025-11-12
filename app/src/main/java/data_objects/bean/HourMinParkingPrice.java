package data_objects.bean;

public class HourMinParkingPrice {
    private String Hour;
    private String Minutes;
    private String TwoWPrice;
    private String FourWPrice;
    private String HeavyWPrice;
    private String CyclePrice;
    private String PremiumCarPrice;
    private String CommercialTaxiVanPrice;
    private String AmbulanceArmyVVIPAuthPrice;
    private String MVCStaffPrice;
    private String SettlementPassPrice;
    private String OlaUberPrice;
    private String RailwayEmployeesPrice;
    private String Helmet2WPrice;

    public HourMinParkingPrice() {
    }

    public HourMinParkingPrice(String hour, String minutes, String twoWPrice, String fourWPrice, String heavyWPrice, String cyclePrice, String premiumCarPrice, String commercialTaxiVanPrice, String ambulanceArmyVVIPAuthPrice, String MVCStaffPrice, String settlementPassPrice, String olaUberPrice, String railwayEmployeesPrice, String helmet2WPrice) {
        Hour = hour;
        Minutes = minutes;
        TwoWPrice = twoWPrice;
        FourWPrice = fourWPrice;
        HeavyWPrice = heavyWPrice;
        CyclePrice = cyclePrice;
        PremiumCarPrice = premiumCarPrice;
        CommercialTaxiVanPrice = commercialTaxiVanPrice;
        AmbulanceArmyVVIPAuthPrice = ambulanceArmyVVIPAuthPrice;
        this.MVCStaffPrice = MVCStaffPrice;
        SettlementPassPrice = settlementPassPrice;
        OlaUberPrice = olaUberPrice;
        RailwayEmployeesPrice = railwayEmployeesPrice;
        Helmet2WPrice = helmet2WPrice;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public String getTwoWPrice() {
        return TwoWPrice;
    }

    public void setTwoWPrice(String twoWPrice) {
        TwoWPrice = twoWPrice;
    }

    public String getFourWPrice() {
        return FourWPrice;
    }

    public void setFourWPrice(String fourWPrice) {
        FourWPrice = fourWPrice;
    }

    public String getHeavyWPrice() {
        return HeavyWPrice;
    }

    public void setHeavyWPrice(String heavyWPrice) {
        HeavyWPrice = heavyWPrice;
    }

    public String getCyclePrice() {
        return CyclePrice;
    }

    public void setCyclePrice(String cyclePrice) {
        CyclePrice = cyclePrice;
    }

    public String getPremiumCarPrice() {
        return PremiumCarPrice;
    }

    public void setPremiumCarPrice(String premiumCarPrice) {
        PremiumCarPrice = premiumCarPrice;
    }

    public String getCommercialTaxiVanPrice() {
        return CommercialTaxiVanPrice;
    }

    public void setCommercialTaxiVanPrice(String commercialTaxiVanPrice) {
        CommercialTaxiVanPrice = commercialTaxiVanPrice;
    }

    public String getAmbulanceArmyVVIPAuthPrice() {
        return AmbulanceArmyVVIPAuthPrice;
    }

    public void setAmbulanceArmyVVIPAuthPrice(String ambulanceArmyVVIPAuthPrice) {
        AmbulanceArmyVVIPAuthPrice = ambulanceArmyVVIPAuthPrice;
    }

    public String getMVCStaffPrice() {
        return MVCStaffPrice;
    }

    public void setMVCStaffPrice(String MVCStaffPrice) {
        this.MVCStaffPrice = MVCStaffPrice;
    }

    public String getSettlementPassPrice() {
        return SettlementPassPrice;
    }

    public void setSettlementPassPrice(String settlementPassPrice) {
        SettlementPassPrice = settlementPassPrice;
    }

    public String getOlaUberPrice() {
        return OlaUberPrice;
    }

    public void setOlaUberPrice(String olaUberPrice) {
        OlaUberPrice = olaUberPrice;
    }

    public String getRailwayEmployeesPrice() {
        return RailwayEmployeesPrice;
    }

    public void setRailwayEmployeesPrice(String railwayEmployeesPrice) {
        RailwayEmployeesPrice = railwayEmployeesPrice;
    }

    public String getHelmet2WPrice() {
        return Helmet2WPrice;
    }

    public void setHelmet2WPrice(String helmet2WPrice) {
        Helmet2WPrice = helmet2WPrice;
    }
}
