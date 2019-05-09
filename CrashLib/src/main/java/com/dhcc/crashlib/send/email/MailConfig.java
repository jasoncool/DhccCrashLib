package com.dhcc.crashlib.send.email;

/**
 * @author jasoncool
 * 邮件配置文件
 */
public class MailConfig {

    private MailConfig() {

    }

    static String MAIL_PROTOCOL_PROPERTY = "mail.transport.protocol";
    static String MAIL_HOST_PROPERTY = "mail.smtp.host";
    static String MAIL_PORT_PROPERTY = "mail.smtp.port";
    static String MAIL_AUTH_PROPERTY = "mail.smtp.auth";
    static String MAIL_TIME_OUT_PROPERTY = "mail.smtp.timeout";


  /*  static String  MAIL_PROTOCOL_VALUE="smtp";
    static String MAIL_HOST_VALUE="smtp.163.com";
    static String MAIL_PORT_VALUE="25";
    static String MAIL_AUTH_VALUE="true";
    static String MAIL_TIME_OUT_VALUE="5000";

    *//**
     收件人电子邮箱
     *//*
    static String MAIL_TO_ADDRESS = "jasoncool_521@qq.com";
    *//**
     * 发件人电子邮箱
     *//*
    static String MAIL_FROM_ADDRESS = "jasoncool_521@163.com";

    static String MAIL_FROM_PASSWORD="7758521@";*/

}
