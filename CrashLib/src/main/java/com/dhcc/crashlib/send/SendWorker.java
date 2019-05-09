package com.dhcc.crashlib.send;

import android.content.Context;
import android.support.annotation.Nullable;

import com.dhcc.crashlib.send.email.EmailSender;
import com.dhcc.crashlib.send.net.NetSender;
import com.socks.library.KLog;

import java.io.File;

public enum SendWorker {

    /**
     * 单例
     */
    INSTANCE;

    public void sendWithEmail(Context context, String content, @Nullable  File file){

        if(file==null||!file.exists()){
            KLog.e("发送的文件不能为空!");
            return;
        }
            EmailSender.INSTANCE.sendLogWithFile(context,content,file);
    }

    public void sendWithEmail(Context context,String content){
        EmailSender.INSTANCE.sendLog(context,content);
    }


    public String sendToServer(String crashServerUrl,String[] contentArray){
      return   NetSender.INSTANCE.sendLog(crashServerUrl,contentArray);
    }

}
