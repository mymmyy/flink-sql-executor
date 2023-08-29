package com.mym.flink.sqlexecutor.sqlclient;

import com.mym.flink.sqlexecutor.sqlclient.enumtype.GraphNodeOptions;
import com.mym.flink.sqlexecutor.sqlclient.graph.GraphNode;
import com.mym.flink.sqlexecutor.sqlclient.utils.GraphNodeParser;
import com.mym.flink.sqlexecutor.sqlclient.utils.SqlFileReader;
import com.mym.flink.sqlexecutor.sqlclient.utils.SqlRender;
import com.mym.flink.sqlexecutor.sqlclient.bean.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SqlClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqlClient.class);

    private final List<SqlUdfDescriptor> udfDescriptors = new ArrayList<>();

    private final List<CustomOperatorDescriptor> userCustomOperatorDescriptors = new ArrayList<>();

    private final AbstractTaskAspectsDescriptor taskAspectsDescriptor;

    public SqlClient(AbstractTaskAspectsDescriptor taskAspectsDescriptor) {
        this.taskAspectsDescriptor = taskAspectsDescriptor == null ? new DefaultEmptyTaskAspectsDescriptor() : taskAspectsDescriptor;
    }

    public SqlClient() {
        this.taskAspectsDescriptor =  new DefaultEmptyTaskAspectsDescriptor();
    }

    public void execute(String[] args) throws Exception {
        execute(args, null);
    }

    public void execute(String[] args, String baseFilePath) throws Exception {
        /* parse outer param */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        Map<String, String> paramMap = parameterTool.toMap();
        taskAspectsDescriptor.beforeParseParam(args, this);
        taskAspectsDescriptor.registerUdf(this);
        taskAspectsDescriptor.registerCustomOperator(this);

        /* parse execute info */
        Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> ddldmlTuple2 = baseFilePath == null ? SqlFileReader.loadSql() : SqlFileReader.loadSql(baseFilePath);
        Tuple2<LinkedList<GraphNode>, LinkedList<GraphNode>> ddldmlGraphNodeTuple2 = GraphNodeParser.parseToGraphNode(ddldmlTuple2, paramMap);

        List<CustomOperatorGraphNode> userCodeSetGraphNode = userCustomOperatorDescriptors.stream()
                .map(v -> GraphNodeParser.parseCustomOperatorGraphNode(v, paramMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // TODO 加到依赖节点后面，保证依赖有序
        ddldmlGraphNodeTuple2.f1.addAll(userCodeSetGraphNode);
        GraphNodeParser.parseNodeContact(ddldmlGraphNodeTuple2);
        LOGGER.info("parse execute info over.");

        /* init env */
        StreamingExecuteOption executeOption = new StreamingExecuteOption(parameterTool, "sql-job-" + UUID.randomUUID(), new JobEnvConfig());
        StreamTableEnvironment tableEnvironment = executeOption.getTableEnvironment();
        for (SqlUdfDescriptor udf : udfDescriptors) {
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(udf.getClassFullName());
            tableEnvironment.createTemporarySystemFunction(udf.getCallName(), (Class<? extends UserDefinedFunction>) aClass);
            LOGGER.info("register udf: {}", udf);
        }
        LOGGER.info("init env and register udf over.");

        /* render sql */
        ddldmlGraphNodeTuple2.f0.stream().filter(v -> v.getGraphNodeOptions() == GraphNodeOptions.SQL).forEach(v -> {
            SqlGraphNode sqlGraphNode = (SqlGraphNode)v;
            sqlGraphNode.getStatement().setSql(SqlRender.render(sqlGraphNode.getStatement().getSql(), paramMap));
            LOGGER.info("rend sql, nodeName:{}, temporaryTableName:{}, sql:\n{}", sqlGraphNode.getNodeName(), sqlGraphNode.getTemporaryTableName(), sqlGraphNode.getStatement().getSql());
        });
        ddldmlGraphNodeTuple2.f1.stream().filter(v -> v.getGraphNodeOptions() == GraphNodeOptions.SQL).forEach(v -> {
            SqlGraphNode sqlGraphNode = (SqlGraphNode)v;
            sqlGraphNode.getStatement().setSql(SqlRender.render(sqlGraphNode.getStatement().getSql(), paramMap));
            LOGGER.info("rend sql, nodeName:{}, temporaryTableName:{}, sql:\n{}", sqlGraphNode.getNodeName(), sqlGraphNode.getTemporaryTableName(), sqlGraphNode.getStatement().getSql());
        });
        LOGGER.info("parse outer param and render sql over.");

        /* build execute graph */
        // load customOperatorGraphNode
        Map<String, CustomOperatorGraphNode> customOperatorGraphNodeMap = new HashMap<>();
        ddldmlGraphNodeTuple2.f0.stream().parallel().filter(v -> v.getGraphNodeOptions() == GraphNodeOptions.CODE_OPERATOR).forEach(v -> {
            v.getDependentTable().forEach(dt -> customOperatorGraphNodeMap.put(dt, (CustomOperatorGraphNode)v));
        });
        ddldmlGraphNodeTuple2.f1.stream().parallel().filter(v -> v.getGraphNodeOptions() == GraphNodeOptions.CODE_OPERATOR).forEach(v -> {
            v.getDependentTable().forEach(dt -> customOperatorGraphNodeMap.put(dt, (CustomOperatorGraphNode)v));
        });

        // ddl execute graph
        for (GraphNode graphNode : ddldmlGraphNodeTuple2.f0) {
            buildExecuteGraph(tableEnvironment, customOperatorGraphNodeMap, graphNode);
        }

        // dml execute graph
        for (GraphNode graphNode : ddldmlGraphNodeTuple2.f1) {
            buildExecuteGraph(tableEnvironment, customOperatorGraphNodeMap, graphNode);
        }
        LOGGER.info("build execute graph over.");

        /* execute */
        taskAspectsDescriptor.beforeExecute(args, this);
        executeOption.execute();
        taskAspectsDescriptor.afterExecute(args, this);
        LOGGER.info("submit execute.");
    }

    private void buildExecuteGraph(StreamTableEnvironment tableEnvironment, Map<String, CustomOperatorGraphNode> customOperatorGraphNodeMap, GraphNode graphNode) {
        if(graphNode.getGraphNodeOptions() == GraphNodeOptions.SQL){
            SqlGraphNode sqlGraphNode = (SqlGraphNode) graphNode;
            if(customOperatorGraphNodeMap.containsKey(sqlGraphNode.getTemporaryTableName())){
                Table table = tableEnvironment.sqlQuery(sqlGraphNode.getStatement().getSql());
                CustomOperatorGraphNode customOperatorGraphNode = customOperatorGraphNodeMap.get(sqlGraphNode.getTemporaryTableName());
                DataStream<Row> rowDataStream = tableEnvironment.toDataStream(table);
                DataStream<Row> dataStream = customOperatorGraphNode.getCustomOperator().buildStream(rowDataStream);
                tableEnvironment.createTemporaryView(customOperatorGraphNode.getTemporaryTableName(), dataStream);
            } else {
                tableEnvironment.executeSql(sqlGraphNode.getStatement().getSql().replaceAll(";", ""));
            }
        }
    }

    public void registerUdf(SqlUdfDescriptor udfDescriptor){
        udfDescriptors.add(udfDescriptor);
    }

    public void registerCustomOperator(CustomOperatorDescriptor customOperatorDescriptor){
        userCustomOperatorDescriptors.add(customOperatorDescriptor);
    }

}
