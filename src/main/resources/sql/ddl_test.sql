--# 格式说明：
--# ！！！重要重要重要 "--#" 是当作下一条sql的注释。所有注释都应该用这样的格式。支持多行注释但都必须以此格式开头。
--# 0. sql以";"结尾，建议单独一行。系统将以";"切分sql。
--# 1. ddl_开头文件的sql会率先执行，故建议把source、sink等外部连接表的create sql在此文件中定义
--# 2. 非ddl_开头的文件会根据sql顺序执行去构建流的graph
--# 3. sql注释建议主动设置（如果是sql注释，如下说明可不显示注释，但系统会自动设置随机值，其中dependentTable会自动解析但准确性不能保证100%）
--#     3.1 tableName（作为临时表名，下一个算子可以直接读取，不支持中文），例如tableName=t1
--#     3.2 nodeName（作为SQL节点名，不支持中文）,例如 nodeName=n1
--#     3.3 dependentTable（依赖表名，用<>包裹）,例如 dependentTable=<t1>
--#     3.4 alias（sql别名）,例如 alias=a1
--# 4. 自定义算子可以在sql文件中把自定义算子类全路径名当作sql，并建议显示设置第三点中的关键配置项（tableName、nodeName、dependentTable）
--# 5. 自定义算子包名必须以com.leiting开头
--# 6. sql中读取变量通过${变量}方式
--# 关于注释的保留字符：
--#     英文逗号","用于切分不同注释短语，从短语中提取关键注释信息；
--#     "="用于切分注释短语的关键词名和值；
--#     英文分号";"当作sql切分字符，不可在注释中出现

--# afds
--# alias=eeeee, nodeName=node1
select 1;

--# tableName=t2, gfdgdgsfafsaf
select 2;