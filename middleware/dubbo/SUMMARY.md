Dubbo
负载均衡：轮询，随机，一致性哈希
注册中心：广播，数据库，simple，redis，ZK
成为注册中心条件：建立提供者和消费者的联系，并实现多对多；及时发现断开的异常并通知消费者；注册中心重启能自动回复数据；注册中心高可用。
redis心跳保活机制:/dubbo/接口限定/provider作为name，其下存储多个消费者和提供者的地址信息以及失效时间，30秒刷新一次，断开后客户端也会检测状态，所以不会存在调不通状态，也通过订阅通道通知调用者。
zk的存储结构：provicer，router，consumer，configure，使用临时节点存储提供者和消费者。