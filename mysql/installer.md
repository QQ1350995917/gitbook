ubuntu 安装mysql-server以及client

sudo apt-get install mysql-server

apt-get isntall mysql-client

sudo apt-get install libmysqlclient-dev

检测

**sudo netstat -tap \| grep mysql**

安装过程如果没有设置密码则可以通过如下方式设置

cd etc/mysql

cat debian.cnf  
使用文件中的user和password登陆

设定密码

show databases;

use mysql;

update user set authentication\_string=PASSWORD\("自定义密码"\) where user='root';

update user set plugin="mysql\_native\_password";

flush privileges;

quit;

重启服务

/etc/init.d/mysql restart;

卸载

sudo apt purge mysql-\*

sudo rm -rf /etc/mysql/ /var/lib/mysql

sudo apt autoremove

