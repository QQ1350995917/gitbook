## [磁盘](disk/SUMMARY.md)

## 存储
- [块存储-对象存储-文件存储](01-block-object-file.md)
- [五大主流分布式存储技术对比分析](02-storage-pick.md)

## 文件存储
- [MinIO分布式存储](minio/SUMMARY.md)
  - [MinIO选型](minio/01-chooser.md)
  - [MinIO部署](minio/02-deploy.md)
  - [MinIO集群](minio/03-deploy-cluster.md)

## 数据库
### 关系型数据库
- 做表结构设计
- 主外键关联
- 数据强一致性
- 事务 
- 对性能和灵活性支持不够，对目前大数据业务支持不足。
#### SQL数据库
- [Oracle](oracle/SUMMARY.md)：功能强大，商用数据库，技术支持全面。
- [MySQL](mysql/SUMMARY.md)：互联网行业中最流行的数据库，这不仅仅是因为MySQL的免费。可以说关系数据库场景中你需要的功能，MySQL都能很好的满足。
- MariaDB：是MySQL的分支，由开源社区维护，MariaDB虽然被看作MySQL的替代品，但它在扩展功能、存储引擎上都有非常好的改进
- PostgreSQL：也叫PGSQL，PGSQL类似于Oracle的多进程框架，可以支持高并发的应用场景，PG几乎支持所有的SQL标准，支持类型相当丰富。PG更加适合严格的企业应用场景，而MySQL更适合业务逻辑相对简单、数据可靠性要求较低的互联网场景。

#### NewSQL数据库（新一代关系型数据库）
- TiDB：开源的分布式关系数据库，几乎完全兼容MySQL，能够支持水平弹性扩展、ACID事务、标准SQL、MySQL语法和MySQL协议，具有数据强一致的高可用特性。既适合在线事务处理，也适合在线分析处理。
- OceanBase：OceanBase是蚂蚁金服的数据库，OB是可以满足金融级的可靠性和数据一致性要求的数据库系统。当你需要使用事务，并且数据量比较大，就比较适合使用OB。不过目前OB已经商业化，不再开源。

### NoSql数据库(NoSQL=No Only SQL)
- 性能
- 灵活性
#### 文档型数据库
- [MongoDB](mongodb/SUMMARY.md)

  一个基于分布式文件存储的数据库，将数据存储为一个文档，数据结构由键值对组成。MongoDB比较适合表结构不明确，且数据结构可能不断变化的场景，不适合有事务和复杂查询的场景。
- [ES](elasticsearch/SUMMARY.md)

#### 时间序列数据库(Time Series Database)

#### KV数据库
- [Redis](redis/SUMMARY.md)
  
  提供了持久化能力，支持多种数据类型。Redis适用于数据变化快且数据大小可预测的场景。
  - [部署](redis/01-install.md)
  - [部署模式以及对比](redis/02-deploy-model.md)
  - [基本概念介绍](redis/03-concept.md)
  - [RedisMQ](redis/04-mq.md)
  - [其他](redis/chapter1.md)

- [Cassandra](cassandra/SUMMARY.md):一个高可靠的大规模分布式存储系统。支持分布式的结构化Key-value存储，以高可用性为主要目标。适合写多的场景，适合做一些简单查询，不适合用来做数据分析统计。
- Pika：一个可持久化的大容量类Redis存储服务， 兼容五种主要数据结构的大部分命令。Pika使用磁盘存储，主要解决Redis大容量存储的成本问题。

#### 图数据库
- [Neo4J]()

#### 列数据库
- [HBase](../hadoop/hadoop/SUMMARY.md)：建立在HDFS，也就是Hadoop文件系统之上的分布式面向列的数据库。类似于谷歌的大表设计，HBase可以提供快速随机访问海量结构化数据。在表中它由行排序，一个表有多个列族以及每一个列族可以有任意数量的列。 HBase依赖HDFS可以实现海量数据的可靠存储，适用于数据量大，写多读少，不需要复杂查询的场景。

## 大数据存储与处理 
1. ### [大数据架构方案](01-big-data-plan.md)
1. ### [Hadoop](hadoop/SUMMARY.md)
1. ### [HBase](hbase/SUMMARY.md)
1. ### [Hive](hive/SUMMARY.md)
1. ### [Impala](impala/SUMMARY.md)
1. ### [Storm](storm/SUMMARY.md)
1. ### [Spark](spark/SUMMARY.md)
