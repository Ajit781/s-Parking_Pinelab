package data_objects.bean;

/**
 * Created by server1 on 6/14/2019.
 */

public class GateOpenBoomBarrierBean {
    private Integer status;
    private Integer gateid;


    public GateOpenBoomBarrierBean() {
    }

    public GateOpenBoomBarrierBean(Integer status, Integer gateid) {
        this.status = status;
        this.gateid = gateid;
    } public GateOpenBoomBarrierBean(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGateid() {
        return gateid;
    }

    public void setGateid(Integer gateid) {
        this.gateid = gateid;
    }
}
