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

    private final AbstractTaskAspectsDescriptor userTaskAspectsDescriptor;

    private final AbstractTaskAspectsDescriptor defaultTaskAspectsDescriptor = new DefaultTaskAspectsDescriptor();

    public SqlClient(AbstractTaskAspectsDescriptor taskAspectsDescriptor) {
        this.userTaskAspectsDescriptor = taskAspectsDescriptor == null ? new DefaultEmptyTaskAspectsDescriptor() : taskAspectsDescriptor;
    }

    public SqlClient() {
        this(new DefaultEmptyTaskAspectsDescriptor());
    }

    /**
     * 执行
     * 默认读resource的sql目录
     *
     * @param args arg参数
     * @throws Exception 异常
     */
    public void execute(String[] args) throws Exception {
        execute(args, "sql");
    }

    /**
     * 执行
     *
     * @param args         args
     * @param baseFilePath sql文件目录。如果在jar的resource目录，则不需要/开头，如果在其他地方，需要绝对路径；为null默认读resource的sql目录
     * @throws Exception 异常
     */
    public void execute(String[] args, String baseFilePath) throws Exception {
        /* parse outer param */
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        Map<String, String> argsParamMap = parameterTool.toMap();
        Map<String, String> paramMap = new HashMap<>(argsParamMap);
        Optional.ofNullable(defaultTaskAspectsDescriptor.configProgramParam(args, this)).ifPresent(paramMap::putAll);
        Optional.ofNullable(userTaskAspectsDescriptor.configProgramParam(args, this)).ifPresent(paramMap::putAll);
        defaultTaskAspectsDescriptor.beforeParseParam(args, this);
        defaultTaskAspectsDescriptor.registerUdf(this);
        defaultTaskAspectsDescriptor.registerCustomOperator(this);
        userTaskAspectsDescriptor.beforeParseParam(args, this);
        userTaskAspectsDescriptor.registerUdf(this);
        userTaskAspectsDescriptor.registerCustomOperator(this);
        LOGGER.info("task program param:{}", paramMap);

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
        JobEnvConfig jobEnvConfig = defaultTaskAspectsDescriptor.configJobEnvSetting(args, this);
        jobEnvConfig = jobEnvConfig == null ? new JobEnvConfig() : jobEnvConfig;
        jobEnvConfig.merge(userTaskAspectsDescriptor.configJobEnvSetting(args, this));

        StreamingExecuteOption executeOption = new StreamingExecuteOption(paramMap, "sql-job-" + UUID.randomUUID(), jobEnvConfig);
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
        defaultTaskAspectsDescriptor.beforeExecute(args, this);
        userTaskAspectsDescriptor.beforeExecute(args, this);
        try {
            executeOption.execute();
        } catch (IllegalStateException e){
            LOGGER.warn("executeOption execute catch a IllegalStateException error, if the task is a FlinkSQL task, you just ignore it. if not, you must check problem!", e);
            if(!"No operators defined in streaming topology. Cannot execute.".equals(e.getMessage())){
                throw e;
            }
        }
        defaultTaskAspectsDescriptor.afterExecute(args, this);
        userTaskAspectsDescriptor.afterExecute(args, this);
        LOGGER.info("submit execute.");
    }

    private void buildExecuteGraph(StreamTableEnvironment tableEnvironment, Map<String, CustomOperatorGraphNode> customOperatorGraphNodeMap, GraphNode graphNode) {
        try {
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
        } catch (Exception e){
            if(graphNode.getGraphNodeOptions() == GraphNodeOptions.SQL){
                SqlGraphNode sqlGraphNode = (SqlGraphNode) graphNode;
                LOGGER.error("build execute graph occur error!, sqlGraphNode, nodeName:{}, temporaryTableName:{}, \n sql:{} \n errorMsg:{}", sqlGraphNode.getNodeName(), sqlGraphNode.getTemporaryTableName(), sqlGraphNode.getStatement().getSql(), e.getMessage());
            } else {
                LOGGER.error("build execute graph occur error!, graphNode:{},  errorMsg:{}", graphNode, e.getMessage());
            }
            throw e;
        }
    }

    public void registerUdf(SqlUdfDescriptor udfDescriptor){
        udfDescriptors.add(udfDescriptor);
    }

    public void registerCustomOperator(CustomOperatorDescriptor customOperatorDescriptor){
        userCustomOperatorDescriptors.add(customOperatorDescriptor);
    }

}
