package com.example.im_exam_m3;

public class TransactionData {
    private int amount;
    private int contractID;
    private String date;
    private int ID;
    private String mode;
    private String type;

    private String name;

    // Constructor
    public TransactionData(int ID, int contractID,String name, String type, String mode, String date, int amount) {
        this.ID = ID;
        this.contractID = contractID;
        this.name = name;
        this.type = type;
        this.mode = mode;
        this.date = date;
        this.amount = amount;
    }

    // Getters and setters for amount
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    // Getters and setters for contractID
    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    // Getters and setters for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getters and setters for ID
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    // Getters and setters for mode
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    // Getters and setters for type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // Getters and setters for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
