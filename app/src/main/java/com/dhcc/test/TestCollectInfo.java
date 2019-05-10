package com.dhcc.test;

import android.content.Context;

import com.dhcc.crashlib.data.ICollector;

public class TestCollectInfo implements ICollector {
    @Override
    public String collectInfo(Context context) {
        return "这是一条测试采集异常信息";
    }
}
