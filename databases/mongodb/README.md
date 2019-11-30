MongoDB集群

MongoDB主从环境搭建

MongoDB分片+副本集环境搭建

SQL
优点：强结构，强一致性，强实务操作
缺点：对性能和灵活性支持不够，对目前大数据业务支持不足。
NoSql
类型：key-value(redis),文档性（MongoDB）,图形（neo4J)

倒排引擎
给文档的分词建立索引，并指向文档ID

索引（正序索引1，倒叙索引-1）
1：单键索引
2：复合索引（命中规律和mysql不同，explain可以查看执行情况）
3：全文索引

聚合操作
1：pipeline（占用内存限制不超过20%）
$match,$project,$group等
2：MapReduce
map>reduct>result

集群
1：主从复制
oplog，自动切换，slaveOK，配置property越可能成为master，配置arbiterOnly不会复制数据，仅作为裁判，不能把就有的节点变成arbiterOnly。
2：分片
开启分片功能
分片有三部分组成，如右图，路由，配置信息，分片；数据存储在分片上，在配置信息上记录数据存储的节点，在路由上访问时选择分片。
每个分片的大小可以配置默认64M
