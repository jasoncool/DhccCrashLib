---

## 异常崩溃怎么办？

关于异常崩溃是每个App都要面对的，平时开发还好，在调试状态下遇到的问题，可以通过LogCat打印的异常日志信息进行分析处理，但是一旦App上线后，大量用户安装了你的应用，每个用户的手机大小、传感器、SDK版本都不尽相同，可能你在测试机上跑的稳稳的应用，到了客户手机上就会出现一些莫名其妙的异常，如果只是一些内存泄露的问题可能还好，最起码不会瞬间崩溃，但是如果遇到一些可以导致手机崩溃Bug的话，你让出问题的用户来复现Bug是不可能的，所以，全局异常捕获就显得很重要了，而DhccCrashLib就是一个全局异常捕获的组件。

## DhccCrashLib怎么用？

<!--more-->

[Github地址](https://github.com/jasoncool/DhccCrashLib)

使用方法还是比较简单的，首先在项目的根目录下的build.gradle中加入Jcenter仓库:

```xml
 repositories {
        jcenter()
    }
```

然后在你的项目的build.gradle中添加依赖:

```xml
implementation 'com.dhcc.crashlib:CrashLib:1.0.3'
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.github.zhaokaiqiang.klog:library:1.6.0'
implementation "com.sun.mail:android-mail:1.6.0"
```

这四个依赖都需要加，因为担心版本冲突，所以我在组件中使用的依赖方式是compileOnly,那么你在你的项目中如果有引用除了CrashLib外的这三个依赖的话，就可以换成你自己的版本号即可。

使用方式  在你项目的自定义Application中：

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initCrash();
    }

    /**
     * 初始化崩溃采集服务
     */
    private void initCrash() {
        EmailConfigBean emailConfigBean = new EmailConfigBean("你的发送邮箱", "你的接收邮箱", "你的发送邮箱密码");
        Configuration configuration=Configuration.getInstance()
                //你的邮件配置实例
                .setEmailConfig(emailConfigBean)
                //是否通过邮件发送异常
                .setSendWithEmail(true)
                //是否通过邮件发送异常并将本地存储的异常已附件的形式发送
                .setSendEmailWithFile(true)
                //异常服务器的API
                .setCrashServerUrl("http://111.222.333.444:9999/api/crashs")
                //是否给服务器发送异常信息
                .setSendWithNet(true)
                //异常的描述信息
                .setCrashDescription("测试异常~~")
                //捕获异常后退出App的等待时间 毫秒
                .setExitWaitTime(5000)
                ;
        LogCenter.getLogCenter("进程名", configuration)
                //可以自定义异常 只要实现ICollector 并传入网络提交时所需要的key即可
                .strategy(new TestCollectInfo(), "网络属性的Key")
                .init(this);
    }
}
```

就这么简单,首先先把你的发送邮箱和接收邮箱的相关信息都配置到EmailConfigBean中去，然后再调用LogCenter初始化相关参数即可，不过这里有一个细节需要讲一下，注意看TestCollectInfo()这个方法:

```java
public class TestCollectInfo implements ICollector {
    @Override
    public String collectInfo(Context context) {
        return "这是一条测试采集异常信息";
    }
}
```

由于每个项目不同，可能需要采集的异常信息外的其他一些手机信息都不尽相同，我这里在源码中只设计了Key为deviceInfo和Key为exceptionInfo的两种捕获信息，deviceInfo主要是为了捕获手机信息的而exceptionInfo就是捕获异常崩溃信息的了，如果你的项目中还需要捕获其他类型的信息，可以通过实现ICollector接口来定义自己想提交的采集信息即可，记得在初始化时调用.strategy(new TestCollectInfo(), "网络属性的Key")将采集信息传入即可。

## 面临的问题
在网上可以看到很多类似于全局捕获异常发送服务器或者发送邮件给指定邮箱的功能，但是这些文章都没有实际的深入场景，只是写出了逻辑代码，这样就会面临到一个很实际的问题:

异常发生时，我们要做的是将异常信息和一些其他捕获到的手机信息或上传服务器或通过邮件发送给指定邮箱，但是如果这个时间过长，导致App已经退出，进程退出后，此进程的线程也不复存在，那么如果你要做的逻辑操作还没做完，那么你这次异常的捕获就是失败的。

基于这个原因，我在异常发生时做的操作是这样的：

1. 捕获异常并写入本地异常捕获文件;
2. 给写入文件的操作加入回调接口，告知主线程异常写入完毕;
3. 将异常信息、异常文件路径、手机设备信息等参数传入子进程的IntentService;

由于是子进程启动的Service进行的业务逻辑操作，就算主进程已经退出，也不会影响子进程的耗时操作，问题也就随之解决了。

## 配套的Express文件

你可能会纳闷了，什么是Express？这文件是干嘛的？

看过前面的部分后，你可能知道了这个组件是可以将异常信息发送给服务器的，而看这篇文章的很多可能都是移动端的开发人员，不一定懂服务端，就算懂，也未必能很快的搭建一个可以接受异常信息的服务端来测试，那么为了大家测试方便，我就把我的Express文件分享出来，如果你还不知道什么是Express或者Node.js，建议你先看这篇：

[Express快速搭建移动端测试用Api服务端](https://www.jianshu.com/p/d6f3f1634396)

之后将你Nodejs根目录下的app.js改为：

```javascript
var fs = require('fs');
var path = require('path');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var CRASH_FILE = path.join(__dirname, 'api/crashs.json'); // user.json文件的路径

app.set('port', (process.env.PORT || 9999));
app.use('/', express.static(path.join(__dirname, 'public')));
//使用body-parser中间件
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(function(req, res, next) {
    // Set permissive CORS header - this allows this server to be used only as
    // an API server in conjunction with something like webpack-dev-server.
    res.setHeader('Access-Control-Allow-Origin', '*');
    // Disable caching so we'll always get the latest comments.
    res.setHeader('Cache-Control', 'no-cache');
    next();
});


//处理/api/crashs的POST请求
app.post('/api/crashs', function(req, res) {
  fs.readFile(CRASH_FILE, function(err, data) {
    if (err) {
      console.error(err);
      process.exit(1);
    }
    var crashs = JSON.parse(data);
    //控制post提交的参数类型
    var crash = {
      deviceinfo: req.body.deviceInfo,
      exceptioninfo: req.body.exceptionInfo,
		  testinfo:req.body.testInfo
    };
    //将user加入到users中去。
    crashs.push(crash);
    fs.writeFile(CRASH_FILE, JSON.stringify(crashs, null, 4), function(err) {
      if (err) {
        console.error(err);
        process.exit(1);
      }
      //请求成功后返回的提示json
      res.json("{code: 200, message: 'upload crash successful.'}");
    });
  });
});


app.listen(app.get('port'), function() {
  console.log('Server started: http://localhost:' + app.get('port') + '/');
});
```

并且在同目录的文件夹：api中放入crashs.json:

```xml
[
    {
        "deviceinfo": "手机信息异常===========================================<br>DISPLAY=Flyme 6.8.3.31R beta<br>REGION=CN<br>SERIAL=d4aa09c3<br>BOOTLOADER=unknown<br>SOFT_VERSION=Y.30<br>SUPPORTED_64_BIT_ABIS=[Ljava.lang.String;@e6de412<br>PERMISSIONS_REVIEW_REQUIRED=false<br>AUTO_TEST_ONEPLUS=false<br>ID=NMF26F<br>TAG=Build<br>HOST=xs-MacBookPro<br>TAGS=test-keys<br>TIME=1522481855000<br>TYPE=user<br>USER=xs<br>BOARD=QC_Reference_Phone<br>BRAND=OnePlus<br>MODEL=ONEPLUS A3010<br>RADIO=unknown<br>SUPPORTED_ABIS=[Ljava.lang.String;@833c7e3<br>MANUFACTURER=OnePlus<br>PRODUCT=OnePlus3<br>UNKNOWN=unknown<br>versionCode=1<br>versionName=1.0<br>IS_EMULATOR=false<br>FINGERPRINT=OnePlus/OnePlus3/OnePlus3T:7.1.1/NMF26F/builder.20180331153735_R:user/test-keys<br>HARDWARE=qcom<br>SUPPORTED_32_BIT_ABIS=[Ljava.lang.String;@b31279d<br>IS_BETA_ROM=true<br>CPU_ABI2=<br>CPU_ABI=arm64-v8a<br>IS_DEBUGGABLE=false<br>DEBUG_ONEPLUS=false<br>DEVICE=OnePlus3T<br>===========================================<br>",
        "exceptioninfo": "Time:Fri May 10 14:23:32 GMT+08:00 2019  [Thread(id:3321, name:pool-2-thread-1, priority:5, groupName:main): LogCenter.java:184 run java.lang.RuntimeException: 测试CrashLib\n\tat com.dhcc.test.MainActivity$1.onClick(MainActivity.java:18)\n\tat android.view.View.performClick(View.java)\n\tat android.view.View$PerformClick.run(View.java:22549)\n\tat android.os.Handler.handleCallback(Handler.java:751)\n\tat android.os.Handler.dispatchMessage(Handler.java:95)\n\tat android.os.Looper.loop(Looper.java:154)\n\tat android.app.ActivityThread.main(ActivityThread.java)\n\tat java.lang.reflect.Method.invoke(Native Method)\n\tat com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:886)\n\tat com.android.internal.os.ZygoteInit.main(ZygoteInit.java:776)\n ] - 测试异常~~",
        "testinfo": "这是一条测试采集异常信息"
    }
]
```

然后启动服务：

```xml
在Cmd下输入：
> node app.js
```

之后在移动端，我们就可以设置成一下代码来捕获我们的异常信息了：

```java
...
    .setCrashServerUrl("http://你的ip:9999/api/crashs")
...

LogCenter.getLogCenter("com.dhcc.crashInfo", configuration)
                //可以自定义异常 只要实现ICollector 并传入网络提交时所需要的key即可
                .strategy(new TestCollectInfo(), "testInfo")
                .init(this);
```

这里注意.strategy(new TestCollectInfo(), "testInfo")的testInfo，其实就是app.js中的req.body.testInfo和crashs.json中的testInfo字段。

## 整体设计架构



![](https://user-gold-cdn.xitu.io/2019/5/13/16aaede931d4f2ef?w=1204&h=663&f=png&s=46092)

源码就不细说了，大家可以自己去看，有什么问题可以给我留言，谢谢你看完。

## 感谢

[这是一份详细清晰的 上传Android Library到JCenter 教程](https://blog.csdn.net/carson_ho/article/details/89305524)

[编写 Android Library 的最佳实践](https://juejin.im/post/5c9228e7f265da60fe7c2732)

[Android程序制作自己Log日志收集系统](https://blog.csdn.net/cn_1937/article/details/80105445)

[Android进阶：一、日志打印和保存策略](https://blog.51cto.com/14295695/2384021)
