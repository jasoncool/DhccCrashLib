package com.dhcc.crashlib.log;

/**
 * @author jasoncool
 * 崩溃信息的实例
 */
public class CrashLogBean {
    /**
     * 崩溃信息
     */
   private String message;
    /**
     * 崩溃信息写入本地文件后的回调接口
     */
   private SaveCrashLogImpl.IFileCloseListener iFileCloseListener;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SaveCrashLogImpl.IFileCloseListener getiFileCloseListener() {
        return iFileCloseListener;
    }

    public void setiFileCloseListener(SaveCrashLogImpl.IFileCloseListener iFileCloseListener) {
        this.iFileCloseListener = iFileCloseListener;
    }

}
