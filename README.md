### 1.背景：bettergi 一条龙能够完成原神自动化任务运行。但是无法完成后之后关机。
### 2.分析：但是提供了webhook接口,参见官方文档 https://bettergi.com/dev/webhook.html
### 3.解决：用java写一个服务来接收 执行回调结果
### 4.使用：
##### 1.
修改yml
```
server:
  port: 8080
  servlet:
    context-path: /bgi
log:
  file:
    path: C:\\Users\\Administrator\\Desktop\\auto\\ #当前jar应用所在目录  【修改】
    suffix: .log 
    timeStamp: yyyyMMdd
    startScript: C:\\Program Files\\BetterGI\\BetterGI.exe startOneDragon # bettergi 启动一条龙  【修改】
    endScript: shutdown -s -t 60 #执行完后操作，60s后关机
    closeSecond: 3 # 3s后关闭应用
    event: DRAGON_END #关注事件 一条龙
```
##### 2.
http://localhost:8080/bgi/shutdown 填入bettergi webhook端点
##### 3.
先关闭bettergi,再启动应用：java -jar better_gi_server-1.0.0.jar
#### 5.结构：
```
├─java
│  └─com
│      └─yll
│          └─better_gi_server
│              └─bgi
│                  ├─config
│                  ├─controller
│                  │  ├─enums
│                  │  └─req
│                  └─init
└─resources
    ├─static
    └─templates
```
