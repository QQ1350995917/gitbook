Kafka高可用集群

1：kafka使用磁盘顺序写入，要比内存随机写入高性能

2：kafaka消息无状态，不判断是否要删除消息。

kafka选举是发生在切片之间的，依托的是Zookeeper分布式锁实现，故分片在ZK中的注册都是临时节点，以保证不会在选举时候产生死锁。

![](/kafka/images/kafka-01.jpg)

![](/kafka/images/kafka-02.jpg)

![](/kafka/images/kafka-03.jpg)
