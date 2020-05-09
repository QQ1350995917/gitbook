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

