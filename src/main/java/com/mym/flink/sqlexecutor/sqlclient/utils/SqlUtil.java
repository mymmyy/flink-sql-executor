package com.mym.flink.sqlexecutor.sqlclient.utils;


import java.util.Map;

/**
 * SqlUtil
 *
 * @since 2021/7/14 21:57
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";

    private SqlUtil() {}

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("\\$\\{" + name + "\\}", value);
    }

    /**
     * replace sql context with values params, map's key is origin variable express by `${key}`,
     * value is replacement. for example, if key="name", value="replacement", and sql is "${name}",
     * the result will be "replacement".
     *
     * @param sql sql context
     * @param values replacement
     * @return replace variable result
     */
    public static String replaceAllParam(String sql, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sql = replaceAllParam(sql, entry.getKey(), entry.getValue());
        }
        return sql;
    }
}
