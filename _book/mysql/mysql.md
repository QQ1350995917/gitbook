# MySQL

# mysql
cd /usr/local/bin
mkdir mysql
cd mysql/
wget https://repo.mysql.com//mysql-apt-config_0.8.10-1_all.deb
dpkg -i mysql-apt-config_0.8.10-1_all.deb
# select 8.0 and press ok
apt update
apt-get install mysql-server
# input the password of root
# select and pres "Use Legacy Authentication Method (Retain MySQL 5.x Compatibility) "

# apt isntall mysql-client
# apt install libmysqlclient-dev


# mysql 常用命令模板
# 创建数据库
# drop database if exists db_guest;
# create database if not exists db_guest default charset=utf8;
# 创建用户
# insert into mysql.user(Host,User,Password) values("localhost","guest",password("guest"));
# 更新用户密码
# SET PASSWORD FOR 'root'@'localhost' = PASSWORD('newpass');
# 删除用户
# use mysql
# Delete from user where user = "user_name" and host = "host_name" ;
# 使改变的用户生效
# flush privileges;
# 对用户授权指定的数据库的表的所有权限
# GRANT ALL PRIVILEGES ON db_guest.* TO 'guest'@'localhost' IDENTIFIED BY 'guest' WITH GRANT OPTION;
# 对用户授权指定的数据库的表的指定权限
# grant select,update on db_guest.* to 'guest'@'localhost'   identified by 'guest';
# 撤销权限 Revoke privileges (columns) on what from user ;  privileges是要取消的权限，user是要被取消权限的用户名
# revoke all on phpmyadmin.* from guest@localhost ;

# 设置phpadmin初始化用户 username:guest password:guest databasename:db_guest
drop database if exists db_guest;
drop database if exists phpmyadmin;
create database if not exists db_guest default charset=utf8;
create database if not exists phpmyadmin default charset=utf8;
insert into mysql.user(Host,User,Password) values("localhost","guest",password("guest"));
flush privileges;
GRANT ALL PRIVILEGES ON db_guest.* TO 'guest'@'localhost' IDENTIFIED BY 'guest' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON phpmyadmin.* TO 'guest'@'localhost' IDENTIFIED BY 'guest' WITH GRANT OPTION;

# mysql.server start
# mysql.server stop
# mysql -u root -p
# exit
# select * from mysql.user;//查询所有用户
# status;
# select version();  
# show global variables like 'port';
mysql> set character_set_client=utf8;
mysql> set character_set_connection=utf8;
mysql> set character_set_database=utf8;
mysql> set character_set_results=utf8;
mysql> set character_set_server=utf8;
mysql> set character_set_system=utf8;
mysql> set collation_connection=utf8;
mysql> set collation_database=utf8;
mysql> set collation_server=utf8;
mysql> show variables like 'character_set_%';
