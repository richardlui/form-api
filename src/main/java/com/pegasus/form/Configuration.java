package com.pegasus.form;

import java.util.HashMap;
import java.util.Map;

import com.pegasus.form.property.PropertyManager;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Configuration {
    private static String FORM_API_KEY = PropertyManager.getProperty("form.api.key");
    private static String FORM_ENDPOINT = PropertyManager.getProperty("form.api.endpoint");
    private static String FORM_BLOB_CONTAINER_URL = PropertyManager.getProperty("form.blob.container.url");
    
    private static Map<String, String> MODEL_ID_MAP= new HashMap<>();
    
    static {
        // 2.1 model
        //MODEL_ID_MAP.put("Ideal", "3bd942b1-c49a-4d20-99aa-de199e61fa15");
        MODEL_ID_MAP.put("prostretch", "2efeb896-b0f0-41bf-a9e6-9908b4362b30");
       
    }
    
    public static String getModelId(String company) {
        return MODEL_ID_MAP.get(company);
    }
    
    public static String getFormEndPoint() {
        return FORM_ENDPOINT;
    }

    public static String getFormApiKey() {
        return FORM_API_KEY;
    }

    public static String getFormBlobContainerUrl() {
        return FORM_BLOB_CONTAINER_URL;
    }
}
