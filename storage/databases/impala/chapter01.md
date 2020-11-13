hive 日期函数

## 增加月份
- add_months(timestamp date, int months)
- add_months(timestamp date, bigint months)
- Return type: timestamp
- usage:add_months(now(),1)

## 增加日期
- adddate(timestamp startdate, int days), 
- adddate(timestamp startdate, bigint days)
- Return type: timestamp
- usage:adddate(now(),1)
## 当前时间戳
- current_timestamp()和now()等价
## 日期相减
- datediff(string enddate, string startdate)
- Return type: int
- usage:datediff("2018-08-05", "2018-08-03")
## 得到天，得到月份
- day(string date)
- Return type: int
- usage: day("2018-08-05")
## 得到星期英文
- dayname(string date) 
- Return type: string
- usage:dayname("2018-08-05") Sunday
## 得到这一天是这周的第几天
- dayofweek(string date)  1 (Sunday) to 7 (Saturday).
- Return type: int
- usage:dayofweek("2018-08-06")
## 加天数
- days_add(timestamp startdate, int days) 
- Return type: timestamp
- usage:days_add(now(),2)
## 减天数
- days_sub(timestamp startdate, int days)
- Return type: timestamp
- usage:days_sub(now(), 2)
## 格式化日期
- from_unixtime(bigint unixtime[, string format])
- Return type: string
## 注意参数
- usage:from_unixtime(1392394861,"yyyy-MM-dd");
## 得到小时
- hour(string date)
- Return type: int
- usage:hour("2018-08-06 12:32:54")
## 增加小时
- hours_add(timestamp date, int hours)
- Return type: timestamp
- usage:hours_add(now(),2)
## 减少小时
- hours_sub(timestamp date, int hours)
- Return type: timestamp
- usage:hours_sub(now(),2)
## 得到分钟
- minute(string date)
- Return type: int
- usage:minute(now())
## 增加分钟
- minutes_add(timestamp date, int minutes)
- Return type: timestamp
- usage:minutes_add(now(),2)
## 减少分钟
- minutes_sub(timestamp date, int minutes)
- Return type: timestamp
- usage:minutes_sub(now(),2)
## 得到月份
- month(string date)
- Return type: int
- usage:month("2018-08-06 12:32:54")
## 月份相加
- months_add(timestamp date, int months)
- Return type: timestamp
- usage:months_add(now(),3)
## 减月份
- months_sub(timestamp date, int months)
- Return type: timestamp
- months_sub(now(),3)
## 得到秒
- second(string date)
- Return type: int
## 秒加
- seconds_add(timestamp date, int seconds)
- Return type: timestamp
## 秒减
- seconds_sub(timestamp date, int seconds)
- Return type: timestamp
## 得到日期
- to_date(now())
## 得到1970到今秒
- unix_timestamp(), 
- unix_timestamp(string datetime),
- unix_timestamp(string datetime, string format),
- unix_timestamp(timestamp datetime)
- Return type: bigint
## 得到这周是这年的多少周
- weekofyear(string date)
- Return type: int
- usage:weekofyear("2018-08-06 12:32:54")
## 周加
- weeks_add(timestamp date, int weeks)
- Return type: timestamp
- usage:weeks_add("2018-08-06 12:32:54", 1)
## 周减
- weeks_sub(timestamp date, int weeks)
- Return type: timestamp
- usage:weeks_sub("2018-08-06 12:32:54", 1)
## 得到年
- year(string date)
- Return type: int
## 年加
- years_add(timestamp date, int years)
- Return type: timestamp
## 年减
- years_sub(timestamp date, int years)
- Return type: timestamp
