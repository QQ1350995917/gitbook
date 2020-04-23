# Hadoop 安装部署
首先配置JDK环境
- 本地模式安装
- 伪分布式安装
- 
- 
## 本地模式安装(使用的存储系统是当前的操作系统)
https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html

下载地址 https://hadoop.apache.org/releases.html
```bash
mkdir -p /usr/local/bin/hadoop
wget https://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-3.2.1/hadoop-3.2.1.tar.gz
tar zxvf hadoop-3.2.1.tar.gz
ln -s hadoop-3.2.1 hadoop
```
配置环境变量
```bash
vim /etc/profile

export HADOOP_HOME=/usr/local/bin/hadoop/hadoop
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin

source /etc/profile
```
验证 Hadoop
```bash
hadoop version                 // 查看版本
hdfs dfs -ls /　　　　　   　　　　//列出
hdfs dfs -mkdir dir_name　　　　 //创建文件夹
hdfs dfs -cat file_name　　　　　//查看文件内容
hdfs dfs -touch file_name　　　 //创建文件
hdfs dfs -rm file_name　　  　　 //删除文件
```


