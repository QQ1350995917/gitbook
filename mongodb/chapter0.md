1：首先查看cpu指令集

cat /proc/cpuinfo \| grep name \| cut -f2 -d: \| uniq -c

去官网下载对应的版本，选择TGZ格式。

创建数据库文件夹与日志文件

2：curl -O https://fastdl.mongodb.org/linux/mongodb-linux-x86\_64-ubuntu1804-4.2.0.tgz

mkdir /usr/local/mongodb/data

touch /usr/local/mongodb/logs

2：mongod --dbpath /data/&lt;path&gt; --port &lt;port no&gt;

无密码启动

[https://www.cnblogs.com/mnote/p/8979299.html](https://www.cnblogs.com/mnote/p/8979299.html)

[https://www.cnblogs.com/shileima/p/7823434.html](https://www.cnblogs.com/shileima/p/7823434.html)

[https://www.jianshu.com/p/71f915b01bdf](https://www.jianshu.com/p/71f915b01bdf)

