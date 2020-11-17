# hbase cluster

## ununtu18

- [同步时钟](../../os/linux/ntp.md)
- [准备好hadoop环境](../hadoop/SUMMARY.md)
- [准备好zookeeper环境](../../distributed/middleware/zookeeper/SUMMARY.md)
- [查看hbase-hadoop兼容性版本](http://hbase.apache.org/book.html#hadoop)
- [下载hbase](https://archive.apache.org/dist/hbase/)  

### 部署 
```bash
cd /usr/local/bin/
mkdir hbase
wget https://www.apache.org/dyn/closer.lua/hbase/2.2.6/hbase-2.2.6-bin.tar.gz
tar -zxvf hbase-2.2.6-bin.tar.gz
ln -s hbase-2.2.6-bin hbaes
```

### 验证
```bash
cd /usr/local/bin/hbase/hbase
./bin/hbase version
```

输出
```text
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2395: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2360: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2455: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_OPTS: bad substitution
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/usr/local/bin/hadoop/hadoop-3.1.4/share/hadoop/common/lib/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/usr/local/bin/hbase/hbase-2.2.6/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
HBase 2.2.6
Source code repository git://or1-hadoop-build02.awsus/home/zhangguanghao1/code/hbase revision=88c9a386176e2c2b5fd9915d0e9d3ce17d0e456e
Compiled by zhangguanghao1 on Tue Sep 15 17:36:14 CST 2020
From source with checksum a590ebb32ebb0937ddc7287f7c1b0f80
```

### 配置环境变量
```bash
# setting hbase home
export HBASE_HOME=/usr/local/bin/hbase/hbase
export PATH=$PATH:$HBASE_HOME/bin
```

### 配置 hbase-env.sh
```bash
cd /usr/local/bin/hbase/hbase
vim conf/hbase-env.sh 

export JAVA_HOME=/usr/local/bin/java/jdk
配置使用外部ZK
export HBASE_MANAGES_ZK=false
```


### 配置 hbase-site.xml

```bash
cd /usr/local/bin/hbase/hbase

vim conf/hbase-site.xml 

<configuration>
	<!-- hbase数据存放的目录，若用本地目录，必须带上file://,否则hbase启动不起来 -->
	<property>
		<name>hbase.rootdir</name>
		<value>file:///usr/local/bin/hbase/data</value>
	</property>
	<!-- ZK -->
	<property>
		<name>hbase.zookeeper.quorum</name>
		<value>localhost:2181</value>
		<description>the cluster of zk</description>
	</property>
	<!-- 此处必须为true，不然hbase仍用自带的zk，若启动了外部的zookeeper，会导致冲突，hbase启动不起来 -->
	<property>
		<name>hbase.cluster.distributed</name>
		<value>true</value>
	</property>
	<!-- hbase主节点的位置 -->
	<property>
		<name>hbase.master</name>
		<value>localhost:60000</value>
	</property>
	<!-- hbase web 服务端口 -->
	<property>
    <name>hbase.master.info.port</name>
    <value>60010</value>
  </property>
</configuration>
```

### 配置zoo.cfg
拷贝zookeeper conf/zoo.cfg到hbase的conf/下


### 参考资料
https://blog.csdn.net/xuedingkai/article/details/78816862
https://blog.csdn.net/weixin_40782143/article/details/87936380
