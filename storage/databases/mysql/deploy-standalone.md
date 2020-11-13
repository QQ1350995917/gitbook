# 安装

## 安装mysql-server以及client

sudo apt-get install mysql-server

sudo apt-get install mysql-client

sudo apt-get install libmysqlclient-dev

## 检测

sudo netstat -tap \| grep mysql

或

sudo service mysql status

## 异常处理


### 安装过程如果没有设置密码

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

## 卸载

sudo apt purge mysql-\*

sudo rm -rf /etc/mysql/ /var/lib/mysql

sudo apt autoremove

# [Docker部署mysql](https://dev.mysql.com/doc/refman/5.7/en/docker-mysql-getting-started.html#docker-starting-mysql-server)
[MySQL Docker镜像](https://hub.docker.com/_/mysql)

根据镜像说明可知：
- 默认的配置文件是：/etc/mysql/my.cnf
- 默认的数据目录是：/var/lib/mysql
1. 拉取mysql镜像
```
docker pull mysql　
或者
docker pull mysql/mysql-server:5.7
```
2. 查看镜像
```
docker images
```
3. 创建MySQL容器
```
docker run -di --name standalone -p 33306:3306 -e MYSQL_ROOT_PASSWORD=root mysql
```
- -p 代表端口映射，格式为  宿主机映射端口:容器运行端口
- -e 代表添加环境变量  MYSQL_ROOT_PASSWORD是root用户的登陆密码
4. 进入MySQL容器
```
docker exec -it standalone /bin/bash
```
5. 登陆mysql
```
mysql -u root -p
```
6. 远程连接调整
容器中登录mysql,查看mysql的版本
```
status;
```
注意mysql 8.0跟之前的授权方式不同，需要进行如下调整，才能远程连接
```
GRANT ALL ON *.* TO 'root'@'%';
flush privileges;
```
此时,还不能远程访问,因为Navicat只支持旧版本的加密,需要更改mysql的加密规则
```
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root' PASSWORD EXPIRE NEVER;
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
flush privileges;
```
设置完成，再次使用 Navicat 连接数据库

## 自定义配置
1. 进入MySQL容器查看my.cnf
```
docker exec -it standalone /bin/bash
cat /etc/my.cnf
```

```
mkdir /home/mysql/standalone/data
mkdir /home/mysql/standalone/mysql
mkdir /home/mysql/standalone/logger
```
```
mkdir -p /home/mysql/standalone/data /home/mysql/standalone/conf /home/mysql/standalone/mysqld /home/mysql/standalone/logger
```

在/home/mysql/standalone/mysql下创建配置文件my.cnf\
```
[mysqld]   
port=3306
lower_case_table_names=1 
character-set-server=utf8
collation-server=utf8_general_ci
init_connect='SET NAMES utf'
default_authentication_plugin=mysql_native_password
max_connections=320
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
```

接下来分别映射数据库目录和配置文件目录，启动容器：
```
docker run -d --name standalone \
-v /home/mysql/standalone/mysql/my.cnf:/etc/my.cnf \
-v /home/mysql/standalone/data:/var/lib/mysql \
-p 33306:3306 -e MYSQL_ROOT_PASSWORD=root mysql
```

```
docker run -d --name standalone \
-v /home/mysql/standalone/mysql/my.cnf:/etc/my.cnf \
-v /home/mysql/standalone/data:/var/lib/mysql \
-p 33306:3306 -e MYSQL_ROOT_PASSWORD=root mysql \
--character-set-server=utf8 \
--collation-server=utf8_general_ci \
--restart always \ 开机启动
--privileged=true \ 提升容器内权限为root
--default-authentication-plugin=mysql_native_password 
```

```
docker run -p 3306:3306 --name test -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
docker run -p 3306:3306 --name test -v /home/mysql/standalone/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:latest  
```

### docker 部署
```

docker run -p 3306:3306 --name standalone -v /home/mysql/standalone/data:/var/lib/mysql -v /home/mysql/standalone/mysql/my.cnf:/etc/my.cnf -e MYSQL_ROOT_PASSWORD=root -d mysql:latest
```
