MongoDB主从环境搭建

主从环境搭建有两种方式，一主一从，一主多从。这里仅做一主多从方式的搭建。

主节点配置

> dbpath=/data/mongo/master
>
> port=27017
>
> fork=true
>
> logpath=master.log
>
> replSet=cluster

从节点1配置

> dbpath=/data/mongo/slave1
>
> port=27018
>
> fork=true
>
> logpath=slave1.log
>
> replSet=cluster

从节点2配置

> dbpath=/data/mongo/slave2
>
> port=27019
>
> fork=true
>
> logpath=slave2.log
>
> replSet=cluster

分别启动三个节点并进入其中一个节点

集群复制配置管理

> \#查看复制集群的帮助方法
>
> rs.help\(\)

添加配置

> // 声明配置变量
>
> var cfg ={"\_id":"tulingCluster",
>
> 			"members":\[
>
> 				{"\_id":0,"host":"127.0.0.1:27017"},
>
> 				{"\_id":1,"host":"127.0.0.1:27018"}
>
> 			\]
>
> 		 }
>
> // 初始化配置
>
> rs.initiate\(cfg\)
>
> // 查看集群状态
>
> rs.status\(\)

变更节点示例：

> // 插入新的复制节点
>
> rs.add\("127.0.0.1:27019"\)
>
> // 删除slave 节点
>
> rs.remove\("127.0.0.1:27019"\)

演示复制状态

进入主节点客户端

插入数据

进入从节点查看数据

尝试在从节点下插入数据

注：默认节点下从节点不能读取数据。调用 rs.slaveOk\(\) 解决。

### 复制集群选举操作

为了保证高可用，在集群当中如果主节点挂掉后，会自动 在从节点中选举一个 重新做为主节点。

演示节点的切换操作

kill 主节点

进入从节点查看集群状态 。rs.status\(\)**选举的原理：**

在mongodb 中通过在 集群配置中的 rs.属性值大小来决定选举谁做为主节点，通时也可以设置arbiterOnly 为true 表示 做为裁判节点用于执行选举操作，该配置下的节点 永远不会被选举为主节点和从节点。

> 重新配置节点
>
> var cfg ={"\_id":"tulingCluster",
>
> 		  "protocolVersion" : 1,
>
> 		  "members":\[
>
> 				{"\_id":0,"host":"127.0.0.1:27017","priority":10},
>
> 				{"\_id":1,"host":"127.0.0.1:27018","priority":2},
>
> 				{"\_id":2,"host":"127.0.0.1:27019","arbiterOnly":true}
>
> 			\]
>
> 		 }
>
> // 重新装载配置，并重新生成集群节点。
>
> rs.reconfig\(cfg\)
>
> //重新查看集群状态
>
> rs.status\(\)

**节点说明：**

 PRIMARY 节点： 可以查询和新增数据  SECONDARY 节点：只能查询 不能新增  基于priority 权重可以被选为主节点  RBITER 节点： 不能查询数据 和新增数据 ，不能变成主节点







