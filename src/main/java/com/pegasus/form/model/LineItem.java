package com.pegasus.form.model;

public class LineItem {
    private String poNumber;
    private String itemNumber;
    private String description;
    private String quantity;
    private String netWeight;
    private String grossWeight;
    private String color;
    
    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
    
    public LineItem withPoNumber(String poNumber) {
        this.poNumber = poNumber;
        return this;
    }

    public String getItemNumber() {
        return itemNumber;
    }
    
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
    
    public LineItem withItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LineItem withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public LineItem withQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }
    
    public LineItem withNetWeight(String netWeight) {
        this.netWeight = netWeight;
        return this;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public LineItem withGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
        return this;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public LineItem withColor(String color) {
        this.color = color;
        return this;
    }
}
