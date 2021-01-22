# Kafka配置参数详解

每个kafka broker中配置文件server.properties默认必须配置的属性如下：
```text
broker.id=0  
num.network.threads=2  
num.io.threads=8  
socket.send.buffer.bytes=1048576  
socket.receive.buffer.bytes=1048576  
socket.request.max.bytes=104857600  
log.dirs=/tmp/kafka-logs  
num.partitions=2  
log.retention.hours=168  
  
log.segment.bytes=536870912  
log.retention.check.interval.ms=60000  
log.cleaner.enable=false  
  
zookeeper.connect=localhost:2181  
```

系统参数
```text
#唯一标识在集群中的ID，要求是正数。
broker.id=0
#服务端口，默认9092
port=9092
#监听地址
host.name=debugo01

# 处理网络请求的最大线程数
num.network.threads=2
# 处理磁盘I/O的线程数
num.io.threads=8
# 一些后台线程数
background.threads = 4
# 等待IO线程处理的请求队列最大数
queued.max.requests = 500

# socket的发送缓冲区（SO_SNDBUF）
socket.send.buffer.bytes=1048576
# socket的接收缓冲区 (SO_RCVBUF)
socket.receive.buffer.bytes=1048576
# socket请求的最大字节数。为了防止内存溢出，message.max.bytes必然要小于
socket.request.max.bytes = 104857600
```

Topic参数
```text
# 每个topic的分区个数，更多的partition会产生更多的segment file
num.partitions=2
# 是否允许自动创建topic ，若是false，就需要通过命令创建topic
auto.create.topics.enable =true
# 一个topic ，默认分区的replication个数 ，不能大于集群中broker的个数。
default.replication.factor =1
# 消息体的最大大小，单位是字节
message.max.bytes = 1000000
```

ZooKeeper参数
```text
# Zookeeper quorum设置。如果有多个使用逗号分割
zookeeper.connect=debugo01:2181,debugo02,debugo03
# 连接zk的超时时间
zookeeper.connection.timeout.ms=1000000
# ZooKeeper集群中leader和follower之间的同步实际
zookeeper.sync.time.ms = 2000
```

日志参数
```text
#日志存放目录，多个目录使用逗号分割
log.dirs=/var/log/kafka

# 日志清理策略（delete|compact）
log.cleanup.policy = delete
# 日志保存时间 (hours|minutes)，默认为7天（168小时）。超过这个时间会根据policy处理数据。bytes和minutes无论哪个先达到都会触发。
log.retention.hours=168
# 日志数据存储的最大字节数。超过这个时间会根据policy处理数据。
#log.retention.bytes=1073741824

# 控制日志segment文件的大小，超出该大小则追加到一个新的日志segment文件中（-1表示没有限制）
log.segment.bytes=536870912
# 当达到下面时间，会强制新建一个segment
log.roll.hours = 24*7
# 日志片段文件的检查周期，查看它们是否达到了删除策略的设置（log.retention.hours或log.retention.bytes）
log.retention.check.interval.ms=60000

# 是否开启压缩
log.cleaner.enable=false
# 对于压缩的日志保留的最长时间
log.cleaner.delete.retention.ms = 1 day

# 对于segment日志的索引文件大小限制
log.index.size.max.bytes = 10 * 1024 * 1024
#y索引计算的一个缓冲区，一般不需要设置。
log.index.interval.bytes = 4096
```

副本参数
```text
# 是否自动平衡broker之间的分配策略
auto.leader.rebalance.enable = false
# leader的不平衡比例，若是超过这个数值，会对分区进行重新的平衡
leader.imbalance.per.broker.percentage = 10
# 检查leader是否不平衡的时间间隔
leader.imbalance.check.interval.seconds = 300
# 客户端保留offset信息的最大空间大小
offset.metadata.max.bytes = 1024
```

消费者参数
```text
# Consumer端核心的配置是group.id、zookeeper.connect
# 决定该Consumer归属的唯一组ID，By setting the same group id multiple processes indicate that they are all part of the same consumer group.
group.id
# 消费者的ID，若是没有设置的话，会自增
consumer.id
# 一个用于跟踪调查的ID ，最好同group.id相同
client.id = <group_id>

# socket的超时时间，实际的超时时间为max.fetch.wait + socket.timeout.ms.
socket.timeout.ms= 30 * 1000
# socket的接收缓存空间大小
socket.receive.buffer.bytes=64 * 1024
#从每个分区fetch的消息大小限制
fetch.message.max.bytes = 1024 * 1024

# true时，Consumer会在消费消息后将offset同步到zookeeper，这样当Consumer失败后，新的consumer就能从zookeeper获取最新的offset
auto.commit.enable = true
# 自动提交的时间间隔
auto.commit.interval.ms = 60 * 1000

# 用于消费的最大数量的消息块缓冲大小，每个块可以等同于fetch.message.max.bytes中数值
queued.max.message.chunks = 10

# 当有新的consumer加入到group时,将尝试reblance,将partitions的消费端迁移到新的consumer中, 该设置是尝试的次数
rebalance.max.retries = 4
# 每次reblance的时间间隔
rebalance.backoff.ms = 2000
# 每次重新选举leader的时间
refresh.leader.backoff.ms

# server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收。
fetch.min.bytes = 1
# 若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
fetch.wait.max.ms = 100
# 如果指定时间内没有新消息可用于消费，就抛出异常，默认-1表示不受限
consumer.timeout.ms = -1
```

生产者参数
```text
# 消费者获取消息元信息(topics, partitions and replicas)的地址,配置格式是：host1:port1,host2:port2，也可以在外面设置一个vip
metadata.broker.list

#消息的确认模式
# 0：不保证消息的到达确认，只管发送，低延迟但是会出现消息的丢失，在某个server失败的情况下，有点像TCP
# 1：发送消息，并会等待leader 收到确认后，一定的可靠性
# -1：发送消息，等待leader收到确认，并进行复制操作后，才返回，最高的可靠性
request.required.acks = 0

# 异步模式下缓冲数据的最大时间。例如设置为100则会集合100ms内的消息后发送，这样会提高吞吐量，但是会增加消息发送的延时
queue.buffering.max.ms = 5000
# 异步模式下缓冲的最大消息数，同上
queue.buffering.max.messages = 10000
# 异步模式下，消息进入队列的等待时间。若是设置为0，则消息不等待，如果进入不了队列，则直接被抛弃
queue.enqueue.timeout.ms = -1
# 异步模式下，每次发送的消息数，当queue.buffering.max.messages或queue.buffering.max.ms满足条件之一时producer会触发发送。
batch.num.messages=200
```

server.properties中所有配置参数说明(解释)如下列表：

|参数 |说明(解释)|
|---|---|
|broker.id =0|每一个broker在集群中的唯一表示，要求是正数。当该服务器的IP地址发生改变时，broker.id没有变化，则不会影响consumers的消息情况|
|log.dirs=/data/kafka-logs|kafka数据的存放地址，多个地址的话用逗号分割/data/kafka-logs-1，/data/kafka-logs-2|
|port =9092|broker server服务端口|
|message.max.bytes =6525000|表示消息体的最大大小，单位是字节|
|num.network.threads =4|broker处理消息的最大线程数，一般情况下不需要去修改|
|num.io.threads =8|broker处理磁盘IO的线程数，数值应该大于你的硬盘数|
|background.threads =4|一些后台任务处理的线程数，例如过期消息文件的删除等，一般情况下不需要去做修改|
|queued.max.requests =500|等待IO线程处理的请求队列最大数，若是等待IO的请求超过这个数值，那么会停止接受外部消息，应该是一种自我保护机制。|
|host.name|broker的主机地址，若是设置了，那么会绑定到这个地址上，若是没有，会绑定到所有的接口上，并将其中之一发送到ZK，一般不设置|
|socket.send.buffer.bytes=100*1024|socket的发送缓冲区，socket的调优参数SO_SNDBUFF|
|socket.receive.buffer.bytes =100*1024|socket的接受缓冲区，socket的调优参数SO_RCVBUFF|
|socket.request.max.bytes =100*1024*1024|socket请求的最大数值，防止serverOOM，message.max.bytes必然要小于socket.request.max.bytes，会被topic创建时的指定参数覆盖|
|log.segment.bytes =1024*1024*1024|topic的分区是以一堆segment文件存储的，这个控制每个segment的大小，会被topic创建时的指定参数覆盖|
|log.roll.hours =24*7|这个参数会在日志segment没有达到log.segment.bytes设置的大小，也会强制新建一个segment会被 topic创建时的指定参数覆盖|
|log.cleanup.policy = delete|日志清理策略选择有：delete和compact主要针对过期数据的处理，或是日志文件达到限制的额度，会被 topic创建时的指定参数覆盖|
|log.retention.minutes=3days|数据存储的最大时间超过这个时间会根据log.cleanup.policy设置的策略处理数据，也就是消费端能够多久去消费数据.log.retention.bytes和log.retention.minutes任意一个达到要求，都会执行删除，会被topic创建时的指定参数覆盖|
|log.retention.bytes=-1|topic每个分区的最大文件大小，一个topic的大小限制 =分区数*log.retention.bytes。-1没有大小限log.retention.bytes和log.retention.minutes任意一个达到要求，都会执行删除，会被topic创建时的指定参数覆盖|
|log.retention.check.interval.ms=5minutes|文件大小检查的周期时间，是否处罚 log.cleanup.policy中设置的策略|
|log.cleaner.enable=false|是否开启日志压缩|
|log.cleaner.threads = 2|日志压缩运行的线程数|
|log.cleaner.io.max.bytes.per.second=None|日志压缩时候处理的最大大小|
|log.cleaner.dedupe.buffer.size=500*1024*1024|日志压缩去重时候的缓存空间，在空间允许的情况下，越大越好|
|log.cleaner.io.buffer.size=512*1024|日志清理时候用到的IO块大小一般不需要修改|
|log.cleaner.io.buffer.load.factor =0.9|日志清理中hash表的扩大因子一般不需要修改|
|log.cleaner.backoff.ms =15000|检查是否处罚日志清理的间隔|
|log.cleaner.min.cleanable.ratio=0.5|日志清理的频率控制，越大意味着更高效的清理，同时会存在一些空间上的浪费，会被topic创建时的指定参数覆盖|
|log.cleaner.delete.retention.ms =1day|对于压缩的日志保留的最长时间，也是客户端消费消息的最长时间，同log.retention.minutes的区别在于一个控制未压缩数据，一个控制压缩后的数据。会被topic创建时的指定参数覆盖|
|log.index.size.max.bytes =10*1024*1024|对于segment日志的索引文件大小限制，会被topic创建时的指定参数覆盖|
|log.index.interval.bytes =4096|当执行一个fetch操作后，需要一定的空间来扫描最近的offset大小，设置越大，代表扫描速度越快，但是也更好内存，一般情况下不需要搭理这个参数|
|log.flush.interval.messages=None|log文件”sync”到磁盘之前累积的消息条数,因为磁盘IO操作是一个慢操作,但又是一个”数据可靠性"的必要手段,所以此参数的设置,需要在"数据可靠性"与"性能"之间做必要的权衡.如果此值过大,将会导致每次"fsync"的时间较长(IO阻塞),如果此值过小,将会导致"fsync"的次数较多,这也意味着整体的client请求有一定的延迟.物理server故障,将会导致没有fsync的消息丢失.|
|log.flush.scheduler.interval.ms =3000|检查是否需要固化到硬盘的时间间隔|
|log.flush.interval.ms = None|仅仅通过interval来控制消息的磁盘写入时机,是不足的.此参数用于控制"fsync"的时间间隔,如果消息量始终没有达到阀值,但是离上一次磁盘同步的时间间隔达到阀值,也将触发.|
|log.delete.delay.ms =60000|文件在索引中清除后保留的时间一般不需要去修改|
|log.flush.offset.checkpoint.interval.ms =60000|控制上次固化硬盘的时间点，以便于数据恢复一般不需要去修改|
|auto.create.topics.enable =true|是否允许自动创建topic，若是false，就需要通过命令创建topic|
|default.replication.factor =1|是否允许自动创建topic，若是false，就需要通过命令创建topic|
|num.partitions =1|每个topic的分区个数，若是在topic创建时候没有指定的话会被topic创建时的指定参数覆盖|
|以下是kafka中Leader,replicas配置参数|---|
|controller.socket.timeout.ms =30000|partition leader与replicas之间通讯时,socket的超时时间|
|controller.message.queue.size=10|partition leader与replicas数据同步时,消息的队列尺寸|
|replica.lag.time.max.ms =10000|replicas响应partition leader的最长等待时间，若是超过这个时间，就将replicas列入ISR(in-sync replicas)，并认为它是死的，不会再加入管理中|
|replica.lag.max.messages =4000|如果follower落后与leader太多,将会认为此follower[或者说partition relicas]已经失效.通常,在follower与leader通讯时,因为网络延迟或者链接断开,总会导致replicas中消息同步滞后.如果消息之后太多,leader将认为此follower网络延迟较大或者消息吞吐能力有限,将会把此replicas迁移到其他follower中.在broker数量较少,或者网络不足的环境中,建议提高此值.|
|replica.socket.timeout.ms=30*1000|follower与leader之间的socket超时时间|
|replica.socket.receive.buffer.bytes=64*1024|leader复制时候的socket缓存大小|
|replica.fetch.max.bytes =1024*1024|replicas每次获取数据的最大大小|
|replica.fetch.wait.max.ms =500|replicas同leader之间通信的最大等待时间，失败了会重试|
|replica.fetch.min.bytes =1|fetch的最小数据尺寸,如果leader中尚未同步的数据不足此值,将会阻塞,直到满足条件|
|num.replica.fetchers=1|leader进行复制的线程数，增大这个数值会增加follower的IO|
|replica.high.watermark.checkpoint.interval.ms =5000|每个replica检查是否将最高水位进行固化的频率|
|controlled.shutdown.enable =false|是否允许控制器关闭broker ,若是设置为true,会关闭所有在这个broker上的leader，并转移到其他broker|
|controlled.shutdown.max.retries =3|控制器关闭的尝试次数|
|controlled.shutdown.retry.backoff.ms =5000|每次关闭尝试的时间间隔|
|leader.imbalance.per.broker.percentage =10|leader的不平衡比例，若是超过这个数值，会对分区进行重新的平衡|
|leader.imbalance.check.interval.seconds =300|检查leader是否不平衡的时间间隔|
|offset.metadata.max.bytes|客户端保留offset信息的最大空间大小|
|kafka中zookeeper参数配置|---|
|zookeeper.connect = localhost:2181|zookeeper集群的地址，可以是多个，多个之间用逗号分割hostname1:port1,hostname2:port2,hostname3:port3|
|zookeeper.session.timeout.ms=6000|ZooKeeper的最大超时时间，就是心跳的间隔，若是没有反映，那么认为已经死了，不易过大|
|zookeeper.connection.timeout.ms =6000|ZooKeeper的连接超时时间|
|zookeeper.sync.time.ms =2000|ZooKeeper集群中leader和follower之间的同步实际那|
 


## 生产端核心参数
###  acks
参数说明：这是一个非常重要的参数，表示指定分区中成功写入消息的副本数量，这是Kafka生产端消息的持久性（durability）保证。只有当leader确认已成功写入消息的副本数后，才会给Producer发送响应，此时消息才可以认为“已提交”。该参数影响着消息的可靠性以及生产端的吞吐量，并且两者往往相向而驰，通常消息可靠性越高则生产端的吞吐量越低，反之亦然。acks有3个取值：

- acks = 0：表示生产端发送消息后立即返回，不等待broker端的响应结果。通常此时生产端吞吐量最高，消息发送的可靠性最低。
- acks = 1: 表示leader副本成功写入就会响应Producer，而无需等待ISR（同步副本）集合中的其他副本写入成功。这种方案提供了适当的持久性，保证了一定的吞吐量。默认值即是1。
- acks = all或-1: 表示不仅要等leader副本成功写入，还要求ISR中的其他副本成功写入，才会响应Producer。这种方案提供了最高的持久性，但也提供了最差的吞吐量。

调优建议：建议根据实际情况设置，如果要严格保证消息不丢失，请设置为all或-1；如果允许存在丢失，建议设置为1；一般不建议设为0，除非无所谓消息丢不丢失。

### max.request.size
     
参数说明：这个参数比较重要，表示生产端能够发送的最大消息大小，默认值为1048576，即1M。
     
调优建议：一般而言，这个配置有点小，为了避免因消息过大导致发送失败，建议适当调大，比如调到10485760即10M。

### retries
参数说明：表示生产端消息发送失败时的重试次数，默认值为0，表示不进行重试。这个参数一般是为了解决因瞬时故障导致的消息发送失败，比如网络抖动、leader换主，其中瞬时的leader重选举是比较常见的。因此这个参数的设置显得非常重要。

另外为了避免频繁重试的影响，两次重试之间都会停顿一段时间，受参数retry.backoff.ms，默认为100ms，通常可以不调整。

调优建议：这里要尽量避免消息丢失，建议设置为一个大于0的值，比如3或者更大值。

### compression.type
参数说明：表示生产端是否对消息进行压缩，默认值为none，即不压缩消息。压缩可以显著减少网络IO传输、磁盘IO以及磁盘空间，从而提升整体吞吐量，但也是以牺牲CPU开销为代价的。当前Kafka支持4种压缩方式，分别是gzip、snappy、lz4 及 zstd（Kafka 2.1.0开始支持）。

调优建议：出于提升吞吐量的考虑，建议在生产端对消息进行压缩。对于Kafka而已，综合考虑吞吐量与压缩比，建议选择lz4压缩。如果追求最高的压缩比则推荐zstd压缩。

### buffer.memory
参数说明：表示生产端消息缓冲池或缓冲区的大小，默认值为33554432，即32M。这个参数基本可以认为是Producer程序所使用的内存大小。

当前版本中，如果生产消息的速度过快导致buffer满了的时候，将阻塞max.block.ms（默认60000即60s）配置的时间，超时将会抛TimeoutException异常。在Kafka 0.9.0及之前版本，建议设置另一个参数block.on.buffer.full为true，该参数表示当buffer填满时Producer处于阻塞状态并停止接收新消息而不是抛异常。

调优建议：通常我们应尽量保证生产端整体吞吐量，建议适当调大该参数，也意味着生产客户端会占用更多的内存。也可以选择不调整。

### batch.size
参数说明：发送到缓冲区中的消息会被分为一个一个的batch，分批次的发送到broker 端，这个参数就表示batch批次大小，默认值为16384，即16KB。因此减小batch大小有利于降低消息延时，增加batch大小有利于提升吞吐量。

调优建议：通常合理调大该参数值，能够显著提升生产端吞吐量，比如可以调整到32KB，调大也意味着消息会有相对较大的延时。


### linger.ms
参数说明：用来控制batch最大的空闲时间，超过该时间的batch也会被发送到broker端。这实际上是一种权衡，即吞吐量与延时之间的权衡。默认值为0，表示消息需要被立即发送，无需关系batch是否被填满。

调优建议：通常为了减少请求次数、提升整体吞吐量，建议设置一个大于0的值，比如设置为100，此时会在负载低的情况下带来100ms的延时。

### request.timeout.ms
参数说明：这个参数表示生产端发送请求后等待broker端响应的最长时间，默认值为30000，即30s，超时生产端可能会选择重试（如果配置了retries）。

调优建议：该参数默认值一般够用了。如果生产端负载很大，可以适当调大以避免超时，比如可以调到60000。


### max.in.fight.requests.per.connection
参数说明：这个参数非常重要，表示生产端与broker之间的每个连接最多缓存的请求数，默认值为5，即每个连接最多可以缓存5个未响应的请求。这个参数通常用来解决分区乱序的问题。

调优建议：为了避免消息乱序问题，建议将该参数设置为1，表示生产端在某个broker响应之前将无法再向该broker发送消息请求，这能够有效避免同一分区下的消息乱序问题。

## 生产端无消息丢失配置
本文开始我们提到，Producer既要保证吞吐量，又要确保无消息丢失。结合上面的参数介绍，这里给出消息无丢失的配置列表，如下：

- acks = all or -1
- retries = 3
- max.in.fight.requests.per.connection = 1
- 使用带回调机制的send方法即send(record, callback)发送消息，并对失败消息进行处理
- unclean.leader.election.enable = false
- replication.factor = 3
- min.insync.replicas = 2
- enable.auto.commit = false

前3个参数本文已介绍。最后一个是消费端参数，表示禁用自动提交，后面我们再介绍。其他几个属于broker端的参数。

其中比较难理解的是min.insync.replicas，这个参数表示ISR集合中的最少副本数，默认值是1，并只有在acks=all或-1时才有效。acks与min.insync.replicas搭配使用，才能为消息提供最高的持久性保证。我们知道leader副本默认就包含在ISR中，如果ISR中只有1个副本，acks=all也就相当于acks=1了，引入min.insync.replicas的目的就是为了保证下限：不能只满足于ISR全部写入，还要保证ISR中的写入个数不少于min.insync.replicas。常见的场景是创建一个三副本（即replication.factor=3）的topic，最少同步副本数设为2（即min.insync.replicas=2），acks设为all，以保证最高的消息持久性。

## 参考资料
https://zhuanlan.zhihu.com/p/136705624
https://mp.weixin.qq.com/s?__biz=MzUxOTU5Mjk2OA==&mid=2247485857&idx=1&sn=c00aa6fc62be86cb26ee4fe9438b86fb&chksm=f9f604c6ce818dd002244a2798e5a60a2369037f1720ae4719b48082b9b9eaf07e6edb275f2c&scene=21#wechat_redirect
