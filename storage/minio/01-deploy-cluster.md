# MinIO集群部署

## MinIO分布式概念
分布式 Minio 可以让你将多块硬盘（甚至在不同的机器上）组成一个对象存储服务。由于硬盘分布在不同的节点上，分布式 Minio 避免了单点故障。

Minio 分布式模式可以搭建一个高可用的对象存储服务，你可以使用这些存储设备，而不用考虑其真实物理位置。

###（1）高可用

单机 Minio 服务存在单点故障，相反，如果是一个 N 节点的分布式 Minio ,只要有 N/2 节点在线，你的数据就是安全的。不过你需要至少有 N/2+1 个节点来创建新的对象。

例如，一个 8 节点的 Minio 集群，每个节点一块盘，就算 4 个节点宕机，这个集群仍然是可读的，不过你需要 5 个节点才能写数据。

###（2）限制

分布式 Minio 单租户存在最少 4 个盘最多 16 个盘的限制（受限于纠删码）。这种限制确保了 Minio 的简洁，同时仍拥有伸缩性。如果你需要搭建一个多租户环境，你可以轻松的使用编排工具（Kubernetes）来管理多个Minio实例。

注意，只要遵守分布式 Minio 的限制，你可以组合不同的节点和每个节点几块盘。比如，你可以使用 2 个节点，每个节点 4 块盘，也可以使用 4 个节点，每个节点两块盘，诸如此类。

###（3）一致性

Minio 在分布式和单机模式下，所有读写操作都严格遵守 read-after-write 一致性模型。

###（4）数据保护

分布式 Minio 采用纠删码（erasure code）来防范多个节点宕机和位衰减（bit rot）。

分布式 Minio 至少需要 4 个节点，使用分布式 Minio 就自动引入了纠删码功能。

纠删码是一种恢复丢失和损坏数据的数学算法， Minio 采用 Reed-Solomon code 将对象拆分成 N/2 数据和 N/2 奇偶校验块。 这就意味着如果是 12 块盘，一个对象会被分成 6 个数据块、6 个奇偶校验块，你可以丢失任意 6 块盘（不管其是存放的数据块还是奇偶校验块），你仍可以从剩下的盘中的数据进行恢复。

纠删码的工作原理和 RAID 或者复制不同，像 RAID6 可以在损失两块盘的情况下不丢数据，而 Minio 纠删码可以在丢失一半的盘的情况下，仍可以保证数据安全。 而且 Minio 纠删码是作用在对象级别，可以一次恢复一个对象，而RAID 是作用在卷级别，数据恢复时间很长。 Minio 对每个对象单独编码，存储服务一经部署，通常情况下是不需要更换硬盘或者修复。Minio 纠删码的设计目标是为了性能和尽可能的使用硬件加速。

位衰减又被称为数据腐化 Data Rot、无声数据损坏 Silent Data Corruption ，是目前硬盘数据的一种严重数据丢失问题。硬盘上的数据可能会神不知鬼不觉就损坏了，也没有什么错误日志。正所谓明枪易躲，暗箭难防，这种背地里犯的错比硬盘直接故障还危险。 所以 Minio 纠删码采用了高速 HighwayHash 基于哈希的校验和来防范位衰减。

## 搭建
启动一个分布式 Minio 实例，你只需要把硬盘位置做为参数传给 minio server 命令即可，然后，你需要在所有其它节点运行同样的命令。

注意

- 分布式 Minio 里所有的节点需要有同样的 access 秘钥和 secret 秘钥，这样这些节点才能建立联接。为了实现这个，你需要在执行 minio server 命令之前，先将 access 秘钥和 secret 秘钥 export 成环境变量。
- 分布式 Minio 使用的磁盘里必须是干净的，里面没有数据。
- 下面示例里的 IP 仅供示例参考，你需要改成你真实用到的 IP 和文件夹路径。
- 分布式 Minio 里的节点时间差不能超过 3 秒，你可以使用 NTP 来保证时间一致。
- 在 Windows 下运行分布式 Minio 处于实验阶段，不建议用于生产环境。

## 规划
生产环境建议最少4节点，本例采用两台服务器，四个节点

|节点	|IP|	data|
|---|---|---|
|minio1|	10.51.52.20 |	/data/minio/data1|
|minio2|	10.51.52.20 |	/data/minio/data1|
|minio3|	10.51.52.21 |	/data/minio/data3|
|minio4|	10.51.52.21 |	/data/minio/data4|

```bash
adduser minio
echo minio|passwd --stdin minio
su minio
cd ~
```

## 下载minio
- https://min.io/download#/linux

```bash
su minio
cd ~
curl -O https://dl.min.io/server/minio/release/linux-amd64/minio


```
## 系统最大文件数修改

```bash
echo "*   soft    nofile  65535" >> /etc/security/limits.conf
echo "*   hard    nofile  65535" >> /etc/security/limits.conf
```

## 目录创建

```bash
mkdir -p {data1,data2}
```

## 创建集群启动脚本

```bash
vim /data/minio/run.sh

MINIO_ACCESS_KEY：用户名，长度最小是5个字符
MINIO_SECRET_KEY：密码，密码不能设置过于简单，不然minio会启动失败，长度最小是8个字符
–config-dir：指定集群配置文件目录
```

```bash
#!/bin/bash
export MINIO_ACCESS_KEY=Z2QldTk4NzkldTU3Q0VGaWxl
export MINIO_SECRET_KEY=JXVGRjAxQCUyMyV1OTg3OSV1NTdDRQ

nohup /data/minio/minio server http://10.51.52.20/data/minio/data1  http://10.51.52.20/data/minio/data2 \
			http://10.51.52.21/data/minio/data3 http://10.51.52.21/data/minio/data4 
	>/data/minio/logs/running.log 2>&1 &
```

## minio.service服务脚本

- WorkingDirectory：二进制文件目录
- ExecStart：指定集群启动脚本

```bash
cat > /usr/lib/systemd/system/minio.service <<EOF
[Unit]
Description=Minio service
Documentation=https://docs.minio.io/

[Service]
WorkingDirectory=/data/minio/
ExecStart=/data/minio/run.sh

Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

## 权限修改
```bash
chmod +x /usr/lib/systemd/system/minio.service && chmod +x /data/minio/minio && chmod +x /data/minio/run.sh
```

## nginx代理
```text
upstream minio{
        server 10.51.52.20:9000;
        server 10.51.52.21:9000;
}
server {
        listen 9000;
        server_name minio;
        location / {
                proxy_pass http://minio;
                proxy_set_header Host $http_host;
                client_max_body_size 1000m;
        }
}
```

## 访问策略设置(公开访问)

```text
#创建桶
./mc mbpublic桶名
Bucket created successfully `public`.

cd /data/minio/
wget https://dl.min.io/client/mc/release/linux-amd64/mc
#添加host 添加Key
./mc config host add minio http://10.51.52.20:9000 Z2QldTk4NzkldTU3Q0VGaWxl JXVGRjAxQCUyMyV1OTg3OSV1NTdDRQ
# 设置捅公开访问
./mc policy set public(访问级别) minio/public/(桶路径)
./mc policy set public minio/public/
```

## 参考资料
https://min.io/download#/linux
https://www.cnblogs.com/erdongx/p/11829726.html
https://www.yepk.cn/archives/minio-cluster.html
https://www.cnblogs.com/aloneysir/p/12874984.html
