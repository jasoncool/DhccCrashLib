package com.dhcc.crashlib.send.net;

import android.text.TextUtils;
import android.util.ArrayMap;
import com.dhcc.crashlib.utils.HttpConnectionUtil;
import com.socks.library.KLog;

import java.util.HashMap;


public enum NetSender  {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 发送不带附件的错误日志
     *
     * @param crashServerUrl
     * @param exceptionMap        崩溃信息
     */
    public String sendLog(String crashServerUrl, HashMap<String,String> exceptionMap) {

        if(TextUtils.isEmpty(crashServerUrl)){
            KLog.e("崩溃服务器地址不能为空");
            return "崩溃服务器地址不能为空";
        }

        if(exceptionMap==null||exceptionMap.size()==0){
            KLog.e("傳入的異常信息不能为空");
            return "傳入的異常信息不能为空";
        }
        return HttpConnectionUtil.getHttp().postRequset(crashServerUrl, exceptionMap);
    }

}
