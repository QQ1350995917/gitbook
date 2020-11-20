



## 确认你要修改的网卡号
查看 ip地址
ip addr 
```text
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: enp0s25: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc fq_codel state UP group default qlen 1000
    link/ether 44:37:e6:49:c4:9c brd ff:ff:ff:ff:ff:ff
    inet 192.168.50.52/26 brd 192.168.50.63 scope global dynamic enp0s25
       valid_lft 80937sec preferred_lft 80937sec
    inet6 fe80::4637:e6ff:fe49:c49c/64 scope link 
       valid_lft forever preferred_lft forever
3: br-7fdb36cdda16: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:79:94:ac:23 brd ff:ff:ff:ff:ff:ff
    inet 172.19.0.1/16 brd 172.19.255.255 scope global br-7fdb36cdda16
       valid_lft forever preferred_lft forever
    inet6 fe80::42:79ff:fe94:ac23/64 scope link 
       valid_lft forever preferred_lft forever

```

查看 网关
netstat -rn
```text
Kernel IP routing table
Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
0.0.0.0         192.168.50.1    0.0.0.0         UG        0 0          0 enp0s25
172.17.0.0      0.0.0.0         255.255.0.0     U         0 0          0 docker0
172.19.0.0      0.0.0.0         255.255.0.0     U         0 0          0 br-7fdb36cdda16
192.168.50.0    0.0.0.0         255.255.255.192 U         0 0          0 enp0s25
192.168.50.1    0.0.0.0         255.255.255.255 UH        0 0          0 enp0s25

```
## 默认的网卡配置文件

vim /etc/netplan/50-cloud-init.yaml 

```bash

# This file is generated from information provided by
# the datasource.  Changes to it will not persist across an instance.
# To disable cloud-init's network configuration capabilities, write a file
# /etc/cloud/cloud.cfg.d/99-disable-network-config.cfg with the following:
# network: {config: disabled}
network:
    ethernets:
        enp0s25:
            dhcp4: true
    version: 2
```

查看DNS
cat /etc/resolv.conf

```text

# This file is managed by man:systemd-resolved(8). Do not edit.
#
# This is a dynamic resolv.conf file for connecting local clients to the
# internal DNS stub resolver of systemd-resolved. This file lists all
# configured search domains.
#
# Run "systemd-resolve --status" to see details about the uplink DNS servers
# currently in use.
#
# Third party programs must not access this file directly, but only through the
# symlink at /etc/resolv.conf. To manage man:resolv.conf(5) in a different way,
# replace this symlink by a static file or a different symlink.
#
# See man:systemd-resolved.service(8) for details about the supported modes of
# operation for /etc/resolv.conf.

nameserver 127.0.0.53
options edns0

```

## 设置静态IP

vim /etc/netplan/50-cloud-init.yaml 

假设IP地址修改为192.168.50.52，子网掩码即255.255.255.0，网关设置为192.168.50.1，DNS1：127.0.0.53，DNS2： 8.8.8.8

```bash

# This file is generated from information provided by
# the datasource.  Changes to it will not persist across an instance.
# To disable cloud-init's network configuration capabilities, write a file
# /etc/cloud/cloud.cfg.d/99-disable-network-config.cfg with the following:
# network: {config: disabled}
network:
    ethernets:
        enp0s25:
            dhcp4: no
            addresses: [192.168.50.52/24]
            optional: true
            gateway4: 192.168.50.1
            nameservers:
                    addresses: [127.0.0.53, 8.8.8.8]
    version: 2
```

## 应用新配置

netplan apply



## 参考资料
https://www.cnblogs.com/yaohong/p/11593989.html
