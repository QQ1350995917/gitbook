# 生成SSL证书和公私钥

## WIN10下安装openSSL
1. [openSSL官网](https://www.openssl.org/)
1. [二进制文件下载地址](https://slproweb.com/products/Win32OpenSSL.html)，可下载EXE或者MSI的全量包

## 生成根秘钥、证书以及CA文件
### 生成根秘钥
```text
openssl.exe genrsa -out root.key 2048
```
### 生成根证书
```text
openssl req -x509 -new -nodes -key root.key -sha256 -days 3650 -out root.crt

Country Name (2 letter code) []:CN                            // 输入国家代码，如：CN
State or Province Name (full name) []:BeiJing                 // 输入省份，如：BeiJing
Locality Name (eg, city) []:BeiJing                           // 输入城市，如：BeiJing
Organization Name (eg, company) []:XD                         // 输入组织机构，如：XD
Organizational Unit Name (eg, section) []:SDP                 // 输入机构部门，如：SDP
Common Name (eg, fully qualified host name) []:192.168.10.10  // 输入域名，IP地址或者域名，不准确无法生效  
Email Address []:xxx@xxx.xxx                                  // 邮箱地址
```
### 生成CA所需ca-key.pem和ca-cert.pem文件
```text
openssl genrsa -out ca-key.pem 2048
openssl req -out ca-cert.pem -x509 -new -key ca-key.pem
```

## 生成并签发server秘钥和证书
### 生成server秘钥和server证书
```text
openssl req -new -sha256 -nodes -out server.csr -newkey rsa:2048 -keyout server.key

You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:CN
State or Province Name (full name) [Some-State]:BeiJing
Locality Name (eg, city) []:BeiJing
Organization Name (eg, company) [Internet Widgits Pty Ltd]:XD
Organizational Unit Name (eg, section) []:SDP
Common Name (e.g. server FQDN or YOUR name) []:192.168.10.201
Email Address []:xxx@xxx.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
```
### 使用根证书和根秘钥签发server证书
```text
openssl x509 -req -in server.csr -CA root.crt -CAkey root.key -CAcreateserial -out server.crt -days 3650 -sha256
```
或者
```text
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 3650 -sha256 -extfile v3.ext
```
v3.ext文件如下
```text
authorityKeyIdentifier = keyid, issuer
basicConstraints = CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost   //使用是ip 则使用IP.1=xxxx
```
## 生成并签发client秘钥和证书
### 生成client秘钥和client证书
```text
openssl req -new -sha256 -nodes -out client.csr -newkey rsa:2048 -keyout client.key
# 填写相关信息
```
### 使用根证书和根秘钥签发client证书
```text
openssl x509 -req -in client.csr -CA root.crt -CAkey root.key -CAcreateserial -out client.crt -days 3650 -sha256
# 填写相关信息
```
#### 客户端证书转成pfx格式（双向验证时使用）
```text
# 客户端证书转成pfx格式，生成后，直接双击文件安装到浏览器
openssl pkcs12 -export -clcerts -in client.crt -inkey client.key -out client.pfx
# 注意：这个时候填写的密码，是客户安装证书的时候，需要填写的密码。密码可以为空
```

#### 注意
1. 生成证书时候Common Name处填写域名/ip需要特别注意，和要访问的IP保持一致

## 参考
http://www.manongjc.com/detail/24-bvvdfncxohopdju.html
https://www.cnblogs.com/qq917937712/p/8876513.html