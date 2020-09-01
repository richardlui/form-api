package com.pegasus.form.processor;

import java.util.List;

import com.azure.ai.formrecognizer.models.RecognizedForm;

public class IdealProcessor extends FormProcessor {

    public IdealProcessor(String result) {
        super(result);
    }
    
    public IdealProcessor(List<RecognizedForm> forms) {
        super(forms);
    }
    
    @Override
    public String extractLabel(List<RecognizedForm> forms) {
        return "PO#32414141";
    }
    
    @Override
    public String extractLineItems(List<RecognizedForm> forms) {
        return "table";
    }
    
}
