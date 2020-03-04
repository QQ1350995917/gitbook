## 客户端
zookeeper 提供了命令行模式下的客户端连接以及java与C两种语言的客户端

## 命令行模式下连接
```
zkCli.sh -server 127.0.0.1:2181
zkCli.sh -server 127.0.0.1:21811,127.0.0.1:21812,127.0.0.1:21813
```

## 原生客户端
```xml
<dependency>
  <groupId>org.apache.zookeeper</groupId>
  <artifactId>zookeeper</artifactId>
  <version>3.5.5</version>
</dependency>
```
客户端类是 org.apache.zookeeper.ZooKeeper，实例化该类之后将会自动与集群建立连接。


## ZkClient 
个人开源项目，是对于原生的封装。相对于原生特色有：
- 自动化创建节点层级
- 可以设置持久监听，或删除某个监听
- 可以插入JAVA对象，自动进行序列化和反序列化
- 简化了基本的增删改查操作。
```xml
<dependencies>
  <dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.5.5</version>
  </dependency>
  
  <dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.11</version>
  </dependency>
</dependencies>
```

## CURATOR
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

```xml
<dependency>
  <groupId>org.apache.curator</groupId>
  <artifactId>curator-recipes</artifactId>
  <version>4.2.0</version>
  <type>bundle</type>
</dependency>
```
