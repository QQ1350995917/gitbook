## 监控
1. mongostat 可用于查看当前QPS/内存使用/连接数，以及多个shard的压力分布

mongostat --port 27000  -u admin -p xxx --authenticationDatabase=admin --discover -n 30 3

参数说明
-discover 提供集群中所有节点的状态
-n 30 3 表示输出30次，每次休眠3秒钟

#扩展(增加mongostat输出列)
mongostat -O 'host,version,network.numRequests=network requests'

输出参数说明：

inserts/s 每秒插入次数
query/s   每秒查询次数
update/s  每秒更新次数
delete/s  每秒删除次数
getmore/s 每秒执行getmore次数
command/s 每秒的命令数，比以上插入、查找、更新、删除的综合还多，还统计了别的命令
flushs/s  每秒执行fsync将数据写入硬盘的次数。
mapped/s  所有的被mmap的数据量，单位是MB，
vsize     虚拟内存使用量，单位MB
res       物理内存使用量，单位MB
faults/s  每秒访问失败数(只有Linux有)，数据被交换出物理内存，放到swap。不要超过100，否则就是机器内存太小，造成频繁swap写入。此时要升级内存或者扩展
locked %  被锁的时间百分比，尽量控制在50%以下吧
idx miss %  索引不命中所占百分比。如果太高的话就要考虑索引是不是少了
q t|r|w   当Mongodb接收到太多的命令而数据库被锁住无法执行完成，它会将命令加入队列。这一栏显示了总共、读、写3个队列的长度，都为0的话表示mongo毫无压力。高并发时，一般队列值会升高。
conn      当前连接数
time      时间戳

2. mongotop

mongotop --port 27000  -u admin -p xxx --authenticationDatabase=admin


3、profiler
类似于MySQL的slow log, MongoDB可以监控所有慢的以及不慢的查询。
Profiler默认是关闭的，你可以选择全部开启，或者有慢查询的时候开启

> use test
switched to db test
> db.setProfilingLevel(2);
{ "was" : 0, "slowms" : 100, "sampleRate" : 1, "ok" : 1 }
> db.getProfilingLevel()
2

slowms:代表输出 操作大于100毫秒的语句

设置0代表当前的数据库禁止profiler功能，但是这个Mongo实例下的数据库，只要等级是1的，都会使用20ms的配置

db.setProfilingLevel(0,20)

查看Profile日志

> db.system.profile.find().sort({$natural:-1})
{"ts" : "Thu May 17 2019 12:19:32 GMT-0500 (EST)" , "info" :
"query test.$cmd ntoreturn:1 reslen:66 nscanned:0 query: { profile: 2 } nreturned:1 bytes:50" ,
"millis" : 0} ...

3个字段的意义

ts：时间戳
info：具体的操作
millis：操作所花时间，毫秒

4、其他

> db.serverStatus()    #详细的Mongo服务器信息
> db.currentOp()       #显示当前的用户操作
> db.stats()           #当前数据库的信息
> db.collection.stats()   #mongo数据库的集合详细信息
> db.printShardingStatus()   #分片状态，或者用  sh.status()
https://docs.mongodb.com/manual/tutorial/manage-the-database-profiler/
