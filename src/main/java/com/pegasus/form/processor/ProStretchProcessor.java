package com.pegasus.form.processor;

import java.util.List;

import com.azure.ai.formrecognizer.models.RecognizedForm;

public class ProStretchProcessor extends FormProcessor {

    public ProStretchProcessor(String result) {
        super(result);
    }
    
    public ProStretchProcessor(List<RecognizedForm> forms) {
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
