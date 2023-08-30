package com.mym.flink.sqlexecutor.sqlclient.udf;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.table.functions.ScalarFunction;

public class JsonValueUDF extends ScalarFunction {

    /**
     * eval
     *
     * @param jsonString json字符串
     * @param keyPath    key路径  如 $.name 表示取name值。只支持第一级
     * @return {@link String}
     */
    public String eval(String jsonString, String keyPath){
        try {
            if(jsonString == null || keyPath == null || "".equals(jsonString) || "".equals(keyPath)){
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            Object o = jsonObject.get(keyPath.substring(2));
            if (o == null){
                return null;
            } else {
                return String.valueOf(o);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("parse json error! jsonString:" + jsonString);
        }
        return "";
    }

}
