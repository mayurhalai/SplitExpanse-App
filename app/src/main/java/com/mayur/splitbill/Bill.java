package com.mayur.splitbill;

/**
 * Created by Mayur on 6/29/2015.
 */
public class Bill {
    private String id, amount, description, date;
    public Bill(String d_id, String d_amount, String d_description, String d_date) {
        this.id = d_id;
        this.amount = d_amount;
        this.description = d_description;
        this.date = d_date;
    }
    public String getId() {
        return id;
    }
    public String getAmount() {
        return amount;
    }
    public String getDescription() {
        return description;
    }
    public String getDate() {
        return date;
    }
    public void setId(String d_id) {
        this.id = d_id;
    }
    public void setAmount(String d_amount) {
        this.amount = d_amount;
    }
    public void setDescription(String d_description) {
        this.description = d_description;
    }
    public void setDate(String d_date) {
        this.date = d_date;
    }
}
