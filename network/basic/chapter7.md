# DNS

一道面试题：在浏览器中输入一个域名，如www.google.com，点击回车后，发生了什么？

## DNS是什么
DNS是域名系统(Domain Name System)或者域名服务(Domain Name Service)的英文缩写。一个字典，一个电话簿。

https://root-servers.org/

## DNS用途是什么


- 第一步：输入域名后，电脑首先看自己有没有对应域名的缓存，有缓存就直接用缓存里的ip访问。
    ```text
    在windows系统中通过ipconfig管理dns缓存，查看：ipconfig /displaydns；清空：ipconfig /flushdns、
    在MacOS系统中通过lookupd管理dns缓存，清空：lookupd -flushcache 
    在Linux各个发行版有区别：nscd DNS cache；dnsmasq dns cache；BIND server dns cache
    ```
- 第二步，如果缓存中没有，则去查询hosts文件
- 第三步，如果hosts文件里也没找到想解析的域名，则将域名发往自己配置的dns服务器，也叫本地dns服务器
    ```text
    在windows命令行ipconfig /all可以看到自己的本地dns服务器
    
    ```
- 第四步，如果本地dns服务器有相应域名的记录，则返回记录。
  电脑的dns服务器一般是各大运营商如电信联通提供的，或者像180.76.76.76，223.5.5.5，4个114等知名dns服务商提供的，本身缓存了大量的常见域名的ip，所以常见的网站，都是有记录的。不需要找根服务器。

- 第五步，如果电脑自己的服务器没有记录，会去找根服务器。根服务器全球只要13台，回去找其中之一。

找了根服务器后，根服务器会根据请求的域名，返回对应的“顶级域名服务器”，如：

如果请求的域名是http://xxx.com，则返回负责com域的服务器

如果是http://xxx.cn，则发给负责cn域的服务器

如果是http://xxx.ca，则发给负责ca域的服务器

https://zhuanlan.zhihu.com/p/150417003





分为正向与反向域名解析，适用C/S,端口路53/udp，53/tcp，属于应用层协议；

DNS是应用层协议，client端（一般指浏览器）构建DNS查询请求，依次被传输层，网络层，数据链路层等封装传送到达DNS服务器端，最终client端接收到DNS响应消息

DNS主要基于UDP运输层协议，这里解释下为什么使用UDP（User Datagram Protocol）这样的无连接的，尽最大能力交付的不可靠数据连接，而不是使用TCP\(Transmission Control Protocol 传输控制协议\)这样的面向连接的可靠数据连接。

一次UDP名字服务器交换可以短到两个包：一个查询包、一个响应包。一次TCP交换则至少包含9个包：三次握手初始化TCP会话、一个查询包、一个响应包以及四次分手的包交换。

考虑到效率原因，TCP连接的开销大得，故采用UDP作为DNS的运输层协议，这也将导致只有13个根域名服务器的结果。

只会在UDP报文中表明有截断的时候使用TCP查询。

参考资料

* [https://www.cnblogs.com/zhangxingeng/p/9970733.html](https://www.cnblogs.com/zhangxingeng/p/9970733.html)

* [https://www.zhihu.com/question/22587247](https://www.zhihu.com/question/22587247)

* [https://www.cnblogs.com/gopark/p/8430916.html](https://www.cnblogs.com/gopark/p/8430916.html)

* [https://miek.nl/2013/november/10/why-13-dns-root-servers/](https://miek.nl/2013/november/10/why-13-dns-root-servers/)



