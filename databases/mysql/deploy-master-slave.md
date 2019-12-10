# 主从（多主）

# 主从（一主多从）

MySQL 主从复制为异步方式，也可以设置为半同步方式。

## 以测试环境（mac）为例做配置说明
如果机器上已经安装了MySQL正在运行中，可以执行

`ps -ef | grep mysql`

找到MySQL的目录信息，测试机器配置如下

   `74 44342     1   0 Thu08AM ??         0:17.13 /usr/local/mysql/bin/mysqld --user=_mysql --basedir=/usr/local/mysql --datadir=/usr/local/mysql/data --plugin-dir=/usr/local/mysql/lib/plugin --log-error=/usr/local/mysql/data/mysqld.local.err --pid-file=/usr/local/mysql/data/mysqld.local.pid --keyring-file-data=/usr/local/mysql/keyring/keyring --early-plugin-load=keyring_file=keyring_file.so`

注意--basedir,--datadir,--log-error,--pid-file信息

找到MySQL的配置信息(测试环境路径为`/private/etc/my.cnf`)

[client]
default-character-set=utf8

[mysqld]
server-id=3306  # 配置server-id，必须属性

replicate-do-db=cs_article  # 配置要同步的数据库，可选属性

log-bin=mysql-bin  # 开启主库binlog，必须属性

log-bin-index=mysql-bin.index  # 开启主库binlog索引，推荐属性

default-storage-engine=INNODB

character-set-server=utf8

collation-server=utf8_general_ci


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
