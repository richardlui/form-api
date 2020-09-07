package com.pegasus.form.processor;

import java.util.List;
import java.util.Map;

import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.pegasus.form.model.LineItem;

public class IdealProcessor extends FormProcessor {

    public IdealProcessor(String result) {
        super(result);
    }
    
    public IdealProcessor(List<RecognizedForm> forms) {
        super(forms);
    }
    
    @Override
    public Map<String, String> extractLabel(List<RecognizedForm> forms) {
        return null;
    }
    
    @Override
    public List<LineItem> extractLineItems(List<RecognizedForm> forms) {
        return null;
    }
    
}
