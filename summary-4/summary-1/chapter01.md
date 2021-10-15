# 单机部署

### [部署zookeeper](../summary-2/chapter01-0.md)

### 部署kafka

官网地址[http://kafka.apache.org/downloads.html](http://kafka.apache.org/downloads.html)

```
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

### [kafka-manager](chapter06.md)

*   软连接 

    **集群配置**

[https://www.cnblogs.com/zikai/p/9627736.html](https://www.cnblogs.com/zikai/p/9627736.html)
