package com.dhcc.crashlib;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import com.dhcc.crashlib.data.DeviceCollectInfo;
import com.dhcc.crashlib.data.ICollector;
import com.dhcc.crashlib.data.ReportData;
import com.dhcc.crashlib.log.CrashLoggerClient;
import com.dhcc.crashlib.log.SaveCrashLogImpl;
import com.dhcc.crashlib.send.email.EmailConfigBean;
import com.dhcc.crashlib.service.CrashService;
import com.dhcc.crashlib.service.SerializableHashMap;
import com.dhcc.crashlib.utils.SingleTaskPool;
import com.socks.library.KLog;
import java.io.File;
import java.util.HashMap;

/**
 * 日志中心
 * 负责控制日志的发送逻辑
 * @author jasoncool
 */
public class LogCenter implements Thread.UncaughtExceptionHandler {

    private String crashServerUrl;
    private boolean isSendEmail;
    private boolean isSendEmailWithFile;
    private boolean isSendByNet;
    private int exitWaitTime;
    private String crashDescription;
    private EmailConfigBean emailConfigBean;
    private HashMap<String,String> exceptionMap=new HashMap<>();
    private SerializableHashMap serializableHashMap=new SerializableHashMap();

    /**
     * 存储不同进程下的LogCenter的实例ArrayMap
     */
    private static ArrayMap<String,LogCenter> logCenterMap=new ArrayMap<>();

    /**
     * 系统默认UncaughtExceptionHandler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 默认为类名的tag 设置不同的进程名
     */
    private  String tag = this.getClass().getCanonicalName();
    private Context mContext;

    @Keep
    private LogCenter(Configuration configuration){
        this.crashServerUrl= configuration.crashServerUrl;
        this.isSendEmail = configuration.isSendWithEmail;
        this.isSendEmailWithFile = configuration.isSendEmailWithFile;
        this.isSendByNet = configuration.isSendWithNet;
        this.exitWaitTime = configuration.exitWaitTime;
        this.crashDescription = configuration.crashDescription;
        this.emailConfigBean=configuration.emailConfigBean;

    }


    /**
     * 根据进程名称获取Logcenter的实例方法
     * @param processName 进程名
     * @return LogCenter实例
     */
    public static LogCenter getLogCenter(String processName){
        LogCenter logCenter;
        if(logCenterMap.containsKey(processName)){
            return logCenterMap.get(processName);
        }else{
            logCenter=new LogCenter();
            logCenterMap.put(processName,logCenter);
            return logCenter;
        }
    }

    public static LogCenter getLogCenter(String processName,@NonNull Configuration configuration){
        LogCenter logCenter;
            if(logCenterMap.containsKey(processName)){
                logCenter= logCenterMap.get(processName);
                if(logCenter!=null){
                    logCenter.setConfigtion(configuration);
                }
                return logCenter;
            }else{
                logCenter=new LogCenter(configuration);
                logCenterMap.put(processName,logCenter);
                return logCenter;
            }
    }

    private void setConfigtion(Configuration configuration){
        this.crashServerUrl= configuration.crashServerUrl;
        this.isSendEmail = configuration.isSendWithEmail;
        this.isSendEmailWithFile = configuration.isSendEmailWithFile;
        this.isSendByNet = configuration.isSendWithNet;
        this.exitWaitTime = configuration.exitWaitTime;
        this.crashDescription = configuration.crashDescription;
        this.emailConfigBean=configuration.emailConfigBean;
    }

    private LogCenter(){
        Configuration configuration= Configuration.getCleanInstance();
        this.crashServerUrl= configuration.crashServerUrl;
        this.isSendEmail = configuration.isSendWithEmail;
        this.isSendEmailWithFile = configuration.isSendEmailWithFile;
        this.isSendByNet = configuration.isSendWithNet;
        this.exitWaitTime = configuration.exitWaitTime;
        this.crashDescription = configuration.crashDescription;
        this.emailConfigBean=configuration.emailConfigBean;
    }

    /**
     * 初始化方法
     * @param context 上下文
     */
    public void init(Context context) {
        KLog.init(true);
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        KLog.e("init");
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        KLog.e("uncaughtException");
        if(!handleException(e) && mDefaultHandler != null){
            KLog.e("mDefaultHandler.uncaughtException");
            mDefaultHandler.uncaughtException(t, e);
        }else {
            try {
                Thread.sleep(exitWaitTime);
            } catch (InterruptedException ex) {
             e.printStackTrace();
            }finally {
                System.exit(0);
            }
        }
    }

    /**
     * 处理异常逻辑方法
     * @param e 异常实例
     * @return 是否处理成功
     */
    private boolean handleException(final Throwable e) {
        if (e == null) {
            return false;
        }
        /*
         * 初始化异常信息的本地存储客户端LoggerClient
         */
        CrashLoggerClient.getLogger(tag).initSaveImpl(mContext);
        // 初始化执行异常的线程池
        SingleTaskPool.init();
        //执行异常处理线程池的线程
        SingleTaskPool.execute(new Runnable() {
            @Override
            public void run() {
                /*
                 * 传入异常说明并设置文件存储的回调方法
                 * 这里之所以用回调是需要确保文件存储完毕后
                 * 再去执行类似于邮件发送或者上传崩溃信息至服务端等操作
                 */
                CrashLoggerClient.getLogger(tag).e(crashDescription, e, new SaveCrashLogImpl.IFileCloseListener() {
                    @Override
                    public void onFileClose(String content,File file) {
                        /*
                        ReportData 使用的是策略模式
                        目前传入的是设备的采集信息
                        有需要的话你也可以传入蓝牙，设置等采集信息并将异常信息进行拼装上传
                         */
                        ReportData reportData=new ReportData(new DeviceCollectInfo());
                        String deviceInfo=reportData.collectInfo(mContext);
                        //拼装异常信息，这里使用<br>是因为邮件中如果要换行必须使用html的换行符
                        exceptionMap.put("deviceInfo",deviceInfo);
                        exceptionMap.put("exceptionInfo",content);
                        serializableHashMap.setMap(exceptionMap);
                        /*
                        调用多进程的后台服务CrashService来启动另一个进程执行发邮件和上传服务器等操作
                        为什么要调用多进程的方式来做呢？
                        因为如果是主进程来做发邮件和上传服务器等操作的话很有可能主进程已经被杀死了，这些逻辑操作却还没执行完
                        所以需要用到多进程的方式执行崩溃信息采集逻辑
                        CrashService在manifests中的注册方式是：
                         <service
                          android:name="com.dhcc.componentlib.crashlog.service.CrashService"
                          android:process="com.dhcc.remote.crashService"/>
                          利用android:process属性将该Service放在com.dhcc.remote.crashService进程下执行
                         */
                        CrashService.startCrashService(mContext,crashServerUrl,isSendEmail,isSendEmailWithFile,isSendByNet,serializableHashMap,file.getAbsolutePath(),emailConfigBean);
                    }
                });
            }
        });
        return true;
    }

    public <T extends ICollector> LogCenter strategy(T t,String key) {
        ReportData reportData=new ReportData(t);
        String collectInfo=reportData.collectInfo(mContext);
        if(exceptionMap!=null){
            exceptionMap.put(key,collectInfo);
        }
        return this;
    }
}
