操作系统的预处理和优化：
yum -y install gcc glibc libaio

防火墙和selinux关闭：
(1)、关闭SElinux

                                         setenforce 0

                                          修改/etc/selinux/config

 

                                          vim /etc/selinux/config

                                          SELINUX=disabled

(2)、关闭防火墙

 

                                          systemctl stop firewalld

                                          systemctl disable firewalld

sysctl.conf 优化：
cat>>/etc/sysctl.conf <<EOF

fs.aio-max-nr = 1048576

fs.file-max = 681574400

kernel.shmmax = 137438953472

kernel.shmmni = 4096

kernel.sem = 250 32000 100 200

net.ipv4.ip_local_port_range = 9000 65000

net.core.rmem_default = 262144

net.core.rmem_max = 4194304

net.core.wmem_default = 262144

net.core.wmem_max = 1048586

EOF

 

limit 优化：
cat>>/etc/security/limits.conf <<EOF

mysql soft nproc 65536

mysql hard nproc 65536

mysql soft nofile 65536

mysql hard nofile 65536

EOF

 

cat>>/etc/pam.d/login <<EOF

session required /lib/security/pam_limits.so

session required pam_limits.so

EOF

 

 

cat>>/etc/profile<<EOF

 

if [ $USER = "mysql" ]; then

ulimit -u 16384 -n 65536

fi

EOF

 

 

source /etc/profile




https://www.cnblogs.com/fangxuanlang/p/10383544.html
