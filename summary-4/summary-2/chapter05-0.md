# 应用场景

## 分部署配置中心

在我们的应用中除了代码外，还有一些就是各种配置。比如数据库连接等。一般我们都 是使用配置文件的方式，在代码中引入这些配置文件。当我们只有一种配置，只有一台服务 器，并且不经常修改的时候，使用配置文件是一个很好的做法，但是如果我们配置非常多， 有很多服务器都需要这个配置，这时使用配置文件就不是个好主意了。这个时候往往需要寻 找一种集中管理配置的方法，我们在这个集中的地方修改了配置，所有对这个配置感兴趣的 都可以获得变更。Zookeeper 就是这种服务，它使用 Zab 这种一致性协议来提供一致性。现 在有很多开源项目使用 Zookeeper 来维护配置，比如在 HBase 中，客户端就是连接一个 Zookeeper，获得必要的 HBase 集群的配置信息，然后才可以进一步操作。还有在开源的消 息队列 Kafka 中，也使用 Zookeeper来维护broker的信息。在 Alibaba开源的 SOA 框架Dubbo 中也广泛的使用 Zookeeper 管理一些配置来实现服务治理。

### disconf

## 注册中心

dubbo的注册中心使用了zk kafka的注册中心使用了zk

## 分布式锁

参考 下一节 使用zookeeper来做集群的选举

## 使用zookeeper来做集群的选举

### 非公平模式

所谓的非公平模式的选举是相对的，假设有10台机器进行选举，最后会选到哪一个机器，是完全随机的（看谁抢的快）。比如选到了A机器。某一时刻，A机器挂掉了，这时候会再次进行选举，这一次的选举依然是随机的。与某个节点是不是先来的，是不是等了很久无关。这种选举算法，就是非公平的算法。

### 算法步骤

* 1）首先通过zk创建一个 /server 的PERSISTENT节点
* 2）多台机器同时创建 /server/leader EPHEMERAL子节点
* 3）子节点只能创建一个，后创建的会失败。创建成功的节点被选为leader节点
* 4）重新进行选举，所有机器监听 /server/leader 的变化，一旦节点被删除，抢占式地创建 /server/leader节点，谁创建成功谁就是leader。

### java实现非公平模式选举

```
public static void main(String[] args) throws Exception {
    zk = new ZooKeeper("127.0.0.1:2181", FairSelectDemo.SESSION_TIMEOUT, new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println(event.getType() + "---" + event.getPath() + "---" + event.getState());
        }
    });
    //zk启动后试着进行选举
    selection();

    TimeUnit.HOURS.sleep(1); //阻塞住
    zk.close();
}

private static void selection() throws Exception {
    try {
        //1、创建/server（这个通过zkCli创建好了），参数3表示公有节点，谁都可以改
        zk.create("/server/leader", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //2、没有抛异常，表示创建节点成功了
        System.out.println("选举成功");
    } catch (KeeperException.NodeExistsException e) {
        System.out.println("选举失败");
    } finally {
        //3、监听节点删除事件，如果删除了，重新进行选举
        zk.getData("/server/leader", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType() + "---" + event.getPath() + "---" + event.getState());
                try {
                    if (Objects.equals(event.getType(), Event.EventType.NodeDeleted)) {
                        selection();
                    }
                } catch (Exception e) {
                }
            }
        }, null);
    }
}
```

### 公平选举算法

公平选举的区别是，增加了先来的优先被选为leader的保证。

### 算法步骤

* 1）首先通过zk创建一个 /server 的PERSISTENT节点
* 2）多台机器同时创建 /server/leader EPHEMERAL_SEQUENTIAL子节点
* 3）/server/leader000000xxx 后面数字最小的那个节点被选为leader节点
* 4）所有机器监听 前一个 /server/leader 的变化，比如 (leader00002监听 leader00001) 一旦节点被删除，就获取/server下所有leader，如果自己的数字最小那么自己就被选为leader

### java实现公平模式选举

```
public static void main(String[] args) throws Exception {
    zk = new ZooKeeper("127.0.0.1:2181", UnFairSelectDemo.SESSION_TIMEOUT, new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println(event.getType() + "---" + event.getPath() + "---" + event.getState());
        }
    });

    String leaderPath = "/server/leader";

    //1、创建/server（这个通过zkCli创建好了），注意这里是EPHEMERAL_SEQUENTIAL的
    //2、和非公平模式不一样，只需要创建一次节点就可以了
    nodeVal = zk.create(leaderPath, "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

    //System.out.println(nodeVal);

    //启动后试着进行选举
    selection();

    TimeUnit.HOURS.sleep(1); //阻塞住
    zk.close();
}

private static void selection() throws Exception {
    //2、遍历/server下的子节点，看看自己的序号是不是最小的
    List<String> children = zk.getChildren("/server", null);
    Collections.sort(children);

    String formerNode = "";  //前一个节点，用于监听
    for (int i = 0; i < children.size(); i++) {
        String node = children.get(i);
        if (nodeVal.equals("/server/" + node)) {
            if (i == 0) {
                //第一个
                System.out.println("我被选为leader节点了");
            } else {
                formerNode = children.get(i - 1);
            }
        }
    }
    if (!"".equals(formerNode)) {
            //自己不是第一个，如果是第一个formerNode应该没有值
        System.out.println("我竞选失败了");
        //3、监听前一个节点的删除事件，如果删除了，重新进行选举
        zk.getData("/server/" + formerNode, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType() + "---" + event.getPath() + "---" + event.getState());
                try {
                    if (Objects.equals(event.getType(), Event.EventType.NodeDeleted)) {
                        selection();
                    }
                } catch (Exception e) {
                }
            }
        }, null);
    }
    //System.out.println("children:" + children);
}
```

但是其实上述的写法不是很严谨，比如公平选举算法，如果中间一个节点挂掉了，假设有01，02，03，04节点 比如02挂掉了，03一直监听着02，那么这个时候03应该改为监听01，否则，当01挂了，没有任何节点能被选为leader。 除此之外，各种异常状态都需要我们自己处理。

## 参考资料

[https://www.jianshu.com/p/87556c35d932](https://www.jianshu.com/p/87556c35d932)
