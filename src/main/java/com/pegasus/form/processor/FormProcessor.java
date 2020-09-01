package com.pegasus.form.processor;

import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.LineItem;

import java.util.List;

public abstract class FormProcessor {
    
    private String jsonResult;
    private String labels;
    private List<RecognizedForm> forms;
    private List<LineItem> lineItems;
    private String table;
    
    public FormProcessor(String result) {
        this.jsonResult = result;
    }
    
    public FormProcessor(List<RecognizedForm> forms) {
        this.forms = forms;
    }
    
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void process() {
        labels = extractLabel(forms);
        table = extractLineItems(forms);
    }
    
    public String getJsonResult() {
        return jsonResult;
    }
    
    abstract String extractLabel(List<RecognizedForm> forms);
    abstract String extractLineItems(List<RecognizedForm> forms);
    
}
