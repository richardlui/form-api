package com.pegasus.form;

import com.pegasus.form.model.LineItem;

import java.util.Arrays;
import java.util.List;

public class ProStretchProcessor extends ScanProcessor {

    public ProStretchProcessor(String result) {
        super(result);
    }
    
    @Override
    public String extractPONumber() {
        return "PO#32414141";
    }
    
    @Override
    public List<LineItem> extractLineItems() {
        return Arrays.asList(new LineItem());
    }
    
}
