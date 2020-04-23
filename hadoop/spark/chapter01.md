```bash
mkdir -p /usr/local/bin/spark
cd /usr/local/bin/spark
wget https://mirrors.tuna.tsinghua.edu.cn/apache/spark/spark-2.4.5/spark-2.4.5-bin-without-hadoop.tgz
tar xzvf spark-2.4.5-bin-without-hadoop.tgz
ln -s spark-2.4.5-bin-without-hadoop spark

```
环境配置
```bash
vim /etc/profile

export SPARK_HOME=/usr/local/bin/spark/spark
export PATH=$PATH:$SPARK_HOME/bin
export PATH=$PATH:$SPARK_HOME/sbin

source /etc/profile
```

http://dblab.xmu.edu.cn/blog/1709-2/
