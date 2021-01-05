# 核心概念
## 数据日志
zookeeper服务器会产生三类日志：

- 事务日志（zoo.cfg dataLogDir）（相关读写的类可以见org.apache.zookeeper.server.persistence.FileTxnLog）

- 快照日志（zoo.cfg dataDir）（相关序列化和反序列化的类可以看org.apache.zookeeper.server.persistence.FileSnap）

- log4j日志（log4j.properties zookeeper.log.dir）

在zookeeper默认配置文件zoo.cfg（可以修改文件名）中有一个配置项dataDir，该配置项用于配置zookeeper快照日志和事务日志的存储地址。

在官方提供的默认参考配置文件zoo_sample.cfg中，只有dataDir配置项。其实在实际应用中，还可以为事务日志专门配置存储地址，配置项名称为dataLogDir，在zoo_sample.cfg中并未体现出来。

在没有dataLogDir配置项的时候，zookeeper默认将事务日志文件和快照日志文件都存储在dataDir对应的目录下。

建议将事务日志（dataLogDir）与快照日志（dataDir）单独配置，因为当zookeeper集群进行频繁的数据读写操作是，会产生大量的事务日志信息，将两类日志分开存储会提高系统性能，而且，可以允许将两类日志存在在不同的存储介质上，减少磁盘压力。

log4j用于记录zookeeper集群服务器运行日志，该日志的配置地址在conf/目录下的log4j.properties文件中，该文件中有一个配置项为“zookeeper.log.dir=.”，表示log4j日志文件在与执行程序（zkServer.sh）在同一目录下。当执行zkServer.sh 时，在该文件夹下会产生zookeeper.out日志文件。

下面主要介绍事务日志与快照日志。



### 事务日志
事务日志指zookeeper系统在正常运行过程中，针对所有的更新操作，在返回客户端“更新成功”的响应前，zookeeper会保证已经将本次更新操作的事务日志已经写到磁盘上，只有这样，整个更新操作才会生效。

日志文件的命名规则为log.**，默认文件大小为64MB，**表示写入该日志的第一个事务的ID，十六进制表示。

zookeeper的事务日志为二进制文件，不能通过vim等工具直接访问。其实可以通过zookeeper自带的jar包读取事务日志文件。

首先将libs中的slf4j-api-1.6.1.jar文件和zookeeper根目录下的zookeeper-3.4.9.jar文件复制到临时文件夹tmplibs中，然后执行如下命令：

```bash
java -classpath .:slf4j-api-1.6.1.jar:zookeeper-3.4.9.jar  org.apache.zookeeper.server.LogFormatter   ../Data/datalog/version-2/log.1
```

### 快照日志
zookeeper的数据在内存中是以树形结构进行存储的，而快照就是每隔一段时间就会把整个DataTree的数据序列化后存储在磁盘中，这就是zookeeper的快照文件。

zookeeper快照日志的存储路径同样可以在zoo.cfg中查看，访问dataDir路径可以看到version-2文件夹:

zookeeper快照文件的命名规则为snapshot.\*\*，其中\*\*表示zookeeper触发快照的那个瞬间，提交的最后一个事务的ID。

### 日志清理
在zookeeper 3.4.0以后，zookeeper提供了自动清理snapshot和事务日志功能，通过配置zoo.cfg下的autopurge.snapRetainCount和autopurge.purgeInterval这两个参数实现日志文件的定时清理。

autopurge.snapRetainCount这个参数指定了需要保留的文件数目，默认保留3个；

autopurge.purgeInterval这个参数指定了清理频率，单位是小时，需要填写一个1或者更大的数据，默认0表示不开启自动清理功能。

### zookeeper的数据恢复
当进行一次快照时，就会重新生成一个新的日志文件，对于每次的修改会有对应的事务日志。

二者结合可以把数据恢复(断点+动作重放)，启动时数据恢复的具体的逻辑在{org.apache.zookeeper.server.persistence.FileTxnSnapLog}中：代码中先读取快照数据生成断点映像，然后根据断点映像中最大zxid开始进行事务重放。

## Session会话

客户端会话，客户端和服务端建立一个 TCP 长连接

## 数据模型
![](images/data-model.png)

### Znode 结构
```bash
stat 
```

|状态属性 | 说明|
|---|---| 
|czxid|节点创建时的 zxid|
|mzxid|节点最新一次更新发生时的 zxid|
|ctime|节点创建时的时间戳.|
|mtime|节点最新一次更新发生时的时间戳.|
|dataVersion|节点数据的更新次数.|
|cversion|其子节点的更新次数|
|aclVersion|节点 ACL(授权信息)的更新次数.|
|ephemeralOwner|如果该节点为 ephemeral 节点, ephemeralOwner 值表示与该节点绑定的 session id. 如果该节点不是ephemeral节点, ephemeralOwner 值为 0. 至于什么是 ephemeral 节点|
|dataLength|节点数据的字节数.|
|numChildren|子节点个数.|

## 节点类型:
- 两大类、四种类型 持久、临时、持久有序、临时有序
- PERSISTENT 持久类型，如果不手动删除 是一直存在的 PERSISTENT_SEQUENTIAL
- EPHEMERAL 临时 客户端 session 失效就会随着删除节点 没有子节点 EPHEMERAL_SEQUENTIAL 有序 自增

## 顺序号:
创建 znode 时设置顺序标识，znode 名称后会附加一个值 顺序号是一个单调递增的计数器，由父节点维护 在分布式系统中，顺序号可以被用于为所有的事件进行全局排序，这样客户端可以通过顺 序号推断事件的顺序

## Watcher:监听 
| KeeperState | EventType | 触发条件 | 说明 | 操作 |
|---|---|---|---|---|
|SyncConnected (3)|None (-1)|客户端与服务端 成功建立连接|此时客户端和服务器处于连接状态|-|
|SyncConnected (3)|NodeCreated (1)|Watcher 监听的 对应数据节点被 创建|此时客户端和服务器处于连接状态|Create|
|SyncConnected (3)|NodeDeleted (2)|Watcher 监听的 对应数据节点被 删除|此时客户端和服务器处于连接状态|Delete/znode|
|SyncConnected (3)|NodeDataChanged(3)|Watcher 监听的 对应数据节点的 数据内容发生变 更|-|setDate/znode|
|SyncConnected (3)|NodeChildChanged(4)|Wather 监听的 对应数据节点的 子节点列表发生 变更|-|Create/child|
|Disconnected (0)|None (-1)|客户端与 ZooKeeper 服务 器断开连接|此时客户端和服|-|
|Expired (-112)|None (-1)|会话超时|此时客户端会话 失效，通常同时 也会受到 务器处于断开连SessionExpiredEx ception 异常|-|
|AuthFailed (4)|None (-1)|通常有两种情 况，1:使用错 误的 schema 进接状态 行权限检查 2: SASL 权限检查 失败|通常同时也会收 到 AuthFailedExcept ion 异常|-|

## ACL(Access Control List)
org.apache.zookeeper.ZooDefs

内置的 ACL schemes
- world:默认方式，相当于全世界都能访问
- auth:代表已经认证通过的用户(cli中可以通过 addauth digest user:pwd来添加前上下文中的授权用户）
- digest:即用户名:密码这种方式认证，这也是业务系统中最常用的
- ip:使用Ip地址认证

ACL支持权限:
- CREATE:能创建子节点
- READ:能获取节点数据和列出其子节点
- WRITE:能设置节点数据
- DELETE:能删除子节点
- ADMIN:能设置权限

## 运维四字命令
| ZooKeeper四字命令 |	功能描述 |
|---|---|
| conf | 3.3.0版本引入的。打印出服务相关配置的详细信息。 |
| cons | 3.3.0版本引入的。列出所有连接到这台服务器的客户端全部连接/会话详细信息。包括"接受/发送"的包数量、会话id、操作延迟、最后的操作执行等等信息。 |
| crst | 3.3.0版本引入的。重置所有连接的连接和会话统计信息。 |
| dump | 列出那些比较重要的会话和临时节点。这个命令只能在leader节点上有用。 |
| envi | 打印出服务环境的详细信息。 |
| reqs | 列出未经处理的请求 |
| ruok | 测试服务是否处于正确状态。如果确实如此，那么服务返回"imok"，否则不做任何相应。 |
| stat | 输出关于性能和连接的客户端的列表。 |
| srst | 重置服务器的统计。 |
| srvr | 3.3.0版本引入的。列出连接服务器的详细信息 |
| wchs | 3.3.0版本引入的。列出服务器watch的详细信息。 |
| wchc | 3.3.0版本引入的。通过session列出服务器watch的详细信息，它的输出是一个与watch相关的会话的列表。 |
| wchp | 3.3.0版本引入的。通过路径列出服务器watch的详细信息。它输出一个与session相关的路径。 |
| mntr | 3.4.0版本引入的。输出可用于检测集群健康状态的变量列表 |


## Zookeeper节点
zookeeper 中节点叫znode存储结构上跟文件系统类似，以树级结构进行存储。不同之外在于znode没有目录的概念，不能执行类似cd之类的命令。znode结构包含如下：
- path:唯一路径 
- childNode：子节点
- type:节点类型
- stat:状态属性

### 节点类型
|类型|描述|
|----|----|
|PERSISTENT|持久节点|
|PERSISTENT_SEQUENTIAL|持久序号节点|
|EPHEMERAL|临时节点(不可在拥有子节点)|
|EPHEMERAL_SEQUENTIAL|临时序号节点(不可在拥有子节点)|
1. PERSISTENT（持久节点）  
   持久化保存的节点，默认创建的就是持久节点
   ```
   create /test
   ```
2. PERSISTENT_SEQUENTIAL(持久序号节点)  
   创建时zookeeper会在路径上加上序号作为后缀。非常适合用于分布式锁、分布式选举等场景。创建时添加 -s 参数即可。
   ```
   create -s /test_s
   ```
   返回创建的实际路径
   Created /test0000000001
   create -s /test_s
   返回创建的实际路径
   Created /test0000000002

3. EPHEMERAL（临时节点）
   临时节点会在客户端会话 断开后自动删除。适用于心跳，服务发现等场景。创建时添加参数-e 即可。
   ```
   create -e /test_e
   ```

4. EPHEMERAL_SEQUENTIAL（临时序号节点）
   与持久序号节点类似，不同之处在于EPHEMERAL_SEQUENTIAL是临时的会在会话断开后删除。创建时添加 -e -s 
   ```
   create -e -s /test_e_s
   ``` 

### 节点属性
1. 查看节点属性
```
stat /test

cZxid = 0xc
ctime = Sun Dec 08 19:32:11 CST 2019
mZxid = 0xc
mtime = Sun Dec 08 19:32:11 CST 2019
pZxid = 0xc
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 0

```
其属性说明如下：
- cZxid：创建节点的事务ID
- ctime: 创建时间
- mZxid: 修改节点的事物ID
- mtime: 最后修改时间
- pZxid: 子节点变更的事物ID
- cversion: 表示对此znode的子节点进行的更改次数（不包括子子节点）
- dataVersion: 数据版本，变更次数
- aclVersion: 权限版本，变更次数
- ephemeralOwner: 临时节点所属会话ID
- dataLength: 数据长度
- numChildren: 子节点数(不包括子子节点)

### 节点的监听：
客户端添加 -w 参数可实时监听节点与子节点的变化，并且实时收到通知。非常适用保障分布式情况下的数据一至性。其使用方式如下：

|命令|描述|
|----|----|
|ls -w path|监听子节点的变化（增，删）|
|get -w path|监听节点数据的变化|
|stat -w path|监听节点属性的变化|
|printwatches on/off|触发监听后，是否打印监听事件(默认on)|


### acl权限设置
ACL全称为Access Control List（访问控制列表），用于控制资源的访问权限。ZooKeeper使用ACL来控制对其znode的防问。基于scheme:id:permission的方式进行权限控制。scheme表示授权模式、id模式对应值、permission即具体的增删改权限位。

- scheme:认证模型

|方案|描述|
|----|----|
|world|开放模式，world表示全世界都可以访问（这是默认设置）|
|ip|ip模式，限定客户端IP防问|
|auth|用户密码认证模式，只有在会话中添加了认证才可以防问|
|digest|与auth类似，区别在于auth用明文密码，而digest 用sha-1+base64加密后的密码。在实际使用中digest 更常见。|

- permission权限位

|权限位	|权限|描述|
|----|----|----|
|c|CREATE|可以创建子节点|
|d|DELETE|可以删除子节点（仅下一级节点）|
|r|READ|可以读取节点数据及显示子节点列表|
|w|WRITE|可以设置节点数据|
|a|ADMIN|可以设置节点访问控制列表权限|

- acl 相关命令：

|命令|使用方式|描述|
|----|----|----|
|getAcl|getAcl <path>|读取ACL权限|
|setAcl|setAcl <path> <acl>|设置ACL权限|
|addauth|addauth <scheme> <auth>|添加认证用户|

- world权限示例
语法： setAcl <path> world:anyone:<权限位>
注：world模式中anyone是唯一的值,表示所有人
1. 查看默认节点权限：  
   创建一个节点
   ```
   create -e /testAcl
   ```
   查看节点权限
   ```
   getAcl /testAcl
   ```
   返回的默认权限表示 ，所有人拥有所有权限。
   'world,'anyone: cdrwa

2. 修改默认权限为 读写
   设置为rw权限
   ```
   setAcl /testAcl world:anyone:rw
   ``` 
   可以正常读
   ```
   get /testAcl
   ```
   无法正常创建子节点
   ```
   create -e /testAcl/t "hi"
   ```
   返回没有权限的异常
   ```
   Authentication is not valid : /testAcl/t
   ```

- IP权限示例：
语法： setAcl <path> ip:<ip地址|地址段>:<权限位>

- auth模式示例:
语法： 
1.	setAcl <path> auth:<用户名>:<密码>:<权限位>
2.	addauth digest <用户名>:<密码>

- digest 权限示例：
语法： 
1.	setAcl <path> digest :<用户名>:<密钥>:<权限位>
2.	addauth digest <用户名>:<密码>
注1：密钥 通过sha1与base64组合加密码生成，可通过以下命令生成
echo -n <用户名>:<密码> | openssl dgst -binary -sha1 | openssl base64
注2：为节点设置digest 权限后，访问前必须执行addauth，当前会话才可以防问。

1.	设置digest 权限  
    先 sha1 加密，然后base64加密
    ```
    echo -n pwd:123456 | openssl dgst -binary -sha1 | openssl base64
    返回密钥
    ```
    设置digest权限
    ```
    setAcl /test digest:pwd:返回秘钥:cdrw
    ```

2.	查看节点将显示没有权限
    查看节点
    ```
    get /test
    ```
    显示没有权限访问
    ```
    org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /test
    ```
3.	给当前会话添加认证后在次查看
    给当前会话添加权限帐户
    ```
    addauth digest pwd:123456
    ```
    在次查看
    ```
    get /test
    ```
    获得返回结果
    ```
    hello world
    ```
    ACL的特殊说明：
    权限仅对当前节点有效，不会让子节点继承。如限制了IP防问A节点，但不妨碍该IP防问A的子节点 /A/B。



## 参考资料
https://www.cnblogs.com/jxwch/p/6526271.html
https://www.cnblogs.com/leesf456/p/6179118.html
