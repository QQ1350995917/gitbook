### 新增
```
//插入单条 
db.friend.insertOne({name:"wukong"，sex:"man"});
// 插入多条
db.friend.insertMany([
{name:"wukong",sex:"man"},{name:"diaocan",sex:"woman",age:18,birthday:new Date("1995-11-02")},{name:"zixiao",sex:"woman"}
]);

 db.friend.insert([
        {_id:1,name:"wokong",sex:"man",age:1},
        {_id:2,name:"diaocan",sex:"women",birthday:new Date("1988-11-    11")}
  ])
```
### 删除

删除：
```
// 基于查找删除
db.emp.deleteOne({_id:1101})
// 删除整个集合
db.project.drop()
// 删除库
db.dropDatabase()
```

### 修改



### 修改
```
#设置值
db.emp.update({_id:1101} ,{ $set:{salary:10300}  })
#自增
db.emp.update({_id:1101} ,{ $inc:{salary:200}})

#基于条件 更新多条数据
# 只会更新第一条 
db.emp.update({"dep":"客服部"},{$inc:{salary:100}})
# 更新所有 匹配的条件 
db.emp.updateMany({"dep":"客服部"},{$inc:{salary:100}})
```


### **3、数据的修改与删除**
修改
```
#设置值
db.emp.update({_id:1101} ,{ $set:{salary:10300}  })
#自增
db.emp.update({_id:1101} ,{ $inc:{salary:200}})

#基于条件 更新多条数据
# 只会更新第一条 
db.emp.update({"dep":"客服部"},{$inc:{salary:100}})
# 更新所有 匹配的条件 
db.emp.updateMany({"dep":"客服部"},{$inc:{salary:100}})

```

### 查找
1. 基于条件的基础查询
2. $and、$or、$in、$gt、$gte、$lt、$lte 运算符
3. 基于 sort skip limit 方法实现排序与分页
4. 嵌套查询
5. 数组查询
6. 数组嵌套查询
```
#基于ID查找
db.emp.find({_id:1101})
#基于属性查找
db.emp.find({"name":"鲁班"})
# && 运算 与大于 运算
db.emp.find({"job":"讲师","salary":{$gt:8000}})
# in 运算
 db.emp.find({"job":{$in:["讲师","客服部"]}})
# or 运算
db.emp.find({$or:[{job:"讲师"  },{job:"客服部"}] })

```
### 排序与分页：
//  sort skip limit
```
db.emp.find().sort({dep:1,salary:-1}).skip(5).limit(2)
```
嵌套查询：

```
# 错误示例：无结果
db.student.find({grade:{redis:87,dubbo:90 });
#错误示例：无结果 
db.student.find({grade:{redis:87,dubbo:90,zookeper:85} })

# 基于复合属性查找 时必须包含其所有的值 并且顺序一至
db.student.find({grade:{redis:87,zookeper:85,dubbo:90} })

#基于复合属性当中的指定值 查找。注：名称必须用双引号
db.student.find({"grade.redis":87});

db.student.find({"grade.redis":{"$gt":80}});
```

数组查询：
```
db.subject.insertMany([
{_id:"001",name:"陈霸天",subjects:["redis","zookeper","dubbo"]},
{_id:"002",name:"张明明",subjects:["redis","Java","mySql"]},
{_id:"003",name:"肖炎炎",subjects:["mySql","zookeper","bootstrap"]},
{_id:"004",name:"李鬼才",subjects:["Java","dubbo","Java"]},
])
```

```
#无结果
db.subject.find({subjects:["redis","zookeper"]})
#无结果
db.subject.find({subjects:["zookeper","redis","dubbo"]})
# 与嵌套查询一样，必须是所有的值 并且顺序一至
db.subject.find({subjects:["redis","zookeper","dubbo"]})

# $all 匹配数组中包含该两项的值。注：顺序不作要求
db.subject.find({subjects:{"$all": ["redis","zookeper"]}})
注：
# 简化数组查询
db.subject.find({subjects:"redis"})
# 简化数组查询 ，匹配数组中存在任意一值。与$all相对应
db.subject.find({subjects:{$in: ["redis","zookeper"]}})
```


数组嵌套查询：
```
#基础查询 ，必须查询全部，且顺序一至
db.subject2.find({subjects:{name:"redis",hour:12} })
#指定查询第一个数组 课时大于12
db.subject2.find({"subjects.0.hour":{$gt:12}})
#查询任科目 课时大于12
db.subject2.find({"subjects.hour":{$gt:12}})
# $elemMatch 元素匹配，指定属性满足，且不要求顺序一至
db.subject2.find({subjects:{$elemMatch:{name:"redis",hour:12}}})

# 数组中任意元素匹配 不限定在同一个对象当中
db.subject2.find({"subjects.name":"mysql","subjects.hour":120})
```
### 索引

### **4、全文索引**
索引的创建
```
db.project.createIndex({name:"text",description:"text"})
```
基于索引分词进行查询
```
db.project.find({$text:{$search:"java jquery"}})
```
   基于索引 短语
```
db.project.find({$text:{$search:"\"Apache ZooKeeper\""}})
```
过滤指定单词
```
db.project.find({$text:{$search:"java apache -阿里"}})
```
查看执行计划
```
db.project.find({$text:{$search:"java -阿里"}}).explain("executionStats")
```

### 聚合
1.	pipeline 聚合（占用内存限制不超过20%）
2.	mapRedurce 聚合
3.	在聚合中使用索引

#### pipeline 聚合
pipeline相关运算符：
-	$match ：匹配过滤聚合的数据
- $project：返回需要聚合的字段
-	$group：统计聚合数据
示例：
# $match 与 $project使用
db.emp.aggregate(
{$match:{"dep":{$eq:"客服部"}}},
{$project:{name:1,dep:1,salary:1}}
);

# $group 与 $sum 使用
db.emp.aggregate(
 {$project:{dep:1,salary:1}},
 {$group:{"_id":"$dep",total:{$sum:"$salary"}}}
 );

#  低于4000 忽略
db.emp.aggregate(
 {$match:{salary:{$gt:4000}}},
 {$project:{dep:1,salary:1}},
 {$group:{"_id":"$dep",total:{$sum:"$salary"}}} 
 );

# 基于多个字段 进行组合group  部门+职位进行统计
db.emp.aggregate(
 {$project:{dep:1,job:1,salary:1}},
 {$group:{"_id":{"dep":"$dep","job":"$job"},total:{$sum:"$salary"}}} 
 );



二次过滤
db.emp.aggregate(
 {$project:{dep:1,job:1,salary:1}},
 {$group:{"_id":{"dep":"$dep","job":"$job"},total:{$sum:"$salary"}}}，
 {$match:{"$total":{$gt:10000}}} 
 );


#### mapRedurce 聚合
mapRedurce 说明：
    为什么需要 MapReduce？
     (1) 海量数据在单机上处理因为硬件资源限制，无法胜任
     (2) 而一旦将单机版程序扩展到集群来分布式运行，将极大增加程序的复杂度和开发难度
     (3) 引入 MapReduce 框架后，开发人员可以将绝大部分工作集中在业务逻辑的开发上，而将 分布式计算中的复杂性交由框架来处理

mongodb中mapRedurce的使用流程
1.	创建Map函数，
2.	创建Redurce函数
3.	将map、Redurce 函数添加至集合中，并返回新的结果集
4.	查询新的结果集
示例操作
// 创建map 对象 
var map1=function (){
emit(this.job,1);
 }
// 创建reduce 对象 
 var reduce1=function(job,count){
 return Array.sum(count);
 }
 // 执行mapReduce 任务 并将结果放到新的集合 result 当中
db.emp.mapReduce(map1,reduce1,{out:"result"})
// 查询新的集合
db.result.find()


# 使用复合对象作为key
 var map2=function (){
emit({"job":this.job,"dep":this.dep},1);
 }
 
 var reduce2=function(key,values){
return values.length;
 }
 
db.emp.mapReduce(map2,reduce2,{out:"result2"}).find()

mapRedurce的原理
在map函数中使用emit函数添加指定的 key 与Value ，相同的key 将会发给Redurce进行聚合操作，所以Redurce函数中第二个参数 就是 所有集的数组。return 的显示就是聚合要显示的值。

#### 在聚合中使用索引
通过$Math内 可以包合对$text 的运算
示例：
db.project.aggregate(
{$match:{$text:{$search:"apache"}}},
{$project:{"name":1,"price":1}},
{$group:{_id:"$name",price:{$sum:"$price"}}}
)
关于索引
除了全文索引之外，还有单键索引。即整个字段的值作为索引。单键索引用值1和-1表示，分别代表正序和降序索引。

示例：
de 创建单键索引
db.emp.createIndex({"dep":1})

查看基于索引的执行计划
db.emp.find({"dep":"客服部"}).explain()

除了单键索引外还可以创建联合索引如下：
db.emp.createIndex({"dep":1,"job":-1})
查看 复合索引的执行计划
db.emp.find({"dep":"ddd"}).explain()

查看索引在排序当中的使用
db.emp.find().sort({"job":-1,"dep":1}).explain()
