# Reids基本概念

Redis是现在最受欢迎的NoSQL数据库之一，Redis是一个使用ANSI C编写的开源、包含多种数据结构、支持网络、基于内存、可选持久性的键值对存储数据库，其具备如下特性：
- 基于内存运行，性能高效
- 支持分布式，理论上可以无限扩展
- key-value存储系统
- 开源的使用ANSI C语言编写、遵守BSD协议、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API

相比于其他数据库类型，Redis具备的特点是：
- C/S通讯模型
- 单进程单线程模型
- 丰富的数据类型
- 操作具有原子性
- 持久化
- 高并发读写
- 支持lua脚本

## Redis的应用场景有哪些？
- 缓存热点数据
- 计数器、限流
- 发布订阅、排行榜
- 分布式锁
- 分布式队列
- 共享Session

## Redis的数据类型及主要特性
Redis提供的数据类型主要分为5种自有类型和一种自定义类型，这5种自有类型包括：
- String类型
- 哈希类型
- 列表类型
- 集合类型
- 顺序集合类型

### String类型：
它是一个二进制安全的字符串，意味着它不仅能够存储字符串、还能存储图片、视频等多种类型, 最大长度支持512M。

对每种数据类型，Redis都提供了丰富的操作命令，如：

- GET/MGET
- SET/SETEX/MSET/MSETNX
- INCR/DECR
- GETSET
- DEL

### 哈希类型：
该类型是由field和关联的value组成的map。其中，field和value都是字符串类型的。

Hash的操作命令如下：

- HGET/HMGET/HGETALL
- HSET/HMSET/HSETNX
- HEXISTS/HLEN
- HKEYS/HDEL
- HVALS

### 集合-列表类型：
该类型是一个插入顺序排序的字符串元素集合, 基于双链表实现。

List的操作命令如下：

- LPUSH/LPUSHX/LPOP/RPUSH/RPUSHX/RPOP/LINSERT/LSET
- LINDEX/LRANGE
- LLEN/LTRIM

### 集合-去重类型：
Set类型是一种无顺序集合, 它和List类型最大的区别是：集合中的元素没有顺序, 且元素是唯一的。

Set类型的底层是通过哈希表实现的，其操作命令为：

- SADD/SPOP/SMOVE/SCARD
- SINTER/SDIFF/SDIFFSTORE/SUNION

Set类型主要应用于：在某些场景，如社交场景中，通过交集、并集和差集运算，通过Set类型可以非常方便地查找共同好友、共同关注和共同偏好等社交关系。

### 集合-有序集合类型：
ZSet是一种有序集合类型，每个元素都会关联一个double类型的分数权值，通过这个权值来为集合中的成员进行从小到大的排序。与Set类型一样，其底层也是通过哈希表实现的。

ZSet命令：

- ZADD/ZPOP/ZMOVE/ZCARD/ZCOUNT
- ZINTER/ZDIFF/ZDIFFSTORE/ZUNION


## Redis的数据结构
Redis的数据结构如下图所示：

![](images/concept-01.png)

如下是定义一个Struct数据结构的例子：

![](images/concept-02.png)

### 简单动态字符串SDS (Simple Dynamic String)
基于C语言中传统字符串的缺陷，Redis自己构建了一种名为简单动态字符串的抽象类型，简称SDS，其结构如下：

![](images/concept-03.png)

SDS几乎贯穿了Redis的所有数据结构，应用十分广泛。

SDS的特点

和C字符串相比，SDS的特点如下：

![](images/concept-04.png)

1. 常数复杂度获取字符串长度
    
    Redis中利用SDS字符串的len属性可以直接获取到所保存的字符串的长
　　　　度，直接将获取字符串长度所需的复杂度从C字符串的O(N)降低到了O(1)。

1. 减少修改字符串时导致的内存重新分配次数

    通过C字符串的特性，我们知道对于一个包含了N个字符的C字符串来说，其底层实现总是N+1个字符长的数组（额外一个空字符结尾）

　　那么如果这个时候需要对字符串进行修改，程序就需要提前对这个C字符串数组进行一次内存重分配（可能是扩展或者释放）　

　　而内存重分配就意味着是一个耗时的操作。

Redis巧妙的使用了SDS避免了C字符串的缺陷。在SDS中，buf数组的长度不一定就是字符串的字符数量加一，buf数组里面可以包含未使用的字节，而这些未使用的字节由free属性记录。

与此同时，SDS采用了空间预分配的策略，避免C字符串每一次修改时都需要进行内存重分配的耗时操作，将内存重分配从原来的每修改N次就分配N次——>降低到了修改N次最多分配N次。

如下是Redis对SDS的简单定义：

![](images/concept-04.png)

![](images/concept-06.png)

## Redis特性1：事务
- 命令序列化，按顺序执行
- 原子性
- 三阶段: 开始事务 - 命令入队 - 执行事务
- 命令：MULTI/EXEC/DISCARD

## Redis特性2：发布订阅(Pub/Sub)
- Pub/sub是一种消息通讯模式
- Pub发送消息, Sub接受消息
- Redis客户端可以订阅任意数量的频道
- “fire and forgot”, 发送即遗忘
- 命令：Publish/Subscribe/Psubscribe/UnSub

![](images/concept-07.png)

## Redis特性3：Stream
- Redis 5.0新增
- 等待消费
- 消费组(组内竞争)
- 消费历史数据
- FIFO

![](images/concept-08.png)

## Redis常见问题解析：雪崩


## Redis常见问题解析：击穿
概念：在Redis获取某一key时, 由于key不存在, 而必须向DB发起一次请求的行为, 称为“Redis击穿”。

![](images/concept-09.png)

引发击穿的原因：

- 第一次访问
- 恶意访问不存在的key
- Key过期

合理的规避方案：

- 服务器启动时, 提前写入
- 规范key的命名, 通过中间件拦截
- 对某些高频访问的Key，设置合理的TTL或永不过期

## Redis常见问题解析：穿透


## Redis协议简介
Redis客户端通讯协议：RESP(Redis Serialization Protocol)，其特点是：

- 简单
- 解析速度快
- 可读性好

Redis集群内部通讯协议：RECP(Redis Cluster Protocol ) ，其特点是：

每一个node两个tcp 连接
一个负责client-server通讯(P: 6379)
一个负责node之间通讯(P: 10000 + 6379)

![](images/concept-10.png)

## Redis协议支持的数据类型：

- 简单字符(首字节: “+”)
  ```text
  “+OK\r\n”
  ```
      
- 错误(首字节: “-”)
  ```text
  “-error msg\r\n”
  ```

- 数字(首字节: “:”)
  ```text
  “:123\r\n”
  ```

- 批量字符(首字节: “$”)
  ```text
  “&hello\r\nWhoa re you\r\n”
  ```

- 数组(首字节: “*”)
  ```text
  “*0\r\n”
  “*-1\r\n”
  ```
