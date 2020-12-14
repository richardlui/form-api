package com.pegasus.form.property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PropertyManager {

    private static final String DEFAULT_PROPERTY_FILE = "application.properties";
    private static Map<String, String> propertyCache = new HashMap<>();
    
    public static String getProperty(String key) {
        return propertyCache.get(key);
    }
    
    public static void load() {
        InputStream is =  PropertyManager.class.getResourceAsStream("/" + DEFAULT_PROPERTY_FILE);
        try (
            BufferedReader br
               = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] token = line.split("=");
                propertyCache.put(token[0], token[1]);
                System.out.println("Input line: " + line);
            }
        }
        catch (IOException e) {
            System.out.println("Error opening file");
        }
    }
}
