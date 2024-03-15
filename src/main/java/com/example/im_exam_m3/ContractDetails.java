package com.example.im_exam_m3;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ContractDetails {
    private final SimpleIntegerProperty Contract_ID;
    private final SimpleStringProperty Customer_name;
    private final SimpleStringProperty Room_ID;
    private final SimpleStringProperty Contract_type;
    private final SimpleStringProperty Date_start;
    private final SimpleStringProperty Date_end;

    public ContractDetails(int Contract_ID, String Customer_name, String Room_ID, String Contract_type, String Date_start, String Date_end) {
        this.Contract_ID = new SimpleIntegerProperty(Contract_ID);
        this.Customer_name = new SimpleStringProperty(Customer_name);
        this.Room_ID = new SimpleStringProperty(Room_ID);
        this.Contract_type = new SimpleStringProperty(Contract_type);
        this.Date_start = new SimpleStringProperty(Date_start);
        this.Date_end = new SimpleStringProperty(Date_end);
    }

    // Getter and setter for Contract_ID
    public int getContract_ID() {
        return Contract_ID.get();
    }

    public void setContract_ID(int Contract_ID) {
        this.Contract_ID.set(Contract_ID);
    }

    // Getter and setter for Customer_name
    public String getCustomer_name() {
        return Customer_name.get();
    }

    public void setCustomer_name(String Customer_name) {
        this.Customer_name.set(Customer_name);
    }

    // Getter and setter for Room_ID
    public String getRoom_ID() {
        return Room_ID.get();
    }

    public void setRoom_ID(String Room_ID) {
        this.Room_ID.set(Room_ID);
    }

    // Getter and setter for Contract_type
    public String getContract_type() {
        return Contract_type.get();
    }

    public void setContract_type(String Contract_type) {
        this.Contract_type.set(Contract_type);
    }

    // Getter and setter for Date_start
    public String getDate_start() {
        return Date_start.get();
    }

    public void setDate_start(String Date_start) {
        this.Date_start.set(Date_start);
    }

    // Getter and setter for Date_end
    public String getDate_end() {
        return Date_end.get();
    }

    public void setDate_end(String Date_end) {
        this.Date_end.set(Date_end);
    }
}
