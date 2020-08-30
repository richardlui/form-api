package com.pegasus.form;

import com.pegasus.form.model.LineItem;

import java.util.List;

public abstract class ScanProcessor {
    
    private String jsonResult;
    private String poNumber;
    private List<LineItem> lineItems;
    
    public ScanProcessor(String result) {
        this.jsonResult = result;
    }
    
    public String getPoNumber() {
        return poNumber;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void process() {
        poNumber = extractPONumber();
        lineItems = extractLineItems();
    }
    
    public String getJsonResult() {
        return jsonResult;
    }
    
    abstract String extractPONumber();
    abstract List<LineItem> extractLineItems();
    
}
