# Dubbo 架构与设计说明 

## Dubbo工作原理
- 服务启动的时候，provider和consumer根据配置信息，连接到注册中心register，分别向注册中心注册和订阅服务 
- register根据服务订阅关系，返回provider信息到consumer，同时consumer会把provider信息缓存到本地。如果信息有变更，consumer会收到来自register的推送 
- consumer生成代理对象，同时根据负载均衡策略，选择一台provider，同时定时向monitor记录接口的调用次数和时间信息 
- 拿到代理对象之后，consumer通过代理对象发起接口调用 
- provider收到请求后对数据进行反序列化，然后通过代理调用具体的接口实现

![](images/dubbo-6.png)


## Dubbo负载均衡策略
- 加权随机：假设我们有一组服务器 servers = [A, B, C]，他们对应的权重为 weights = [5, 3, 2]，权重总和为10。现在把这些权重值平铺在一维坐标值上，[0, 5) 区间属于服务器 A，[5, 8) 区间属于服务器 B，[8, 10) 区间属于服务器 C。接下来通过随机数生成器生成一个范围在 [0, 10) 之间的随机数，然后计算这个随机数会落到哪个区间上就可以了。 
- 最小活跃数：每个服务提供者对应一个活跃数 active，初始情况下，所有服务提供者活跃数均为0。每收到一个请求，活跃数加1，完成请求后则将活跃数减1。在服务运行一段时间后，性能好的服务提供者处理请求的速度更快，因此活跃数下降的也越快，此时这样的服务提供者能够优先获取到新的服务请求。 
- 一致性hash：通过hash算法，把provider的invoke和随机节点生成hash，并将这个 hash 投射到 [0, 2^32 - 1] 的圆环上，查询的时候根据key进行md5然后进行hash，得到第一个节点的值大于等于当前hash的invoker。

![](images/dubbo-7.png)

- 加权轮询：比如服务器 A、B、C 权重比为 5:2:1，那么在8次请求中，服务器 A 将收到其中的5次请求，服务器 B 会收到其中的2次请求，服务器 C 则收到其中的1次请求。

## 集群容错
- Failover Cluster失败自动切换：dubbo的默认容错方案，当调用失败时自动切换到其他可用的节点，具体的重试次数和间隔时间可用通过引用服务的时候配置，默认重试次数为1也就是只调用一次。 
- Failback Cluster失败自动恢复：在调用失败，记录日志和调用信息，然后返回空结果给consumer，并且通过定时任务每隔5秒对失败的调用进行重试 
- Failfast Cluster快速失败：只会调用一次，失败后立刻抛出异常 
- Failsafe Cluster失败安全：调用出现异常，记录日志不抛出，返回空结果 
- Forking Cluster并行调用多个服务提供者：通过线程池创建多个线程，并发调用多个provider，结果保存到阻塞队列，只要有一个provider成功返回了结果，就会立刻返回结果 
- Broadcast Cluster广播模式：逐个调用每个provider，如果其中一台报错，在循环调用结束后，抛出异常。


## dubbo架构简要讲解
架构图

![](images/dubbo-0.png)

## 流程说明
1.	Provider(提供者)绑定指定端口并启动服务
2.	指供者连接注册中心，并发本机IP、端口、应用信息和提供服务信息发送至注册中心存储
3.	Consumer(消费者），连接注册中心 ，并发送应用信息、所求服务信息至注册中心
4.	注册中心根据 消费 者所求服务信息匹配对应的提供者列表发送至Consumer 应用缓存。
5.	Consumer 在发起远程调用时基于缓存的消费者列表择其一发起调用。
6.	Provider 状态变更会实时通知注册中心、在由注册中心实时推送至Consumer

这么设计的意义：
1.	Consumer 与Provider 解偶，双方都可以横向增减节点数。
2.	注册中心对本身可做对等集群，可动态增减节点，并且任意一台宕掉后，将自动切换到另一台
3.	去中心化，双方不直接依懒注册中心，即使注册中心全部宕机短时间内也不会影响服务的调用
4.	服务提供者无状态，任意一台宕掉后，不影响使用


## Dubbo 整体设计

![](images/dubbo-1.png)

-	config 配置层：对外配置接口，以 ServiceConfig, ReferenceConfig 为中心，可以直接初始化配置类，也可以通过 spring 解析配置生成配置类
-	proxy 服务代理层：服务接口透明代理，生成动态代理 扩展接口为 ProxyFactory
-	registry 注册中心层：封装服务地址的注册与发现，以服务 URL 为中心，扩展接口为 RegistryFactory, Registry, RegistryService
-	cluster 路由层：封装多个提供者的路由及负载均衡，并桥接注册中心，以 Invoker 为中心，扩展接口为 Cluster, Directory, Router, LoadBalance
-	monitor 监控层：RPC 调用次数和调用时间监控，以 Statistics 为中心，扩展接口为 MonitorFactory, Monitor, MonitorService
-	protocol 远程调用层：封装 RPC 调用，以 Invocation, Result 为中心，扩展接口为 Protocol, Invoker, Exporter
-	exchange 信息交换层：封装请求响应模式，同步转异步，以 Request, Response 为中心，扩展接口为 Exchanger, ExchangeChannel, ExchangeClient, ExchangeServer
-	transport 网络传输层：抽象 mina 和 netty 为统一接口，以 Message 为中心，扩展接口为 Channel, Transporter, Client, Server, Codec
-	serialize 数据序列化层：可复用的一些工具，扩展接口为 Serialization, ObjectInput, ObjectOutput, ThreadPool

其协作流程如下

![](images/dubbo-2.png)

## [Dubbo 中的SPI机制](../../../java/spi.md)
dubbo作为一个高度可扩展的rpc框架，也依赖于java的spi，并且dubbo对java原生的spi机制作出了一定的扩展，使得其功能更加强大。

首先，从上面的java spi的原理中可以了解到，java的spi机制有着如下的弊端：
- 只能遍历所有的实现，并全部实例化。
- 配置文件中只是简单的列出了所有的扩展实现，而没有给他们命名。导致在程序中很难去准确的引用它们。
- 扩展如果依赖其他的扩展，做不到自动注入和装配。
- 扩展很难和其他的框架集成，比如扩展里面依赖了一个Spring bean，原生的Java SPI不支持。

### dubbo的spi有如下几个概念
- 扩展点：一个接口。
- 扩展：扩展（接口）的实现。
- 扩展自适应实例：其实就是一个Extension的代理，它实现了扩展点接口。在调用扩展点的接口方法时，会根据实际的参数来决定要使用哪个扩展。dubbo会根据接口中的参数，自动地决定选择哪个实现。
- @SPI:该注解作用于扩展点的接口上，表明该接口是一个扩展点。
- @Adaptive：@Adaptive注解用在扩展接口的方法上。表示该方法是一个自适应方法。Dubbo在为扩展点生成自适应实例时，如果方法有@Adaptive注解，会为该方法生成对应的代码。

dubbo的spi也会从某些固定的路径下去加载配置文件，并且配置的格式与java原生的不一样，类似于property文件的格式：

![](images/dubbo-3.png)

下面将基于dubbo去实现一个简单的扩展实现。首先，要实现LoadBalance这个接口，当然这个接口是被注解标注的可以扩展的：

```java
@SPI("random")
public interface LoadBalance {
    @Adaptive({"loadbalance"})
    <T> Invoker<T> select(List<Invoker<T>> var1, URL var2, Invocation var3) throws RpcException;
}

public class DemoLoadBalance implements LoadBalance {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        System.out.println("my demo loadBalance is used, hahahahh");
        return invokers.get(0);//选择第一个
    }
}
```

然后，需要在duboo SPI的扫描目录下，添加配置文件，注意配置文件的名称要和扩展点的接口名称对应起来：

![](images/dubbo-4.png)

还需要在dubbo的spring配置中显式的声明，使用上面自己实现的负载均衡策略：
```xml
 <dubbo:reference id="helloService" interface="com.dubbo.spi.demo.api.IHelloService" loadbalance="demo" />
```
然后，启动dubbo，调用service，就可以发现确实是使用了自定义的负载策略：

dubbo spi的原理和jdk的实现稍有不同，大概流程如下图

![](images/dubbo-5.png)
