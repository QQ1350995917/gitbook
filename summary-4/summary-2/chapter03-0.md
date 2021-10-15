# 常用命令

## 服务连接

*   启动服务

    ```
    zkServer.sh --config /home/zookeeper/stand-alone/conf start
    ```
*   停止

    ```
    zkServer.sh stop
    ```
*   重启

    ```
    zkServer.sh restart
    ```
*   查看状态

    ```
    zkServer.sh status
    ```

    或者

    ```
    zkServer.sh --config /home/zookeeper/stand-alone/conf status
    ```
*   查看zookeeper输出信息

    ```
    tail -f /home/zookeeper/stand-alone/zoo/zookeeper.out
    ```
*   客户端连接

    ```
    zkCli.sh
    zkCli.sh host:port
    ```

    重新连接指定Zookeeper服务

    ```
    connect host:port
    ```

    关闭当前会话

    ```
    close
    ```
*   客户端退出

    ```
    quit
    ```

## 节点操作

创建节点

```
create [-s] [-e] [-c] [-t ttl] path [data] [acl]
create /test "hello world"
```

创建子节点

```
create /test/pwd "hello pwd"
```

列出子节点

```
ls /
```

查看节点

```
get /luban
```

删除节点，(不能存在子节点）

```
delete [-v version] path
delete /test/pwd
```

删除路径及所有子节点

```
deleteall path
deleteall /test
```

设置节点限额 -n 子节点数 -b 字节数

```
setquota -n|-b val path
```

查看节点限额

```
listquota path
```

删除节点限额

```
delquota [-n|-b] path
```

查看节点数据 -s 包含节点状态 -w 添加监听

```
get [-s] [-w] path
```

列出子节点 -s状态 -R 递归查看所有子节点 -w 添加监听

```
getAcl [-s] path
ls [-s] [-w] [-R] path
```

是否打印监听事件

```
printwatches on|off
```

查看执行的历史记录

```
history
```

重复 执行命令，history 中命令编号确定

```
redo cmdno
```

删除指定监听

```
removewatches path [-c|-d|-a] [-l]
```

设置值

```
set [-s] [-v version] path data
```

为节点设置ACL权限

```
setAcl [-s] [-v version] [-R] path acl
```

查看节点状态 -w 添加监听

```
stat [-w] path
```

强制同步节点

```
sync path
```
