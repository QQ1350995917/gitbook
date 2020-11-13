# 备份与还原
## 备份目的
- 做灾难恢复：对损坏的数据进行恢复和还原
- 需求改变：因需求改变而需要把数据还原到改变以前
- 测试：测试新功能是否可用

## 备份要考虑的问题
- 可以容忍丢失多长时间的数据；
- 恢复数据要在多长时间内完； 
- 恢复的时候是否需要持续提供服务；
- 恢复的对象，是整个库，多个表，还是单个库，单个表。
## 备份的分类
### 1、根据是否需要数据库离线
- 冷备（cold backup）：需要关mysql服务，读写请求均不允许状态下进行；
- 温备（warm backup）： 服务在线，但仅支持读请求，不允许写请求；
- 热备（hot backup）：备份的同时，业务不受影响。

注意事项
- 1、这种类型的备份，取决于业务的需求，而不是备份工具
- 2、MyISAM不支持热备，InnoDB支持热备，但是需要专门的工具

### 2、根据要备份的数据集合的范围
- 完全备份：full backup，备份全部字符集。
- 增量备份: incremental backup 上次完全备份或增量备份以来改变了的数据，不能单独使用，要借助完全备份，备份的频率取决于数据的更新频率。
- 差异备份：differential backup 上次完全备份以来改变了的数据。

建议的恢复策略：
- 完全+增量+二进制日志
- 完全+差异+二进制日志    

### 3、根据备份数据或文件
- 物理备份：直接备份数据文件
  - 优点：备份和恢复操作都比较简单，能够跨mysql的版本，恢复速度快，属于文件系统级别的
  - 建议：不要假设备份一定可用，要通过mysql>check tables；检测表是否可用
- 逻辑备份: 备份表中的数据和代码
  - 优点：恢复简单、备份的结果为ASCII文件，可以编辑，与存储引擎无关，可以通过网络备份和恢复
  - 缺点：备份或恢复都需要mysql服务器进程参与，备份结果占据更多的空间，浮点数可能会丢失精度，还原之后，缩影需要重建
  
## 备份的对象
-  数据；
- 配置文件；
- 代码：存储过程、存储函数、触发器
- os相关的配置文件
- 复制相关的配置
- 二进制日志  

## 备份和恢复的实现
- 利用select into outfile实现数据的备份与还原
  - 数据备份
  ```
  mysql> use hellodb;     　　　　　　//打开hellodb库
  mysql> select * from students;  　　查看students的属性
  mysql> select * from students where Age > 30 into outfile ‘/tmp/stud.txt' ;   　　//将年龄大于三十的同学的信息备份出来 
  ```
  注意：
  
  备份的目录路径必须让当前运行mysql服务器的用户mysql具有访问权限。
  
  - 数据恢复
  ```
  mysql> delete from students where Age > 30; // 模拟数据被破坏
  mysql> load data infile '/tmp/stud.txt' into table students;
  ```
  
- 利用mysqldump工具对数据进行备份和还原

  mysqldump 常用来做温备，所以我们首先需要对想备份的数据施加读锁，
  
  2.1 施加读锁：
 
  1.直接在备份的时候添加选项
  
  --lock-all-tables 是对要备份的数据库的所有表施加读锁
  
  --lock-table 仅对单张表施加读锁，即使是备份整个数据库，它也是在我们备份某张表的时候才对该表施加读锁，因此适用于备份单张表
  
  ```
  mysql> flush tables with read lock; 施加锁，表示把位于内存上的表统统都同步到磁盘上去，然后施加读锁
  mysql> flush tables with read lock;释放读锁
  
  但这对于InnoDB存储引擎来讲，虽然你也能够请求道读锁，但是不代表它的所有数据都已经同步到磁盘上，
  因此当面对InnoDB的时候，我们要使用
  mysql> show engine innodb status; 
  看看InnoDB所有的数据都已经同步到磁盘上去了，才进行备份操作。
  ```
  
  2.2备份的策略：

  完全备份+增量备份+二进制日志

  先给数据库做完全备份：
  
  ```
  [root@www ~]# mysqldump -uroot --single-transaction --master-data=2 --databases hellodb > /backup/hellodb_`date +%F`.sql
  
  --single-transaction: 基于此选项能实现热备InnoDB表；因此，不需要同时使用--lock-all-tables；
  --master-data=2  记录备份那一时刻的二进制日志的位置，并且注释掉，1是不注释的
  --databases hellodb 指定备份的数据库
  然后回到mysql服务器端， 
  ```
  
  回到mysql服务器端更新数据
  
  ```
  mysql> create table tb1(id int); 创建表
  mysql> insert into tb1 values (1),(2),(3);  插入数据，这里只做演示，随便插入了几个数据
  ```
  
  先查看完全备份文件里边记录的位置：
  ```
  [root@www backup]# cat hellodb_2013-09-08.sql | less
  -- CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000013', MASTER_LOG_POS=15684; 记录了二进制日志的位置
  ```
  
  在回到服务器端：
  ```
  mysql> show master status;
  显示此时的二进制日志的位置，从备份文件里边记录的位置到我们此时的位置，即为增量的部分
  +------------------+----------+--------------+------------------+
  | File             | Position | Binlog_Do_DB | Binlog_Ignore_DB |
  +------------------+----------+--------------+------------------+
  | mysql-bin.000004 |      15982 |              |                  |
  +------------------+----------+--------------+------------------+
  ```
  做增量备份
  ```
  [root@www backup]# mysqlbinlog --start-position=15694 --stop-position=15982
  /mydata/data/mysql-bin.000013 > /backup/hellodb_`date +$F_%H`.sql
  ```  
  再回到服务器
  ```
  mysql> insert into tb1 values (4),(5); 在插入一些数值
  mysql> drop database hellodb;   删除hellodb库 
  ```
  
  导出这次得二进制日志：
  ```
  mysqlbinlog --start-position=15982 /mydata/data/mysql-bin.000013 
  查看删除操作时二进制日志的位置
  # mysqlbinlog --start-position=15982 --stop-position=16176 /mydata/data/mysql-bin.000013 > /tmp/hellodb.sql  
  //导出二进制日志 
  ```
  
  先让mysql离线
  ```
  mysql> set sql_log_bin=0;  关闭二进制日志
  mysql> flush logs; 滚动下日志 
  ```
  
  模拟数据库损坏
  ```
  mysql> drop database hellodb;
  ```
  
  开始恢复数据：
  ```
  [root@www ]# mysql < /backup/hellodb_2013-09-08.sql  
  //导入完全备份文件
  [root@www ]# mysql < /backup/hellodb_2013-09-08_05.sql 
  //导入增量备份文件
  [root@www ]# mysql< hellodb.sql 
  //导入二进制文件
  ```
  验证完成，显示结果为我们预想的那样
  
  注：
  
  1、真正在生产环境中，我们应该导出的是整个mysql服务器中的数据，而不是单个库，因此应该使用--all-databases
  2、在导出二进制日志的时候，可以直接复制文件即可，但是要注意的是，备份之前滚动下日志。
  3、利用lvm快照实现几乎热备的数据备份与恢复
  
  
  3.1策略：
  
  完全备份+二进制日志；
  
  3.2准备：
  
  注：事务日志必须跟数据文件在同一个LV上；
  
  3.3创建lvm Lvm的创建这里就不多说了，想了解话点击http://www.jb51.net/LINUXjishu/105937.html
  
  3.4 修改mysql主配置文件存放目录内的文件的权限与属主属组，并初始化mysql
  
  ```
  [root@www ~]# mkdir /mydata/data             //创建数据目录
  [root@www ~]# chown mysql:mysql /mydata/data  //改属组属主
  [root@www ~]# cd /usr/local/mysql/    //必须站在此目录下      
  [root@www mysql]# scripts/mysql_install_db --user=mysql --datadir=/mydata/data  //初始化mysql 
  ```
  
  3.5修改配置文件：
  ```
  vim /etc/my.cof
  datadir=/mydata/data   添加数据目录
  sync_binlog = 1  开启此功能 
  ```
  
  3.6 启动服务
  ```
  [root@www mysql]# service mysqld start
  mysql> set session sql_log_bin=0;  关闭二进制日志
  mysql> source /backup/all_db_2013-09-08.sql   读取备份文件
  ```
  
  3.7 回到mysql服务器：
  ```
  mysql> FLUSH TABLES WITH READ LOCK;
  请求读锁  注：不要退出，另起一个终端：
  mysql> SHOW MASTER STATUS;         
  查看二进制文件的位置
  +------------------+----------+--------------+------------------+
  | File             | Position | Binlog_Do_DB | Binlog_Ignore_DB |
  +------------------+----------+--------------+------------------+
  | mysql-bin.000004 |      107 |              |                  |
  +------------------+----------+--------------+------------------+
  1 row in set (0.00 sec)
  mysql> FLUSH LOGS;  
  建议滚动下日志。这样备份日志的时候就会很方便了
  ```
  
  3.8导出二进制文件，创建个目录单独存放
  ```
  [root@www ~]# mkdir /backup/limian
  [root@www ~]# mysql -e 'show master status;' > /backup/limian/binlog.txt
  ```
  
  3.9为数据所在的卷创建快照：
  ```
  [root@www ~]# lvcreate -L 100M -s -p r -n mysql_snap /dev/myvg/mydata
  ``` 
  回到服务器端，释放读锁
  ```
  mysql> UNLOCK TABLES;
  [root@www ~]# mount /dev/myvg/mysql_snap /mnt/data
  [root@www data]# cp * /backup/limian/
  [root@www data]#lvremove /dev/myvg/mylv_snap 
  ```
  3.10更新数据库的数据，并删除数据目录先的数据文件，模拟数据库损坏

  ```  
  mysql>  create table limiantb (id int,name CHAR(10));
  mysql> insert into limiantb values (1,'tom');
  [root@www data]# mysqlbinlog --start-position=187 mysql-bin.000003 > /backup/limian/binlog.sql
  [root@www backup]# cd /mydata/data/
  [root@www data]#  rm -rf *
  [root@www ~]# cp -a /backup/limian/* /mydata/data/
  [root@www data]# chown mysql:mysql *
  ```
  
  3.11测试
  
  启动服务
  
  ```
  [root@www data]# service mysqld start
  [root@www data]# mysql 登陆测试
  mysql> SHOW DATABASES;
  mysql> SET sql_log_bin=0
  mysql> source/backup/limian/binlog.sql; #二进制恢复
  mysql> SHOW TABLES;         #查看恢复结果
  mysql> SET sql_log_bin=1;   #开启二进制日志
  
  注：此方式实现了接近于热备的方式备份数据文件，而且数据文件放在lvm中可以根据数据的大小灵活改变lvm的大小，备份的方式也很简单。
  ```
  
- 基于Xtrabackup做备份恢复
官方站点：www.percona.com

优势：

1、快速可靠的进行完全备份
2、在备份的过程中不会影响到事务
3、支持数据流、网络传输、压缩，所以它可以有效的节约磁盘资源和网络带宽。
4、可以自动备份校验数据的可用性。

安装Xtrabackup

```
[root@www ~]# rpm -ivh percona-xtrabackup-2.1.4-656.rhel6.i686.rpm 
```  

其最新版的软件可从 http://www.percona.com/software/percona-xtrabackup/ 获得

注意：在备份数据库的时候，我们应该具有权限，但需要注意的是应该给备份数据库时的用户最小的权限，以保证安全性，

4.1前提：

应该确定采用的是单表一个表空间，否则不支持单表的备份与恢复。
在配置文件里边的mysqld段加上

 

innodb_file_per_table = 1
4.2备份策略
完全备份+增量备份+二进制日志
4.3准备个目录用于存放备份数据

[root@www ~]# mkdir /innobackup
4.4做完全备份：

[root@www ~]# innobackupex --user=root --password=mypass /innobackup/
注：

1、只要在最后一行显示 innobackupex: completed OK!，就说明你的备份是正确的。
2、另外要注意的是每次备份之后，会自动在数据目录下创建一个以当前时间点命名的目录用于存放备份的数据，那我们去看看都有什么

[root@www 2013-09-12_11-03-04]# ls
backup-my.cnf ibdata1 performance_schema xtrabackup_binary xtrabackup_checkpoints
hellodb mysql test xtrabackup_binlog_info xtrabackup_logfile
[root@www 2013-09-12_11-03-04]#
xtrabackup_checkpoints ：备份类型、备份状态和LSN(日志序列号)范围信息；
xtrabackup_binlog_info ：mysql服务器当前正在使用的二进制日志文件及至备份这一刻为止二进制日志事件的位置。
xtrabackup_logfile ：非文本文件，xtrabackup自己的日志文件
xtrabackup_binlog_pos_innodb ：二进制日志文件及用于InnoDB或XtraDB表的二进制日志文件的当前position。
backup-my.cnf ：备份时数据文件中关于mysqld的配置

4.5回到mysql服务器端对数据进行更新操作

mysql> use hellodb;
mysql> delete from students where StuID>=24;
4.6增量备份

innobackupex --user=root --password=mypass --incremental /innobackup/--incremental-basedir=/innobackup/2013-09-12_11-03-04/
--incremental  指定备份类型
--incremental-basedir= 指定这次增量备份是基于哪一次备份的，这里是完全备份文件,这样可以把增量备份的数据合并到完全备份中去
4.7第二次增量

先去修改数据

mysql> insert into students (Name,Age,Gender,ClassID,TeacherID) values ('tom',33,'M',2,4);
innobackupex --user=root --password=mypass --incremental /innobackup/ --incremental-basedir=/innobackup/2013-09-12_11-37-01/
 这里只须要把最后的目录改为第一次增量备份的数据目录即可
4.8最后一次对数据更改但是没做增量备份
mysql> delete from coc where id=14;

4.9把二进制日志文件备份出来，(因为最后一次修改，没做增量备份，要依赖二进制日志做时间点恢复)
[root@www data]# cp mysql-bin.000003 /tmp/

4.10模拟数据库崩溃
[root@www data]# service mysqld stop
[root@www data]# rm -rf *

恢复前准备

4.11对完全备份做数据同步
[root@www ~]# innobackupex --apply-log --redo-only /innobackup/2013-09-12_11-03-04/

4.12对第一次增量做数据同步
innobackupex --apply-log --redo-only /innobackup/2013-09-12_11-03-04/ --incremental-basedir=/innobackup/2013-09-12_11-37-01/

4.13对第二次增量做数据同步
innobackupex --apply-log --redo-only /innobackup/2013-09-12_11-03-04/ --incremental-basedir=/innobackup/2013-09-12_11-45-53/
--apply-log 的意义在于把备份时没commit的事务撤销，已经commit的但还在事务日志中的应用到数据库

注：

对于xtrabackup来讲，它是基于事务日志和数据文件备份的，备份的数据中可能会包含尚未提交的事务或已经提交但尚未同步至数据库文件中的事务，还应该对其做预处理，把已提交的事务同步到数据文件，未提交的事务要回滚。因此其备份的数据库，不能立即拿来恢复。

预处理的过程：

首先对完全备份文件只把已提交的事务同步至数据文件，要注意的是有增量的时候，不能对事务做数据回滚，不然你的增量备份就没有效果了。

然后把第一次的增量备份合并到完全备份文件内，

以此类推，把后几次的增量都合并到前一次合并之后的文件中，这样的话，我们只要拿着完全备份+二进制日志，就可以做时间点恢复。

4.14数据恢复

[root@www ~]# service mysqld stop
[root@www data]# rm -rf *  模拟数据库崩溃
[root@www ~]# innobackupex --copy-back /innobackup/2013-09-12_11-03-04/
--copy-back数据库恢复，后面跟上备份目录的位置
4.15检测：

[root@www ~]# cd /mydata/data/
[root@www data]# chown mysql:mysql *
[root@www data]#service mysqld start

## 参考
https://www.cnblogs.com/fengzhongzhuzu/p/9101782.html
https://blog.csdn.net/mashuai720/article/details/83347029

