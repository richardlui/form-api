package com.pegasus.form.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "company", "date", "lineItems"})
public class PackingList {
    private String company;
    private String date;
    private List<LineItem> lineItems;
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public List<LineItem> getLineItems() {
        return lineItems;
    }
    
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
