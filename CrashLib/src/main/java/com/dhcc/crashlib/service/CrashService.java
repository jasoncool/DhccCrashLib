package com.dhcc.crashlib.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.dhcc.crashlib.send.email.EmailConfigBean;
import com.dhcc.crashlib.send.SendWorker;
import com.socks.library.KLog;
import java.io.File;


/**
 * @author jasoncool
 */
public class CrashService extends IntentService {

    private static final String CRASH_CONTEXT="com.dhcc.componentlib.extra.CRASH_CONTEXT";
    private static final String CRASH_FILE="com.dhcc.componentlib.extra.CRASH_FILE";
    private static final String IS_SEND_WITH_EMAIL="com.dhcc.componentlib.extra.IS_SEND_WITH_EMAIL";
    private static final String IS_UPLOAD_LOG_FILE="com.dhcc.componentlib.extra.IS_UPLOAD_LOG_FILE";
    private static final String IS_SEND_WITH_NET="com.dhcc.componentlib.extra.IS_SEND_WITH_NET";
    private static final String CRASH_SERVER_URL="com.dhcc.componentlib.extra.CRASH_SERVER_URL";
    private static final String EMAIL_CONFIG="com.dhcc.componentlib.extra.EMAIL_CONFIG";
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
        String[] content = intent.getStringArrayExtra(CRASH_CONTEXT);
        String filePath=intent.getStringExtra(CRASH_FILE);
        boolean isSendWithEmail=intent.getBooleanExtra(IS_SEND_WITH_EMAIL,true);
        boolean isUploadWithFile=intent.getBooleanExtra(IS_UPLOAD_LOG_FILE,true);
        boolean isSendWithNet=intent.getBooleanExtra(IS_SEND_WITH_NET,true);
        String crashServerUrl=intent.getStringExtra(CRASH_SERVER_URL);
        EmailConfigBean emailConfigBean=intent.getParcelableExtra(EMAIL_CONFIG);

        if(isSendWithEmail){
            if(isUploadWithFile){
                if(filePath!=null&&filePath.length()>0&&new File(filePath).exists()){
                    File file=new File(filePath);
                    SendWorker.INSTANCE.sendWithEmail(getBaseContext(),emailConfigBean,parseExceptionContent(content),file);
                }else{
                    SendWorker.INSTANCE.sendWithEmail(getBaseContext(),emailConfigBean,parseExceptionContent(content));
                }
            }else{
                SendWorker.INSTANCE.sendWithEmail(getBaseContext(),emailConfigBean,parseExceptionContent(content));
            }
        }

        if(isSendWithNet){
            SendWorker.INSTANCE.sendToServer(crashServerUrl,content);
        }
    }

    private String parseExceptionContent(String[] exceptionArray){
        if(exceptionArray==null||exceptionArray.length==0){
            KLog.e("传入的异常信息不对");
            return "";
        }
        String seprator="<br>";
        StringBuilder stringBuilder=new StringBuilder();
        for(String perException:exceptionArray){
            stringBuilder.append(perException+seprator);
        }
        return stringBuilder.toString();
    }

    /**
     * 启动多进程Service的调用方法
     * @param ctn 上下文
     * @param isSendWithEmail 崩溃信息是否发送邮件
     * @param isUploadLogFile 发送崩溃信息邮件时是否将崩溃信息以附件的形式发送
     * @param isSendWithNet 是否发送崩溃信息给服务器？
     * @param exceptionArray 崩溃信息主体
     * @param filePath 崩溃信息文件路径
     */
    public static void startCrashService(Context ctn, String crashServerUrl, boolean isSendWithEmail, boolean isUploadLogFile, boolean isSendWithNet, String[] exceptionArray, String filePath, EmailConfigBean emailConfigBean){
        Intent intent=new Intent(ctn,CrashService.class);
        intent.putExtra(CRASH_CONTEXT,exceptionArray);
        intent.putExtra(CRASH_FILE,filePath);
        intent.putExtra(IS_SEND_WITH_EMAIL,isSendWithEmail);
        intent.putExtra(IS_UPLOAD_LOG_FILE,isUploadLogFile);
        intent.putExtra(IS_SEND_WITH_NET,isSendWithNet);
        intent.putExtra(CRASH_SERVER_URL,crashServerUrl);
        intent.putExtra(EMAIL_CONFIG,emailConfigBean);
        ctn.startService(intent);
    }

}
