package com.pegasus.form.processor;

import java.util.HashMap;
import java.util.Map;

public class FormProcessorFactory {
    
    private static Map<String, FormProcessorV2> processorMap = new HashMap<>();
    static {
        processorMap.put("prostretch", new ProStretchProcessor());
    }

    public static FormProcessorV2 getInstance(String companyCode) {
        FormProcessorV2 formProcessor = processorMap.get(companyCode.toLowerCase());
        return formProcessor;
    }
}
