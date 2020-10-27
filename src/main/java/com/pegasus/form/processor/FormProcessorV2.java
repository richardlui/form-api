package com.pegasus.form.processor;

import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.Container;
import com.pegasus.form.model.PackingList;

import java.util.List;
import java.util.Map;

public abstract class FormProcessorV2 {
    
    private String jsonResult;
    private Map<String, String> labels;
    private List<RecognizedForm> forms;
    private PackingList plist;
    private Container container;
    
    public FormProcessorV2(String result) {
        this.jsonResult = result;
    }
    
    public FormProcessorV2(List<RecognizedForm> forms) {
        this.forms = forms;
    }
    
    public PackingList getPackingList() {
        return plist;
    }
    
    public Container getContainer() {
        return container;
    }
    
    public void process() {
        labels = extractLabel(forms);
        container = extractLineItems(forms, labels);
        //plist = new PackingList();
        //plist.setCompany(labels.get("sellerName"));
        //plist.setDate(labels.get("issueDate"));
    }
    
    public String getJsonResult() {
        return jsonResult;
    }
    
    abstract Map<String, String> extractLabel(List<RecognizedForm> forms);
    abstract Container extractLineItems(List<RecognizedForm> forms, Map<String, String>labels);
}
