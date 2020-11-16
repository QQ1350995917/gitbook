## 1.什么是索引以及为什么使用索引
### 1.1 单列索引
创建三个单列索引：

1. 查询条件为 userid and mobile and billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' AND mobile='13281899972' AND billMonth='2018-04'
    
    1
    
    我们发现三个单列索引只有 userid 有效（位置为查询条件第一个），其他两个都没有用上

2. 查询条件为 mobile and billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE mobile='13281899972' AND billMonth='2018-04'
    
    1
    
    我们发现此处两个查询条件只有 mobile 有效（位置也为查询条件第一个），后面的无效

3. 查询条件为 userid or mobile

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' OR mobile='13281899972' 
    
    1
    
    这次把 and 换成 or，发现两个查询条件都用上索引了！

### 1.2 复合索引（联合索引）
注：Mysql版本为 5.7.20

创建测试表(表记录数为63188)：

```mysql
CREATE TABLE `t_mobilesms_11` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`userId` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户id，创建任务时的userid',
`mobile` varchar(24) NOT NULL DEFAULT '' COMMENT '手机号码',
`billMonth` varchar(32) DEFAULT NULL COMMENT '账单月',
`time` varchar(32) DEFAULT NULL COMMENT '收/发短信时间',
`peerNumber` varchar(64) NOT NULL COMMENT '对方号码',
`location` varchar(64) DEFAULT NULL COMMENT '通信地(自己的)',
`sendType` varchar(16) DEFAULT NULL COMMENT 'SEND-发送; RECEIVE-收取',
`msgType` varchar(8) DEFAULT NULL COMMENT 'SMS-短信; MSS-彩信',
`serviceName` varchar(256) DEFAULT NULL COMMENT '业务名称. e.g. 点对点(网内)',
`fee` int(11) DEFAULT NULL COMMENT '通信费(单位分)',
`createTime` datetime DEFAULT NULL COMMENT '创建时间',
`lastModifyTime` datetime DEFAULT NULL COMMENT '最后修改时间',
PRIMARY KEY (`id`),
KEY `联合索引` (`userId`,`mobile`,`billMonth`)
) ENGINE=InnoDB AUTO_INCREMENT=71185 DEFAULT CHARSET=utf8 COMMENT='手机短信详情'

```
我们为userId, mobile, billMonth三个字段添加上联合索引！

我们选择 explain 查看执行计划来观察索引利用情况：

1. 查询条件为 userid

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222'
    
    1

    可以通过key看到，联合索引有效

2. 查询条件为 mobile

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE mobile='13281899972'
    
    1
    
    可以看到联合索引无效

3. 查询条件为 billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE billMonth='2018-04'
    
    1
    
    联合索引无效

4. 查询条件为 userid and mobile

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' AND mobile='13281899972'
    
    1
    
    联合索引有效

5. 查询条件为 mobile and userid

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE mobile='13281899972' AND userid='2222' 
    
    1
    
    在4的基础上调换了查询条件的顺序，发现联合索引依旧有效

6. 查询条件为 userid or mobile

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' OR mobile='13281899972'
    
    1
    
    把 and 换成 or，发现联合所索引无效！

7. 查询条件为 userid and billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' AND billMonth='2018-04'
    
    1
    
    这两个条件分别位于联合索引位置的第一和第三，测试联合索引依旧有效！

8. 查询条件为 mobile and billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE mobile='13281899972' AND billMonth='2018-04'
    
    1
    
    这两个条件分别位于联合索引位置的第二和第三，发现联合索引无效！

9. 查询条件为 userid and mobile and billMonth

    EXPLAIN SELECT * FROM `t_mobilesms_11` WHERE userid='2222' AND mobile='13281899972' AND billMonth='2018-04'
    
    1
    
    所有条件一起查询，联合索引有效！（当然，这才是最正统的用法啊！）
    
###1.3 为什么需要复合索引
通过上述可得出结论
通俗理解： 
利用索引中的附加列，您可以缩小搜索的范围，但使用一个具有两列的索引 不同于使用两个单独的索引。复合索引的结构与电话簿类似，人名由姓和名构成，电话簿首先按姓氏对进行排序，然后按名字对有相同姓氏的人进行排序。如果您知道姓，电话簿将非常有用；如果您知道姓和名，电话簿则更为有用，但如果您只知道名不姓，电话簿将没有用处。

所以说创建复合索引时，应该仔细考虑列的顺序。对索引中的所有列执行搜索或仅对前几列执行搜索时，复合索引非常有用；仅对后面的任意列执行搜索时，复合索引则没有用处。

重点：

多个单列索引在多条件查询时只会生效第一个索引！所以多条件联合查询时最好建联合索引！

最左前缀原则：

顾名思义是最左优先，以最左边的为起点任何连续的索引都能匹配上， 
注：如果第一个字段是范围查询需要单独建一个索引 
注：在创建联合索引时，要根据业务需求，where子句中使用最频繁的一列放在最左边。这样的话扩展性较好，比如 userid 经常需要作为查询条件，而 mobile 不常常用，则需要把 userid 放在联合索引的第一位置，即最左边

同时存在联合索引和单列索引（字段有重复的），这个时候查询mysql会怎么用索引呢？

这个涉及到mysql本身的查询优化器策略了，当一个表有多条索引可走时, Mysql 根据查询语句的成本来选择走哪条索引；

有人说where查询是按照从左到右的顺序，所以筛选力度大的条件尽量放前面。网上百度过，很多都是这种说法，但是据我研究，mysql执行优化器会对其进行优化，当不考虑索引时，where条件顺序对效率没有影响，真正有影响的是是否用到了索引！

联合索引本质：

当创建(a,b,c)联合索引时，相当于创建了(a)单列索引，(a,b)联合索引以及(a,b,c)联合索引 
想要索引生效的话,只能使用 a和a,b和a,b,c三种组合；当然，我们上面测试过，a,c组合也可以，但实际上只用到了a的索引，c并没有用到！ 
注：这个可以结合上边的 通俗理解 来思考！

其他知识点：

1. 需要加索引的字段，要在where条件中 
2. 数据量少的字段不需要加索引；因为建索引有一定开销，如果数据量小则没必要建索引（速度反而慢） 
3. 如果where条件中是OR关系，加索引不起作用 
4. 联合索引比对每个列分别建索引更有优势，因为索引建立得越多就越占磁盘空间，在更新数据的时候速度会更慢。另外建立多列索引时，顺序也是需要注意的，应该将严格的索引放在前面，这样筛选的力度会更大，效率更高。

## 2.索引的数据结构
### 2.1 二叉树
### 2.2 红黑树
### 2.3 HASH表
### 2.4 B树
### 2.5 B+树
### 2.6 FullText
FullText索引又称全文索引，是一种记录关键字与对应文档关系的倒排索引。

## 3.索引分类
+ 唯一索引：就是索引列中的值必须是唯一的，但是允许出现空值。这种索引一般用来保证数据的唯一性，比如保存账户信息的表，每个账户的id必须保证唯一，如果重复插入相同的账户id时会MySQL返回异常。
+ 主键索引：是一种特殊的唯一索引，但是它不允许出现空值。
+ 普通索引：与唯一索引不同，它允许索引列中存在相同的值。例如学生的成绩表，各个学科的分数是允许重复的，就可以使用普通索引。
+ 联合索引：就是由多个列共同组成的索引。一个表中含有多个单列的索引并不是联合索引，联合索引是对多个列字段按顺序共同组成一个索引。应用联合索引时需要注意最左原则，就是Where查询条件中的字段必须与索引字段从左到右进行匹配。比如，一个用户信息表，用姓名和年龄组成了联合索引，如果查询条件是姓名等于张三，那么满足最左原则；如果查询条件是年龄大于20，由于索引中最左的字段是姓名不是年龄，所以不能使用这个索引。
+ 全文索引：前面提到了，MyISAM引擎中实现了这个索引，在5.6版本后InnoDB引擎也支持了全文索引，并且在5.7.6版本后支持了中文索引。全文索引只能在CHAR,VARCHAR,TEXT类型字段上使用，底层使用倒排索引实现。要注意对于大数据量的表，生成全文索引会非常消耗时间也非常消耗磁盘空间。

## 3.MyIASAM索引

## 4.InnoDB索引

## 5.全文索引
MySQL支持对文本进行全文检索，全文检索可以类似搜索引擎的功能，相比较模糊匹配更加灵活高效且更快。MySQL5.7之后也支持对中文的全文检索，这里描述如何启用MySQL的中文全文检索。

首先，MySQL启用全文检索要对字段加全文检索的索引，注意，一个表只能建立一个全文检索字段，如需要检索多个字段，需要将多个字段一起建立索引，单独建立多个索引是无效的。所以建立方法如下：


```mysql
ALTER TABLE `localgo`.`entity` ADD FULLTEXT INDEX `entity_info` (`entity_name`, `entity_introduction`) WITH PARSER ngram;
```

MySQL5.7支持对中文进行全文检索，其自带了内部的切词系统，默认切词系统的字段是4，但中文一般是两个字组成一个单词，因此需要改变。首先看一下内部切词的长度

```mysql
SHOW VARIABLES LIKE 'ft_min_word_len';
SHOW VARIABLES LIKE 'ft%';
```
发现结果都是4，下面更改数据库配置，在[client]下加上ft_min_word_len = 2，在[mysqld]下加上另两行。如下所示，注意，一般情况下这两个配置下都含有其他设置，这里省去了，只需要在这两个配置的末尾加上如下内容即可，不要删除之前的配置。

```mysql
[mysqld]
ft_min_word_len = 2
ngram_token_size=2
[client]
ft_min_word_len = 2
```
最后，用如下语句即可支持MySQL对中文进行全文检索了（注意检索词需要放在星号之间，支持空格或者分号作为关键词分隔符）。

```mysql
SELECT * FROM localgo.entity WHERE MATCH(`entity_name`,`entity_introduction`) AGAINST('*程序*' IN BOOLEAN MODE);
```

## 索引操作
MySQL中可以使用alter table这个SQL语句来为表中的字段添加索引。
使用alter table语句来为表中的字段添加索引的基本语法是：
ALTER TABLE <表名> ADD INDEX (<字段>);

查看表的索引
```mysql
show index from `table_name`;
```
添加PRIMARY KEY（主键索引）
```mysql
ALTER TABLE `table_name` ADD PRIMARY KEY ( `column_name` )
```
添加UNIQUE(唯一索引) 
```mysql
ALTER TABLE `table_name` ADD UNIQUE ( `column_name` )
```
添加INDEX(普通索引) 
```mysql
ALTER TABLE `table_name` ADD INDEX `index_name`( `column_name` )
```
添加FULLTEXT(全文索引) 
```mysql
ALTER TABLE `table_name` ADD FULLTEXT `index_name`( `column_name` )
```
添加多列索引 
```mysql
ALTER TABLE `table_name` ADD INDEX `index_name` ( `column1`, `column2`, `column3` )
```
删除索引
```mysql
alter table `table_name` drop index `index_name`;/*mdl_tag_use_ix是上表查出的索引名，key_name*/
```

## 参考资料
https://www.cnblogs.com/wherein/p/7525687.html