# 安装

## 安装zookeeper

1：官网地址[https://archive.apache.org/dist/zookeeper/](https://archive.apache.org/dist/zookeeper/)

2：cd /usr/local/bin/

3：mkdir zookeeper

4：cd zookeeper

5：curl -o apache-zookeeper-3.5.5-bin.tar.gz [https://archive.apache.org/dist/zookeeper/zookeeper-3.5.5/apache-zookeeper-3.5.5-bin.tar.gz](https://archive.apache.org/dist/zookeeper/zookeeper-3.5.5/apache-zookeeper-3.5.5-bin.tar.gz)

6：tar -zxvf apache-zookeeper-3.5.5-bin.tar.gz

7：ln -s apache-zookeeper-3.5.5-bin zookeeper

8：cd zookeeper/conf

9：cp zoo\_sample.cfg zoo.cfg

10：cd ../bin

11：./zkServer.sh start

## 安装kafka

1：官网地址[http://kafka.apache.org/downloads.html](http://kafka.apache.org/downloads.html)

2：cd /usr/local/bin/

3：mkdir kafka

4：cd kafka

5：curl -o kafka\_2.11-2.3.0.tgz [https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka\_2.11-2.3.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka_2.11-2.3.0.tgz)

6：tar -zxvf kafka\_2.11-2.3.0.tgz

7：ln -s kafka\_2.11-2.3.0 kafka

8：cd kafka/bin

9：./zookeeper-server-start.sh ../config/zookeeper.properties（尚未启动zookeeper时）

10：./kafka-server-start.sh ../config/server.properties

11：./kafka-server-start.sh ../config/server.properties 1>/dev/null 2>&1 & （守护进程）

12：./kafka-server-start.sh -daemon ../config/server.properties 1>/dev/null 2>&1 & （守护进程）

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
sh start.sh
bin/cmak 
```
### Java 8
```text
curl https://codeload.github.com/yahoo/CMAK/zip/2.0.0.2
unzip unzip CMAK-2.0.0.2.zip
ln -s CMAK-2.0.0.2 cmak
cd cmak
# sbt安装地址 https://docs.huihoo.com/sbt/0.13/tutorial/zh-cn/Manual-Installation.html

```

- 软连接 
# 集群配置

[https://www.cnblogs.com/zikai/p/9627736.html](https://www.cnblogs.com/zikai/p/9627736.html)

