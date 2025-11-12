package data_objects.bean;

/**
 * Created by server1 on 7/16/2019.
 */

public class SpclPassStoreBean {
    private String passStoreId;
    private String passStoreName;
    private String SpecialPassAddress;
    private String AvgRequiredSpecialPass;
    private String StartingFrom;

    public SpclPassStoreBean() {
    }

    public SpclPassStoreBean(String passStoreId,
                             String passStoreName, String specialPassAddress,
                             String avgRequiredSpecialPass, String startingFrom) {
        this.passStoreId = passStoreId;
        this.passStoreName = passStoreName;
        SpecialPassAddress = specialPassAddress;
        AvgRequiredSpecialPass = avgRequiredSpecialPass;
        StartingFrom = startingFrom;
    }

    public String getPassStoreId() {
        return passStoreId;
    }

    public void setPassStoreId(String passStoreId) {
        this.passStoreId = passStoreId;
    }

    public String getPassStoreName() {
        return passStoreName;
    }

    public void setPassStoreName(String passStoreName) {
        this.passStoreName = passStoreName;
    }

    public String getSpecialPassAddress() {
        return SpecialPassAddress;
    }

    public void setSpecialPassAddress(String specialPassAddress) {
        SpecialPassAddress = specialPassAddress;
    }

    public String getAvgRequiredSpecialPass() {
        return AvgRequiredSpecialPass;
    }

    public void setAvgRequiredSpecialPass(String avgRequiredSpecialPass) {
        AvgRequiredSpecialPass = avgRequiredSpecialPass;
    }

    public String getStartingFrom() {
        return StartingFrom;
    }

    public void setStartingFrom(String startingFrom) {
        StartingFrom = startingFrom;
    }
}
