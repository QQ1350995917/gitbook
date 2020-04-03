# 
## Ubuntu
- 下载中文版openoffice
```bash
wget https://downloads.sourceforge.net/project/openofficeorg.mirror/4.1.6/binaries/zh-CN/Apache_OpenOffice_4.1.6_Linux_x86-64_install-deb_zh-CN.tar.gz
```
- 解压并安装
```bash
tar -xzvf Apache_OpenOffice_4.1.6_Linux_x86-64_install-deb_zh-CN.tar.gz
cd zh-CN/
cd DEBS/
dpkg -i *.deb

apt install libxext6 
apt-get install libxt6
apt-get install libxrender
```
- 启动
```bash
/opt/openoffice4/program/soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard &
```
