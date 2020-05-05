# spark环境的搭建
## Mac下spark环境的搭建
下载：http://spark.apache.org/downloads.html

解压配置环境变量

```bash
cd spark/conf
cp spark-env.sh.template spark-env.sh
vim spark-env.sh

# 添加scala的环境变量
export SCALA_HOME=/usr/local/Cellar/scala/2.12.6
export SPARK_MASTER_IP=localhost
export SPARK_WORKER_MEMORY=4G

# 添加hadoop的环境变量
export SPARK_DIST_CLASSPATH=$(hadoop classpath)
```

```bash
spark-shell
```
