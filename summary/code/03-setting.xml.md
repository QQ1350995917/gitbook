# 客户端设置

## 文件路径

\~/home/{user}/.m2/setting.xml

### 样例

```markup
<?xml version="1.0" encoding="UTF-8" ?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository>E:\maven</localRepository>
  <pluginGroups>
  </pluginGroups>
  <proxies>
  </proxies>
  <servers>
    <server>
      <id>serverId1</id>
      <username>username1</username>
      <password>password1</password>
    </server>
    <server>
      <id>serverId2</id>
      <username>username2</username>
      <password>password2</password>
    </server>
  </servers>
  <mirrors>
    <mirror>
      <id>private</id>
      <mirrorOf>public</mirrorOf>
      <name>nexus</name>
      <url>http://192.168.50.50:8282/repository/public/</url>
    </mirror>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>nexus</id>
      <repositories>
        <repository>
          <id>private</id>
          <name>private</name>
          <url>http://192.168.50.50:8282/repository/public/</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>

        <repository>
          <id>alimaven</id>
          <url>https://maven.aliyun.com/repository/public</url>
        </repository>
        <repository>
          <id>sonatype</id>
          <url>https://oss.sonatype.org/content/repositories/releases/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>private</id>
          <name>private</name>
          <url>http://192.168.50.50:8282/repository/public/</url>
          <releases>
              <enabled>true</enabled>
          </releases>
          <snapshots>
              <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>        
  </profiles>
  <activeProfiles>
      <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
```

```markup
<?xml version="1.0" encoding="UTF-8" ?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository>E:\maven</localRepository>
    <pluginGroups>
    </pluginGroups>
    <proxies>
    </proxies>
    <servers>
        <server>
            <id>tomcat</id>
            <username>admin</username>
            <password>admin123</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>deployment</username>
            <password>deployment123</password>
        </server>
    </servers>
    <mirrors>

        <mirror>
            <id>nexus-private-central</id>
            <name>nexus-private-central</name>
            <mirrorOf>central</mirrorOf>
            <url>http://192.168.205.11:8282/repository/central/</url>
        </mirror>

        <mirror>
            <id>nexus-private-public</id>
            <name>nexus-private-public</name>
            <mirrorOf>public</mirrorOf>
            <url>http://192.168.205.11:8282/repository/public/</url>
        </mirror>

        <mirror>
            <id>nexus-private-release</id>
            <name>nexus-private-release</name>
            <mirrorOf>release</mirrorOf>
            <url>http://192.168.205.11:8282/repository/releases/</url>
        </mirror>

        <mirror>
            <id>nexus-private-snapshots</id>
            <name>nexus-private-snapshots</name>
            <mirrorOf>snapshots</mirrorOf>
            <url>http://192.168.205.11:8282/repository/snapshots/</url>
        </mirror>

        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
    <profiles>
        <profile>
            <id>nexus-private</id>
            <repositories>
                <repository>
                    <id>nexus-central</id>
                    <name>nexus-central</name>
                    <url>http://192.168.205.11:8282/repository/central/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>nexus-public</id>
                    <name>nexus-public</name>
                    <url>http://192.168.205.11:8282/repository/public/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>nexus-releases</id>
                    <name>nexus-releases</name>
                    <url>http://192.168.205.11:8282/repository/releases/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>nexus-snapshots</id>
                    <name>nexus-snapshots</name>
                    <url>http://192.168.205.11:8282/repository/snapshots/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository> 
            </repositories>
        </profile>    
        <profile>
            <id>aliyunmaven</id>
            <repositories>         
                <repository>
                    <id>alimaven</id>
                    <url>https://maven.aliyun.com/repository/public</url>
                </repository>
            </repositories>
        </profile>
        <profile>
            <id>sonatype</id>
            <repositories>         
                <repository>
                    <id>sonatype</id>
                    <url>https://oss.sonatype.org/content/repositories/releases/</url>
                </repository>
            </repositories>
        </profile>    
    </profiles>
    <activeProfiles>
        <activeProfile>aliyunmaven</activeProfile>
        <activeProfile>nexus-private</activeProfile>
        <activeProfile>sonatype</activeProfile>
    </activeProfiles>
</settings>
```

* localRepository：本地仓库的目录。默认是用户目录下面的.m2/repository目录。
* interactiveMode：表示是否使用交互模式，默认是true；如果设为false，那么当Maven需要用户进行输入的时候，它会使用一个默认值。
* offline：表示是否离线，默认是false。这个属性表示在Maven进行项目编译和部署等操作时是否允许Maven进行联网来下载所需要的信息。
* mirrors：定义一系列的远程仓库的镜像，用于缓解远程仓库的压力。
* profiles：用于指定一系列的profile。 可以配置构件和插件的远程仓库，并且可以通过来配置多个profile，最后通过来指定当前处于活跃状态(即有效的)的profile配置是哪一个。
* activeProfiles：指定当前正在活跃的profile。
* servers：表示当需要连接到一个远程服务器的时候需要使用到的验证方式。

## [setting诸元素详解](https://github.com/QQ1350995917/gitbook/tree/1301eb5173842b2e1ed6ae91795462f32063b906/ide/mvn/04-setting.md)
