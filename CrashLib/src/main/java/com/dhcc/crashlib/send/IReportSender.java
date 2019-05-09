package com.dhcc.crashlib.send;

import java.io.File;

/**
 * 上报崩溃信息的规范接口
 * @author jasoncool
 */
public interface IReportSender {

    /**
     * 发送不带附件的错误日志
     * @param content 崩溃信息
     */
    String sendLog(String content);


    /**
     * 发送带附件的错误日志
     * @param content 崩溃信息
     * @param file 崩溃日志文件
     */
    void sendLogWithFile(String content, File file);

    /**
     * 发送完毕后的操作
     */
    void onSendSuccess();

}
