package com.pegasus.form.model;

import java.util.HashMap;
import java.util.Map;

public enum CompanyEnum {
    
    IDEAL("Ideal Fastener Asia Ltd"),
    WILSON_GARMENT("Wilson Garment Accessories"),
    PRO_STRETCH_INTERNATIONAL("Pro-Stretch International Ltd");
    
    private String name;
    private final static Map<String, CompanyEnum> NAMES = new HashMap<>();
    
    static {
        for (CompanyEnum elem: values()) {
            NAMES.put(elem.getName(), elem);
        }
    }
    
    public String getName() {
        return name;
    }
    
    CompanyEnum(String name) {
        this.name = name;
    }
}
