安装步骤：
1.更新Ubuntu的apt源索引
```
$ sudo apt-get update
```

2.安装包允许apt通过HTTPS使用仓库
```
$ sudo dpkg --configure -a
$ sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
```

3.添加Docker官方GPG key
```
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

4.设置Docker稳定版仓库
```
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
```

5.更新apt源索引
```
$ sudo apt-get update
```

6.安装最新版本Docker CE（社区版）
```
$ sudo apt-get install docker-ce
```

查看安装Docker的版本
```
$ docker --version
```

检查Docker CE 是否安装正确
```
$ sudo docker run hello-world
```


```
# 启动docker
sudo service docker start

# 停止docker
sudo service docker stop

# 重启docker
sudo service docker restart

# 列出镜像
docker image ls

# 拉取镜像
docker image pull library/hello-world

# 删除镜像
docker image rm 镜像id/镜像ID

# 创建容器
docker run [选项参数] 镜像名 [命令]

# 停止一个已经在运行的容器
docker container stop 容器名或容器id

# 启动一个已经停止的容器
docker container start 容器名或容器id

# kill掉一个已经在运行的容器
docker container kill 容器名或容器id

# 删除容器
docker container rm 容器名或容器id
```
