# flink-sql-executor
一个通用执行FlinkSQL的jar封装，实现快速FlinkSQL作业开发。复杂逻辑支持自定义算子代码 + SQL 的混合模式开发。

# 功能介绍
## 已实现
- 通用执行FlinkSQL的jar
- 读取SQL文件（sql注释有一定格式要求方便解析上下游关系）（SQL文件可放在resource目录，即jar中）
- SQL + 自定义算子模式：无法用SQL实现的逻辑可通过实现自定义算子接口，继而通过代码实现相关逻辑。
- 支持UDF注册。
## 规划开发中
- 读取SQL文件远程目录
- SQL校验
- 作业开发时流图（开发时生成逻辑执行图，通过markdown格式打开可清晰看到作业节点之间关系）
- SQL血缘
- 其他。。。。

# 适用场景
- 实时计算较小的团队。
- 个人开发者。
- 不需要UI界面的SQL开发场景。

建议：如果FlinkSQL作业规划较多，团队规模较大，需要多租户形式等，可使用开源项目如Dinky、StreamPark等