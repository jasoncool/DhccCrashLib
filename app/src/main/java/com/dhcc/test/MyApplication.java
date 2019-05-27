package com.dhcc.test;

import android.app.Application;

import com.dhcc.crashlib.Configuration;
import com.dhcc.crashlib.LogCenter;
import com.dhcc.crashlib.send.email.EmailConfigBean;

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
        EmailConfigBean emailConfigBean = new EmailConfigBean("jasoncool_521@163.com", "jasoncool_521@qq.com", "haojunmei1982@");
        Configuration configuration=Configuration.getInstance()
                //你的邮件配置实例
                .setEmailConfig(emailConfigBean)
                //是否通过邮件发送异常
                .setSendWithEmail(true)
                //是否通过邮件发送异常并将本地存储的异常已附件的形式发送
                .setSendEmailWithFile(true)
                //异常服务器的API
                .setCrashServerUrl("123")
                //是否给服务器发送异常信息
                .setSendWithNet(true)
                //异常的描述信息
                .setCrashDescription("测试异常~~")
                //捕获异常后退出App的等待时间 毫秒
                .setExitWaitTime(5000)
                ;
        LogCenter.getLogCenter("com.dhcc.crashInfo", configuration)
                //可以自定义异常 只要实现ICollector 并传入网络提交时所需要的key即可
                .strategy(new TestCollectInfo(), "testInfo")
                .init(this);
    }
}
