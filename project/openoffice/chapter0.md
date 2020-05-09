# 
## centos
### libreoffice
安装，镜像地址： http://mirrors.ustc.edu.cn/tdf/libreoffice/stable
```
wget http://mirrors.ustc.edu.cn/tdf/libreoffice/stable/6.3.5/rpm/x86_64/LibreOffice_6.3.5_Linux_x86-64_rpm.tar.gz
wget http://mirrors.ustc.edu.cn/tdf/libreoffice/stable/6.3.5/rpm/x86_64/LibreOffice_6.3.5_Linux_x86-64_rpm_langpack_zh-CN.tar.gz
wget http://mirrors.ustc.edu.cn/tdf/libreoffice/stable/6.3.5/rpm/x86_64/LibreOffice_6.3.5_Linux_x86-64_rpm_sdk.tar.gz

tar -xzvf LibreOffice_6.3.5_Linux_x86-64_rpm.tar.gz
tar -xzvf LibreOffice_6.3.5_Linux_x86-64_rpm_langpack_zh-CN.tar.gz
tar -zxvf LibreOffice_6.3.5_Linux_x86-64_rpm_sdk.tar.gz

yum install LibreOffice_6.3.5.2_Linux_x86-64_rpm/RPMS/*.rpm
yum install LibreOffice_6.3.5.2_Linux_x86-64_rpm_langpack_zh-CN/RPMS/*.rpm        
yum install LibreOffice_6.3.5.2_Linux_x86-64_rpm_sdk/RPMS/*.rpm

```

启动，自启动设置
```
# 启动命令
libreoffice6.3 --headless --accept="socket,host=127.0.0.1,port=8100;urp;" --nofirststartwizard

soffice -accept=socket,host=localhost,port=2083;urp;StarOffice.ServiceManager

#修改启动脚本
vim /etc/rc.d/rc.local
#末尾添加
libreoffice6.3 --headless --accept="socket,host=127.0.0.1,port=8100;urp;" --nofirststartwizard
#脚本授权
chmod +x /etc/rc.d/rc.local
```

检查
```
ps -ef|grep libreoffice
```

转换，成功后生成同名pdf
```
libreoffice6.3 --headless --invisible --convert-to pdf x.docx
libreoffice6.3 --headless --invisible --convert-to pdf <待转换的word路径> --outdir <生成的pdf文件名>
```


## centos中转换中文乱码
- 查看操作系统是否中文字体
```
yum -y install cups-libs fontconfig

fc-list
```

- 添加中文字体
```bash
mkdir /usr/share/fonts/chinese
chmod -R 755 /usr/share/fonts/chinese
```

中文字体文件在windows系统中c盘下的Windows/Fonts(C:\Windows\Fonts)，把相关的字体放入到chinese目录中

- 配置字体
```bash
vim /etc/fonts/fonts.conf

<!-- Font directory list -->

        <dir>/usr/share/fonts</dir>
        <dir>/usr/share/X11/fonts/Type1</dir>
        <dir>/usr/share/X11/fonts/TTF</dir>
        <dir>/usr/local/share/fonts</dir>
        <dir>/usr/local/share/fonts/chinese</dir>

<!--
```

- 刷新字体缓存
```bash
fc-cache
fc-list
```







 

