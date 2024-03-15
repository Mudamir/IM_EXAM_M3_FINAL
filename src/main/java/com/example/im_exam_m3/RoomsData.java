package com.example.im_exam_m3;

public class RoomsData {

    private String Room_ID;
    private int Building_ID;
    private String Room_Type;
    private String Room_Status;
    private String Room_occupants; // Change data type to String
    private int Room_price;

    public RoomsData(String Room_ID, int Building_ID, String Room_Type, String Room_Status, String Room_occupants, int Room_price) {
        this.Room_ID = Room_ID;
        this.Building_ID = Building_ID;
        this.Room_Type = Room_Type;
        this.Room_Status = Room_Status;
        this.Room_occupants = Room_occupants;
        this.Room_price = Room_price;
    }

    public String getRoom_ID() {
        return Room_ID;
    }

    public void setRoom_ID(String room_ID) {
        Room_ID = room_ID;
    }

    public int getBuilding_ID() {
        return Building_ID;
    }

    public void setBuilding_ID(int building_ID) {
        Building_ID = building_ID;
    }

    public String getRoom_Type() {
        return Room_Type;
    }

    public void setRoom_Type(String room_Type) {
        Room_Type = room_Type;
    }

    public String getRoom_Status() {
        return Room_Status;
    }

    public void setRoom_Status(String room_Status) {
        Room_Status = room_Status;
    }

    public String getRoom_occupants() { // Change return type to String
        return Room_occupants;
    }

    public void setRoom_occupants(String room_occupants) { // Change parameter type to String
        Room_occupants = room_occupants;
    }

    public int getRoom_price() {
        return Room_price;
    }

    public void setRoom_price(int room_price) {
        Room_price = room_price;
    }
}