# 时钟同步
查看是否已经安装ntp
```
 rpm -qa | grep ntp
```
若只有ntpdate而未见ntp，则需删除原有ntpdate。如：
```text
ntpdate-4.2.6p5-22.el7_0.x86_64
fontpackages-filesystem-1.44-8.el7.noarch
```

删除已安装的ntp
```text
yum erase ntp ntpdate
```

重新安装ntp

在线安装
```text
 yum -y install ntp
```

离线安装
```text
yum -y install ntp --downloadonly --downloaddir /root/ntp
```

```text
rpm -ivh *.rpm --force --nodeps
```

```text
vi /etc/ntp.conf
```

```text
driftfile /var/lib/ntp/drift
restrict default nomodify notrap nopeer noquery
restrict 127.0.0.1
restrict ::1

server ntp.aliyun.com

includefile /etc/ntp/crypto/pw
keys /etc/ntp/keys
disable monitor
```

```text
systemctl restart ntpd.service
systemctl enable ntpd.service
ntpstat
ntpdate -u ntp.aliyun.com 
```