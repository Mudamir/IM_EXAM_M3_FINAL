package com.example.im_exam_m3;

public class CustomerDetails_Data {

    private int customerId;
    private String name;
    private String contact;
    private String employment;
    private String city;
    private String address;

    public CustomerDetails_Data(int customerId, String name, String contact, String city, String employment, String address) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
        this.city = city;
        this.employment = employment;
        this.address = address;
    }

    // Getter for 'customerId'
    public int getCustomerId() {
        return customerId;
    }

    // Getter and setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for 'contact'
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // Getter and setter for 'employment'
    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    // Getter and setter for 'city'
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter and setter for 'address'
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
