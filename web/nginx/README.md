# NGINX
## [单机部署](chapter1.md)
## [集群部署](chapter2.md)

## [Nginx](https://www.jianshu.com/p/e90050dc89b6)

docker run --name nginx -p 13500:80 -v /home/nginx/docker/nginx.conf:/etc/nginx/nginx.conf -v /home/nginx/docker/logs:/var/log/nginx -v /home/nginx/docker/html:/usr/share/nginx/html -v /home/nginx/docker/conf:/etc/nginx/conf.d -d nginx 
