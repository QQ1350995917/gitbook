## Ubuntu下ElasticSearch安装
参考 http://blog.itpub.net/31077337/viewspace-2212771/
- ElasticSearch
```bash
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.3.2.deb
dpkg -i elasticsearch-6.3.2.deb
vim /etc/elasticsearch/elasticsearch.yml
# network.host: 192.168.0.1 # 取消注释并将IP设置为服务器地址

systemctl enable elasticsearch.service
systemctl start elasticsearch.service
```

验证

http://<your-ip>:9200/_cat/health?v

- 安装Kibana
```bash
wget https://artifacts.elastic.co/downloads/kibana/kibana-6.3.2-amd64.deb
dpkg -i kibana-6.3.2-amd64.deb

vim /etc/kibana/kibana.yml
server.host: "192.168.1.15"
elasticsearch.url: "http://192.168.1.15:9200"

# 配置JVM的VM堆大小
sysctl -w vm.max_map_count=262144


systemctl enable kibana.service
systemctl start kibana.service
```

验证

http://<your-ip>:5601

- 安装Logstash
```bash
wget https://artifacts.elastic.co/downloads/logstash/logstash-6.3.2.deb
dpkg -i logstash-6.3.2.deb
vim /etc/logstash/logstash.yml

http.host: "192.168.1.15"

systemctl enable logstash.service
systemctl start logstash.service

```




## Mac下ElasticSearch安装

- 首先需要安装jdk8
```bash
brew cask install homebrew/cask-versions/java8
```
- 安装ES
```bash
brew search elasticsearch
brew install elasticsearch
```
- 运行es
```bash
brew services start elasticsearch
brew info elasticsearch
```
- 访问
```url
http://localhost:9200

{
    name: "90LNUen",
    cluster_name: "elasticsearch_shengyulong",
    cluster_uuid: "vMztQUmQS7-_aJ1b37URYg",
    version: {
        number: "6.6.2",
        build_flavor: "oss",
        build_type: "tar",
        build_hash: "3bd3e59",
        build_date: "2019-03-06T15:16:26.864148Z",
        build_snapshot: false,
        lucene_version: "7.6.0",
        minimum_wire_compatibility_version: "5.6.0",
        minimum_index_compatibility_version: "5.0.0",
    },
    tagline: "You Know, for Search",
}
```

- 安装Kibana
Kibana是ES的一个配套工具，可以让用户在网页中与ES进行交互
```bash
brew install kibana
```

- 启动Kibana
```bash
brew services start kibana
```

- 本地浏览器访问
```bash
http://localhost:5601
```

- 下载插件并安装

插件主页：https://github.com/mobz/elasticsearch-head

```bash
brew install node
git clone git://github.com/mobz/elasticsearch-head.git
cd elasticsearch-head
npm install
```
安装完成后在elasticsearch-head/node_modules目录下会出现grunt文件。
如果没有grunt二进制程序，需要执行
```bash
cd elasticsearch-head
npm install grunt --save
```

修改服务器监听地址,修改elasticsearch-head下Gruntfile.js文件，默认监听在127.0.0.1下9200端口

```json
connect: {
        server: {
            options: {
                hostname: '*',
                port: 9100,
                base: '.',
                keepalive: true
            }
        }
    } 
```

修改连接地址
```bash
cd elasticsearch-head/_site
vim app.js
```
```xml
this.base_uri = this.config.base_uri || this.prefs.get("app-base_uri") || "http://localhost:9200";
```

在cd elasticsearch-head目录下运行

```bash
grunt server
```

输出：
```babsh
>> Local Npm module "grunt-contrib-jasmine" not found. Is it installed?
 
Running "connect:server" (connect) task
Waiting forever...
Started connect web server on http://localhost:9100
```
浏览器访问
http://localhost:9100
访问后会发现 集群健康值：显示“未连接”

解决方案：
```bash
vim $ES_HOME$/config/elasticsearch.yml
```


由于我采用brew安装的ES所以$ES_HOME$/config为/usr/local/etc/elasticsearch/
增加如下字段
```bash
http.cors.enabled: true
http.cors.allow-origin: "*"
```

重启es，并刷新head页面，发现已经可以连接上。

使用brew install安装es后的一些安装路径：
```bash
elasticsearch:  /usr/local/Cellar/elasticsearch/6.2.4
Data:    /usr/local/var/elasticsearch/elasticsearch_xuchen/
Logs:    /usr/local/var/log/elasticsearch/elasticsearch_xuchen.log
Plugins: /usr/local/opt/elasticsearch/libexec/plugins/
Config:  /usr/local/etc/elasticsearch/
plugin script: /usr/local/opt/elasticsearch/libexec/bin/elasticsearch-plugin
```

## 启动命令 
./filebeat -e -c filebeat.yml 


## 配置文件

filebeat.inputs:

- type: log
  enabled: true
  paths:
    - /Users/pwd/.log/account/log_debug.log
  fields:
    application: account
    level: debug
- type: log
  enabled: true
  paths:
    - /Users/pwd/.log/account/log_error.log
  fields:
    application: account
    level: error
- type: log
  enabled: true
  paths:
    - /Users/pwd/.log/account/log_info.log
  fields:
    application: account
    level: info      
- type: log
  enabled: true
  paths:
    - /Users/pwd/.log/account/log_warn.log
  fields:
    application: account
    level: warn     
output.kafka:
  enabled: true
  hosts: ["192.168.31.18:9092"]
  topic: '%{[fields][application]}-%{[fields][level]}' 
  
## 启动命令
bin/logstash -f config/logstash.conf

## 配置文件

input{
	kafka{
		bootstrap_servers => ["localhost:9092"]
		group_id => "logstash" 
		auto_offset_reset => "earliest"
		consumer_threads => 4 
		decorate_events => true
		topics => ["account-debug","account-info","account-error","account-warn"]
	}
}

output {
	elasticsearch { 
    index => "%{[@metadata][kafka][topic]}-%{+YYYY.MM.dd}"
  	hosts => ["localhost:9200"]
  	document_type=> "doc"
  }
}