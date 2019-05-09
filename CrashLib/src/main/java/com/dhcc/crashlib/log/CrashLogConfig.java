package com.dhcc.crashlib.log;

/**
 * @author jasoncool
 * 崩溃日志的设置文件
 */
 class CrashLogConfig {

     private CrashLogConfig(){

     }
    /**
     * 是否记录崩溃信息到本地文件
     */
    static final boolean ISRECORD = true;
    /**
     * 单个本地文件的最大的容量
     */
     static final int MAX_BYTES = 1024 * 1024;
    /**
     * 崩溃信息存储的文件夹名
     */
    static final String LOG_FLODER_NAME="crash_log";

    /**
     * 崩溃信息存储的文件名
     */
    static final String LOG_FILE_NAME="crashLogs";


}
