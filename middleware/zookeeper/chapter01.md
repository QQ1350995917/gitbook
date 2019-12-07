## 单机部署Zookeeper
## 
## []
## [版本列表](https://archive.apache.org/dist/zookeeper/)
## 准备部署环境
- 使用root账号登录系统并[创建用户](../../linux/SUMMARY.md)后完成如下配置
  ```
  cd /usr/local/bin
  mkdir zookeeper
  cd zookeeper
  curl -O https://archive.apache.org/dist/zookeeper/zookeeper-3.5.5/apache-zookeeper-3.5.5.tar.gz
  tar -zxvf apache-zookeeper-3.5.5.tar.gz
  ln -s apache-zookeeper-3.5.5 zookeeper
  ```
  在/etc/profile中配置
  ```
  export ZK_HOME=/usr/local/bin/zookeeper/zookeeper
  export PATH=$PATH:$ZK_HOME/bin
  ```
## 部署
- [切换回创建的账号](../../linux/SUMMARY.md)并做如下配置
  ```
  cd /home/zookeeper
  mkdir stand_alone
  cd stand_alone
  mkdir data
  mkdir conf
  mkdir logger
  ```
  从zookeeper的安装路径下的conf中复制一份配置文件到新建的conf目录中
  ```
   cp /usr/local/bin/zookeeper/zookeeper/conf/zoo_sample.cfg stand-alone/conf/zoo.cfg
  ```
  修改zoo.cfg中的相关配置为
  ```
  # The number of milliseconds of each tick
  tickTime=2000
  # The number of ticks that the initial 
  # synchronization phase can take
  initLimit=10
  # The number of ticks that can pass between 
  # sending a request and getting an acknowledgement
  syncLimit=5
  # the directory where the snapshot is stored.
  # do not use /tmp for storage, /tmp here is just 
  # example sakes.
  dataDir=/home/zookeeper/stand-alone/data
  dataLogDir=/home/zookeeper/stand-alone/logger
  # the port at which the clients will connect
  clientPort=2181
  # the maximum number of client connections.
  # increase this if you need to handle more clients
  #maxClientCnxns=60
  #
  # Be sure to read the maintenance section of the 
  # administrator guide before turning on autopurge.
  #
  # http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
  #
  # The number of snapshots to retain in dataDir
  #autopurge.snapRetainCount=3
  # Purge task interval in hours
  # Set to "0" to disable auto purge feature
  #autopurge.purgeInterval=1
  ```

## 启动服务
尝试如下指令启动
```
zkServer.sh
```
输出日志信息如下
```
Using config: /usr/local/bin/zookeeper/zookeeper/bin/../conf/zoo.cfg
grep: /usr/local/bin/zookeeper/zookeeper/bin/../conf/zoo.cfg: No such file or directory
grep: /usr/local/bin/zookeeper/zookeeper/bin/../conf/zoo.cfg: No such file or directory
mkdir: cannot create directory ‘’: No such file or directory
mkdir: cannot create directory ‘/usr/local/bin/zookeeper/zookeeper/bin/../logs’: Permission denied
Usage: /usr/local/bin/zookeeper/zookeeper/bin/zkServer.sh [--config <conf-dir>] {start|start-foreground|stop|restart|status|print-cmd}
```
按照日志提示做如下启动
```
zkServer.sh --config /home/zookeeper/stand-alone/conf start
```
#启动
./zkServer.sh start
#停止
./zkServer.sh stop
#重启
./zkServer.sh restart
#查看状态
./zkServer.sh status
#查看zookeeper输出信息
tail -f zookeeper.out
