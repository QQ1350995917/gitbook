# REDIS安装

系统环境配置

sudo apt-get update

sudo apt-get upgrade

redis安装

sudo apt-get install redis-server

如果需要设置为系统服务则执行

sudo systemctl enable redis-server.service

安装完成后查看版本以及帮助信息

redis-server -v

redis-server -h

启动服务

redis-server 或者redis-server start

重启服务

redis-server restart

启动客户端

redis-cli

关闭服务

redis-server stop

关闭客户端

redis-cli shutdown

查看进程状态

ps aux \| grep "redis"

连接测试

redis-cli

执行后会显示服务器地址以及端口

输入ping则服务器会相应pong

配置信息

sudo vim /etc/redis/redis.conf

允许远程连接并关闭保护模式

\# 把以下注释掉（前面加\#）

bind 127.0.0.1 ::1

\# 以下改为 yes → no

protected-mode no

\# 如果需要，设置验证密码

requirepass 123456

通过客户端设置

redis 127.0.0.1:6379&gt; CONFIG SET requirepass YOURPASSPHRASE

OK

redis 127.0.0.1:6379&gt; AUTH YOURPASSPHRASE

Ok

设置密码后的链接方式

redis-cli -h 127.0.0.1 -p 6379 -a YOURPASSPHRASE

配置文件中可以更改内存大小，设置淘汰策略等

maxmemory 256mb

maxmemory-policy allkeys-lru

设置变更后需要重启服务

可视化客户端工具

[https://redisdesktop.com/download](https://redisdesktop.com/download)

可通过源码编译开源版本

[https://github.com/uglide/RedisDesktopManager/releases](https://github.com/uglide/RedisDesktopManager/releases)

# REDIS集群配置

# REDIS命令行客户端安装以及使用

# REDIS监控工具



