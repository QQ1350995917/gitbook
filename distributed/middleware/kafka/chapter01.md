# 单机部署

## [部署zookeeper](../zookeeper/chapter01-0.md)

## 部署kafka

官网地址[http://kafka.apache.org/downloads.html](http://kafka.apache.org/downloads.html)

```text
cd /usr/local/bin/
mkdir kafka
cd kafka
curl -o kafka\_2.11-2.3.0.tgz [https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka\_2.11-2.3.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka_2.11-2.3.0.tgz)
tar -zxvf kafka\_2.11-2.3.0.tgz
ln -s kafka\_2.11-2.3.0 kafka
cd kafka/bin
# 尚未启动zookeeper时
# ./zookeeper-server-start.sh ../config/zookeeper.properties
# 三种启动方式
./kafka-server-start.sh ../config/server.properties
./kafka-server-start.sh ../config/server.properties 1>/dev/null 2>&1 & （守护进程）
./kafka-server-start.sh -daemon ../config/server.properties 1>/dev/null 2>&1 & （守护进程）
```

## kafka-manager
[下载地址](https://github.com/yahoo/CMAK/releases)
### Java 11
```text
unzip cmak-3.0.0.5.zip
ln -s cmak-3.0.0.5 cmak
cd cmak
vim conf/application.conf
# kafka-manager.zkhosts="192.168.1.22:2181,192.168.1.23:2181,192.168.1.24:2181"
# cmak.zkhosts="192.168.105.25:2181"
# 创建启动脚本
vim start.sh
# bin/cmak -Dconfig.file=conf/application.conf -Dhttp.port=9900
# nohup bin/cmak -Dconfig.file=conf/application.conf -Dhttp.port=9900 &
sh start.sh
bin/cmak 
```
### Java 8
```text
# download bin package
unzip kafka-manager-1.3.3.4.zip
ln -s kafka-manager-1.3.3.4 kafka-manager
cd kafka-manager
vim conf/application.conf
# kafka-manager.zkhosts="10.0.0.50:12181,10.0.0.60:12181,10.0.0.70:12181"
# 创建启动脚本
vim start.sh
# bin/kafka-manager -Dconfig.file=conf/application.conf -Dhttp.port=9900
# nohup bin/kafka-manager -Dconfig.file=conf/application.conf -Dhttp.port=9900 > application.log 2>&1 &
sh start.sh
```

### 验证
```text
ps -ef | grep kafka-manager
# kafka     3663     1  3 16:36 pts/2    00:00:27 java -Duser.dir=/home/kafka/kafka-manager-1.3.3.4 
jps -ml
# 3663 play.core.server.ProdServerStart

http://ip:9900
```


- 软连接 
# 集群配置

[https://www.cnblogs.com/zikai/p/9627736.html](https://www.cnblogs.com/zikai/p/9627736.html)

