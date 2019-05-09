package com.dhcc.crashlib.data;

import android.content.Context;
import android.util.ArrayMap;

public interface ICollector {
    /**
     * 用来存储采集信息的ArrayMap
     */
    ArrayMap<String, String> CRASH_INFO_MAP = new ArrayMap<>();
    String collectInfo(Context context);
}
