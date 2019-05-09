package com.dhcc.crashlib.send;

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

    public void sendWithEmail(String content,@Nullable  File file){

        if(file==null||!file.exists()){
            KLog.e("发送的文件不能为空!");
            return;
        }
            EmailSender.INSTANCE.sendLogWithFile(content,file);
    }

    public void sendWithEmail(String content){
        EmailSender.INSTANCE.sendLog(content);
    }


    public String sendToServer(String content){

      return   NetSender.INSTANCE.sendLog(content);
    }

}
