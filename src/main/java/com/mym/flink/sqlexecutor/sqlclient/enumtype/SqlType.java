
package com.mym.flink.sqlexecutor.sqlclient.enumtype;

import java.util.regex.Pattern;

public enum SqlType {
    SELECT("SELECT", "^SELECT.*"),

    CREATE("CREATE", "^CREATE(?!.*AS SELECT).*$"),

    DROP("DROP", "^DROP.*"),

    ALTER("ALTER", "^ALTER.*"),

    INSERT("INSERT", "^INSERT.*"),

    DESC("DESC", "^DESC.*"),

    DESCRIBE("DESCRIBE", "^DESCRIBE.*"),

    EXPLAIN("EXPLAIN", "^EXPLAIN.*"),

    USE("USE", "^USE.*"),

    SHOW("SHOW", "^SHOW.*"),

    LOAD("LOAD", "^LOAD.*"),

    UNLOAD("UNLOAD", "^UNLOAD.*"),

    SET("SET", "^SET.*"),

    RESET("RESET", "^RESET.*"),

    EXECUTE("EXECUTE", "^EXECUTE.*"),
    ADD_JAR("ADD_JAR", "^ADD\\s+JAR\\s+\\S+"),
    ADD("ADD", "^ADD\\s+CUSTOMJAR\\s+\\S+"),

    WATCH("WATCH", "^WATCH.*"),

    CTAS("CTAS", "^CREATE\\s.*AS\\sSELECT.*$"),

    UNKNOWN("UNKNOWN", "^UNKNOWN.*");

    private String type;
    private Pattern pattern;

    SqlType(String type, String regrex) {
        this.type = type;
        this.pattern = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean match(String statement) {
        return pattern.matcher(statement).matches();
    }
}
