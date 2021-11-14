# 部署apache
## 在win10部署apache
由于官网不提供二进制部署包，可以在其他分发平台下载，如[apache](https://www.apachelounge.com/download/#google_vignette)，下载后解压。

### 部署
1. 记录解压目录如D:\workspace\container\apache2.4.51
1. 修改conf/下的httpd.conf的37行Define SRVROOT的值为D:\workspace\container\apache2.4.51

以管理员身份运行cmd
```text
cd bin
httpd -k install
```

使用快捷键WIN+R，输入services.msc打开服务列表，找到apache的服务并启动，浏览器访问localhost即可显示页面内容It works!。

### [生成站点证书](../ssl/README.md)

### 配置https
#### 设置OPENSSL_CONFIG配置
```text
cd bin
set OPENSSL_CONF=..\conf\openssl.cnf
```
#### 1. conf/httpd.conf
```text
#LoadModule ssl_module modules/mod_ssl.so （去掉前面的#号）
#Include conf/extra/httpd-ssl.conf （去掉前面的#号）
#LoadModule proxy_module modules/mod_proxy.so （去掉前面的#号）
#LoadModule proxy_http_module modules/mod_proxy_http.so （去掉前面的#号）
#LoadModule socache_shmcb_module modules/mod_socache_shmcb.so（去掉前面的#号）
```
#### 设置证书秘钥路径
```text
# mkdir conf/key
# 在conf/key中放入openSSL生成的证书和秘钥
# 设置证书和秘钥的路径
```
#### 1. conf/extra/httpd-ssl.conf
```text
# ServerName和证书中的IP保持一致
Listen 443
<VirtualHost _default_:443>

    DocumentRoot "D:/www"
    ServerName  localhost # 和签名中的IP保持一致
 
    SSLEngine on
    SSLCertificateFile "${SRVROOT}/conf/key/server.crt"
    SSLCertificateKeyFile "${SRVROOT}/conf/key/server.key"

    # 如果是双向认证，开启下面3行
    # SSLCACertificateFile "${SRVROOT}/conf/key/root.crt"
    # SSLVerifyClient require
    # SSLVerifyDepth  1
</VirtualHost>    
```
#### 1. conf/extra/httpd-vhosts.conf
```text
<VirtualHost *:80>  
    DocumentRoot "${SRVROOT}/www"
    ServerName localhost
</VirtualHost>
<VirtualHost *:443>  
    DocumentRoot "${SRVROOT}/www"
    ServerName example.com
    ErrorLog "${SRVROOT}/www/app/logs/error.log"
    TransferLog "${SRVROOT}/www/app/logs/error.log"
 
    SSLEngine on
    SSLCertificateFile "${SRVROOT}/conf/key/app/root.crt"
    SSLCertificateKeyFile "${SRVROOT}/conf/key/app/server.key"    
    
    # 如果是双向认证，开启下面3行
    # SSLCACertificateFile "${SRVROOT}/conf/key/app/root.crt"
    # SSLVerifyClient require
    # SSLVerifyDepth  1
</VirtualHost>
```

### 问题排查
如果加入以上、配置造成apache无法通过系统启动，则在bin/目录下执行httpd.exe进行手动启动，手动启动验证配置文件，可以显示错误信息。
