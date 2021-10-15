# HDFS单机部署

## ununtu18

* 准备好JRE环境
* [安装包下载地址](https://archive.apache.org/dist/hadoop/common/)

### 部署

```bash
cd /usr/local/bin/
mkdir hadoop
wget https://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-3.1.4/hadoop-3.1.4.tar.gz
tar -zxvf hadoop-3.1.4.tar.gz
ln -s hadoop-3.1.4 hadoop
```

### 验证

```bash
cd /usr/local/bin/hadoop/hadoop
./bin/hadoop version
```

输出

```
Hadoop 3.1.4
Source code repository https://github.com/apache/hadoop.git -r 1e877761e8dadd71effef30e592368f7fe66a61b
Compiled by gabota on 2020-07-21T08:05Z
Compiled with protoc 2.5.0
From source with checksum 38405c63945c88fdf7a6fe391494799b
This command was run using /usr/local/bin/hadoop/hadoop-3.1.4/share/hadoop/common/hadoop-common-3.1.4.jar
```

### 配置环境变量

```bash
# setting hadoop home
export HADOOP_HOME=/usr/local/bin/hadoop/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
```

### 修改core-site配置文件

```bash
cd /usr/local/bin/hadoop/hadoop
vim etc/hadoop/core-site.xml
```

配置core-site.xml的configuration的节点内容为：

```markup
<configuration>
  <!--hadoop存储目录-->
  <property>
    <name>hadoop.tmp.dir</name>
    <value>file:/usr/local/bin/hadoop/tmp</value>
    <description>Abase for other temporary directories.</description>
  </property>
  <!--指定namenode地址-->
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>
```

### 修改hdfs-site配置文件

```bash
cd /usr/local/bin/hadoop/hadoop
vim etc/hadoop/hdfs-site.xml
```

配置hdfs-site.xml的configuration的节点内容为：

```markup
<configuration>
  <!--hdfs保存副本分数-->
  <property>
    <name>dfs.replication</name>
    <value>1</value>
  </property>
  <!--hdfs namenode存储位置-->
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file:/usr/local/bin/hadoop/tmp/dfs/name</value>
  </property>
  <!--hdfs datanode存储位置-->
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file:/usr/local/bin/hadoop/tmp/dfs/data</value>
  </property>
  <!--web页面启动端口以及允许任意IP访问-->
    <property>
        <name>dfs.http.address</name>
        <value>0.0.0.0:9870</value>
    </property>
</configuration>
```

### 修改hadoop-env配置文件

```bash
cd /usr/local/bin/hadoop/hadoop
vim etc/hadoop/hadoop-env.sh
```

配置hadoop-env.sh的java环境变量为当前设置的环境变量

```bash
export JAVA_HOME=/usr/local/bin/java/jdk
```

### 执行 NameNode 的格式化:

`bash hdfs namenode -format` 输出

```
2020-11-16 22:13:09,680 INFO util.GSet: Computing capacity for map NameNodeRetryCache
2020-11-16 22:13:09,680 INFO util.GSet: VM type       = 64-bit
2020-11-16 22:13:09,680 INFO util.GSet: 0.029999999329447746% max memory 1.7 GB = 532.5 KB
2020-11-16 22:13:09,680 INFO util.GSet: capacity      = 2^16 = 65536 entries
2020-11-16 22:13:09,720 INFO namenode.FSImage: Allocated new BlockPoolId: BP-262111950-127.0.0.1-1605535989706
2020-11-16 22:13:09,755 INFO common.Storage: Storage directory /usr/local/bin/hadoop/tmp/dfs/name has been successfully formatted.
2020-11-16 22:13:09,803 INFO namenode.FSImageFormatProtobuf: Saving image file /usr/local/bin/hadoop/tmp/dfs/name/current/fsimage.ckpt_0000000000000000000 using no compression
2020-11-16 22:13:09,941 INFO namenode.FSImageFormatProtobuf: Image file /usr/local/bin/hadoop/tmp/dfs/name/current/fsimage.ckpt_0000000000000000000 of size 388 bytes saved in 0 seconds .
2020-11-16 22:13:09,980 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
2020-11-16 22:13:09,996 INFO namenode.FSImage: FSImageSaver clean checkpoint: txid = 0 when meet shutdown.
2020-11-16 22:13:09,996 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at localhost/127.0.0.1
************************************************************/
```

### 开启 NameNode 和 DataNode 守护进程

```bash
start-dfs.sh
```

输出

```
Starting namenodes on [localhost]
ERROR: Attempting to operate on hdfs namenode as root
ERROR: but there is no HDFS_NAMENODE_USER defined. Aborting operation.
Starting datanodes
ERROR: Attempting to operate on hdfs datanode as root
ERROR: but there is no HDFS_DATANODE_USER defined. Aborting operation.
Starting secondary namenodes [dingpw]
ERROR: Attempting to operate on hdfs secondarynamenode as root
ERROR: but there is no HDFS_SECONDARYNAMENODE_USER defined. Aborting operation.
```

缺少用户定义而造成的错误，解决方案如下：

```bash
cd /usr/local/bin/hadoop/hadoop
vim sbin/start-dfs.sh
vim sbin/stop-dfs.sh
```

在start-dfs.sh和stop-dfs.sh文件中添加如下内容

```
#!/usr/bin/env bash
HDFS_DATANODE_USER=root
HADOOP_SECURE_DN_USER=hdfs
HDFS_NAMENODE_USER=root
HDFS_SECONDARYNAMENODE_USER=root
```

再次启动即可

输出如下

```
WARNING: HADOOP_SECURE_DN_USER has been replaced by HDFS_DATANODE_SECURE_USER. Using value of HADOOP_SECURE_DN_USER.
Starting namenodes on [localhost]
localhost: Warning: Permanently added 'localhost' (ECDSA) to the list of known hosts.
localhost: root@localhost: Permission denied (publickey,password).
Starting datanodes
localhost: root@localhost: Permission denied (publickey,password).
Starting secondary namenodes [dingpw]
dingpw: Warning: Permanently added 'dingpw' (ECDSA) to the list of known hosts.
dingpw: root@dingpw: Permission denied (publickey,password).
```

解决方法如下

```bash
cd ~/.ssh
ssh-keygen -t rsa
cat id_rsa.pub >> authorized_keys
ssh localhost
```

再次启动即可，输出

```
WARNING: HADOOP_SECURE_DN_USER has been replaced by HDFS_DATANODE_SECURE_USER. Using value of HADOOP_SECURE_DN_USER.
Starting namenodes on [localhost]
Starting datanodes
Starting secondary namenodes [dingpw]
```

启动完成后，可以通过命令 jps 来判断是否成功

```bash
jps -ml
8228 org.apache.hadoop.hdfs.server.datanode.DataNode
8472 org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode
9403 sun.tools.jps.Jps -ml
8062 org.apache.hadoop.hdfs.server.namenode.NameNode
```

### 访问web管理页面

> [http://ip:9870](http://ip:9870)

[页面监控内容解读](https://www.cnblogs.com/go-no-1/p/13032247.html)

### 开启 NameNode 和 DataNode 守护进程

```bash
stop-dfs.sh
WARNING: HADOOP_SECURE_DN_USER has been replaced by HDFS_DATANODE_SECURE_USER. Using value of HADOOP_SECURE_DN_USER.
Stopping namenodes on [localhost]
Stopping datanodes
Stopping secondary namenodes [dingpw]
```

### 参考资料

[https://blog.csdn.net/weixin\_38883338/article/details/82928809](https://blog.csdn.net/weixin\_38883338/article/details/82928809)
