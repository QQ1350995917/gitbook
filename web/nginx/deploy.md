# nginx 部署
## CentOS
### 环境准备
- 一. gcc 安装
安装 nginx 需要先将官网下载的源码进行编译，编译依赖 gcc 环境，如果没有 gcc 环境，则需要安装：
```bash
yum install gcc-c++
```
- 二. PCRE pcre-devel 安装
PCRE(Perl Compatible Regular Expressions) 是一个Perl库，包括 perl 兼容的正则表达式库。nginx 的 http 模块使用 pcre 来解析正则表达式，所以需要在 linux 上安装 pcre 库，pcre-devel 是使用 pcre 开发的一个二次开发库。nginx也需要此库。命令：
```bash
yum install -y pcre pcre-devel
```
- 三. zlib 安装
zlib 库提供了很多种压缩和解压缩的方式， nginx 使用 zlib 对 http 包的内容进行 gzip ，所以需要在 Centos 上安装 zlib 库。
```bash
yum install -y zlib zlib-devel
```
- 四. OpenSSL 安装
OpenSSL 是一个强大的安全套接字层密码库，囊括主要的密码算法、常用的密钥和证书封装管理功能及 SSL 协议，并提供丰富的应用程序供测试或其它目的使用。
nginx 不仅支持 http 协议，还支持 https（即在ssl协议上传输http），所以需要在 Centos 安装 OpenSSL 库。
```bash
yum install -y openssl openssl-devel
```
### 编译部署
- 1.直接下载.tar.gz安装包，地址：https://nginx.org/en/download.html

- 2.使用wget命令下载（推荐）。确保系统已经安装了wget，如果没有安装，执行 yum install wget 安装。
```bash
wget -c https://nginx.org/download/nginx-1.18.0.tar.gz
```  
- 3.解压
```text
tar -zxvf nginx-1.18.0.tar.gz
cd nginx-1.18.0
```

- 4.执行配置命令

  - 1.使用默认配置
    ```bash
    ./configure
    ```
  - 自定义配置（不推荐）
    ```text
    ./configure \
    --prefix=/usr/local/nginx \
    --conf-path=/usr/local/nginx/conf/nginx.conf \
    --pid-path=/usr/local/nginx/conf/nginx.pid \
    --lock-path=/var/lock/nginx.lock \
    --error-log-path=/var/log/nginx/error.log \
    --http-log-path=/var/log/nginx/access.log \
    --with-http_gzip_static_module \
    --http-client-body-temp-path=/var/temp/nginx/client \
    --http-proxy-temp-path=/var/temp/nginx/proxy \
    --http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
    --http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
    --http-scgi-temp-path=/var/temp/nginx/scgi    
    ```  
    
    > 注：将临时文件目录指定为/var/temp/nginx，需要在/var下创建temp及nginx目录
- 编译安装
```text
make
make install
```

- 查找安装路径：
```bash
whereis nginx
```  
### 启动停止刷新退出
启动、停止nginx
```bash
cd /usr/local/nginx/sbin/
./nginx 
./nginx -s stop
./nginx -s quit
./nginx -s reload

```

### 设置开机启动
```text
编辑 vi /etc/rc.local
新增 /usr/local/nginx/sbin/nginx
赋权 chmod 755 rc.local
```

## 自定义配置返向代理

### nginx.conf
> /usr/local/nginx/conf/nginx.conf

```text
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /home/nginx/logs/access.log  main;
    error_log  /home/nginx/logs/error.log  warn;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

    client_max_body_size 1024M;
    client_body_buffer_size 128k;

    #防止504 gateway time out
    fastcgi_connect_timeout 1200s;
    fastcgi_send_timeout 1200s;
    fastcgi_read_timeout 1200s;

    include vhost/*.conf;
}
``` 
### vhost
> vhost目录（vhost目录要和上一步中最后一行的路径相匹配）

### 新建应用conf
```text
  server {
        listen       5566 default_server;
        server_name  localhost;

	location / {
        root   E:\workspace\github\static-web-user\dist;
	    try_files $uri $uri/ /index.html;
	    add_header  Cache-Control no-store;
    }
        
	   
    location ^~ /article/api/ {
        proxy_pass http://localhost:11221;
        proxy_redirect      default;
        proxy_cookie_path   /inspect/ /;
        proxy_set_header    X-Real-IP $remote_addr;
        proxy_set_header    X-Forwarded-Host $remote_addr;
        proxy_set_header    X-Forwarded-Server $remote_addr;
        proxy_set_header    X-Forwarded-For  $remote_addr;
		client_max_body_size 50m; 
	    client_body_buffer_size 256k; 
	    proxy_connect_timeout 1; 
	    proxy_send_timeout 30; 
	    proxy_read_timeout 60; 
	    proxy_buffer_size 256k; 
	    proxy_buffers 4 256k; 
	    proxy_busy_buffers_size 256k; 
	    proxy_temp_file_write_size 256k; 
	    proxy_next_upstream error timeout invalid_header http_500 http_503 http_404; 
	    proxy_max_temp_file_size 128m; 
       }
    }
```
### 刷新
```bash
usr/local/nginx/sbin/nginx -s reload
```

## 参考资料
https://www.cnblogs.com/liujuncm5/p/6713784.html
