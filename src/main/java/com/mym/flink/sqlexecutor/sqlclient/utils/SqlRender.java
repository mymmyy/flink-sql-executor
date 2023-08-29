package com.mym.flink.sqlexecutor.sqlclient.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRender {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public static String render(String sqlTemplate, Map<String, String> variables) {
        Matcher matcher = PATTERN.matcher(sqlTemplate);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (variables.containsKey(key)) {
                matcher.appendReplacement(sb, variables.get(key));
            } else {
                throw new RuntimeException("Variable " + key + " not found");
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        String sql = "SELECT * FROM users WHERE id = ${id} AND name = ${name}";

        Map<String, String> vars = new HashMap<>();
        vars.put("id", "123");
        vars.put("name", "'John'");

        String renderedSQL = render(sql, vars);
        System.out.println(renderedSQL);  // Outputs: SELECT * FROM users WHERE id = 123 AND name = 'John'
    }
}
