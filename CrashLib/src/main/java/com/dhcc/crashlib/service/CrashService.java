package com.dhcc.crashlib.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.dhcc.crashlib.Configuration;
import com.dhcc.crashlib.send.SendWorker;
import com.socks.library.KLog;

import java.io.File;


/**
 * @author jasoncool
 */
public class CrashService extends IntentService {

    public static final String CRASH_CONTEXT="com.dhcc.componentlib.extra.CRASH_CONTEXT";
    public static final String CRASH_FILE="com.dhcc.componentlib.extra.CRASH_FILE";

    public CrashService(){
        super("crashService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CrashService(String name) {
        super(name);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
       if(intent==null){
           KLog.e("启动崩溃上报进程的Itent不能为空");
           return;
       }
        String content = intent.getStringExtra(CRASH_CONTEXT);
        String filePath=intent.getStringExtra(CRASH_FILE);
        if(Configuration.IS_SEND_WITH_EMAIL){
            if(Configuration.IS_UPLOAD_LOG_FILE){
                if(filePath!=null&&filePath.length()>0){
                    File file=new File(filePath);
                    if(file.exists()){
                        SendWorker.INSTANCE.sendWithEmail(content,file);
                    }else{
                        SendWorker.INSTANCE.sendWithEmail(content);
                    }
                }else{
                    SendWorker.INSTANCE.sendWithEmail(content);
                }
            }else{
                SendWorker.INSTANCE.sendWithEmail(content);
            }
        }

        if(Configuration.IS_SEND_WITH_NET){
            SendWorker.INSTANCE.sendToServer(content);
        }
    }

    public static void startCrashService(Context ctn,String content,String filePath){
        Intent intent=new Intent(ctn,CrashService.class);
        intent.putExtra(CRASH_CONTEXT,content);
        intent.putExtra(CRASH_FILE,filePath);
        ctn.startService(intent);
    }

}
