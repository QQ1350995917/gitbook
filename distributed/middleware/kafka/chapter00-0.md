# 常用命令

## 服务端相关命令

启动

./zookeeper-server-start.sh ../config/zookeeper.properties

罗列所有的topic

kafka-topics.sh --list --zookeeper localhost:2181

创建topic并创建分区数量和分片数量

kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic udp-channel-publisher

删除topic

1\) kafka-topics.sh --zookeeper localhost:2181 --topic udp-channel-publisher --delete

2\) kafka-topics.sh --zookeeper localhost:2181/kafka --topic udp-channel-publisher --delete

3\) kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper localhost:2181 --topic udp-channel-publisher

增加topic的partition数量

kafka-topics.sh --zookeeper localhost:2181 --alter --topic udp-channel-publisher  --partitions 3

## 消费者相关命令

1\) 从头开始

kafka-console-consumer.sh --bootstrap-server localhost:9092 --group udp-channel-publisher --topic udp-channel-publisher  --from-beginning

2\) 从尾部开始

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic udp-channel-publisher  --offset latest

3\) 指定分区

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic udp-channel-publisher  --offset latest --partition 1

4\) 取指定个数

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic udp-channel-publisher  --offset latest --partition 1 --max-messages 1

5\) 新消费者（ver&gt;=0.9）

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic udp-channel-publisher  --new-consumer --from-beginning --consumer.config config/consumer.properties

罗列消费者Group

1\) 分ZooKeeper方式（老）

kafka-consumer-groups.sh --zookeeper 127.0.0.1:2181/kafka --list

2\) API方式（新）

kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --list

消费者Group详情

kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --group test --describe

删除消费者Group

老版本的ZooKeeper方式可以删除Group，新版本则自动删除，当执行：

kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --group test --delete

输出如下提示：

| Option '\[delete\]' is only valid with '\[zookeeper\]'.Note that there's no need to delete group metadata for the new consumeras the group is deleted when the last committed offset for that group expires. |
| :--- |


## 生产者相关命令

kafka-console-producer.sh --broker-list localhost:9092 --topic udp-channel-publisher


参考资料

[https://www.cnblogs.com/aquester/p/9891475.html](https://www.cnblogs.com/aquester/p/9891475.html)

