package com.example.bloodbankapp.models;

public class Donation {
    private String bankName;
    private int units;
    private String date;
    private String status;

    public Donation(String bankName, int units, String date, String status) {
        this.bankName = bankName;
        this.units = units;
        this.date = date;
        this.status = status;
    }

    // getters
    public String getBankName() { return bankName; }
    public int getUnits() { return units; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
