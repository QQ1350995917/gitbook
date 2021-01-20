# 服务器详解

## broker配置

|配置项|作用|
|---|---|
|broker.id|	broker的唯一标识|
|auto.create.topics.auto	|设置成true，就是遇到没有的topic自动创建topic。|
|log.dirs	|log的目录数，目录里面放partition，当生成新的partition时，会挑目录里partition数最少的目录放。|


## Partition配置
|配置项|	作用|
|---|---|
|num.partitions	|新建一个topic，会有几个partition。|
|log.retention.ms	|对应的还有minutes，hours的单位。日志保留时间，因为删除是文件维度而不是消息维度，看的是日志文件的mtime。|
|log.retention.bytes	|partion最大的容量，超过就清理老的。注意这个是partion维度，就是说如果你的topic有8个partition，配置1G，那么平均分配下，topic理论最大值8G。|
|log.segment.bytes	|一个segment的大小。超过了就滚动。|
|log.segment.ms	|一个segment的打开时间，超过了就滚动。|
|message.max.bytes	|message最大多大|

关于日志清理，默认当前正在写的日志，是怎么也不会清理掉的。
还有0.10之前的版本，时间看的是日志文件的mtime，但这个指是不准确的，有可能文件被touch一下，mtime就变了。因此在0.10版本开始，改为使用该文件最新一条消息的时间来判断。
按大小清理这里也要注意，Kafka在定时任务中尝试比较当前日志量总大小是否超过阈值至少一个日志段的大小。如果超过但是没超过一个日志段，那么就不会删除。
