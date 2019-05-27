package com.dhcc.crashlib.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.collection.ArrayMap;

import com.socks.library.KLog;
import java.lang.reflect.Field;


/**
 * 获取设备信息
 * @author jasoncool
 */
public class DeviceCollectInfo implements ICollector {
    @Override
    public String collectInfo(Context ctx) {
        if(CRASH_INFO_MAP.size()!=0){
            CRASH_INFO_MAP.clear();
        }
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                CRASH_INFO_MAP.put("versionName", versionName);
                CRASH_INFO_MAP.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            KLog.e("获取包名时发生异常", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                CRASH_INFO_MAP.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                KLog.e("采集崩溃信息时发生异常", e);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("手机信息异常===========================================<br>");
        for (ArrayMap.Entry<String, String> entry : CRASH_INFO_MAP.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "<br>");
        }
        sb.append("===========================================<br>");
        return sb.toString();
    }
}
