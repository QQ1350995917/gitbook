# Group

以root安装docker，普通用户mysql使用docker为例

docker 进程使用 Unix Socket 而不是 TCP 端口。而默认情况下， Unix socket 属于 root 用户，需要 root 权限才能访问。

由于docker守护进程启动的时候，会默认赋予名字为 docker 的用户组读写 Unix socket 的权限，因此只要创建 docker 用户组，并将当前用户加入到 docker 用户组中，那么当前用户就有权限访问 Unix socket 了，进而也就可以执行 docker 相关命令了。

```
su root
groupadd docker #添加docker用户组(docker通过apt 安装后会自动添加)
gpasswd -a mysql docker     #将mysql用户加入到docker用户组中
su mysql
newgrp docker     #更新用户组
```
