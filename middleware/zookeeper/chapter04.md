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

