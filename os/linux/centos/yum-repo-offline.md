## centos 离线yum源
### 下载yum镜像源文件
- https://www.centos.org/download/ 或者 https://mirrors.huaweicloud.com/centos/
- 根据使用系统版本选择对应版本的系统DVD ISO
- 将下载的ISO文件上传到服务器
```bash
scp CentOS-7-x86_64-DVD-1810.iso -P 22 user@ip:/tmp/CentOS-7-x86_64-DVD-1810.iso
```
- 镜像挂载
```bash
mkdir /tmp/yum.repo
mount /tmp/CentOS-7-x86_64-DVD-1810.iso /tmp/yum.repo/
```
- 将挂载盘内的文件复制到本地新建目录中
```bash
mkdir /usr/local/bin/yum.repo
cp -r /tmp/yum.repo/* /usr/local/bin/yum.repo
```
- 将yum源仓库里的repo文件进行备份
```bash
mkdir /etc/yum.repos.d/bak-20201116
cp -r /etc/yum.repos.d/* /etc/yum.repos.d/bak-20201116
```
- 重新编辑CentOS-Media.repo文件
```bash
vim /etc/yum.repos.d/CentOS-Media.repo

# CentOS-Media.repo
#
#  This repo can be used with mounted DVD media, verify the mount point for
#  CentOS-7.  You can use this repo and yum to install items directly off the
#  DVD ISO that we release.
#
# To use this repo, put in your DVD and use it with the other repos too:
#  yum --enablerepo=c7-media [command]
#  
# or for ONLY the media repo, do this:
#
#  yum --disablerepo=\* --enablerepo=c7-media [command]

[c7-media]
name=CentOS-$releasever - Media
baseurl=file:///media/CentOS/
        file:///media/cdrom/
        file:///media/cdrecorder/
gpgcheck=1
enabled=0
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7

设置以下内容
baseurl=file:///home/admin/centos_iso
```
- 显示所有已经安装和可以安装的程序包
```bash
yum list
```
- 卸载镜像
```bash
umount /tmp/yum.repo
```



