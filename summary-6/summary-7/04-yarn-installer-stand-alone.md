# YARN单机部署

## ununtu18

* [准备好HDFS环境](01-hdfs-installer-stand-alone.md)

### 部署

```bash
cd /usr/local/bin/hadoop/hadoop

# etc/hadoop/mapred-site.xml:
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
​
# etc/hadoop/yarn-site.xml:
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```

### 启动与关闭

```bash
start-yarn.sh
stop-yarn.sh
```
