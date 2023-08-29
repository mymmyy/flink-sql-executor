package com.mym.flink.sqlexecutor.sqlclient.example;

import com.mym.flink.sqlexecutor.sqlclient.bean.AbstractCustomOperator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.types.Row;


public class ExampleCustomOperator extends AbstractCustomOperator {
    @Override
    public DataStream<Row> buildStream(DataStream<Row> dataStream) {
        SingleOutputStreamOperator<Row> resultDataStream = dataStream.map(v -> {
            System.out.println("example custom operator, v = " + v);
            return v;
        });

        // other process
        // ......

        return resultDataStream;
    }

}
