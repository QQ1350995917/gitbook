##
- 广义上是一种安全理念和范式，指引安全规划、安全架构、策略设计
- 狭义上一种安全解决方案  
- 零信任旨在缩小资源周围的隐性信任区域，最好是缩小到零。
- 一个零信任的安全架构在公开的互联网上运行时与在防火墙后面运行时一样安全。
- 零信任是一种解决方案，不是单一的产品，需要整合多种能力，以满足用户场景化需求。

典型的供应商方案  
- 充分利用企业现有的优势能力，基于零信任的原则和框架，复用现有的成熟技术，叠加零信任特有的能力，融合成为面向特定企业和场景的解决方案。

## 边界
- 执行安全控制的载体，软件VS硬件，逻辑VS物理

## 零信任解读
以兼顾安全和体验的方式，实现资源的可信任访问
- 身份
- 细粒度
- 动态控制
- 持续认证
- 消除隐形信任

## 历史
- 1994~2004：起源于美国国防部，业界探讨无界化安全架构和方案，2004年出现NAC就架构
- 2010年ForresterResearch第一次提出”零信任“概念
- 2013年CSA云安全联盟成立SDP工作小组，次年发布SDP标准规范1.0
- 2011~2017年BeyondCorp在Google内部部署，成为第一个成功的零信任实践
- 2017年O`Reilly出版《Zero Trust Network》
- 2018年Forrester提出ZTX架构，从未隔离拓展到可视化，自动化编排
- 2018年、2019年，美国三家（OKTA，Zscaler，CloudFlare）零信任企业完成IPO，成功商业化
- 2018年Gartner将零信任列为Top10的安全技术
- 2019年、2020年美国商务部下属的NIST连续发布《零信任架构标准（草案）》的第一版和第二版

## 标准
- BeyondCorp：访问客体必须是受控设备
- ZTN
- ZTX
## 流派
- SDP(微边界)
- 微隔离  
- 增强型IAM：客体访问主体必须进行身份网关


## 理念
- 用不信任，始终验证（Never Trust，Always Verify）
- 技术不可知

## SASE
调研学习一下

## 芯盾
- Gartner:CARTA(ECG)
- Gartner:ZTNA：一种是客户端发起，一种是服务端发起（类BeyondCorp)

## SDP
协议的设计目标是为了IPV4和IPV6提供客户操作的安全控制
- SDPController
- AH（AHAccept host ）
- IH（Initial host）

## SDP自我保护策略
- 隐身
- 访问控制
- 单包授权（SPA)

## SDP部署模型
- ClientToGateway:网关模型，已实现
- ServerToServer：微隔离模型，已实现
- ClientToServer：微网关