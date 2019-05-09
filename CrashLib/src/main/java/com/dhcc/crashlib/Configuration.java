package com.dhcc.crashlib;

import com.dhcc.crashlib.send.email.EmailConfigBean;

/**
 * @author jasoncool
 * 异常设置类
 */
public final class Configuration {

    public String crashServerUrl;
    public boolean isSendWithEmail;
    public boolean isSendEmailWithFile;
    public  boolean isSendWithNet;
    public int exitWaitTime;
    public String crashDescription;
    public EmailConfigBean emailConfigBean;



    private static final class InstanceHolder {
        private static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static Configuration getCleanInstance() {
        Configuration selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    public Configuration setCrashServerUrl(String crashServerUrl){
        this.crashServerUrl=crashServerUrl;
        return  this;
    }

    public Configuration setSendWithEmail(boolean isSendWithEmail){
        this.isSendWithEmail=isSendWithEmail;
        return  this;
    }

    public Configuration setSendEmailWithFile(boolean  isSendEmailWithFile){
        this.isSendEmailWithFile=isSendEmailWithFile;
        return  this;
    }

    public Configuration setSendWithNet(boolean isSendWithNet){
        this.isSendWithNet=isSendWithNet;
        return  this;
    }

    public Configuration setExitWaitTime(int exitWaitTime){
        this.exitWaitTime=exitWaitTime;
        return  this;
    }

    public Configuration setCrashDescription(String crashDescription){
        this.crashDescription=crashDescription;
        return  this;
    }

    public Configuration setEmailConfig(EmailConfigBean emailConfig){
       this.emailConfigBean=emailConfig;
        return  this;
    }

    private void reset() {
        /*
         * 崩溃服务器
         */
        crashServerUrl="http://192.168.3.144:9999/api/crash";
        /*
         * 是否用邮件发送异常信息
         */
        isSendWithEmail=true;
        /*
         * 是否发送邮件时将本地存储的异常信息通过邮件附件的形式发送
         */
        isSendEmailWithFile=true;
        /*
         * 是否将异常信息通过网络发送到崩溃服务器
         */
        isSendWithNet=true;
        /*
         * App崩溃后等待退出的时间（毫秒）
         */
        exitWaitTime=3000;
        /*
         * 崩溃信息的描述文字
         */
        crashDescription="程序出现严重Bug,需要重启应用。";
    }

    private Configuration(){
        crashServerUrl="http://192.168.3.144:9999/api/crash";
        isSendWithEmail=true;
        isSendEmailWithFile=true;
        isSendWithNet=true;
        exitWaitTime=3000;
        crashDescription="程序出现严重Bug,需要重启应用。";
    }
}
