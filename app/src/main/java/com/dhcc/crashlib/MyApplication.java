package com.dhcc.crashlib;

import android.app.Application;

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
        EmailConfigBean emailConfigBean=new EmailConfigBean("jasoncool_521@163.com","jasoncool_521@qq.com","7758521@");
        LogCenter.getLogCenter("com.dhcc.crashLib", Configuration.getInstance().setSendWithNet(true).setEmailConfig(emailConfigBean).setCrashDescription("测试异常~~").setExitWaitTime(5000)).init(this);
    }
}
