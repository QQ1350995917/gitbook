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