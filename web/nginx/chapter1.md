/etc/nginx/nginx.conf

```
# For more information on configuration, see:
#   * Official English Documentation: http://nginx.org/en/docs/
#   * Official Russian Documentation: http://nginx.org/ru/docs/

#user nginx;
user root;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

# Load dynamic modules. See /usr/share/nginx/README.dynamic.
include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 2048;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # Load modular configuration files from the /etc/nginx/conf.d directory.
    # See http://nginx.org/en/docs/ngx_core_module.html#include
    # for more information.
    include /etc/nginx/conf.d/*.conf;

    server {
        listen       80 default_server;
        listen       [::]:80 default_server;
        server_name  _;
        root         /usr/share/nginx/html;

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;

        location / {
        }

        error_page 404 /404.html;
            location = /40x.html {
        }

        error_page 500 502 503 504 /50x.html;
            location = /50x.html {
        }
    }

# Settings for a TLS enabled server.
#
#    server {
#        listen       443 ssl http2 default_server;
#        listen       [::]:443 ssl http2 default_server;
#        server_name  _;
#        root         /usr/share/nginx/html;
#
#        ssl_certificate "/etc/pki/nginx/server.crt";
#        ssl_certificate_key "/etc/pki/nginx/private/server.key";
#        ssl_session_cache shared:SSL:1m;
#        ssl_session_timeout  10m;
#        ssl_ciphers HIGH:!aNULL:!MD5;
#        ssl_prefer_server_ciphers on;
#
#        # Load configuration files for the default server block.
#        include /etc/nginx/default.d/*.conf;
#
#        location / {
#        }
#
#        error_page 404 /404.html;
#            location = /40x.html {
#        }
#
#        error_page 500 502 503 504 /50x.html;
#            location = /50x.html {
#        }
#    }
     include vhost/*.conf;
}

```
nginx.conf同级建立vhost目录，建立conf文件，如user.conf
```
server {
  listen       8888 default_server;
  server_name  172.17.3.164;

	location / {
    root   /home/xxx/xxx/dist;
	  try_files $uri $uri/ /index.html;
	  add_header  Cache-Control no-store;
  }
       
  location ^~ /api/ {
    proxy_pass http://172.17.3.164:8181/;
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
       
  location ^~ /article/ {
    proxy_pass http://172.17.3.164:20000/;
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
       
  location ^~ /access/ {
    proxy_pass http://172.17.3.164:20010/;
    proxy_redirect      default;
    proxy_cookie_path   /inspect/ /;
    proxy_set_header    X-Real-IP $remote_addr;
    proxy_set_header    X-Forwarded-Host $remote_addr;
    proxy_set_header    X-Forwarded-Server $remote_addr;
    proxy_set_header    X-Forwarded-For  $remote_addr;
  }  

  location ^~ /ui/ {
		proxy_pass http://127.0.0.1:20000/;
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
