# ES集群的选举机制
```text
# https://www.elastic.co/guide/en/elasticsearch/reference/index.html

cluster.name: cnvd-es
node.name: node-24
#node.attr.rack: r1
path.data: /home/es/data
path.logs: /home/es/logs
bootstrap.memory_lock: true
network.host: 192.168.105.24
http.port: 1200
discovery.seed_hosts: ["192.168.105.25"]
cluster.initial_master_nodes: ["node-1", "node-2"]
#gateway.recover_after_nodes: 3
#action.destructive_requires_name: true
http.cors.enabled: true
http.cors.allow-origin: "*"
```
在Elasticsearch当中，ES分为四种角色：master、data（数据节点）、Coordinating（协调节点）、Ingest（预处理节点）。

master、data、Coordinating三种角色由elasticsearch.yml配置文件中的node.master、node.data来控制；Ingest角色有node.ingest来控制

如果不修改elasticsearch的节点角色信息，那么默认就是node.master: true、node.data: true、node.ingest: true。

默认情况下，一个节点可以充当一个或多个角色，默认四个角色都有。都有成为主节点的资格，也都存储数据，还可以提供查询服务，负载均衡以及数据合并等服务。在高并发的场景下集群容易出现负载过高问题。、
