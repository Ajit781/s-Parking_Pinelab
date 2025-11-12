package data_objects.bean;

/**
 * Created by server1 on 6/14/2019.
 */

public class FreeParkingPriceType {
    private String StartTime;
    private String EndTime;
    private String Price;

    public FreeParkingPriceType() {
    }

    public FreeParkingPriceType(String startTime, String endTime, String price) {
        StartTime = startTime;
        EndTime = endTime;
        Price = price;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
