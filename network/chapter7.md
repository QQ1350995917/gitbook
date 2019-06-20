# DNS

DNS\( Domain Name System\)是“域名系统”的英文缩写.

　DNS（domain name system）域名系统或者（domain named system）区域名称服务，分为正向与反向域名解析，适用C/S,端口路53/udp，53/tcp，属于应用层协议；

DNS是应用层协议，client端（一般指浏览器）构建DNS查询请求，依次被传输层，网络层，数据链路层等封装传送到达DNS服务器端，最终client端接收到DNS响应消息

DNS主要基于UDP运输层协议，这里解释下为什么使用UDP（User Datagram Protocol）这样的无连接的，尽最大能力交付的不可靠数据连接，而不是使用TCP\(Transmission Control Protocol 传输控制协议\)这样的面向连接的可靠数据连接。

  


 一次UDP名字服务器交换可以短到两个包：一个查询包、一个响应包。一次TCP交换则至少包含9个包：三次握手初始化TCP会话、一个查询包、一个响应包以及四次分手的包交换。

 考虑到效率原因，TCP连接的开销大得，故采用UDP作为DNS的运输层协议，这也将导致只有13个根域名服务器的结果。

  


只会在UDP报文中表明有截断的时候使用TCP查询。



参考资料

* [https://www.cnblogs.com/zhangxingeng/p/9970733.html](https://www.cnblogs.com/zhangxingeng/p/9970733.html)

* [https://www.zhihu.com/question/22587247](https://www.zhihu.com/question/22587247)

* [https://www.cnblogs.com/gopark/p/8430916.html](https://www.cnblogs.com/gopark/p/8430916.html)



