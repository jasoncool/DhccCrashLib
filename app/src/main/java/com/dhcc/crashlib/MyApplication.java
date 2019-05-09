package com.dhcc.crashlib;

import android.app.Application;

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

        LogCenter.getLogCenter("com.dhcc.crashLib", Configuration.getInstance().setSendWithNet(false).setCrashDescription("测试异常~~").setExitWaitTime(5000)).init(this);

    }
}
