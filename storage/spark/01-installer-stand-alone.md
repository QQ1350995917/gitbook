# spark stand alone

## ununtu18

- [spark下载地址](https://mirrors.tuna.tsinghua.edu.cn/apache/spark)  

### 部署 
```bash
cd /usr/local/bin/
mkdir spark
cd spark
wget 
tar -zxvf 
ln -s xxx spark

cd spark
cd conf
cp spark-env.sh.template spark-env.sh
vim spark-env.sh

# 添加scala的环境变量
export SCALA_HOME=/usr/local/Cellar/scala/2.12.6
export SPARK_MASTER_IP=localhost
export SPARK_WORKER_MEMORY=4G

# 添加hadoop的环境变量
export SPARK_DIST_CLASSPATH=$(hadoop classpath)
```
```

### 验证
```bash
cd /usr/local/bin/spark/spark
spark-shell
```



