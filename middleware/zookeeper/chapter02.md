## 集群部署Zookeeper
zookeeper集群的目的是为了保证系统的性能承载更多的客户端连接设专门提供的机制。通过集群可以实现以下功能：
- 读写分离：提高承载，为更多的客户端提供连接，并保障性能。
- 主从自动切换：提高服务容错性，部分节点故障不会影响整个服务集群。  

半数以上运行机制说明：
集群至少需要三台服务器，并且强烈建议使用奇数个服务器。因为zookeeper 通过判断大多数节点的存活来判断整个服务是否可用。比如3个节点，挂掉了2个表示整个集群挂掉，而用偶数4个，挂掉了2个也表示其并不是大部分存活，因此也会挂掉。

## 部署规划
![](images/zookeeper-cluster.jpg)

## [环境准备](chapter01.md)
```
mkdir /home/zookeeper/cluster
cd /home/zookeeper/cluster
mkdir 21811 21812 21813
# 各个目录下执行
mkdir data datalog conf logger zoolog
```
### 准备文件
- 21811/conf
  
  zoo.cfg
  ```
  tickTime=2000
  initLimit=10
  syncLimit=5
  maxClientCnxns=60
  clientPort=21811
  dataDir=/home/zookeeper/cluster/21811/data
  dataLogDir=/home/zookeeper/cluster/21811/datalog
  zooLogDir=/home/zookeeper/cluster/21811/zoolog
  
  server.1=127.0.0.1:21811:28881
  server.2=127.0.0.1:21812:28882
  server.3=127.0.0.1:21813:28883
  ```
  log4j.properties
  ```
  zookeeper.log.dir=/home/zookeeper/cluster/21811/logger
  zookeeper.root.logger=INFO, ROLLINGFILE
  log4j.appender.ROLLINGFILE=org.apache.log4j.DailyRollingFileAppender
  ```
- 21812/conf

  zoo.cfg
  ```
  tickTime=2000
  initLimit=10
  syncLimit=5
  maxClientCnxns=60
  clientPort=21812
  dataDir=/home/zookeeper/cluster/21812/data
  dataLogDir=/home/zookeeper/cluster/21812/datalog
  zooLogDir=/home/zookeeper/cluster/21812/zoolog
  
  server.1=127.0.0.1:21811:28881
  server.2=127.0.0.1:21812:28882
  server.3=127.0.0.1:21813:28883
  ```
  log4j.properties
  ```
  zookeeper.log.dir=/home/zookeeper/cluster/21812/logger
  zookeeper.root.logger=INFO, ROLLINGFILE
  log4j.appender.ROLLINGFILE=org.apache.log4j.DailyRollingFileAppender
  ```
- 21813/zoo.cfg

  zoo.cfg
  ```
  tickTime=2000
  initLimit=10
  syncLimit=5
  maxClientCnxns=60
  clientPort=21813
  dataDir=/home/zookeeper/cluster/21813/data
  dataLogDir=/home/zookeeper/cluster/21813/datalog
  zooLogDir=/home/zookeeper/cluster/21813/zoolog
  
  server.1=127.0.0.1:21811:28881
  server.2=127.0.0.1:21812:28882
  server.3=127.0.0.1:21813:28883
  ```
  log4j.properties
  ```
  zookeeper.log.dir=/home/zookeeper/cluster/21813/logger
  zookeeper.root.logger=INFO, ROLLINGFILE
  log4j.appender.ROLLINGFILE=org.apache.log4j.DailyRollingFileAppender
  ```
  在各个节点下的data下指定server的id
  ```
  echo 1 > /home/zookeeper/cluster/21811/data/myid
  echo 2 > /home/zookeeper/cluster/21812/data/myid
  echo 3 > /home/zookeeper/cluster/21813/data/myid
  ```
### 配置语法：
  server.<节点ID>=IP地址:<数据同步端口>:<选举端口>
  - 节点ID：服务id手动指定1至125之间的数字，并写到对应服务节点的 {dataDir}/myid 文件中。
  - IP地址：节点的远程IP地址，可以相同。但生产环境就不能这么做了，因为在同一台机器就无法达到容错的目的。所以这种称作为伪集群。
  - 数据同步端口：主从同时数据复制端口，（做伪集群时端口号不能重复）。
  - 远举端口：主从节点选举端口，（做伪集群时端口号不能重复）。
   
   
## 服务启动
```
zkServer.sh --config /home/zookeeper/cluster/21811/conf start
zkServer.sh --config /home/zookeeper/cluster/21812/conf start
zkServer.sh --config /home/zookeeper/cluster/21813/conf start
```    
查看启动状态
```
zkServer.sh --config /home/zookeeper/cluster/21811/conf status
zkServer.sh --config /home/zookeeper/cluster/21812/conf status
zkServer.sh --config /home/zookeeper/cluster/21813/conf status
```
可以看出节点的leader，follower，observer角色信息

检查集群复制情况：
1. 分别连接指定节点
zkCli.sh 后加参数-server 表示连接指定IP与端口。
zkCli.sh -server 127.0.0.1:21811
zkCli.sh -server 127.0.0.1:21812
zkCli.sh -server 127.0.0.1:21813
2. 任意节点中创建数据，查看其它节点已经同步成功。
注意： -server参数后同时连接多个服务节点，并用逗号隔开 127.0.0.1:21811,127.0.0.1:21812,127.0.0.1:21813

## 集群角色说明
zookeeper 集群中总共有三种角色，分别是
- leader（主节点）
- follower(子节点) 
- observer（次级子节点）

|角色|描述|
|----|----|
|leader|主节点，又名领导者。用于写入数据，通过选举产生，如果宕机将会选举新的主节点。|
|follower|子节点，又名追随者。用于实现数据的读取。同时他也是主节点的备选节点，并用拥有投票权。|
|observer|次级子节点，又名观察者。用于读取数据，与follower区别在于没有投票权，不能选为主节点。并且在计算集群可用状态时不会将observer计算入内。|

observer配置：  
只要在集群配置中加上observer后缀即可，示例如下：  
server.3=127.0.0.1:21813:28883:observer

## [集群选举](chapter05.md)

