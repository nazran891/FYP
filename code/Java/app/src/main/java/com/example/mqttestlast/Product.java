package com.example.mqttestlast;

public class Product {
    private int id;
    private String name;
    private String dateA;
    private String timeB;
    private String amount;

    public Product(int id, String name, String dateA, String timeB, String amount) {
        this.id = id;
        this.name = name;
        this.dateA = dateA;
        this.timeB = timeB;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateA() {
        return dateA;
    }

    public String getTimeB() {
        return timeB;
    }

    public String getAmount() {
        return amount;
    }
}
