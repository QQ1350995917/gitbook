# Summary
* [Introduction](README.md)
* [IDE](ide/SUMMARY.md)
  * [CodeStyle]()
    * [Java](ide/code/java.md)
  * [Maven](ide/code/README.md)
    * [部署私服](ide/mvn/01-deploy-nexus.md)
    * [客户端安装](ide/mvn/02-installer.md)
    * [客户端设置](ide/mvn/03-setting.xml.md)
  * [Git](ide/git/README.md)
  * [Idea](ide/idea/README.md)
    * [Idea快捷键](ide/idea/README.md)
* [OS](os/SUMMARY.md)    
  * [Linux]()  
    * [User](os/linux/01-user.md)
    * [Group](os/linux/02-group.md)
    * [时钟同步](os/linux/03-ntp.md)
    * [Kill](os/linux/04-kill.md)
    * [CentOS](os/linux/centos/SUMMARY.md)
      * [yum-repo-offline](os/linux/centos/01-yum-repo-offline.md)
    * [Ubuntu](os/linux/01-user.md)
* [Network](network/basic/SUMMARY.md)
  * [网络基础知识](network/basic/SUMMARY.md)
    * [网卡、集成器、交换机、路由工作原理](network/basic/chapter0.md)
    * [网络协议](network/basic/chapter1.md)
    * [HTTP报文结构](network/basic/chapter2.md)
    * [HTTPS工作过程](network/basic/chapter3.md)
    * [关于UDP单播、组播、广播等概念](network/basic/chapter4.md)
    * [IP协议](network/basic/chapter5.md)
    * [Auth2.0以及SSO](network/basic/chapter6.md)
    * [DNS协议以及工作介绍](network/basic/chapter7.md)
    * [TCP三次握手四次回收](network/basic/chapter8.md)
    * [TCP连接数](network/basic/chapter9.md)
    * [网络掩码](network/basic/chapter10.md)
    * [网络工具](network/basic/chapter11.md)
  * [Netty]()
  * [Jersey]()
  * [Socket](network/socket/SUMMARY.md)
    * [Demo私有协议栈](network/socket/demo/protocol.md)
    * [DemoUPD丢包排查](network/socket/demo/udp-lost-packet.md)
    * [DemoUDP可靠传输](network/socket/demo/udp-reliable-demo.md)
  * [nc](network/nc.md)  
* [Java]()
  * [JVM](java/jvm/SUMMARY.md)
    * [HotSpot下的JVM结构](java/jvm/chapter1.md)
      - [Class文件]()
      - [类加载器]()
      - [运行时区]()
        - [方法区](java/jvm/chapter1-02-00.md)
          - [方法区/永久代/元空间演进](java/jvm/chapter1-02-00-01.md)
        - [堆](java/jvm/chapter1-02-01.md)
        - [直接内存](java/jvm/chapter1-02-02.md)
        - [程序计数器](java/jvm/chapter1-02-03.md)
        - [栈](java/jvm/chapter1-02-04.md)
        - [本地方法栈](java/jvm/chapter1-02-05.md) 
    * [内存的分配与回收](java/jvm/chapter2.md)
    * [垃圾回收算法与垃圾收集器](java/jvm/chapter3.md)
    * [JVM内存性能调优常用指令](java/jvm/chapter4.md)
    * [JAVA内存实践](java/jvm/chapter5.md)
    * [JavaPerformance](https://github.com/QQ1350995917/gitbook/raw/master/jvm/JavaPerformance.PDF)
    * [Java内存模型JMM](java/concurrent/jmm.md)
    * [Volatile](java/concurrent/volatile.md)
  * [concurrent](java/concurrent/SUMMARY.md)
    * [并发和并行](java/concurrent/concurrency-parallellism.md)                                
    * [进程和线程](http://blog.sina.com.cn/s/blog_5a2bbc860101gedc.html)
    * [Java中的线程](java/concurrent/thread.md)
    * [Java中的线程池](java/concurrent/thread-pool.md)
    * [Java中的线程同步](java/concurrent/thread-sync-queue.md)
    * [Java内存模型JMM](java/concurrent/jmm.md)
    * [Volatile](java/concurrent/volatile.md)
    * [Synchronized](java/concurrent/synchronized.md)
    * [Lock](java/concurrent/sync-lock.md)
    * [同步工具类](java/concurrent/sync-tools.md)
    * [AQS](java/concurrent/sync-aqs.md)
    * [无锁单例](java/concurrent/no-lock-singleton.md)
    * [创建多少线程最合适](java/concurrent/best-thread-num.md)
  * [容器](java/container/SUMMARY.md)
     * [HashMap](java/container/map-hash-map.md)   
  * [IO](java/SUMMARY.md)
     * [IO](java/io/io.md) 
     * [BIO]()
     * [NIO]()
     * [AIO]() 
     * [零拷贝IO](java/io/0io.md)
     * [磁盘IO状态监控](java/io/iostat.md)
  * [SPI机制](java/spi.md)          

* [JDBC](jdbc/SUMMARY.md)
  * [JDBC](jdbc/jdbc/SUMMARY.md)
    * [JDBC介绍](jdbc/jdbc.md)
    * [JDBC池化](jdbc/pool.md)
  * [Mybatis](jdbc/mybatis/SUMMARY.md)
    * [基本集成使用方式](jdbc/mybatis/01-usage.md)
    * [缓存](jdbc/mybatis/02-cache.md)
    * [读写分离](jdbc/mybatis/03-read-write.md)
    * [原码](jdbc/mybatis/04-source.md)
    * [一些应用问题](jdbc/mybatis/05-question.md)
  * [Sharding](jdbc/sharding/SUMMARY.md)
* [Web](web/README.md) 
  * [并发量计算](web/concurrency.md) 
  * [Nginx](web/nginx/README.md)
  * [Apache]()
  * [Tomcat](web/tomcat/README.md)
    * [Tomcat基本认知](web/tomcat/chapter0.md)
  * [服务鉴权](web/authentication/README.md)
    * [SSO](web/authentication/sso/README.md)
    * [OAuthor](web/authentication/oauthor/README.md)    
* [Distributed](distributed/SUMMARY.md)    
  * [CAP理论](distributed/chapter0.md)
  * [分布式架构简述](distributed/chapter1.md)
  * [分布式事务的解决方案](distributed/chapter2.md)
  * [分布式Lock]()
  * [Dubbo](distributed/middleware/dubbo/SUMMARY.md)
    * [Dubbo简介](distributed/middleware/dubbo/chapter0.md)
  * [kafka](distributed/middleware/kafka/SUMMARY.md)
    - [单机部署](distributed/middleware/kafka/chapter01.md)
    - [集群部署](distributed/middleware/kafka/chapter02.md)
      - [集群部署规划](distributed/middleware/kafka/chapter02-1.md)
    - [架构详解](distributed/middleware/kafka/chapter03.md)
    - [配置参数详解](distributed/middleware/kafka/chapter04.md)    
    - [常用命令](distributed/middleware/kafka/chapter05.md)
    - [kafka-manager](distributed/middleware/kafka/chapter06.md)
  * [zookeeper](distributed/middleware/zookeeper/SUMMARY.md)
    - [单机部署](distributed/middleware/zookeeper/chapter01-0.md)
    - [集群部署](distributed/middleware/zookeeper/chapter01-1.md)
    - [核心概念](distributed/middleware/zookeeper/chapter02-0.md)
    - [集群详解](distributed/middleware/zookeeper/chapter02-1.md)
    - [集群选举](distributed/middleware/zookeeper/chapter02-2.md)
    - [常用命令](distributed/middleware/zookeeper/chapter03-0.md)
    - [应用场景](distributed/middleware/zookeeper/chapter05-0.md)
    - [客户端](distributed/middleware/zookeeper/chapter06-0.md)
  * [RabbitMQ](distributed/middleware/rabbitmq/SUMMARY.md)
    - [单机部署](distributed/middleware/rabbitmq/chapter01.md)
    - [集群部署](distributed/middleware/rabbitmq/chapter02.md)
    - [架构详解](distributed/middleware/rabbitmq/chapter03.md)
    - [运维工具](distributed/middleware/rabbitmq/chapter04.md) 
  * [ZeroMQ]()
  * [MQ选型](distributed/middleware/mq.md)
* [Spring](spring/SUMMARY.md)  
  * [Spring-context](spring/spring-context/SUMMARY.md)
    * [SpringIOC](spring/spring-context/ioc-index.md)
    * [SpringAOP](spring/spring-context/aop-index.md)
    * [SpringMVC](spring/spring-context/mvc-index.md)
    * [DoGetBean](spring/spring-context/do-get-bean.md)
    * [事务与隔离](spring/spring-context/transactional.md)
  * [Spring-Cloud](spring/spring-cloud/SUMMARY.md)
    * [feign-ribbon-hystrix](spring/spring-cloud/feign-ribbon-hystrix.md) 
    * [Register](spring/spring-cloud/eureka/SUMMARY.md)
      * [使用示例](spring/spring-cloud/eureka/Chapter01.md)
      * [源码分析](spring/spring-cloud/eureka/Chapter02.md)
    * [Gateway]()
    * [Config]()
    * [Breaker]()
    * [Tracing]()
  * [Spring-Boot](spring/spring-boot/README.md)
    * [优雅停机](spring/spring-boot/killer.md)  
* [Storage](storage/SUMMARY.md)  
  * [磁盘](storage/disk/SUMMARY.md)
  * [块存储-对象存储-文件存储](storage/01-block-object-file.md)
  * [五大主流分布式存储技术对比分析](storage/02-storage-pick.md)
  * [MinIO分布式存储](storage/minio/SUMMARY.md)
    * [MinIO选型](storage/minio/01-chooser.md)
    * [MinIO部署](storage/minio/02-deploy.md)
    * [MinIO集群](storage/minio/03-deploy-cluster.md)
  * [MySQL](storage/mysql/SUMMARY.md)
    * [单机部署](storage/mysql/deploy-standalone.md)
    * [主从部署](storage/mysql/deploy-master-slave.md)
    * [主从原理](storage/mysql/deploy-master-slave-concept.md)
    * [NDB-Cluster部署](storage/mysql/deploy-cluster-ndb.md)
    * [INNODB-Cluster部署](storage/mysql/deploy-cluster-innodb.md)
    * [数据类型](storage/mysql/chapter02.md)
    * [设计范式](storage/mysql/chapter03.md)
    * [基本操作](storage/mysql/chapter04.md)
    * [事务与隔离](storage/mysql/chapter05.md)
    * [引擎](storage/mysql/chapter05.md)
    * [锁](storage/mysql/chapter07.md)
    * [索引](storage/mysql/chapter08.md)
    * [调优1](storage/mysql/optimization-01.md)
    * [调优2](storage/mysql/optimization-02.md)
    * [8.0新特性](storage/mysql/chapter10.md)
    * [读写分离](jdbc/mybatis/03-read-write.md)
    * [主从模式](storage/mysql/deploy-master-slave.md)
    * [运维相关](storage/mysql/op.md)
    * [SQL语句常见错误](storage/mysql/sql-optimization.md)
    * [测试数据MySQL插入慢](storage/mysql/sql-optimization-write-slow.md)
  * [MongoDB](storage/mongodb/SUMMARY.md)
    * [单机部署](storage/mongodb/chapter0.md)
    * [主从部署](storage/mongodb/chapter1.md)
    * [分片部署](storage/mongodb/chapter3.md)
    * [运行监控](storage/mongodb/chapter4.md)
    * [库表操作](storage/mongodb/chapter3.md)
    * [数据操作](storage/mongodb/chapter5.md)
  * [Redis](storage/redis/SUMMARY.md)
    * [部署](storage/redis/01-install.md)
    * [部署模式以及对比](storage/redis/02-deploy-model.md)
    * [基本概念介绍](storage/redis/03-concept.md)
    * [RedisMQ](storage/redis/04-mq.md)
    * [其他](storage/redis/chapter1.md)  
  * [RRD](storage/rrd/SUMMARY.md)
  * [TSDB](storage/tsdb/SUMMARY.md)
  * [Neo4j]() 
  * [hadoop](storage/hadoop/SUMMARY.md) 
    * [HDFS单机部署](storage/hadoop/01-hdfs-installer-stand-alone.md)
    * [HDFS集群部署](storage/hadoop/02-hdfs-installer-cluster.md)
    * [HDFS核心概念](storage/hadoop/03-hdfs-concept.md)
    * [YARN单机部署](storage/hadoop/04-yarn-installer-stand-alone.md) 
  * [hbase](storage/hbase/SUMMARY.md)  
    * [单机部署](storage/hbase/01-installer-stand-alone.md)
    * [集群部署](storage/hbase/02-installer-cluster.md)
    * [HBaseShell](storage/hbase/03-usage-shell.md)
    * [HBase详解](storage/hbase/04-hbase-concept.md)
  * [hive](storage/hive/SUMMARY.md)
  * [impala](storage/impala/SUMMARY.md)
  * [spark](storage/spark/SUMMARY.md)
  * [storm](storage/storm/SUMMARY.md)    
* [Logger](logger/SUMMARY.md)
  * [日志门面](logger/chapter1.md)
* [Design pattern](http://m.biancheng.net/design_pattern/) 
* [DDD](ddd/SUMMARY.md)
  * [ModelConcept](ddd/model-concept/SUMMARY.md)
    * [数据模型](ddd/model-concept/chapter3.md)
  * [UML](ddd/uml/SUMMARY.md)
* [运维相关](operation/SUMMARY.md)  
  * [Docker](operation/docker/SUMMARY.md)  
    * [安装与基本操作](operation/docker/chapter1.md)
  * [监控](operation/monitor/SUMMARY.md)
    * [系统监控](operation/monitor/chapter0.md)
* [工程应用]()
  * [内容社交]()
  * [IM]()
  * [UDP-CHANNEL](network/socket/SUMMARY.md)
  * [大数据去重方案](DuplicateRemoval.md)
* [清华大学开源软件镜像站](https://mirrors.tuna.tsinghua.edu.cn/)



## 参考资料
* [GitBook 从懵逼到入门](https://blog.csdn.net/lu_embedded/article/details/81100704)
* [Atom快捷键整理](https://www.jianshu.com/p/e33f864981bb)
* [atom的插件必备](https://www.jianshu.com/p/b779e2e5e3ef)
* [markdown语法](https://www.w3cschool.cn/markdownyfsm/markdownyfsm-odm6256r.html)
* [mermaid](https://mermaidjs.github.io/)
* [DrawIO](https://www.draw.io/)



