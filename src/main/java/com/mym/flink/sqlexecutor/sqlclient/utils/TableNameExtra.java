package com.mym.flink.sqlexecutor.sqlclient.utils;


import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerLexer;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableNameExtra {

    public static Set<String> extractTableNames(String sql) throws Exception {
        Set<String> tableNames = new HashSet<>();

        SqlParser.Config config = SqlParser.configBuilder()
                .setLex(Lex.MYSQL)
                .build();

        SqlParser sqlParser = SqlParser.create(sql, config);
        SqlNode sqlNode = sqlParser.parseQuery();

        // 使用SqlShuttle进行递归处理
        sqlNode.accept(new SqlShuttle() {
            @Override
            public SqlNode visit(SqlIdentifier id) {
                if (id.isSimple()) {
                    tableNames.add(id.getSimple());
                } else {
                    tableNames.add(id.names.get(0));  // 取最左侧的标识符，例如"schema.table"中的"schema"
                }
                return id;
            }

            @Override
            public SqlNode visit(SqlCall call) {
                if (call.getKind() == SqlKind.AS) {
                    // 对于别名，只处理原始名
                    return ((SqlBasicCall) call).getOperands()[0].accept(this);
                }
                return super.visit(call);
            }
        });

        return tableNames;
    }

    public static void main(String[] args) throws Exception {
        String sql = "SELECT a.* FROM (SELECT * FROM users WHERE name = 'John') AS a JOIN orders b ON a.id = b.user_id";
        System.out.println(extractTableNames(sql));  // [users, orders]
    }
}
