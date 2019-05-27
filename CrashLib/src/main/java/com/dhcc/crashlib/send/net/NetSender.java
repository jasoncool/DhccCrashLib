package com.dhcc.crashlib.send.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Toast;

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
    public String sendLog(Context context,String crashServerUrl, HashMap<String,String> exceptionMap) {

        if(TextUtils.isEmpty(crashServerUrl)){
            KLog.e("崩溃服务器地址不能为空");
            return null;
        }

        if(exceptionMap==null||exceptionMap.size()==0){
            KLog.e("傳入的異常信息不能为空");
            return null;
        }

        if(!HttpConnectionUtil.getHttp().isNetworkConnected(context)){
            KLog.e("手机网络不可用");
            return null;

        }else if(!HttpConnectionUtil.getHttp().isIP(crashServerUrl)||!HttpConnectionUtil.getHttp().isURL(crashServerUrl)){
            KLog.e("异常服务器地址不规范");
            return null;
        }
        else{
            return  HttpConnectionUtil.getHttp().postRequset(crashServerUrl, exceptionMap);
        }
    }

}
