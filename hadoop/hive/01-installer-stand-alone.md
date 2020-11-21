# hive stand alone

## ununtu18

- [准备MySQL环境](../../storage/mysql/SUMMARY.md)
- [hive下载地址](https://mirrors.tuna.tsinghua.edu.cn/apache/hive/stable-2/apache-hive-2.3.7-bin.tar.gz)  

### 部署 
```bash
cd /usr/local/bin/
mkdir hive
wget https://mirrors.tuna.tsinghua.edu.cn/apache/hive/stable-2/apache-hive-2.3.7-bin.tar.gz
tar -zxvf apache-hive-2.3.7-bin.tar.gz
ln -s apache-hive-2.3.7 hive
```

### 验证
```bash
cd /usr/local/bin/hive/hive
./bin/hive version
```

输出

```text
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/usr/local/bin/hive/apache-hive-3.1.2-bin/lib/log4j-slf4j-impl-2.10.0.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/usr/local/bin/hadoop/hadoop-3.1.4/share/hadoop/common/lib/slf4j-log4j12-1.7.25.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
Hive 3.1.2
Git git://HW13934/Users/gates/tmp/hive-branch-3.1/hive -r 8190d2be7b7165effa62bd21b7d60ef81fb0e4af
Compiled by gates on Thu Aug 22 15:01:18 PDT 2019
From source with checksum 0492c08f784b188c349f6afb1d8d9847

```


### 配置环境变量
```bash
# setting hive home
export HIVE_HOME=/usr/local/bin/hive/hive
export PATH=$PATH:$HIVE_HOME/bin
```

### 配置 hive-env.sh
```bash
cd /usr/local/bin/hive/hive
cp conf/hive-env.sh.template conf/hive-env.sh
vim conf/hive-env.sh

export JAVA_HOME=/usr/local/bin/java/jdk
export HADOOP_HOME=$HADOOP_HOME
export HIVE_HOME=/usr/local/bin/hive/hive
export HIVE_CONF_DIR=${HIVE_HOME}/conf

```

### 配置 hive-site.xml
```bash
cd /usr/local/bin/hive/hive
cp conf/hive-default.xml.template conf/hive-site.xml
vim conf/hive-site.xml

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
	<property>
	    <name>javax.jdo.option.ConnectionURL</name>
	    <value>jdbc:mysql://192.168.31.17:3306/hive?createDatabaseIfNotExist=true&amp;characterEncoding=UTF-8&amp;useSSL=false&amp;allowPublicKeyRetrieval=true</value>
	</property>
	<property>
	    <name>javax.jdo.option.ConnectionDriverName</name>
	    <value>com.mysql.jdbc.Driver</value>
	</property>
	<property>
	    <name>javax.jdo.option.ConnectionUserName</name>
	    <value>root</value>
	</property>
	<property>
	    <name>javax.jdo.option.ConnectionPassword</name>
	    <value>root</value>
	</property>
	<!--配置缓存目录-->
  <property>
      <name>hive.exec.local.scratchdir</name>
      <value>/usr/local/bin/hive/cache</value>
      <description>Local scratch space for Hive jobs</description>
  </property>
  <property>
      <name>hive.downloaded.resources.dir</name>
      <value>/usr/local/bin/hive/cache</value>
      <description>Temporary local directory for added resources in the remote file system.</description>
  </property> 
</configuration>

```

额外配置hive-site.xml，方便开发调试

```xml
<!--显示列名-->
 <property>
    <name>hive.cli.print.header</name>
    <value>true</value>
    <description>Whether to print the names of the columns in query output.</description>
 </property>
 <property>
<!--显示当前操作的数据库-->
    <name>hive.cli.print.current.db</name>
    <value>true</value>
    <description>Whether to include the current database in the Hive prompt.</description>
 </property>
 <!--设置为本地模式，当MapReduce处理的数据大小小于128MB或者MapTask个数小于5个，自动运行在本地模式下，提高运行速度-->
 <property>
    <name>hive.exec.mode.local.auto</name>
    <value>true</value>
    <description>Let Hive determine whether to run in local mode automatically</description>
 </property>
```

### 初始化
```bash
cd /usr/local/bin/hive/hive
./bin/schematool -dbType mysql -initSchema
```

输出

```text
Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkArgument(ZLjava/lang/String;Ljava/lang/Object;)V
	at org.apache.hadoop.conf.Configuration.set(Configuration.java:1357)
	at org.apache.hadoop.conf.Configuration.set(Configuration.java:1338)
	at org.apache.hadoop.mapred.JobConf.setJar(JobConf.java:518)
	at org.apache.hadoop.mapred.JobConf.setJarByClass(JobConf.java:536)
	at org.apache.hadoop.mapred.JobConf.<init>(JobConf.java:430)
	at org.apache.hadoop.hive.conf.HiveConf.initialize(HiveConf.java:5141)
	at org.apache.hadoop.hive.conf.HiveConf.<init>(HiveConf.java:5104)
	at org.apache.hive.beeline.HiveSchemaTool.<init>(HiveSchemaTool.java:96)
	at org.apache.hive.beeline.HiveSchemaTool.main(HiveSchemaTool.java:1473)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.util.RunJar.run(RunJar.java:318)
	at org.apache.hadoop.util.RunJar.main(RunJar.java:232)
```

解决方案

```text
1、com.google.common.base.Preconditions.checkArgument这个类所在的jar包为：guava.jar

2、hadoop-3.2.1（路径：hadoop\share\hadoop\common\lib）中该jar包为  guava-27.0-jre.jar；而hive-3.1.2(路径：hive/lib)中该jar包为guava-19.0.1.jar

3、将jar包变成一致的版本：删除hive中低版本jar包，将hadoop中高版本的复制到hive的lib中。
```

再次启动，输出

```text
Metastore connection URL:	 jdbc:mysql://192.168.31.17:3306/hive?createDatabaseIfNotExist=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
Metastore Connection Driver :	 com.mysql.jdbc.Driver
Metastore connection User:	 root
Starting metastore schema initialization to 3.1.0
Initialization script hive-schema-3.1.0.mysql.sql

Initialization script completed
schemaTool completed
```

### 验证
登录MySQL
```mysql
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| hive               |
| initializr_monitor |
| mysql              |
| performance_schema |
| sys                |
| tao                |
+--------------------+
7 rows in set (0.03 sec)

mysql> use hive
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+-------------------------------+
| Tables_in_hive                |
+-------------------------------+
| AUX_TABLE                     |
| BUCKETING_COLS                |
| CDS                           |
| COLUMNS_V2                    |
| COMPACTION_QUEUE              |
| COMPLETED_COMPACTIONS         |
| COMPLETED_TXN_COMPONENTS      |
| CTLGS                         |
| DATABASE_PARAMS               |
| DBS                           |
| DB_PRIVS                      |
| DELEGATION_TOKENS             |
| FUNCS                         |
| FUNC_RU                       |
| GLOBAL_PRIVS                  |
| HIVE_LOCKS                    |
| IDXS                          |
| INDEX_PARAMS                  |
| I_SCHEMA                      |
| KEY_CONSTRAINTS               |
| MASTER_KEYS                   |
| MATERIALIZATION_REBUILD_LOCKS |
| METASTORE_DB_PROPERTIES       |
| MIN_HISTORY_LEVEL             |
| MV_CREATION_METADATA          |
| MV_TABLES_USED                |
| NEXT_COMPACTION_QUEUE_ID      |
| NEXT_LOCK_ID                  |
| NEXT_TXN_ID                   |
| NEXT_WRITE_ID                 |
| NOTIFICATION_LOG              |
| NOTIFICATION_SEQUENCE         |
| NUCLEUS_TABLES                |
| PARTITIONS                    |
| PARTITION_EVENTS              |
| PARTITION_KEYS                |
| PARTITION_KEY_VALS            |
| PARTITION_PARAMS              |
| PART_COL_PRIVS                |
| PART_COL_STATS                |
| PART_PRIVS                    |
| REPL_TXN_MAP                  |
| ROLES                         |
| ROLE_MAP                      |
| RUNTIME_STATS                 |
| SCHEMA_VERSION                |
| SDS                           |
| SD_PARAMS                     |
| SEQUENCE_TABLE                |
| SERDES                        |
| SERDE_PARAMS                  |
| SKEWED_COL_NAMES              |
| SKEWED_COL_VALUE_LOC_MAP      |
| SKEWED_STRING_LIST            |
| SKEWED_STRING_LIST_VALUES     |
| SKEWED_VALUES                 |
| SORT_COLS                     |
| TABLE_PARAMS                  |
| TAB_COL_STATS                 |
| TBLS                          |
| TBL_COL_PRIVS                 |
| TBL_PRIVS                     |
| TXNS                          |
| TXN_COMPONENTS                |
| TXN_TO_WRITE_ID               |
| TYPES                         |
| TYPE_FIELDS                   |
| VERSION                       |
| WM_MAPPING                    |
| WM_POOL                       |
| WM_POOL_TO_TRIGGER            |
| WM_RESOURCEPLAN               |
| WM_TRIGGER                    |
| WRITE_SET                     |
+-------------------------------+
74 rows in set (0.01 sec)
```

### 启动
启动metastore服务 
```bash
hive –service metastore &
```

登录hive
```bash
hive
```

### 参考资料
https://www.cnblogs.com/web424/p/7543719.html



