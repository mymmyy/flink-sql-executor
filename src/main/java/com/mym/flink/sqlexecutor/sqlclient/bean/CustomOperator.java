package com.mym.flink.sqlexecutor.sqlclient.bean;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

public interface CustomOperator {

    /**
     * 处理流，并产生新的数据流
     *
     * @param dataStream 上游数据流
     * @return {@link DataStream}<{@link Row}>
     */
    public DataStream<Row> buildStream(DataStream<Row> dataStream);

    /**
     * 得到参数Map
     *
     * @return {@link Map}<{@link String}, {@link String}>
     */
    public Map<String, String> getParamMap();

}
