# 单机部署

## ununtu18

* [下载hbase](https://archive.apache.org/dist/hbase/)  

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

```
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
```

### 配置 hbase-site.xml

```bash
cd /usr/local/bin/hbase/hbase

mkdir /usr/local/bin/hbase/data
mkdir /usr/local/bin/hbase/zookeeper

vim conf/hbase-site.xml 

<configuration>
    <!-- hbase数据存放的目录，若用本地目录，必须带上file://,否则hbase启动不起来 -->
    <property>
        <name>hbase.rootdir</name>
        <value>file:///usr/local/bin/hbase/data</value>
        <!-- <value>hdfs://dingpw:9000/hbase</value>  -->
    </property>
  <property>
      <name>hbase.unsafe.stream.capability.enforce</name>
      <value>false</value>
    </property>
  <!-- hbase web 服务端口 -->
  <property>
    <name>hbase.master.info.port</name>
    <value>60010</value>
  </property>
</configuration>
```

### 启动

```bash
cd /usr/local/bin/hbase/hbase
start-hbase.sh
```

输出

```
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2395: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2360: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2455: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_OPTS: bad substitution
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/usr/local/bin/hadoop/hadoop-3.1.4/share/hadoop/common/lib/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/usr/local/bin/hbase/hbase-2.2.6/lib/client-facing-thirdparty/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
running master, logging to /usr/local/bin/hbase/hbase/logs/hbase-root-master-dingpw.out
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2395: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2360: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER: bad substitution
/usr/local/bin/hadoop/hadoop/libexec/hadoop-functions.sh: line 2455: HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_OPTS: bad substitution
```

### 验证

```bash
ps -ef | grep hbase
```

输出

```
root     20827     1  0 23:34 pts/0    00:00:00 bash /usr/local/bin/hbase/hbase/bin/hbase-daemon.sh --config /usr/local/bin/hbase/hbase/conf foreground_start master
root     20879 20827 25 23:34 pts/0    00:00:17 /usr/local/bin/java/jdk/bin/java -Dproc_master -XX:OnOutOfMemoryError=kill -9 %p -XX:+UseConcMarkSweepGC -Dhbase.log.dir=/usr/local/bin/hbase/hbase/logs -Dhbase.log.file=hbase-root-master-dingpw.log -Dhbase.home.dir=/usr/local/bin/hbase/hbase -Dhbase.id.str=root -Dhbase.root.logger=INFO,RFA -Djava.library.path=/usr/local/bin/hadoop/hadoop/lib/native -Dhbase.security.logger=INFO,RFAS org.apache.hadoop.hbase.master.HMaster start
root     21266  5217  0 23:35 pts/0    00:00:00 grep --color=auto hbase
```

```bash
curl localhost:60010
```

输出

```
<!--
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-->
<meta HTTP-EQUIV="REFRESH" content="0;url=/master-status"/>
```

访问web页面

> [http://ip:60010](http://ip:60010)

### 参考资料

[https://www.cnblogs.com/h--d/p/11580398.html](https://www.cnblogs.com/h--d/p/11580398.html) [https://hbase.apache.org/book.html](https://hbase.apache.org/book.html)
