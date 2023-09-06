package com.mym.flink.sqlexecutor.sqlclient.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {

    public static Map<String, String> readPropertiesConfig(String filePath) {
        try {
            URL resource = ConfigReader.class.getResource(filePath);
            if(resource == null){
                return null;
            }
            Map<String, String> result = new HashMap<String, String>();
            FileInputStream fis = new FileInputStream(resource.getFile());
            Properties properties = new Properties();
            properties.load(fis);
            for (Object o : properties.keySet()) {
                result.put(String.valueOf(o), properties.getProperty((String)o));
            }
            fis.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, String> stringStringMap = ConfigReader.readPropertiesConfig("/test.properties");
        System.out.println(stringStringMap);
    }
}
