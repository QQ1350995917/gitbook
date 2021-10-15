# 主从部署

## 主从（多主）

## 主从（一主多从）

### 准备工作

*   规划

    | 主机地址           | 主机身份 | 描述 |
    | -------------- | ---- | -- |
    | 192.168.105.50 | 主    |    |
    | 192.168.105.51 | 从    |    |
    | 192.168.105.52 | 从    |    |
*   设置

    检查auto.cnf中配置的server-uuid是否相同，如果相同则修改为不同。
*   准备主库配置文件

    [my.cnf](https://github.com/QQ1350995917/gitbook/tree/1301eb5173842b2e1ed6ae91795462f32063b906/storage/mysql/my-master.cnf)
*   准备从库配置文件

    [my.cnf](https://github.com/QQ1350995917/gitbook/tree/1301eb5173842b2e1ed6ae91795462f32063b906/storage/mysql/my-slave.cnf)

### 配置中的关键参数说明

#### sync_binlog

在主服务器上最重要的二进制日志设置是sync_binlog，这使得mysql在每次提交事务的时候把二进制日志的内容同步到磁盘上，即使服务器崩溃也会把事件写入日志中。 sync_binlog这个参数是对于MySQL系统来说是至关重要的，他不仅影响到Binlog对MySQL所带来的性能损耗，而且还影响到MySQL中数据的完整性。

对于"sync_binlog"参数的各种设置的说明如下：

* sync_binlog=0，当事务提交之后，MySQL不做fsync之类的磁盘同步指令刷新binlog_cache中的信息到磁盘，而让Filesystem自行决定什么时候来做同步，或者cache满了之后才同步到磁盘。
* sync_binlog=n，当每进行n次事务提交之后，MySQL将进行一\`fsync之类的磁盘同步指令来将binlog_cache中的数据强制写入磁盘。

在MySQL中系统默认的设置是sync_binlog=0，也就是不做任何强制性的磁盘刷新指令，这时候的性能是最好的，但是风险也是最大的。因为一旦系统Crash，在binlog_cache中的所有binlog信息都会被丢失。而当设置为“1”的时候，是最安全但是性能损耗最大的设置。因为当设置为1的时候，即使系统Crash，也最多丢失binlog_cache中未完成的一个事务，对实际数据没有任何实质性影响。

对于高并发事务的系统来说，sync_binlog设置为0和设置为1的系统写入性能差距可能高达5倍甚至更多

#### 主从同步方式

*   异步复制

    由于mysql默认的复制方式是异步的，主库把日志发送给从库后不关心从库是否已经处理，这样会产生一个问题就是假设主库挂了，从库处理失败了，这时候从库升为主库后，日志就丢失了。由此产生两个概念。
*   全同步复制

    主库写入binlog后强制同步日志到从库，所有的从库都执行完成后才返回给客户端，但是很显然这个方式的话性能会受到严重影响。
*   半同步复制

    和全同步不同的是，半同步复制的逻辑是这样，从库写入日志成功后返回ACK确认给主库，主库收到至少一个从库的确认就认为写操作完成。

详情请见[主从复制原理](deploy-master-slave-concept.md)

### 主从库设置

配置完成后，启动各个服务，状态如下：

```
show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000001 |      154 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)


show slave status\G
Empty set (0.00 sec)
```

此时各个服务都可以当成主库使用（配置参数合适的话），主从需要设置。

### 设置从库

登录指定的作为slave的MySQL服务，执行接入master

```
change master to master_host='主库IP',master_port=3306,master_user='root',master_password='密码',master_log_file='mysql-bin.000001',master_log_pos=154,master_bind='0.0.0.0';
```

而后启动slave

```
start slave;
```

查看从库状态

```
show slave status \G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.105.24
                  Master_User: root
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000003
          Read_Master_Log_Pos: 154
               Relay_Log_File: relay-bin.000002
                Relay_Log_Pos: 320
        Relay_Master_Log_File: mysql-bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB: 
          Replicate_Ignore_DB: mysql,sys,information_schema,performance_schema
           Replicate_Do_Table: 
       Replicate_Ignore_Table: 
      Replicate_Wild_Do_Table: 
  Replicate_Wild_Ignore_Table: 
                   Last_Errno: 0
                   Last_Error: 
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 154
              Relay_Log_Space: 521
              Until_Condition: None
               Until_Log_File: 
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File: 
           Master_SSL_CA_Path: 
              Master_SSL_Cert: 
            Master_SSL_Cipher: 
               Master_SSL_Key: 
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error: 
               Last_SQL_Errno: 0
               Last_SQL_Error: 
  Replicate_Ignore_Server_Ids: 
             Master_Server_Id: 24
                  Master_UUID: 1be52227-3609-11eb-8488-fa163ed9bc0f
             Master_Info_File: mysql.slave_master_info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind: 0.0.0.0
      Last_IO_Error_Timestamp: 
     Last_SQL_Error_Timestamp: 
               Master_SSL_Crl: 
           Master_SSL_Crlpath: 
           Retrieved_Gtid_Set: 
            Executed_Gtid_Set: 
                Auto_Position: 0
         Replicate_Rewrite_DB: 
                 Channel_Name: 
           Master_TLS_Version: 
1 row in set (0.00 sec)
```

注意，Slave_IO_Running和Slave_SQL_Running状态，并关注mysql日志中的输出是否有错，可根据错误信息解决其值为NO的问题

Slave_IO_Running为NO常见有如下问题：

* 各个主机auto.cnf中的UUID值相同，修改后重启服务即可
* 设置master的受master_log_file或者其他参数设置错误
* 主机之间不能访问
* mysql服务不能远程

重新设置master

* 从库：stop slave
* 主库：flush logs;
* 从库：重新执行change
* 从库：start slave;

## 给已有数据的库做主从

如果要同步有数据的主库，可以先锁定数据库

```
flush tables with read lock;
```

导出数据库结构以及文件

```
mysqldump -uroot -p'root' -S /tmp/mysql.sock --all-databases > /mysql/backup/mysql_bak.$(date +%F).sql
```

或者压缩导出

```
mysqldump -uroot -p'root' -S /tmp/mysql.sock --all-databases | gzip > /mysql/backup/mysql_bak.$(date +%F).sql.gz
```

查看主库binlog的位置信息

```
show master status;
```

留意Position字段值

恢复主库的操作

```
unlock tables;
```

登录从库，导入数据文件到从库

```
mysql -uroot -p'root' -S /tmp/mysql-33061.sock < /mysql/backup/mysql_bak.2019-03-01.sql
```

在从库上做出主库指向

```
change master to master_host='127.0.0.1', master_port=3306, master_user='root', master_password='root', master_log_file='mysql-bin.000001', master_log_pos=1200;
```

其中master_log_file以及master_log_pos为前面`show master status;`中的结果

启动主从同步进程

```
start slave;
```

检查从库状态

```
show slave status \G
```

主要查看两个状态

```
Slave_IO_Running: Yes
Slave_SQL_Running: Yes
```

此时可以更改主库，查看从库同步状态。

* Slave_IO_Running: NO：一般情况下是各个主机连接不同的情况，可以按照ping，或者检查数据库用户名称以及密码检查。
* Slave_SQL_Running: NO：master_log_pos需要和当前主库的Position保持一致。

## 参考资料

* [参考](https://www.cnblogs.com/wade-lt/p/9008058.html)
* [参考](https://blog.51cto.com/kerry/277414)
* [Mysql集群搭建（多实例、主从）](https://blog.csdn.net/qq\_21153619/article/details/81529880)
* [MySQL主从同步](https://www.cnblogs.com/kylinlin/p/5258719.html)
* [Mac10.13.6 Mysql5.7.23多实例部署](https://www.jianshu.com/p/30d504d0ee50)
