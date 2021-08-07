# 集群部署
[镜像地址](https://repo.huaweicloud.com/elasticsearch/)

以6.8.1为例进行部署

部署前服务器设置
```
# vim /etc/security/limits.conf
*               soft    nofile          65536
*               hard    nofile          65536
*               soft    nproc           4096
*               hard    nproc           4096

# vim /etc/sysctl.conf
vm.max_map_count=262144

# sysctl -p
```

# docker镜像部署
```
docker pull elasticsearch:6.8.1

```

# 原生部署
假设前提三台机器配置信息如下：

|机器编号   |IP（假设）   |配置   |
| ------------ | ------------ | ------------ |
|es-node-1   |192.168.1.1   |4C,8G,500G,千兆网口|
|es-node-2   |192.168.1.2   |4C,8G,500G,千兆网口|
|es-node-3   |192.168.1.3   |4C,8G,500G,千兆网口|

在上个机器上执行如下操作：
```
adduser elasticsearch # 创建用户
passwd elasticsearch # 给elasticsearch创建密码
su elasticsearch # 切换到elasticsearch用户
cd /home/elasticsearch # 以elasticsearch目录为根目录
mkdir conf # 创建配置文件目录
mkdir data # 创建数据目录
mkdir logs # 创建日志目录
```
## 下载ElasticSearch
```
cd /home/elasticsearch
wget https://mirrors.huaweicloud.com/elasticsearch/6.8.1/elasticsearch-6.8.1.tar.gz
tar -zxvf elasticsearch-6.8.1.tar.gz
ln -s elasticsearch-6.8.1 elasticsearch
```

## 配置ElasticSearch
把下面三个配置文件分别放置到的对应机器的对应位置。
### es-node-1配置文件（/home/elasticsearch/elasticsearch/config/elasticsearch.yml）

```text
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
cluster.name: cnvd-es
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: es-node-1
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
path.data: /home/es/data
#
# Path to log files:
#
path.logs: /home/es/logs
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
#bootstrap.memory_lock: false
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
#
network.host: 0.0.0.0
network.bind_host: ["192.168.1.1"]
network.publish_host: 192.168.1.1
#
# Set a custom port for HTTP:
#
http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
discovery.zen.ping.unicast.hosts: ["192.168.1.1","192.168.1.2", "192.168.1.3"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of master-eligible nodes / 2 + 1):
#
discovery.zen.minimum_master_nodes: 2
#
# For more information, consult the zen discovery module documentation.
#
# ---------------------------------- Gateway -----------------------------------
#
# Block initial recovery after a full cluster restart until N nodes are started:
#
#gateway.recover_after_nodes: 3
#
# For more information, consult the gateway module documentation.
#
# ---------------------------------- Various -----------------------------------
#
# Require explicit names when deleting indices:
#
#action.destructive_requires_name: true

http.cors.enabled: true
http.cors.allow-origin: "*"
```

### es-node-2配置文件（/home/elasticsearch/elasticsearch/config/elasticsearch.yml）
```text
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
cluster.name: cnvd-es
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: es-node-2
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
path.data: /home/es/data
#
# Path to log files:
#
path.logs: /home/es/logs
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
#bootstrap.memory_lock: false
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
#
network.host: 0.0.0.0
network.bind_host: ["192.168.1.2"]
network.publish_host: 192.168.1.2
#
# Set a custom port for HTTP:
#
http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
discovery.zen.ping.unicast.hosts: ["192.168.1.1","192.168.1.2", "192.168.1.3"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of master-eligible nodes / 2 + 1):
#
discovery.zen.minimum_master_nodes: 2
#
# For more information, consult the zen discovery module documentation.
#
# ---------------------------------- Gateway -----------------------------------
#
# Block initial recovery after a full cluster restart until N nodes are started:
#   
#gateway.recover_after_nodes: 3
#
# For more information, consult the gateway module documentation.
#
# ---------------------------------- Various -----------------------------------
#
# Require explicit names when deleting indices:
#
#action.destructive_requires_name: true
http.cors.enabled: true
http.cors.allow-origin: "*"

```

### es-node-3配置文件（/home/elasticsearch/elasticsearch/config/elasticsearch.yml）
```text
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please consult the documentation for further information on configuration options:
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
#
cluster.name: cnvd-es
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: es-node-3
#
# Add custom attributes to the node:
#
#node.attr.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
#
path.data: /home/es/data
#
# Path to log files:
#
path.logs: /home/es/logs
#
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
#bootstrap.memory_lock: false
#
# Make sure that the heap size is set to about half the memory available
# on the system and that the owner of the process is allowed to use this
# limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
#
network.host: 0.0.0.0
network.bind_host: ["192.168.1.3"]
network.publish_host: 192.168.1.3
#
# Set a custom port for HTTP:
#
http.port: 9200
#
# For more information, consult the network module documentation.
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
discovery.zen.ping.unicast.hosts: ["192.168.1.1","192.168.1.2", "192.168.1.3"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of master-eligible nodes / 2 + 1):
#
discovery.zen.minimum_master_nodes: 2
#
# For more information, consult the zen discovery module documentation.
#
# ---------------------------------- Gateway -----------------------------------
#
# Block initial recovery after a full cluster restart until N nodes are started:
#
#gateway.recover_after_nodes: 3
#
# For more information, consult the gateway module documentation.
#
# ---------------------------------- Various -----------------------------------
#
# Require explicit names when deleting indices:
#
#action.destructive_requires_name: true
http.cors.enabled: true
http.cors.allow-origin: "*"

```

## 启动验证
在每个机器上/home/elasticsearch/目录下创建start.sh脚本，内容如下：
```
sh elasticsearch/bin/elasticsearch -d
```
启动执行start.sh即可

进程验证如下图所示

- 集群状态：http://192.168.10.1:9200/_cluster/health?pretty
- 节点状态：http://192.168.30.120:9200/_nodes/stats?pretty


## HEAD插件部署

### node安装（npm）：
```
yum -y install gcc gcc- c++ #安装依赖包

wget https://npm.taobao.org/mirrors/node/v10.14.1/node-v10.14.1-linux-x64.tar.gz #下载node包


tar -xvf  node-v8.0.0-linux-x64.tar.xz #解压

ln  -s  node-v8.1.4-linux-x64 node #软链接

```

### 配置环境变量
```
vim /etc/profile
export NODE_HOME=/home/es/node # 路径为wget的node包的绝对路径 
export PATH=$NODE_HOME/bin:$PATH

source /etc/profile
 node -v
 npm -v

```

### 使用git下载软件es-head软件包

```
yum -y install git

git clone git://github.com/mobz/elasticsearch-head.git  #从官网下载es-head插件

```

### 三个节点都需修改Gruntfile.js
```
 vim  /home/es/elasticsearch-head/Gruntfile.js 
 
```
 在options内插入hostname:
```
connect: {
        server: {
                options: {
                        hostname: '0.0.0.0',
                        port: 9100,
                        base: '.',
                        keepalive: true
                }
        }
}
```
### es-node-1修改app.js 
localhost更改为192.168.1.1
```
 vim /home/es/elasticsearch-head/_site/app.js
 
```
```
this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://192.168.1.1:9200";

```

### es-node-2修改app.js 
 localhost更改为192.168.1.2
```
 vim /home/es/elasticsearch-head/_site/app.js
 
```
```
this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://192.168.1.2:9200";

```

### es-node-3修改app.js 
localhost更改为192.168.1.3
```
 vim /home/es/elasticsearch-head/_site/app.js
 
```
```
this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://192.168.1.3:9200";

```

### 三个节点都需修改Elasticsearch.yml配置文件
在末端添加
```
http.cors.enabled: true
http.cors.allow-origin: "*"

```
### 安装grunt

```
npm install -g grunt-cli      #用于启动head插件的命令

```

### 运行head

```
进入elasticsearch-head目录下：
npm  install  #安装
grunt server  #启动
```

## Elaticsearch 设置开机自启动

```
vim  /etc/init.d/es_run

```
脚本内容：
```
#!/bin/bash
#chkconfig: 345 63 37
#description: elasticsearch
#processname: elasticsearch

export ES_HOME=/home/es/es

case $1 in
        start)
                su es<<!
                cd $ES_HOME
                ./bin/elasticsearch -d -p pid
                exit
!
                echo "elasticsearch is started"
                ;;
        stop)
                pid=`cat $ES_HOME/pid`
                kill -9 $pid
                echo "elasticsearch is stopped"
                ;;
        restart)
                pid=`cat $ES_HOME/pid`
                kill -9 $pid
                echo "elasticsearch is stopped"
                sleep 1
                su es<<!
                cd $ES_HOME
                ./bin/elasticsearch -d -p pid
                exit
!
                echo "elasticsearch is started"
        ;;
    *)
        echo "start|stop|restart"
        ;;
esac
exit 0

```
修改 es_run 权限
```
chmod +x es_run
```
添加启动方式并设置开机启动
```
chkconfig --add es_run　
chkconfig es_run on　
chkconfig --list #查看
```
重启验证脚本是否生效，服务是否起来

## ES-head插件部署
#### node安装（npm）：
```
yum -y install gcc gcc- c++ #安装依赖包

wget https://npm.taobao.org/mirrors/node/v10.14.1/node-v10.14.1-linux-x64.tar.gz #下载node包


tar -xvf  node-v8.0.0-linux-x64.tar.xz #解压

ln  -s  node-v8.1.4-linux-x64 node #软链接

```

#### 配置环境变量
```
vim /etc/profile
export NODE_HOME=/home/es/node # 路径为wget的node包的绝对路径 
export PATH=$NODE_HOME/bin:$PATH

source /etc/profile
 node -v
 npm -v

```

## SSL通信
```
bin/elasticsearch-certutil ca
bin/elasticsearch-certutil cert --ca elastic-stack-ca.p12
```

修改配置文件
```
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization,X-Requested-With,Content-Length,Content-Type
xpack.security.enabled: true
xpack.license.self_generated.type: basic
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: elastic-certificates.p12 # 注意路径
xpack.security.transport.ssl.truststore.path: elastic-certificates.p12 # 注意路径
```




------------


## SSL通信
```
bin/elasticsearch-certutil ca
bin/elasticsearch-certutil cert --ca elastic-stack-ca.p12
```

修改配置文件
```
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization,X-Requested-With,Content-Length,Content-Type
xpack.security.enabled: true
xpack.license.self_generated.type: basic
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.keystore.path: elastic-certificates.p12 # 注意路径
xpack.security.transport.ssl.truststore.path: elastic-certificates.p12 # 注意路径
```


