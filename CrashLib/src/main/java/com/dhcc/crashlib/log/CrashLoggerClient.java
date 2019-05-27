package com.dhcc.crashlib.log;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * @author jasoncool
 * 崩溃日志客户端
 */
public class CrashLoggerClient {

    private static final  String TAG = "CrashLoggerClient";
    /**
     * 保存崩溃信息至本地文件的实现类
     */
    private static SaveCrashLogImpl saveLogImpl;
    private static CrashLoggerClient logger;
    private static boolean isrecord = CrashLogConfig.ISRECORD;
    private String customTag = null;

    private CrashLoggerClient(String customTag) {
        this.customTag = customTag;
    }

    /**
     * 初始化存储崩溃信息至本地的方法
     * 1.初始化崩溃信息存储的文件夹
     * 2.创建一个handlerThread并在这个线程创建一个handler
     * 3.创建一个SaveCrashLogImpl的实例并将刚才创建的handler传递给该实例
     * @param context 上下文
     */
    public void initSaveImpl(Context context) {
        if (saveLogImpl != null || !isrecord) {
            return;
        }
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File cacheFile = context.getCacheDir();
        if (cacheFile != null) {
            diskPath = cacheFile.getAbsolutePath();
        }
        String folder = diskPath + File.separatorChar + CrashLogConfig.LOG_FLODER_NAME;
        HandlerThread ht = new HandlerThread(this.getClass().getCanonicalName()+" "+ folder);
        ht.start();
        Handler handler = new SaveCrashLogImpl.WriteHandler(ht.getLooper(), folder, CrashLogConfig.MAX_BYTES);
        saveLogImpl = new SaveCrashLogImpl(handler);
    }

    public static CrashLoggerClient getLogger(String tag) {
        if (logger == null) {
            logger = new CrashLoggerClient(tag);
        }
        return logger;
    }

    public static CrashLoggerClient getLogger() {
        if (logger == null) {
            logger = new CrashLoggerClient(TAG);
        }
        return logger;
    }

    /**
     * 崩溃信息存储本地的方法
     * @param str 崩溃描述信息
     * @param e  异常实例
     * @param iFileCloseListener 文件写入完毕的回调接口
     */
    public void e(String str, Throwable e, SaveCrashLogImpl.IFileCloseListener iFileCloseListener) {
        logPrint(str,e,iFileCloseListener);
    }

    /**
     * 打印崩溃日志信息
     * @param msg 崩溃描述信息
     * @param e 异常实例
     * @param iFileCloseListener 文件写入完毕的回调接口
     */
    private void logPrint(String msg,Throwable e, SaveCrashLogImpl.IFileCloseListener iFileCloseListener) {
        if (isrecord) {
            String exceptionInfo = getExceptionInfo(e);
            if (saveLogImpl != null) {
                saveLogImpl.saveCrashLog(Log.ERROR, exceptionInfo + " - " + msg,iFileCloseListener);
            }
            Log.println(Log.ERROR, customTag, exceptionInfo + " - " + msg);
        }
    }

    /**
     * 获取异常的堆栈信息以及异常所在线程的线程信息
     * 线程信息包含：
     * 线程Id,线程名称，线程优先级，线程组名等
     * @return  时间戳+线程信息+异常所在文件名+异常所在行号+异常所在方法名+异常堆栈信息
     */
    private String getExceptionInfo(@Nullable Throwable e) {
        String stacktraceAsString="";
        if(e!=null){
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);

            Throwable cause=e;
            while(null!=cause){
                cause.printStackTrace(printWriter);
                cause=cause.getCause();
            }
            stacktraceAsString=result.toString();
            printWriter.close();
        }
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            Thread t = Thread.currentThread();
            return "Time:"+new Date(System.currentTimeMillis())+"  [Thread(id:" + t.getId() +
                    ", name:" + t.getName() +
                    ", priority:" + t.getPriority() +
                    ", groupName:" + t.getThreadGroup().getName() +
                    "): " + st.getFileName() + ":"
                    + st.getLineNumber() + " " + st.getMethodName() + " "+stacktraceAsString+" ]";
        }
        return "";
    }
}
