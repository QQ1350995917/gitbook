## [Introducing InnoDB Cluster](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-introduction.html)
![](images/innodb_cluster_overview.png)
Router可以基于已部署的集群自动生成配置，实现自动切换服务实例，使得客户端应用可以无需关心集群的部署情况。
集群的单主模式下，集群有一个读写实例primary，多个备机secondary实例拥有主实例的所有数据。如果主实例宕机，会自动推举一台备机实例作为主实例。MySQL
Router可以发现并自动重连到新的主实例上。

MySQL InnoDB cluster至少三个MySQL节点实例。  
InnoDB cluster基于Group Replication技术实现，因此你的服务实例必须满足Group  
Replication技术的环境要求，[详情可查看这里](https://dev.mysql.com/doc/refman/5.7/en/group-replication-requirements.html)。  
AdminAPI提供了dba.checkInstanceConfiguration()函数来校验实例是否满足Group  
Replication要求。dba.configureLocalInstance()可以自动配置服务实例来满足环境要求。  
MySQL Shell配置集群实例，需要系统配置有Python，请确保Python版本不低于2.7。可以在linux中使用python命令检查版本。  


使用[MySQL Shell](https://dev.mysql.com/doc/mysql-shell/8.0/en/)进行管理。



dba.deploySandboxInstance\(3310\)

shell.connect\('root@localhost:3310'\)

dba.createCluster\(\)

var cluster = dba.createCluster\('testCluster'\)

## 两种部署方式

[沙盒级](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-sandbox-deployment.html)

[产品级](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-production-deployment.html)

[安装要求](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-requirements.html)

[安装方式](https://dev.mysql.com/doc/refman/5.7/en/mysql-innodb-cluster-methods-installing.html)

## 部署操作
### 下载并配置环境变量
- https://dev.mysql.com/downloads/mysql/
[MySQL Server 5.7](https://cdn.mysql.com//Downloads/MySQL-5.7/mysql-server_5.7.28-1ubuntu18.04_amd64.deb-bundle.tar)
[MySQL Shell 8.0](https://cdn.mysql.com//Downloads/MySQL-Shell/mysql-shell_8.0.18-1ubuntu18.04_amd64.deb)
[MySQL Router 8.0](https://cdn.mysql.com//Downloads/MySQL-Router/mysql-router-community-dbgsym_8.0.18-1ubuntu18.04_amd64.deb)

```
mkdir -p /usr/local/bin/mysql/server
mkdir -p /usr/local/bin/mysql/shell
mkdir -p /usr/local/bin/mysql/router

cd /usr/local/bin/mysql/server
curl -O https://cdn.mysql.com//Downloads/MySQL-5.7/mysql-server_5.7.28-1ubuntu18.04_amd64.deb-bundle.tar
tar -xvf mysql-server_5.7.28-1ubuntu18.04_amd64.deb-bundle.tar

cd /usr/local/bin/mysql/shell
curl -O https://cdn.mysql.com//Downloads/MySQL-Shell/mysql-shell_8.0.18-1ubuntu18.04_amd64.deb
dpkg -i mysql-shell_8.0.18-1ubuntu18.04_amd64.deb

cd /usr/local/bin/mysql/router
curl -O https://cdn.mysql.com//Downloads/MySQL-Router/mysql-router-community-dbgsym_8.0.18-1ubuntu18.04_amd64.deb
dpkg -i mysql-router-community-dbgsym_8.0.18-1ubuntu18.04_amd64.deb

```


这里在一台机器上搭建3个数据库实例，开启端口24801,24802,24803 。

配置/etc/hosts文件，配置域名ic-1，ic-2，ic-3，分别对应三个数据库实例。

在/etc/hosts文件最后添加一行数据，确保域名之间都可以ping通，192.168.220.133是本机IP：

192.168.220.133 ic-1 ic-2 ic-3

Linux下创建mysql用户，否则下一步会提示出错信息：


在/home/mysql目录下，创建data目录并初始化实例：

mysql-5.7/bin/mysqld --initialize-insecure --basedir=\$PWD/mysql-5.7
--datadir=\$PWD/data/s1

mysql-5.7/bin/mysqld --initialize-insecure --basedir=\$PWD/mysql-5.7
--datadir=\$PWD/data/s2

mysql-5.7/bin/mysqld --initialize-insecure --basedir=\$PWD/mysql-5.7
--datadir=\$PWD/data/s3

配置实例
--------

### 基础配置

初始化实例后，在对应目录下编写配置文件

（提示：请关注下章节“[高并发语句导致备机宕机](#高并发语句导致备机宕机)”列出的配置优化部分，避免线上高并发导致宕机）

在data/s1目录下，创建文件my.cnf，配置如下：

[mysqld]

\# server configuration

datadir=/home/mysql/data/s1

basedir=/home/mysql/mysql-5.7/

report_host=ic-1

port=24801

socket=/home/mysql/data/s1/mysql.sock

在data/s2目录下，创建文件my.cnf，配置如下：

[mysqld]

\# server configuration

datadir=/home/mysql/data/s2

basedir=/home/mysql/mysql-5.7/

report_host=ic-2

port=24802

socket=/home/mysql/data/s2/mysql.sock

在data/s3目录下，创建文件my.cnf，配置如下：

[mysqld]

\# server configuration

datadir=/home/mysql/data/s3

basedir=/home/mysql/mysql-5.7/

report_host=ic-3

port=24803

socket=/home/mysql/data/s3/mysql.sock

在/home/mysql目录下，可以使用如下命令启动数据库实例：

mysqld --defaults-file=data/s1/my.cnf --user=root

为了方便，我们使用启动脚本来启动，在数据库目录data/s1下新增脚本start.sh和shutdown.sh。

直接执行对应脚本即可。

注：start.sh脚本里务必设置主机名，防止重启机器后，主机名改变导致集群无法使用！详情可参考
[这里](#重启集群的一台机器后无法加入集群问题)。

start.sh脚本：

\#!/bin/sh

\#设置机器主机名为localhost

hostname localhost

dir=\$(cd \$(dirname \$0); pwd)

mysqladmin shutdown -S \${dir}/mysql.sock

mysqld --defaults-file=\${dir}/my.cnf --user=root \>\>\${dir}/mysqld.log 2\>&1 &

tail -f \${dir}/mysqld.log

shutdown.sh脚本：

\#!/bin/sh

dir=\$(cd \$(dirname \$0); pwd)

mysqladmin shutdown -S \${dir}/mysql.sock

启动完成后，使用mysqlShell登录数据库：

mysqlsh --log-level=DEBUG3

dba.verbose=2

或者直接使用命令：

mysqlsh

建议使用第一种方式，操作时会显示详细日志数据，方便调试。

示例：

![](/redmine/attachments/download/6993/ab8934b79302f64aa48607b966a6834e.png)

使用命令shell.connect('root\@localhost:24801')登录数据库，这里root用户在本机使用时，没有密码。

命令dba.help() 可以查询集群操作指令的帮助信息。

### 集群环境配置

在使用数据库实例搭建集群前，需要检查实例是否满足集群配置。需要使用命令

dba.checkInstanceConfiguration()检查。

这里执行dba.checkInstanceConfiguration('root\@localhost:24801')命令，返回结果如下：

mysql-js\> dba.checkInstanceConfiguration('root\@localhost:24801')

Please provide the password for 'root\@localhost:24801':

Validating instance...

The instance 'localhost:24801' is not valid for Cluster usage.

The following issues were encountered:

\- Some configuration options need to be fixed.

\+----------------------------------+---------------+----------------+--------------------------------------------------+

\| Variable \| Current Value \| Required Value \| Note \|

\+----------------------------------+---------------+----------------+--------------------------------------------------+

\| binlog_checksum \| CRC32 \| NONE \| Update the server variable or restart the
server \|

\| enforce_gtid_consistency \| OFF \| ON \| Restart the server \|

\| gtid_mode \| OFF \| ON \| Restart the server \|

\| log_bin \| 0 \| 1 \| Restart the server \|

\| log_slave_updates \| 0 \| ON \| Restart the server \|

\| master_info_repository \| FILE \| TABLE \| Restart the server \|

\| relay_log_info_repository \| FILE \| TABLE \| Restart the server \|

\| transaction_write_set_extraction \| OFF \| XXHASH64 \| Restart the server \|

\+----------------------------------+---------------+----------------+--------------------------------------------------+

Please fix these issues, restart the server and try again.

{

"config_errors": [

{

"action": "server_update",

"current": "CRC32",

"option": "binlog_checksum",

"required": "NONE"

},

{

"action": "restart",

"current": "OFF",

"option": "enforce_gtid_consistency",

"required": "ON"

},

{

"action": "restart",

"current": "OFF",

"option": "gtid_mode",

"required": "ON"

},

{

"action": "restart",

"current": "0",

"option": "log_bin",

"required": "1"

},

{

"action": "restart",

"current": "0",

"option": "log_slave_updates",

"required": "ON"

},

{

"action": "restart",

"current": "FILE",

"option": "master_info_repository",

"required": "TABLE"

},

{

"action": "restart",

"current": "FILE",

"option": "relay_log_info_repository",

"required": "TABLE"

},

{

"action": "restart",

"current": "OFF",

"option": "transaction_write_set_extraction",

"required": "XXHASH64"

}

],

"errors": [],

"restart_required": true,

"status": "error"

}

可以看到当前实例不满足条件，AdminAPI提供了命令dba.configureLocalInstance()来帮助修改实例的配置文件，确保实例可以满足集群的条件。虽然可以手动修改配置文件，但是强烈建议使用AdminAPI来操作，避免配置出错。配置完成后，必须重启数据库实例确保配置生效。

使用dba.configureLocalInstance()会校验对应用户是否有合适的访问权限，root用户默认不允许其它服务器登录，这里有3个选项供选择。我们选择第一个，为root用户授权所需权限。

执行命令如下图，注意mysql的配置路径务必指定正确，注意这里设置了root用户的非本机访问所用密码。

![](/redmine/attachments/download/6998/c3650caefae1c5c6dccc730cfd3ca31d.png)

可以看到提示root用户权限问题，再次执行相同的命令即可正确配置，如下图：

![](/redmine/attachments/download/6972/89d976d25a0c1d602c279c2602415534.png)

重启实例后，使用dba.checkInstanceConfiguration()命令检查，提示满足集群条件，如下图：

![](/redmine/attachments/download/6971/70f75b089c171bef7f7622de761dac57.png)

同样的方式，将实例2、实例3也按照同样的方式配置。确保三个数据库实例都满足集群要求。

创建集群
--------

首先使用域名的方式登录数据库实例，使用localhost方式登录会提示权限问题！

使用dba.createCluster(name)命令可以创建集群并返回集群对象cluster，执行命令如下：

![](/redmine/attachments/download/6973/90f2d4a0f0d1c6a364219ad02f06481e.png)

使用status()命令查看集群状态，可以看到已经有一台机器在集群中了。

![](/redmine/attachments/download/6995/ba32c7c54646387d740a2c8e6be2b3be.png)

使用cluster.addInstance(instance)命令添加更多数据库实例到集群中。这里参数使用域名方式，如图：

![](/redmine/attachments/download/7001/e70a9000002a2d8ffd73692b8f58ab06.png)

需要至少3个数据库实例才能确保容许一个实例故障。继续添加第三个实例：

![](/redmine/attachments/download/6974/220d0f8dd2b53a5efc0e2b3f8a16e072.png)

![](/redmine/attachments/download/7002/e025308098b727a69502925a321df44a.png)

到这里集群已经形成，但是这里的集群元数据没有持久化到配置文件中，重启实例后，将不会自动加入集群。需要连接并登陆每个数据库实例并在本机执行dba.configureLocalInstance()命令持久化配置信息。这样可以确保实例意外离开集群，重启实例后能够重新自动加入集群中。

持久化实例1的配置，同样的方式持久化实例2和实例3
。注意持久化只读实例的配置会提示暂时禁用只读开关super_read_only，请选择y执行。

![](/redmine/attachments/download/6969/8e6cce7525f43925eff4321869fa0f8a.png)

![](/redmine/attachments/download/6997/ca36a3e668592f69aeaa7d3c10f1f371.png)

![](/redmine/attachments/download/6968/1dc16c91fa11c156717e572f460ea8cc.png)

可以查看实例的配置文件my.cnf，已经自动加入了集群相关的配置，实例1 的如下：

[mysqld]

\# server configuration

datadir=/home/mysql/data/s1

basedir=/home/mysql/mysql-5.7/

report_host=ic-1

port=24801

socket=/home/mysql/data/s1/mysql.sock

log_slave_updates = ON

server_id = 1214009101

relay_log_info_repository = TABLE

master_info_repository = TABLE

transaction_write_set_extraction = XXHASH64

binlog_format = ROW

disabled_storage_engines = MyISAM,BLACKHOLE,FEDERATED,CSV,ARCHIVE

report_port = 24801

binlog_checksum = NONE

enforce_gtid_consistency = ON

log_bin

gtid_mode = ON

group_replication_allow_local_disjoint_gtids_join = OFF

group_replication_allow_local_lower_version_join = OFF

group_replication_auto_increment_increment = 7

group_replication_bootstrap_group = OFF

group_replication_components_stop_timeout = 31536000

group_replication_compression_threshold = 1000000

group_replication_enforce_update_everywhere_checks = OFF

group_replication_flow_control_applier_threshold = 25000

group_replication_flow_control_certifier_threshold = 25000

group_replication_flow_control_mode = QUOTA

group_replication_force_members

group_replication_group_name = 6d3c634e-01a7-11e8-9c80-000c296f3284

group_replication_group_seeds

group_replication_gtid_assignment_block_size = 1000000

group_replication_ip_whitelist = AUTOMATIC

group_replication_local_address = ic-1:34801

group_replication_member_weight = 50

group_replication_poll_spin_loops = 0

group_replication_recovery_complete_at = TRANSACTIONS_APPLIED

group_replication_recovery_reconnect_interval = 60

group_replication_recovery_retry_count = 10

group_replication_recovery_ssl_ca

group_replication_recovery_ssl_capath

group_replication_recovery_ssl_cert

group_replication_recovery_ssl_cipher

group_replication_recovery_ssl_crl

group_replication_recovery_ssl_crlpath

group_replication_recovery_ssl_key

group_replication_recovery_ssl_verify_server_cert = OFF

group_replication_recovery_use_ssl = OFF

group_replication_single_primary_mode = ON

group_replication_ssl_mode = DISABLED

group_replication_start_on_boot = ON

group_replication_transaction_size_limit = 0

group_replication_unreachable_majority_timeout = 0

auto_increment_increment = 1

auto_increment_offset = 2

集群结构变化后，务必连接到每个数据库实例持久化配置，比如新增了数据库节点等。

好了，集群搭建OK，下面介绍使用mysqlRouter实现高可用。

MySql路由器
-----------

建议将MySQL
Router与应用客户端放在同一台机器，当然也可以部署在一台公共机器上使用。

安装好MySQL Router后，执行命令

mysqlrouter --bootstrap root\@ic-1:24801 --user=mysqlrouter
--directory=/home/mysql/mysql-router-dir

![](/redmine/attachments/download/6970/29d18fac35ac3fb425eb9d2fbaf55410.png)

在/home/mysql/mysql-router-dir目录下，生成了mysql router的启停脚本和配置信息。

![](/redmine/attachments/download/6975/888b0a295d54a6df9aa1bd7dedd3139c.png)

Mysql
router由于使用了—bootstrap参数，从集群中自动获取了集群实例信息，并存放在mysqlrouter.conf文件中，从路由的配置文件中可以看到连接路由对应的端口。

默认读写端口为6446，只读端口为6447。

![](/redmine/attachments/download/6989/080690b9f47aed015de630d3c7787127.png)

Mysql router配置完成后，使用脚本start.sh启动路由，即可通过路由的端口连接了。

使用ps –ef\|grep mysqlrouter检查，不要重复启动路由器。正常情况下如图：

![](/redmine/attachments/download/6987/8601bd827eb323b37eb171c76b9e91ba.png)

使用mysqlsh登录检查当前使用的实例端口：

![](/redmine/attachments/download/6967/1d32b25023874cc6cf57ba005dd237bb.png)

可以看到，当前路由连接的是数据库集群的实例1 。

集群维护使用
------------

### 查看集群状态

连接到集群的任意一台机器，使用var cluster =
dba.getCluster()获取集群对象，使用cluster.status()获取集群当前的状态信息，执行命令如图：

![](/redmine/attachments/download/7000/e8ad88c9b83a65da3ee4ed98ec83bb4f.png)

cluster.status()返回的信息：

clusterName：定义集群时指定的名称

primary：当前的主节点（单主模式下）。

status：集群下实例的当前状态。

ONLINE：在线，正常状态

OFFLINE：离线，该实例与其他实例断开连接

RECOVERING：恢复中，该实例在成为ONLINE状态之前，正在尝试同步集群的数据。

UNREACHABLE：不可达，该实例与集群的连接断开

ERROR：错误状态，该实例在恢复过程中或执行事务时发生错误。注意：一旦实例进入ERROR状态，将会变为只读super_read_only=ON。

MISSING：丢失，表示当前实例是集群的一部分，但是当前不可用。

mode：R/W表示读写，R/O表示只读

### 移除实例

使用mysqlsh连接登录到集群的主节点，这里需要使用域名的方式登录。

假如当前集群的主节点为实例1，移除实例3，执行命令：

shell.connect('root\@ic-1:24801')

var cluster = dba.getCluster()

cluster.status()

cluster.removeInstance('root\@ic-3:24803')

cluster.status()

![](/redmine/attachments/download/6990/233049d86eb1e9ad31868d70e1cf6aed.png)

集群常见问题解决
----------------

### 主节点实例离开后，重启主节点实例，没有自动加入集群

配置主节点的my.cnf配置，确保group_replication_group_seeds有一个值，对应一台集群中其它机器的group_replication_local_address的值，确保实例启动后能够自动连接到集群中。

主节点的my.cnf文件：

![](/redmine/attachments/download/6999/e07c4fc06a3e00996ae6c03218d5293e.png)

### 在客户端无法创建数据库

在主节点（RW节点）所在机器登录实例，并授予权限，集群其他实例会自动同步。

参考：

GRANT ALL PRIVILEGES ON \*.\* TO 'root'\@'%' IDENTIFIED BY '密码' WITH GRANT
OPTION;

FLUSH PRIVILEGES;

### 集群全部关闭后如何重新启动

确认每个实例均为关闭状态，然后将所有实例启动，通过日志可以看出每个实例均为单机模式运行。

执行var cluster = dba.getCluster()无法获取集群实例。

![](/redmine/attachments/download/6994/b58fd8f69af9b485cb877ba9a7ca6e47.png)

此时，需要通过域名方式连接到一个实例，然后执行命令

var cluster = dba.rebootClusterFromCompleteOutage()，返回集群对象。

![](/redmine/attachments/download/6996/c1ea23195c0aef316f4067fcb752d3d7.png)

可以看到一台机器已经在线ONLINE中了，其他机器均为丢失状态。

在其他MISSING的实例上，配置my.cnf中的group_replication_group_seeds值为已启动的主实例地址，主机实例地址对应主实例配置my.cnf的group_replication_local_address配置值。

这里的实例1的配置group_replication_local_address：

![](/redmine/attachments/download/6986/6768ec28db663a1be45a8e7a58de07d5.png)

实例2的配置group_replication_group_seeds：

![](/redmine/attachments/download/6992/a2e84fa5c837524564a9588ba80d8f10.png)

配置好后，重新启动实例2，即可自动加入集群了。会先进入RECOVING状态，然后进入ONLINE状态。

实例3按照同样的方式配置后，重启即可。最后检查集群状态可以看到3台均为ONLINE。

### 高并发语句导致备机宕机

在每个数据库实例的配置文件my.cnf中加入配置：

innodb_buffer_pool_size=2G

innodb_buffer_pool_instances=1

innodb_buffer_pool_size的默认值为128M，有关innodb_buffer_pool_size的官方解释如下：

\# InnoDB, unlike MyISAM, uses a buffer pool to cache both indexes and

\# row data. The bigger you set this the less disk I/O is needed to

\# access data in tables. On a dedicated database server you may set this

\# parameter up to 80% of the machine physical memory size. Do not set it

\# too large, though, because competition of the physical memory may

\# cause paging in the operating system. Note that on 32bit systems you

\# might be limited to 2-3.5G of user level memory per process, so do not

\# set it too high.

简单来说，就是pool-size可以缓存索引和行数据，值越大，IO读写就越少，如果服务器单纯的做数据库服务，该参数可以设置到电脑物理内存的80%
。

### 重启集群的一台机器后，无法加入集群问题

当重启集群的一台机器后，启动集群无法正常加入集群，启动日志报错信息如下：

2018-04-26T07:52:38.793757Z 0 [Warning] Neither --relay-log nor
--relay-log-index were used; so replication may break when this MySQL server
acts as a slave and has his hostname changed!! Please use
'--relay-log=s3-a-relay-bin' to avoid this problem.

2018-04-26T07:52:38.894525Z 0 [ERROR] Failed to open the relay log
'./localhost-relay-bin-group_replication_applier.000171' (relay_log_pos
1713762).

2018-04-26T07:52:38.894538Z 0 [ERROR] Could not find target log file mentioned
in relay log info in the index file
'./s3-a-relay-bin-group_replication_applier.index' during relay log
initialization.

2018-04-26T07:52:38.927906Z 0 [ERROR] Slave: Failed to initialize the master
info structure for channel 'group_replication_applier'; its record may still be
present in 'mysql.slave_master_info' table, consider deleting it.

2018-04-26T07:52:39.062098Z 0 [ERROR] Failed to open the relay log
'./localhost-relay-bin-group_replication_recovery.000001' (relay_log_pos 4).

2018-04-26T07:52:39.062110Z 0 [ERROR] Could not find target log file mentioned
in relay log info in the index file
'./s3-a-relay-bin-group_replication_recovery.index' during relay log
initialization.

2018-04-26T07:52:39.095523Z 0 [ERROR] Slave: Failed to initialize the master
info structure for channel 'group_replication_recovery'; its record may still be
present in 'mysql.slave_master_info' table, consider deleting it.

2018-04-26T07:52:39.095626Z 0 [ERROR] Failed to create or recover replication
info repositories.

这里原因是：linux系统默认主机名localhost，配置/etc/hosts文件后，重启服务器后主机名变为了s3-a
，导致mysql无法找到对应的数据文件。

mysql会按照系统当前的主机名，来生成集群对应的记录文件。如果系统重启，会自动识别hosts文件配置的域名
为当前主机名。Linux系统默认主机名为“localhost”，如果在/etc/hosts文件中配置如下，重启linux服务器后，会发现系统主机名变为了ic-1。

192.168.220.133 ic-1 ic-2 ic-3

为了让集群正常工作，请保证机器在安装和重启机器后，主机名不变。

详情可以参考附录“linux的hostname(主机名)修改详解”。

一种解决方案是：在启动mysql实例脚本中加入“hostname localhost”。

start.sh脚本：

\#!/bin/sh

\#设置机器主机名为localhost

hostname localhost

dir=\$(cd \$(dirname \$0); pwd)

mysqladmin shutdown -S \${dir}/mysql.sock

mysqld --defaults-file=\${dir}/my.cnf --user=root \>\>\${dir}/mysqld.log 2\>&1 &

tail -f \${dir}/mysqld.log

集群问题汇总（不准确，供参考）
==============================

配置文件的seeds需要改为所有机器，保证重启后自动加入。

集群全部关机后，需要启动所有机器并执行如下：

mysql-js\> shell.connect('root\@localhost:3310');

mysql-js\> var cluster = dba.rebootClusterFromCompleteOutage();

然后将不在线的机器重启。

出现一个实例ERROR，其它为MISS时，需要重启每个实例，然后连接一台实例，并执行var
cluster =
dba.rebootClusterFromCompleteOutage();然后看到一台实例online，其它实例再次重启后，自动同步并加入online。

案例：备机A停机一段时间后，主机正在大量入库时，启动备机A后，自动同步失败，并且主机状态变为ERROR时，主机打印的日志：

2018-01-17T09:41:54.802120Z 16 [ERROR] Plugin group_replication reported:
'Unable to convert the event into a packet on the applier! Error: -7

'

2018-01-17T09:41:54.802158Z 16 [ERROR] Plugin group_replication reported:
'Failed to fetch transaction data containing required transaction info for
applier'

2018-01-17T09:41:54.950232Z 16 [ERROR] Plugin group_replication reported: 'Error
at event handling! Got error: 1'

2018-01-17T09:41:55.218151Z 16 [ERROR] Plugin group_replication reported: 'Fatal
error during execution on the Applier process of Group Replication. The server
will now leave the group.'

集群配置完成后，退出重新登录，并持久化配置文件，检查seeds是否正确。

备用机改动过后，无法启动时，可以停掉这个实例，先重启集群。后配置该实例的参数为ON，配置：group_replication_allow_local_disjoint_gtids_join

创建集群和添加实例时，指定seeds：

To customize the instances used as seeds when an instance joins the cluster,
pass the groupSeeds

option to the dba.createCluster() and cluster.addInstance() commands. Seed
instances

are contacted when a new instance joins a cluster and used to provide data to
the new instance.

The addresses are specified as a comma separated list such as
host1:port1,host2:port2. This

configures the group_replication_group_seeds system variable.

附录
====

linux的hostname(主机名)修改详解
-------------------------------

摘自：<http://www.jb51.net/LINUXjishu/77534.html>

Linux操作系统的hostname是一个kernel变量，可以通过hostname命令来查看本机的hostname。也可以直接cat
/proc/sys/kernel/hostname查看。   
\#hostname   
\#cat /proc/sys/kernel/hostname   
上面两种输出结果相同。   
修改运行时Linux系统的hostname，即不需要重启系统   
hostname命令可以设置系统的hostname   
\#hostname newname   
newname即要设置的新的hostname，运行后立即生效，但是在系统重启后会丢失所做的修改，如果要永久更改系统的hostname，就要修改相关的设置文件。   
  
**永久更改Linux的hostname **  
man hostname里有这么一句话，”The host name is usually set once at system startup
in /etc/rc.d/rc.inet1 or /etc/init.d/boot (normally by reading the contents of a
file which contains the host name, e.g. /etc/hostname).”
RedHat里没有这个文件，而是由/etc/rc.d/rc.sysinit这个脚本负责设置系统的hostname，它读取/etc
/sysconfig/network这个文本文件，RedHat的hostname就是在这个文件里设置。   
所以，如果要永久修改RedHat的hostname，就修改/etc/sysconfig/network文件，将里面的HOSTNAME这一行修改成HOSTNAME=NEWNAME，其中NEWNAME就是你要设置的hostname。   
Debian发行版的hostname的配置文件是/etc/hostname。   
修该配置文件后，重启系统就会读取配置文件设置新的hostname。   
  
**hostname与/etc/hosts的关系**   
很过人一提到更改hostname首先就想到修改/etc/hosts文件，认为hostname的配置文件就是/etc/hosts。其实不是的。   
hosts文件的作用相当如DNS，提供IP地址到hostname的对应。早期的互联网计算机少，单机hosts文件里足够存放所有联网计算机。不过随着互联网的发展，这就远远不够了。于是就出现了分布式的DNS系统。由DNS服务器来提供类似的IP地址到域名的对应。具体可以man
hosts。   
Linux系统在向DNS服务器发出域名解析请求之前会查询/etc/hosts文件，如果里面有相应的记录，就会使用hosts里面的记录。/etc/hosts文件通常里面包含这一条记录   
  
127.0.0.1 localhost.localdomain localhost   
hosts文件格式是一行一条记录，分别是IP地址 hostname
aliases，三者用空白字符分隔，aliases可选。   
127.0.0.1到localhost这一条建议不要修改，因为很多应用程序会用到这个，比如sendmail，修改之后这些程序可能就无法正常运行。   
  
修改hostname后，如果想要在本机上用newhostname来访问，就必须在/etc/hosts文件里添加一条newhostname的记录。比如我的eth0的IP是192.168.1.61，我将hosts文件修改如下：   
\#hostname blog.infernor.net   
\# cat /etc/hosts   
127.0.0.1 localhost.localdomain localhost   
192.168.1.61 blog.infernor.net blog   
这样，我就可以通过blog或者blog.infernor.net来访问本机。   
从上面这些来看，/etc/hosts于设置hostname是没直接关系的，仅仅当你要在本机上用新的hostname来访问自己的时候才会用到/etc/hosts文件。两者没有必然的联系。   
RHEL还有个问题。   
  
我开始在测试的时候，只修改/etc/hosts，里面添加 192.168.1.61 blog.infernor.net
blog，而/etc/sysconfig/network维持原状，也就是里面的HOSTNAME=localhost.localdomain。我重启系统后居然发现hostname给修改成了blog.infernor.net。这样看的话，倒真觉得/etc/hosts是hostname的配置文件。后来终于在/etc/rc.d/rc.sysinit这个启动脚本里发现了问题的所在。   
rc.sysinit文件里一开始就设置了hostname   
if [ -f /etc/sysconfig/network ]; then   
. /etc/sysconfig/network   
fi   
if [ -z "\$HOSTNAME" -o "\$HOSTNAME" = "(none)" ]; then   
HOSTNAME=localhost   
fi   
确实使用了/etc/sysconfig/network里的hostname值。不过后面还有一段关于设置hostname的   
ipaddr=   
if [ "\$HOSTNAME" = "localhost" -o "\$HOSTNAME" = "localhost.localdomain" ]   
; then   
ipaddr=\$(ip addr show to 0/0 scope global \| awk '/[[:space:]]inet   
/ { print gensub("/.\*","","g",\$2) }')   
if [ -n "\$ipaddr" ]; then   
eval \$(ipcalc -h \$ipaddr 2\>/dev/null)   
hostname \${HOSTNAME}   
fi   
fi   
脚本判断hostname是否为localhost或者localhost.localdomain，如果是的话，将会使用接口IP地址对应的
hostname来重新设置系统的hostname。问题就出在这里，我的/etc/sysconfig/network默认的hostname是
localhost.localdomain，eth0的IP是192.168.1.61，而/etc/hosts里有192.168.1.61的记录。于是就用192.168.1.61这条记录来替换了hostname。   
估计这也是很多人将/etc/hosts误以为是hostname的配置文件的原因。   
  
**hostname带选项查询**   
hostname的-s -f
-i等等选项都用到了/etc/hosts或者DNS系统，跟我们讨论的hostname有点远了，也容易产生误会。具体可以man
hostname查看。


## 参考
https://blog.csdn.net/chenhaifeng2016/article/details/78552803
