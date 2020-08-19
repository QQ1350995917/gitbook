# 客户端与会话
zookeeper 提供了命令行模式下的客户端连接以及java与C两种语言的客户端

## 客户端
### 命令行模式下连接
```
zkCli.sh -server 127.0.0.1:2181
zkCli.sh -server 127.0.0.1:21811,127.0.0.1:21812,127.0.0.1:21813
```
### 原生客户端
- [POM.xml](../../../zythum/zk-api/pom.xml)
- [连接zk并监听事件](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKWatcher.java)
- [创建znode并监听事件](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKCreateNode.java)
- [改变znode数据并监听事件](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKWriteData.java)
- [改变子节点并监听事件](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKChildNodeWatcher.java)
- [异步调用并完成回调](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKAsyncWatcher.java)
- [连接后创建回调](../../../zythum/zk-api/src/main/java/pwd/java/zk/api/ZKAsyncReader.java)

### ZkClient 
个人开源项目，是对于原生的封装。相对于原生特色有：
- 自动化创建节点层级
- 可以设置持久监听，或删除某个监听
- 可以插入JAVA对象，自动进行序列化和反序列化
- 简化了基本的增删改查操作。

基本操作
- [POM.xml](../../../zythum/zk-client/pom.xml)
- [ZkClient递归创建顺序节点](../../../zythum/zk-client/src/main/java/pwd/java/zk/client/ZKCreateNode.java)
- [ZkClient获取数据并监听事件](../../../zythum/zk-client/src/main/java/pwd/java/zk/client/ZKReader.java)
- [ZkClient获取子节点数据并监听事件](../../../zythum/zk-client/src/main/java/pwd/java/zk/client/ZKReaderChild.java)



### Curator
curator是连接ZK应用最广泛的工具

原因如下：

1）zk应用场景（分布式锁，Master选举等等），curator包含了这些场景。
2）应用场景出现极端的情况下，curator考虑到处理了。

apache旗下开源项目。封装功能更多，更完善。相对于原生特色有：
- 流式编程
- 自动化创建节点层级
- 可以设置持久监听，或删除某个监听
- 可以插入JAVA对象，自动进行序列化和反序列化
- 简化了基本的增删改查操作。
- 缓存
- 分布式原子操作
- 分布式锁
- 屏障

基本操作
- [POM.xml](../../../zythum/zk-curator/pom.xml)
- [curator创建连接session](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKSession.java)
- [curator递归创建顺序节点](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKCreateNode1.java)
- [curator异步创建临时节点](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKCreateNode2.java)
- [curator更新节点数据](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKUpdate.java)
- [curator删除节点数据](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKDelete.java)
- [curator事件监听](../../../zythum/zk-curator/src/main/java/pwd/java/zk/curator/ZKWatcher.java)
 

## Session会话

### Session Backoff(退避算法)
   
有这样一种场景，有多个请求，如果网络出现阻塞,每1分钟重试一次。
1. 20：25 request1（block）
1. 20：26 request2（block）
1. 20：27 request3（block）
1. 当网络通顺的时候，请求都累在一起来发送
1. 20：28 request4（通顺）request1、2、3、4

那么前面的请求就没有意义了，所以就有了退避算法，按照指数间隔重试，比如第一次1分钟，第二次2分钟......随着时间的推移，重试间隔越长。
   
### zookeeper连接的几种状态
1. CONNECTING 正在连接
1. CONNECTED 已经连接## 原生客户端
1. RECONNECTING 正在重新连接客户端类是 org.apache.zookeeper.ZooKeeper，实例化该类之后将会自动与集群建立连接。
1. RECONNECTED 重新连接上
1. CLOSE 会话关闭

### session主要由几个类控制：
- SessionTracker, 
- LearnerSessionTracker, 
- SessionTrackerImpl

### session初始化的方法：
```java
org.apache.zookeeper.server.SessionTrackerImpl.initializeNextSession(long)
public static long initializeNextSession(long id) {
        long nextSid = 0;
        nextSid = (System.currentTimeMillis() << 24) >>> 8;
        nextSid =  nextSid | (id <<56);
        return nextSid;
    }

```


说明：

SessionID的分配（初始化）函数，策略如下：

1. 取时间，并且左移24位得到的结果再右移8位（高8位，低16位都是0）
1. sid拿出来进行左移56位
1. 和第一步的结果做或运算

### Session分桶（zookeeper的一个特性）
按照Session会话过期时间进行分区块保存。这样设计的好处：可以快速清理过期的session

### session激活过程：
1. 检测会话是否过期
1. 计算会话下一次超时时间
1. 定位会话的所在区块
1. 迁移会话
