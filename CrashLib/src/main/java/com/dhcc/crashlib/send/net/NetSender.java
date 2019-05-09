package com.dhcc.crashlib.send.net;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.dhcc.crashlib.Configuration;
import com.dhcc.crashlib.send.IReportSender;
import com.dhcc.crashlib.utils.HttpConnectionUtil;

import java.io.File;

public enum NetSender implements IReportSender {


    /**
     * 单例
     */
    INSTANCE;

    /**
     * 发送不带附件的错误日志
     *
     * @param content 崩溃信息
     */
    @Override
    public String sendLog(String content) {
        if(TextUtils.isEmpty(content)){
            return "";
        }
        ArrayMap<String,String> params=new ArrayMap<>(16);
        String[] crashArg=content.split(Configuration.CRASH_INFO_SEPRATOR);
        if(crashArg.length>0){
            params.put("deviceInfo",crashArg[0]);
            params.put("exceptionInfo",crashArg[1]);
        }else{
            params.put("deviceInfo",content);
            params.put("exceptionInfo","");
        }
       return HttpConnectionUtil.getHttp().postRequset(Configuration.CRASH_SERVER_URL, params);
    }

    /**
     * 发送带附件的错误日志
     *
     * @param content 崩溃信息
     * @param file    崩溃日志文件
     */
    @Override
    public void sendLogWithFile(String content, File file) {

    }

    /**
     * 发送完毕后的操作
     */
    @Override
    public void onSendSuccess() {

    }
}
