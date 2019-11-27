
MyBatis可以使用注解和xml配置实现ORM。可以使用插件自动生成mapper.xml,mapper.java 和po等

$适用于后台传参数，#启用预编译，防止sql注入，适用于客户传参数

parameterType 与parameterMap区别：parameterType 指定输入参数的类型，类型可以是简单类型、hashmap、pojo 的包装类型，

resultType 与 resultMap 区别:resultType 进行输出映射，只有查询出来的列名和 pojo 中的属性名一致，该列才可映射成功。mybatis中使用resultMap完成高级输出结果映射。

核心概念：

Configuration管理mysql-config.xml全局配置关系类

SqlSessionFactory  Session 管理工厂接口

Session SqlSession 是一个面向用户(程序员)的接口。SqlSession 中提 供了很多操作数据库的方法

Executor 执行器是一个接口(基本执行器、缓存执行器) 作用:SqlSession 内部通过执行器操作数据库

MappedStatement 底层封装对象 作用:对操作数据库存储封装，包括 sql 语句、输入输出参数

StatementHandler 具体操作数据库相关的 handler 接口

ResultSetHandler 具体操作数据库返回结果的 handler 接口


PreparedStatement
Statement
连接方式的区别
一级缓存二级缓存和缓存淘汰
