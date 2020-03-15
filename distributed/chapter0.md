# 分布式概述
## 什么是分布式

## 分布式系统面临的挑战
1. 什么是分布式
1. 分布式的特点
1. 分布式技术
1. 分布式面临的问题
1. 进程与进程之间

## 一、单体架构
1. Jvm内部调用
2. 单点故障
## 二、集群架构
1. 分布式session问题》session center解决 redis
1. 数据库

## 读写分离 
降低主库的查询、业务：读多写少、主从同步

Proxy：mycat、altas、mysql-proxy

Jdbc：tddl、sharding-jdbc

换数据库

分库分表 不要把鸡蛋放在一个篮子里，本质：热点数据的问题

垂直/水平 

Proxy：mycat、altas

Jdbc：tddl、sharding-jdbc

云数据库（Tidb pingcap）
## 服务化
周四：商品部门上新、会员部门改bug

上线步骤：开发、测试、预演、生产

还有问题 回滚  会员回滚

业务垂直

RPC：远程调用

框架：dubbo、motan（分布式）

如果更多的应用

调用之间更复杂了

排查bug问题：elk（分布式专题）

依赖配置也是个问题：分布式配置中心 disconf（zk）  diamond（zk）（分布式专题）

消息中间件：异步、解耦、消峰

Kafka、rocketmq、activemq、rabbitmq
