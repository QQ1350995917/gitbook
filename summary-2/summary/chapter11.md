# 网络工具

## NC

```
[v1.10]
connect to somewhere: netcat [-options] hostname port[s] [ports] …
listen for inbound: netcat -l -p port [-options] [hostname] [port]
options:
-g gateway source-routing hop point[s], up to 8
-G num source-routing pointer: 4, 8, 12, …
-h this cruft
-i secs delay interval for lines sent, ports scanned
-l listen mode, for inbound connects
-n numeric-only IP addresses, no DNS
-o file hex dump of traffic
-p port local port number
-r randomize local and remote ports
-s addr local source address
-t answer TELNET negotiation
-u UDP mode
-v verbose [use twice to be more verbose]
-w secs timeout for connects and final net reads
-z zero-I/O mode [used for scanning]
port numbers can be individual or ranges: lo-hi [inclusive]

基本格式：nc [-options] hostname port[s] [ports] …
nc -l -p port [options] [hostname] [port]
-d 后台模式
-e prog 程序重定向，一旦连接，就执行 [危险!!]
-g gateway source-routing hop point[s], up to 8
-G num source-routing pointer: 4, 8, 12, …
-h 帮助信息
-i secs 延时的间隔
-l 监听模式，用于入站连接
-L 连接关闭后,仍然继续监听
-n 指定数字的IP地址，不能用hostname
-o file 记录16进制的传输
-p port 本地端口号
-r 随机本地及远程端口
-s addr 本地源地址
-t 使用TELNET交互方式
-u UDP模式
-v 详细输出–用两个-v可得到更详细的内容
-w secs timeout的时间
-z 将输入输出关掉–用于扫描时
端口的表示方法可写为M-N的范围格式。
```

UDP可达测试：

* 报文接收端：nc -uvlp 13500
* 报文发送端：nc -u IP 13500 （不能使用域名）

## TCPDUMP

UDP报文抓包

* tcpdump -i eth1 -nn -vv -X udp port 13500

UDP丢包分析 netstat -s -u netstat -i eth1 udp
