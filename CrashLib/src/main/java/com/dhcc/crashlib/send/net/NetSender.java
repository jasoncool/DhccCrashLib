package com.dhcc.crashlib.send.net;

import android.text.TextUtils;
import android.util.ArrayMap;
import com.dhcc.crashlib.utils.HttpConnectionUtil;
import com.socks.library.KLog;


public enum NetSender  {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 发送不带附件的错误日志
     *
     * @param crashServerUrl
     * @param contentArray        崩溃信息
     */
    public String sendLog(String crashServerUrl, String[] contentArray) {

        if(TextUtils.isEmpty(crashServerUrl)){
            KLog.e("崩溃服务器地址不能为空");
            return "崩溃服务器地址不能为空";
        }

        if(contentArray==null||contentArray.length==0){
            KLog.e("傳入的異常信息不能为空");
            return "傳入的異常信息不能为空";
        }
        ArrayMap<String,String> params=new ArrayMap<>(16);
        if(contentArray.length>1){
            params.put("deviceInfo",contentArray[0]);
            params.put("exceptionInfo",contentArray[1]);
        }else{
            params.put("deviceInfo",contentArray[0]);
            params.put("exceptionInfo","");
        }
        return HttpConnectionUtil.getHttp().postRequset(crashServerUrl, params);
    }

}
