# 消费者详解
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
 


