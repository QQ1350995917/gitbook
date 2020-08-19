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