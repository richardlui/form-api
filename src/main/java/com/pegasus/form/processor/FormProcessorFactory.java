package com.pegasus.form.processor;

public class FormProcessorFactory {
    
    public static FormProcessorV2 getInstance(String companyCode) {
        if (companyCode.equalsIgnoreCase("prostretch")) {
            return new ProStretchProcessor();
        }
        return null;
    }
}
