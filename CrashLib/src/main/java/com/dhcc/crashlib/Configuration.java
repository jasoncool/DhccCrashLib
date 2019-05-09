package com.dhcc.crashlib;

/**
 * @author jasoncool
 * 异常设置类
 */
public class Configuration {

    private Configuration(){

    }
    /**
     * 崩溃服务器
     */
    public static final String CRASH_SERVER_URL="http://192.168.3.144:9999/api/crash";
    /**
     * 是否用邮件发送异常信息
     */
    public static final boolean IS_SEND_WITH_EMAIL=true;
    /**
     * 是否发送邮件时将本地存储的异常信息通过邮件附件的形式发送
     */
    public static final boolean IS_UPLOAD_LOG_FILE=true;
    /**
     * 是否将异常信息通过网络发送到崩溃服务器
     */
    public static final boolean IS_SEND_WITH_NET=true;
    /**
     * 异常信息的分隔符
     */
    public static final String CRASH_INFO_SEPRATOR="##";
    /**
     * App崩溃后等待退出的时间（毫秒）
     */
    public static final int APP_WAIT_TIME=3000;
    /**
     * 崩溃信息的描述文字
     */
    public static final String  CRASH_DESCRIPTION="程序出现严重Bug,需要重启应用。";
}
