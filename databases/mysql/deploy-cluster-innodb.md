## [Introducing InnoDB Cluster](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-introduction.html)
![](images/innodb_cluster_overview.png)
MySQL InnoDB cluster至少三个MySQL节点实例。

使用MySQL Group Replication数据同步方式。

使用[MySQL Shell](https://dev.mysql.com/doc/mysql-shell/8.0/en/)进行管理。



dba.deploySandboxInstance\(3310\)

shell.connect\('root@localhost:3310'\)

dba.createCluster\(\)

var cluster = dba.createCluster\('testCluster'\)

## 两种部署方式

[沙盒级](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-sandbox-deployment.html)

[产品级](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-production-deployment.html)

[安装要求](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-requirements.html)

[安装方式](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-methods-installing.html)

