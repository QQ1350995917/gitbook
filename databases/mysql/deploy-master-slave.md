# 主从（多主）

# 主从（一主多从）
```
mkdir -p /home/mysql/master-slave/{master-2200,slave-22001,slave-22002}
mkdir -p /home/mysql/master-slave/master-2200/{data,mysql,logger}
mkdir -p /home/mysql/master-slave/slave-22001/{data,mysql,logger}
mkdir -p /home/mysql/master-slave/slave-22002/{data,mysql,logger}
```

- /home/mysql/master-slave/master-2200/mysql/my.conf
```
vim /home/mysql/master-slave/master-2200/mysql/my.conf

[mysqld]    
port=3306
lower_case_table_names=1  
character-set-server=utf8 
collation-server=utf8_general_ci 
init_connect='SET NAMES utf' 
default_authentication_plugin=mysql_native_password 
max_connections=320 


# 要同步的库（如果不写，默认全部同步）
binlog-do-db=master2200
# 不同步的库(多个写多行)
binlog-ignore-db=mysql
binlog-ignore-db=information_schema
binlog-ignore-db=performance_schema
binlog-ignore-db=sys
# 自动清理N天前的log文件
expire_logs_days=7
# 启用二进制日志
log-bin=master-bin
# 服务器唯一ID,必须属性
server-id=2200 
# 开启主库binlog索引，推荐属性
log-bin-index=mysql-bin.index  
default-storage-engine=INNODB
character-set-server=utf8
collation-server=utf8_general_ci
sync_binlog=1

[client] 
default-character-set=utf8 
[mysql] 
default-character-set=utf8 
```
在主服务器上最重要的二进制日志设置是sync_binlog，这使得mysql在每次提交事务的时候把二进制日志的内容同步到磁盘上，即使服务器崩溃也会把事件写入日志中。
sync_binlog这个参数是对于MySQL系统来说是至关重要的，他不仅影响到Binlog对MySQL所带来的性能损耗，而且还影响到MySQL中数据的完整性。对于"sync_binlog"参数的各种设置的说明如下：
sync_binlog=0，当事务提交之后，MySQL不做fsync之类的磁盘同步指令刷新binlog_cache中的信息到磁盘，而让Filesystem自行决定什么时候来做同步，或者cache满了之后才同步到磁盘。
sync_binlog=n，当每进行n次事务提交之后，MySQL将进行一次fsync之类的磁盘同步指令来将binlog_cache中的数据强制写入磁盘。
在MySQL中系统默认的设置是sync_binlog=0，也就是不做任何强制性的磁盘刷新指令，这时候的性能是最好的，但是风险也是最大的。因为一旦系统Crash，在binlog_cache中的所有binlog信息都会被丢失。而当设置为“1”的时候，是最安全但是性能损耗最大的设置。因为当设置为1的时候，即使系统Crash，也最多丢失binlog_cache中未完成的一个事务，对实际数据没有任何实质性影响。
对于高并发事务的系统来说，“sync_binlog”设置为0和设置为1的系统写入性能差距可能高达5倍甚至更多

- /home/mysql/master-slave/slave-22001/mysql/my.conf
```
vim /home/mysql/master-slave/slave-22001/mysql/my.conf

[mysqld]    
port=3306
lower_case_table_names=1  
character-set-server=utf8 
collation-server=utf8_general_ci 
init_connect='SET NAMES utf' 
default_authentication_plugin=mysql_native_password 
max_connections=320 


server-id = 22001
# 加上以下参数可以避免更新不及时，SLAVE 重启后导致的主从复制出错
read_only = 1
master_info_repository=TABLE
relay_log_info_repository=TABLE
relay-log = slave-relay-bin
# 主服务器I/O日志读取、记录及存放
relay-log-index=slave-relay-bin.index
replicate-do-db=master2200
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=information_schema
replicate-ignore-db=performance_schema

[client] 
default-character-set=utf8 
[mysql] 
default-character-set=utf8 
```


- /home/mysql/master-slave/slave-22002/mysql/my.conf
```
vim /home/mysql/master-slave/slave-22002/mysql/my.conf

[mysqld]    
port=3306 
lower_case_table_names=1  
character-set-server=utf8 
collation-server=utf8_general_ci 
init_connect='SET NAMES utf' 
default_authentication_plugin=mysql_native_password 
max_connections=320 

server-id = 22002
# 加上以下参数可以避免更新不及时，SLAVE 重启后导致的主从复制出错
read_only = 1
master_info_repository=TABLE
relay_log_info_repository=TABLE
relay-log = slave-relay-bin
# 主服务器I/O日志读取、记录及存放
relay-log-index=slave-relay-bin.index
replicate-do-db=master2200
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=information_schema
replicate-ignore-db=performance_schema


[client] 
default-character-set=utf8 
[mysql] 
default-character-set=utf8 
```

启动
```
docker run -p 2200:3306  --name master-slave-2200  -v /home/mysql/master-slave/master-2200/mysql/my.conf:/etc/my.cnf -v /home/mysql/master-slave/master-2200/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
docker run -p 22001:3306 --name master-slave-22001 -v /home/mysql/master-slave/slave-22001/mysql/my.conf:/etc/my.cnf -v /home/mysql/master-slave/slave-22001/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
docker run -p 22002:3306 --name master-slave-22002 -v /home/mysql/master-slave/slave-22002/mysql/my.conf:/etc/my.cnf -v /home/mysql/master-slave/slave-22002/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
```

登录master查看状态
```
show master status;
show slave status\G
```

登录slave执行接入master
```
change master to master_host='192.168.50.27',master_port=2200,master_user='root',master_password='root',master_log_file='master-bin.000001',master_log_pos=778;
start slave;
```

可能出现的问题Slave_IO_Running为No

通过docker命令查看日志信息
```
docker logs master-slave-2200
docker logs master-slave-22001
docker logs master-slave-22002
```

登录主从节点查看UUID是否冲突
```
cat /var/lib/mysql/auto.cnf
```
如果重复则删除从节点，重启即可

如果不重复

打开主节点，执行flush logs;这时主服务器会重新创建一个binlog文件，在主服务上执行show master slave \G;

来到从节点，执行stop slave;并执行change命令，其中master_log_file为上一步在master上刷新后新创建的binlog，并重新设定master_log_pos。然后执行start slave;show slave status \G


https://www.cnblogs.com/wade-lt/p/9008058.html


MySQL 主从复制为异步方式，也可以设置为半同步方式。

## 以测试环境（mac）为例做配置说明
如果机器上已经安装了MySQL正在运行中，可以执行

`ps -ef | grep mysql`




在合适的路径下创建从库配置文件(测试环境为/usr/local/var/mysql-slave/my_multi.cnf)

[mysqld_multi]

mysqld     = /usr/local/mysql/bin/mysqld_safe

mysqladmin = /usr/local/mysql/bin/mysqladmin

user = root

pass = root

[mysqld33061] # 33061即为mysqld_multi模式下的节点名称，可以有不同名称的多个配置

user = root

server-id  = 33061

port       = 33061

basedir = /usr/local/mysql

socket     = /tmp/mysql-33061.sock # 注意路径，需要预先创建目录，无需创建文件

pid-file   = /usr/local/var/mysql-slave/33061/mysql.pid  # 注意路径，需要预先创建目录，无需创建文件

datadir    = /usr/local/var/mysql-slave/33061/data  # 注意路径，需要预先创建目录，无需创建文件

log-bin    = /usr/local/var/log/mysql-slave/33061/mysql-bin # 注意路径，需要预先创建目录，无需创建文件

binlog_format = mixed

slow_query_log = on

slow_query_log_file = /usr/local/var/log/mysql-slave/33061/slow.log # 注意路径，需要预先创建目录，无需创建文件

long_query_time = 1

log-queries-not-using-indexes

log_output = FILE,TABLE

general_log = on

general_log_file = /usr/local/var/log/mysql-slave/33061/general.log # 注意路径，需要预先创建目录，无需创建文件


[mysqld]

dmax_connections = 2000

wait_timeout = 10000

validate_password = off

character_set_server=utf8

init_connect='SET NAMES utf8'

#skip-grant-tables

#Only allow connections from localhost

bind-address = 127.0.0.1

以上为为使用mysqld_multi命令的创建方式，也可以把主库的配置信息写在这里。

然后执行

`mysql_install_db --basedir=/usr/local/mysql  --datadir=/usr/local/var/mysql-slave/33061`

启动数据库节点

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf  start`

对于单个从库节点的启动可以使用

`sudo mysqld --initialize --datadir=/usr/local/var/mysql-slave/33061`

注意路径

此时留意当前命令行日志输出，每个实例会生成一个随机的初始密码，后面第一次登录时需要用到。

可以单独启动某个实例或部分实例，只要在命令后面指定具体的实例id即可，实例id为上面配置文件里标签[mysqld33061] 后面的数字:

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf  start 33061`

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf  start 33061,33062-33069`

停止实例

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf stop`

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf stop 33061,33062-33069`

report 命令查看实例运行状态：

`mysqld_multi --defaults-file=/usr/local/var/mysql-slave/my_multi.cnf report`

`Reporting MySQL servers`

`MySQL server from group: mysqld33061 is running`

如果节点没有启动则观察启动日志，一般错误日志会在启动后输出的信息中如/usr/local/var/mysql-slave/33062/data/PWD.local.err。

登录从数据库节点

`mysql -uroot -S/tmp/mysql-33061.sock -p`

重置密码

`alter user 'root'@'localhost' identified by 'root';`

登录主库创建同步账号

`grant replication slave on *.* to ‘slave’@‘127.0.0.1’ identified by ‘root’;`

或者

`grant replication slave on *.* to ‘slave’@‘192.168.253.%’ identified by ‘root’;`

如果要同步有数据的主库，可以先锁定数据库

`flush tables with read lock;`

导出数据库结构以及文件

`mysqldump -uroot -p'root' -S /tmp/mysql.sock --all-databases > /mysql/backup/mysql_bak.$(date +%F).sql`

或者压缩导出

`mysqldump -uroot -p'root' -S /tmp/mysql.sock --all-databases | gzip > /mysql/backup/mysql_bak.$(date +%F).sql.gz`

查看主库binlog的位置信息

`show master status;`

留意Position字段值

恢复主库的操作

`unlock tables;`

登录从库，导入数据文件到从库

`mysql -uroot -p'root' -S /tmp/mysql-33061.sock < /mysql/backup/mysql_bak.2019-03-01.sql`

在从库上做出主库指向

`change master to master_host='127.0.0.1', master_port=3306, master_user='slave', master_password='root', master_log_file='mysql-bin.000001', master_log_pos=1200;`

其中master_log_file以及master_log_pos为前面`show master status;`中的结果

启动主从同步进程

`start slave;`

检查从库状态

`show slave status \G`

主要查看两个状态

Slave_IO_Running: Yes

Slave_SQL_Running: Yes

此时可以更改主库，查看从库同步状态。

Slave_IO_Running: NO：一般情况下是各个主机连接不同的情况，可以按照ping，或者检查数据库用户名称以及密码检查。

Slave_SQL_Running: NO：master_log_pos需要和当前主库的Position保持一致。其他[参考](https://blog.51cto.com/kerry/277414)


参考资料
- [Mysql集群搭建（多实例、主从）](https://blog.csdn.net/qq_21153619/article/details/81529880)
- [MySQL主从同步](https://www.cnblogs.com/kylinlin/p/5258719.html)
- [Mac10.13.6 Mysql5.7.23多实例部署](https://www.jianshu.com/p/30d504d0ee50)


