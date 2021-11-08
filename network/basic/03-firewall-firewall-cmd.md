# firewall-cmd
## firewalld的开启和关闭
- 开启firewalld服务: systemctl enable firewalld 
- 打开firewalld服务: systemctl start firewalld

## firewalld的port
开启端口
```text
firewall-cmd --zone=public --add-port=80/tcp --permanent
```
删除端口
```text
firewall-cmd --zone=public --remove-port=80/tcp --permanent
```


## firewalld的zone
zone 是firewalld 的单位。默认使用public zone
- 查看所有的zone : firewall-cmd --get-zones
- 查看默认的zone : firewall-cmd --get-default-zone
### zone说明：
- drop (丢弃) 任何接收到的网络数据都被丢弃，没有任何回复，公有发送出去的网络连接
- block（限制）任何接收的网络连接都被IPV4 的icmp-host-prohibited信息和IPV6的icmp6-adm-prohibited信息所拒绝
- public (公共) 在公共区域内使用，不能相信网络内的其它计算机不会对你的计算机造成危害，只接收经过选取的连接
- external （外部）特别是为路由器启用了伪装功能的外部网。你不能信任来自网络的其它计算，不能信任它们不会对你的计算机造成危害，只能接收经过选择的连接。
- dmz (非军事区) 用于你的非军事区的电脑 ，此区域内可公开访问，可以有限的进入你的内部网络，仅接收经过选择的连接。
- work (工作) 可以基本信任网络内的其它计算机不会危害你的计算机，仅接收经过选择的连接。
- home (家庭) 用于家庭网络，可以基本信任网络内的其它计算机不会危害你的计算机，仅接收经过选择的连接。
- internal （内部）用于内部网络，可以基本信任网络内的其它计算机不会危害你的计算机，仅接收经过选择的连接
- trusted (信任) 可接收所有的网络连接

### 对zone的操作
- 设定默认zone : firewall-cmd --set-default-zone=work
- 查看网卡加入zone ： firewall-cmd --get-zone-of-interface=eno1
- 设置网卡的zone ：firewall-cmd --zone=public --add-interface=eno1 
- 更改网卡的zone : firewall-cmd --zone=dmz --change-interface=eno1 //更改eno1网卡的zone为dmz
- 删除网卡的zone :firewall-cmd --zone=dmz --remove-interface=lo //删除lo网卡的zone
- 查看系统所有的网卡所在的zone : firewall-cmd --get-active-zones

## firewall的service
- 查看所有的service ： firewall-cmd --get-services (最后面可加可不加s这个字母)
- 查看service : firewall-cmd --list-services
- 查看当前zone下有哪些service : firewall-cmd --zone=work --list-services
- 把http增加到work zone 下 ： firewall-cmd --zone=work --add-service=http
- 把ftp增加到work zone 下 ： firewall-cmd --zone=work --add-service=ftp

### 以上的操作只是在内存里面，我们把它们写到配制文件里面去：
- zone的配制文件模版： /usr/lib/firewalld/zones
- services配制模版： usr/lib/firewalld/services
- 更改配制文件之后，会在/etc/firewalld/zones目录下生成配置文件 ：firewall-cmd --zone=work --add-service=http --permanent
- 查看： cat /etc/firewalld/zones/work.xml

### 实践
需求：ftp服务自定议端口1121，需要在work zone 下面放行ftp
- cp /usr/lib/firewalld/services/ftp.xml /etc/firewalld/services  //复制ftp.xml模版 到/etc/firewalld/services/目录下；
- vi /etc/firewalld/services/ftp.xml //把21端口改成1121端口
- cp /usr/lib/firewalld/zones/work.xml /etc/firewalld/zones/  //复制work.xml模版到/etc/firewalld/zones/目录下；
- vi /ec/firewalld/zones/work.xml  //编辑work.xml文件 ，增加一行 <service name=“ftp”/>
- firewall-cmd --reload //重新加载firewall
- firewall-cmd --zone=work --list-service // 查看work下面的services



## 常用命令

- firewall-cmd --zone=public --add-port=4430/udp --permanent
- firewall-cmd --zone=public --add-port=4433/udp --permanent
- firewall-cmd --zone=trusted --add-port=4433/udp --permanent
- firewall-cmd --zone=public --add-port=8007/tcp --permanent
- firewall-cmd --zone=public --add-port=8100/tcp  
- firewall-cmd --zone=public --remove-port=8100/tcp
- firewall-cmd --zone=public --remove-port=8100/tcp --permanent
- firewall-cmd --zone=public --remove-port=8007/tcp --permanent
- firewall-cmd --zone=public --remove-port=8007/tcp --permanent
- firewall-cmd --zone=trusted --add-interface=xdspa4433 --permanent
