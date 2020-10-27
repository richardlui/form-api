package com.pegasus.form.processor;

import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.LineItem;
import com.pegasus.form.model.PackingList;

import java.util.List;
import java.util.Map;

public abstract class FormProcessor {
    
    private String jsonResult;
    private Map<String, String> labels;
    private List<LineItem> lineItems;
    private List<RecognizedForm> forms;
    private PackingList plist;
    
    public FormProcessor(String result) {
        this.jsonResult = result;
    }
    
    public FormProcessor(List<RecognizedForm> forms) {
        this.forms = forms;
    }
    
    public PackingList getPackingList() {
        return plist;
    }
    
    public void process() {
        labels = extractLabel(forms);
        lineItems = extractLineItems(forms);
        plist = new PackingList();
    }
    
    public String getJsonResult() {
        return jsonResult;
    }
    
    abstract Map<String, String> extractLabel(List<RecognizedForm> forms);
    abstract List<LineItem> extractLineItems(List<RecognizedForm> forms);
}
