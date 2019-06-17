数据存储结构：hashtable的存储方式（hash冲突，对scan的影响）

常用命令：get，set，info，keys，scan，setnx，

核心原理：单线程，多路复用IO，常规的操作能达到纳秒级别

五种基本存储类型（1基本数据如字符串，整形浮点型；2双向List；3Set；4ZSet；5Hash对象；）

备份触发：配置多少秒多少次的写触发备份

备份方式：red快照+AOF

rdb方式，主进程判断是否有子进程，如果有则返回，否则主进程fork子进程，过程阻塞，子进程把内存写到临时文件后，替换就文件，过程中存在两份文件，占用双倍磁盘空间，aof（存储操作指令）重启恢复慢，占用空间大。

主从模式：异步同步，不影响M的读写

选举机制：

1：slave发现自己的master变为FAIL

2.将自己记录的集群currentEpoch加1，并广播FAILOVER\_AUTH\_REQUEST信息

3.其他节点收到该信息，只有master响应，判断请求者的合法性，并发送FAILOVER\_AUTH\_ACK，对每一个epoch只发送一次ack

4.尝试failover的slave收集FAILOVER\_AUTH\_ACK

5.超过半数后变成新Master

6.广播Pong通知其他集群节点。

从节点并不是在主节点一进入 FAIL 状态就马上尝试发起选举，而是有一定延迟，一定的延迟确保我们等待FAIL状态在集群中传播，slave如果立即尝试选举，其它masters或许尚未意识到FAIL状态，可能会拒绝投票

  


DELAY = 500ms + random\(0 ~ 500ms\) + SLAVE\_RANK \* 1000ms

  


SLAVE\_RANK表示此slave已经从master复制数据的总量的rank。Rank越小代表已复制的数据越新。这种方式下，持有最新数据的slave将会首先发起选举（理论上）。

  


  


 集群方式：增删节点重新规制Slot分布，异步同步，同步中间状态记录solt流向并对客户端进行重定向操作，

  


  


 分布式锁：基于Redis的分布式锁，使用setnx方式实现

  


  


  


最大内存限制下的淘汰策略：

  


noeviction\(默认策略，可删除，可读，不可写\)，

  


volatile-lru（对设置了过期的key进行lru），

  


allkeys-lru（所有可以的lru\)，

  


volatile-random，

  


allkeys-random，

  


volatile-ttl（对设置了过期的key进行最小寿命淘汰）

