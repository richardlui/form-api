package com.pegasus.form;

import java.util.HashMap;
import java.util.Map;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Configuration {
    public static String FORM_API_KEY = "21e521f9cb774acd8f295fbcb986d064";
    public static String FORM_ENDPOINT = "https://form5354.cognitiveservices.azure.com/";
    public static String READ_API_KEY = "3c9c8481044648d5957f7bbb08377693";
    public static String READ_ENDPOINT = "https://ocr-dev-09238.cognitiveservices.azure.com/";
    public static String BLOB_BASE_PATH = "https://scan5354.blob.core.windows.net/scan/";    
    
    
    public static Map<String, String> MODEL_ID_MAP= new HashMap<>();
    
    static {
        MODEL_ID_MAP.put("1467_001.pdf", "23ff375d-bf51-4374-ac0f-9d7895a4fade");
        MODEL_ID_MAP.put("Ideal", "23ff375d-bf51-4374-ac0f-9d7895a4fade");
        MODEL_ID_MAP.put("1466_001.pdf", "657d5ca6-a751-44ef-9e4a-d9c29f5c1f59");
        MODEL_ID_MAP.put("1466_002.pdf", "657d5ca6-a751-44ef-9e4a-d9c29f5c1f59");
        MODEL_ID_MAP.put("Wilson", "657d5ca6-a751-44ef-9e4a-d9c29f5c1f59");
        MODEL_ID_MAP.put("1464_001.pdf", "5e37be0c-14db-4bd4-b773-69f7c6067580");
        MODEL_ID_MAP.put("1464_002.pdf", "5e37be0c-14db-4bd4-b773-69f7c6067580");

        // 2.1 model
        //MODEL_ID_MAP.put("Ideal", "3bd942b1-c49a-4d20-99aa-de199e61fa15");
        
    }
    
    public static String getModelId(String company) {
        return MODEL_ID_MAP.get(company);
    }
}
