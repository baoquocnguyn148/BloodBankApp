package com.example.bloodbankapp.models;

public class InventoryItem {
    private int id;
    private String bloodType;
    private int quantity;

    public InventoryItem(int id, String bloodType, int quantity) {
        this.id = id;
        this.bloodType = bloodType;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
