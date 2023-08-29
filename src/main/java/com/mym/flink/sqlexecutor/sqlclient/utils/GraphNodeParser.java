package com.mym.flink.sqlexecutor.sqlclient.utils;

import com.mym.flink.sqlexecutor.sqlclient.bean.CustomOperator;
import com.mym.flink.sqlexecutor.sqlclient.bean.CustomOperatorDescriptor;
import com.mym.flink.sqlexecutor.sqlclient.bean.CustomOperatorGraphNode;
import com.mym.flink.sqlexecutor.sqlclient.bean.SqlGraphNode;
import com.mym.flink.sqlexecutor.sqlclient.graph.GraphNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Preconditions;

import java.util.LinkedList;
import java.util.Map;

public class GraphNodeParser {

    private static final String CUSTOM_OPERATOR_CLASS_NAME_PREFIX = "com.leiting";


    /**
     * 解析节点间联系，记录到各个节点的last和next依赖中
     *
     * @param ddldmlTuple2             ddldml tuple2
     */
    public static void parseNodeContact(Tuple2<LinkedList<GraphNode>, LinkedList<GraphNode>> ddldmlTuple2){
        // f0 ddl Node，f1 dml Node
        Map<String, GraphNode> searchMap = new HashedMap();
        ddldmlTuple2.f0.forEach(v -> searchMap.put(v.getTemporaryTableName(), v));
        ddldmlTuple2.f1.forEach(v -> searchMap.put(v.getTemporaryTableName(), v));

        for (GraphNode graphNode : ddldmlTuple2.f1) {
            if (CollectionUtils.isEmpty(graphNode.getDependentTable())) {
                // TODO 主动根据sql内容解析依赖
                continue;
            }

            for (String t : graphNode.getDependentTable()) {
                Preconditions.checkArgument(searchMap.containsKey(t), "Node name of "+ graphNode.getNodeName() + " dependent table " + t + " not exists!");
                graphNode.getLastNode().add(searchMap.get(t));
            }
        }
    }

    /**
     * 转换成GraphNode节点：sql中配置的自定义算子也将被解析成对应实例
     *
     * @param ddldmlTuple2 ddldml tuple2
     * @return {@link Tuple2}<{@link LinkedList}<{@link GraphNode}>, {@link LinkedList}<{@link GraphNode}>>
     * @throws ClassNotFoundException 类没有发现异常
     * @throws InstantiationException 实例化异常
     * @throws IllegalAccessException 非法访问异常
     */
    public static Tuple2<LinkedList<GraphNode>, LinkedList<GraphNode>> parseToGraphNode(Tuple2<LinkedList<SqlGraphNode>, LinkedList<SqlGraphNode>> ddldmlTuple2, Map<String, String> paramMap) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Tuple2<LinkedList<GraphNode>, LinkedList<GraphNode>> resultTuple2 = new Tuple2<>();
        resultTuple2.f0 = new LinkedList<>();
        resultTuple2.f1 = new LinkedList<>();
        resultTuple2.f0.addAll(ddldmlTuple2.f0);

        for (SqlGraphNode sqlGraphNode : ddldmlTuple2.f1) {
            if(sqlGraphNode.getStatement().getSql().startsWith(CUSTOM_OPERATOR_CLASS_NAME_PREFIX)){
                CustomOperatorGraphNode customOperatorGraphNode = new CustomOperatorGraphNode(sqlGraphNode.getNodeName(), sqlGraphNode.getDesc(), sqlGraphNode.getTemporaryTableName());
                Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(sqlGraphNode.getStatement().getSql().replaceAll(";", ""));
                CustomOperator customOperator = (CustomOperator) aClass.newInstance();
                customOperator.getParamMap().putAll(paramMap);
                customOperatorGraphNode.getDependentTable().addAll(sqlGraphNode.getDependentTable());
                customOperatorGraphNode.setCustomOperator(customOperator);
                resultTuple2.f1.add(customOperatorGraphNode);
            } else {
                resultTuple2.f1.add(sqlGraphNode);
            }
        }


        return resultTuple2;
    }

    public static CustomOperatorGraphNode parseCustomOperatorGraphNode(CustomOperatorDescriptor descriptor, Map<String, String> paramMap){
        if(descriptor == null || descriptor.getCustomOperator() == null){
            return null;
        }
        CustomOperatorGraphNode customOperatorGraphNode = new CustomOperatorGraphNode(descriptor.getName(), descriptor.getDesc(), descriptor.getResultAsTable());
        descriptor.getCustomOperator().getParamMap().putAll(paramMap);
        customOperatorGraphNode.setCustomOperator(descriptor.getCustomOperator());
        customOperatorGraphNode.getDependentTable().addAll(descriptor.getDependentTable());
        return customOperatorGraphNode;
    }


}
