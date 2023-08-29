package com.mym.flink.sqlexecutor.sqlclient;

import com.mym.flink.sqlexecutor.sqlclient.bean.AbstractTaskAspectsDescriptor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sql通用执行程序器
 * <br/>
 * <br/> 若使用SqlExecutor作为mainClass：如有额外任务描述（AbstractTaskAspectsDescriptor），则在参数中添加-descriptorClass xxxDescriptor类全名。
 * <br/>
 * <br/> 若自定义类作为mainClass，则在main方法中实例化SqlClient并调用execute方法。
 *
 * @author maoym
 */
public class SqlExecutor {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqlExecutor.class);
    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            LOGGER.info("param args:{}", arg);
        }
        // -descriptorClass
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String descriptorClass = parameterTool.get("descriptorClass");
        LOGGER.info("param args descriptorClass:{}", descriptorClass);
        AbstractTaskAspectsDescriptor descriptor = null;
        if(descriptorClass != null){
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(descriptorClass);
            descriptor= (AbstractTaskAspectsDescriptor) aClass.newInstance();
        }

        new SqlClient(descriptor).execute(args);
    }
}
