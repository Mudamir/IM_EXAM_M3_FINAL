package com.example.im_exam_m3;

public class RentHistoryData {
    private int customerID;
    private String roomID;
    private String startDate;
    private String endDate;
    private String name;

    // Constructor
    public RentHistoryData(int customerID, String roomID, String startDate, String endDate, String name) {
        this.customerID = customerID;
        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
    }

    // Getters and setters for customerID
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    // Getters and setters for roomID
    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    // Getters and setters for startDate
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // Getters and setters for endDate
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Getters and setters for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
