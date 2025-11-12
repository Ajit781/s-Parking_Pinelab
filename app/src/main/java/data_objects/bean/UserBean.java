package data_objects.bean;

public class UserBean {
    private String uid = "";
    private String fullname = "";
    private String agecyname = "";
    private String parkinngslot_number = "";
    private String parkinglocation = "";

    public UserBean() {
    }

    public UserBean(String uid, String fullname, String agecyname, String parkinngslot_number, String parkinglocation) {
        this.uid = uid;
        this.fullname = fullname;
        this.agecyname = agecyname;
        this.parkinngslot_number = parkinngslot_number;
        this.parkinglocation = parkinglocation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAgecyname() {
        return agecyname;
    }

    public void setAgecyname(String agecyname) {
        this.agecyname = agecyname;
    }

    public String getParkinngslot_number() {
        return parkinngslot_number;
    }

    public void setParkinngslot_number(String parkinngslot_number) {
        this.parkinngslot_number = parkinngslot_number;
    }

    public String getParkinglocation() {
        return parkinglocation;
    }

    public void setParkinglocation(String parkinglocation) {
        this.parkinglocation = parkinglocation;
    }
}
