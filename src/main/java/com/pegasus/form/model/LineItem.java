package com.pegasus.form.model;

public class LineItem {
    private String itemNumber;
    private String description;
    private Double quantity;
    private Double amount;
    
    public String getItemNumber() {
        return itemNumber;
    }
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getQuantity() {
        return quantity;
    }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
