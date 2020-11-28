# Mybatis源码解读

## 核心概念：

- Configuration管理mysql-config.xml全局配置关系类
- SqlSessionFactory  Session 管理工厂接口
- Session SqlSession 是一个面向用户(程序员)的接口。SqlSession 中提 供了很多操作数据库的方法
- Executor 执行器是一个接口(基本执行器、缓存执行器) 作用:SqlSession 内部通过执行器操作数据库
- MappedStatement 底层封装对象 作用:对操作数据库存储封装，包括 sql 语句、输入输出参数
- StatementHandler 具体操作数据库相关的 handler 接口
- ResultSetHandler 具体操作数据库返回结果的 handler 接口
