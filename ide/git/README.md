# git协同 

## 本地配置

1. 查看git配置信息
   > $ git config --list

1. 查看git用户名、密码、邮箱的配置
   > $ git config user.name
   
   > $ git config user.password
   
   > $ git config user.email
 
 
1. 设置git用户名、密码、邮箱的配置（工作空间）
   > $ git config user.name "DingPengwei"
   
   > $ git config user.password "123456"
   
   > $ git config user.email "www.dingpengwei@foxmail.com"
   
1. 设置git用户名、密码、邮箱的配置（全局配置）
   > $ git config --global user.name "DingPengwei"
   
   > $ git config --global user.password "123456"
   
   > $ git config --global user.email "www.dingpengwei@foxmail.com"

## 协同模型

![](image/gitlab-flow.jpg)

- master -- 线上正在运行，可随时发版，受保护的分支。
- develop -- 测试环境或者开发环境正在运行，可随时发版，受保护的分支。
- feature -- 逻辑分支，本地开发自测，需从develop pull最新代码，开发完成后提交（或许伴随着变基以解决冲突），提交后发起MR到develop。
- hotfix -- 逻辑分支，线上bug修复，需从master pull最新代码，开发完成后提交（或许伴随着变基以解决冲突），提交后发起MR到master和develop。
- tag -- 可选项
