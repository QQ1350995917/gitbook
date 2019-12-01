# MongoDB分片部署
## 分片部署架构示意图
![分片部署架构示意图](images/deploy-sharding-replication.png)
随着数据的增长，单机实例的瓶颈是很明显的。可以通过复制的机制应对压力，但mongodb中单个集群的 节点数量限制到了12个以内，所以需要通过分片进一步横向扩展。此外分片也可节约磁盘的存储。

MongoDB分片+副本集集群环境搭建

## 1.**分片中的节点说明：**

* 路由节点\(mongos\)：用于分发用户的请求，起到反向代理的作用。
* 配置节点\(config\)：用于存储分片的元数据信息，路由节基于元数据信息 决定把请求发给哪个分片。（3.4版本之后，该节点，必须使用复制集。）
* 分片节点\(shard\):用于实际存储的节点，其每个数据块默认为64M，满了之后就会产生新的数据库。

开启分片功能
分片有三部分组成，如右图，路由，配置信息，分片；数据存储在分片上，在配置信息上记录数据存储的节点，在路由上访问时选择分片。
每个分片的大小可以配置默认64M



## **2.分片示例流程：**

1. 配置 并启动config 节点集群
   1. 配置集群信息
2. 配置并启动2个shard 节点
3. 配置并启动路由节点
   1. 添加shard 节点
   2. 添加shard 数据库
   3. 添加shard 集合
4. 插入测试数据
   1. 检查数据的分布
5. 插入大批量数据查看shard 分布
   1. 设置shard 数据块为一M
   2. 插入10万条数据

**配置 并启动config 节点集群**



## 节点1 config1-37017.conf

> dbpath=/data/mongo/config1
>
> port=37017
>
> fork=true
>
> logpath=logs/config1.log
>
> replSet=configCluster
>
> configsvr=true

## 节点2 config2-37018.conf

> dbpath=/data/mongo/config2
>
> port=37018
>
> fork=true
>
> logpath=logs/config2.log
>
> replSet=configCluster
>
> configsvr=true

进入shell 并添加 config 集群配置：

> var cfg ={"\_id":"configCluster",
>
> 		  "protocolVersion" : 1,
>
> 		  "members":\[
>
> 				{"\_id":0,"host":"127.0.0.1:37017"},
>
> 				{"\_id":1,"host":"127.0.0.1:37018"}
>
> 			\]
>
> 		 }
>
> // 重新装载配置，并重新生成集群。
>
> rs.initiate\(cfg\)

## 配置 shard 节点集群

> \# 节点1 shard1-47017.conf
>
> dbpath=/data/mongo/shard1
>
> port=47017
>
> fork=true
>
> logpath=logs/shard1.log
>
> shardsvr=true
>
>
>
> \# 节点2 shard2-47018.conf
>
> dbpath=/data/mongo/shard2
>
> port=47018
>
> fork=true
>
> logpath=logs/shard2.log
>
> shardsvr=true

配置 路由节点 mongos

## 节点 route-27017.conf

> port=27017
>
> bind\_ip=0.0.0.0
>
> fork=true
>
> logpath=logs/route.log
>
> configdb=configCluster/127.0.0.1:37017,127.0.0.1:37018

添加分片节点

> sh.status\(\)
>
> sh.addShard\("127.0.0.1:47017"\);
>
> sh.addShard\("127.0.0.1:47018"\);

为数据库开启分片功能

> sh.enableSharding\("testdb"\)

为指定集合开启分片功能

> sh.shardCollection\("testdb.emp",{"\_id":1}\)

修改分片大小

> use config
>
> db.settings.find\(\)
>
> db.settings.save\({\_id:"chunksize",value:1}\)

尝试插入1万条数据：

> for\(var i=1;i&lt;=100000;i++\){
>
> 			     db.emp.insert\({"\_id":i,"name":"copy"+i}\);
>
> }

## 用户管理与数据集验证

创建管理员用户

> use admin;
>
> db.createUser\({"user":"admin","pwd":"123456","roles":\["root"\]}\)
>
> \#验证用户信息
>
> db.auth\("admin","123456"\)
>
> \#查看用户信息
>
> db.getUsers\(\) 
>
> \# 修改密码
>
> db.changeUserPassword\("admin","123456"\)

以auth 方式启动mongod，需要添加auth=true 参数 ，mongdb 的权限体系才会起作用：

> \#以auth 方向启动mongod （也可以在mongo.conf 中添加auth=true 参数）
>
> ./bin/mongod -f conf/mongo.conf --auth
>
> \# 验证用户
>
> use admin;
>
> db.auth\("admin","123456"\)

创建只读用户

> db.createUser\({"user":"dev","pwd":"123456","roles":\["read"\]}\)

重新登陆 验证用户权限

> use test  ;
>
> db.auth\("dev","123456"\)








