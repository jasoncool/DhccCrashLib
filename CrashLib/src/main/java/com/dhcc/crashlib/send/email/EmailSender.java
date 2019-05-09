package com.dhcc.crashlib.send.email;

import com.dhcc.crashlib.send.IReportSender;
import com.dhcc.crashlib.utils.SingleTaskPool;
import com.socks.library.KLog;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author jasoncool
 * 邮件发送
 */
public enum  EmailSender implements IReportSender {

    /**
     * 单例
     */
    INSTANCE;

    private Session getMailSession(){
        // 获取系统属性
        Properties properties = new Properties();
        // 设置邮件服务器
        properties.setProperty(MailConfig.MAIL_PROTOCOL_PROPERTY, MailConfig.MAIL_PROTOCOL_VALUE);
        properties.setProperty(MailConfig.MAIL_HOST_PROPERTY,MailConfig.MAIL_HOST_VALUE);
        properties.setProperty(MailConfig.MAIL_AUTH_PROPERTY,MailConfig.MAIL_AUTH_VALUE);
        // 开启debug调试
        //properties.setProperty("mail.debug", "true");
        // 获取默认session对象
        return  Session.getDefaultInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        // 登陆邮件发送服务器的用户名和密码
                        return new PasswordAuthentication(MailConfig.MAIL_FROM_ADDRESS, MailConfig.MAIL_FROM_PASSWORD);
                    }
                });
    }

    /**
     * 发送普通邮件
     * @throws Exception 捕获的异常
     */
    private  void sendSimpleEmail(String content) throws Exception {

        // 创建默认的 MimeMessage 对象
        MimeMessage message = new MimeMessage(getMailSession());
        // Set From: 头部头字段
        message.setFrom(new InternetAddress(MailConfig.MAIL_FROM_ADDRESS));
        // Set To: 头部头字段
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(MailConfig.MAIL_TO_ADDRESS));
        // Set Subject: 头部头字段
        message.setSubject(new Date(System.currentTimeMillis())+"错误日志");
        // 设置消息体
        message.setText(content);
        // 发送消息
        Transport.send(message);
    }

    /**
     * 发送带附件的邮件
     * @param file 附件
     * @throws Exception 捕获的异常
     */
    private  void sendFileEmail(String content,File file){
        try{
            Session session=getMailSession();
            MimeBodyPart text = new MimeBodyPart();
            text.setContent("<h4>"+content+"</h4>", "text/html;charset=UTF-8");
            //创建邮件附件
            MimeBodyPart attach = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource(file));
            attach.setDataHandler(dh);
            attach.setFileName(dh.getName());

            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            InternetAddress address = new InternetAddress(MailConfig.MAIL_FROM_ADDRESS);
            message.setFrom(address);
            message.addRecipient(Message.RecipientType.CC, address);

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(MailConfig.MAIL_TO_ADDRESS));

            //创建容器描述数据关系
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(text);
            mp.addBodyPart(attach);
            mp.setSubType("mixed");

            message.setSubject(new Date(System.currentTimeMillis())+"错误日志");
            message.setContent(mp);
            message.saveChanges();

            Transport transport = session.getTransport();
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送文字邮件
     * @param content 日志主体
     */
    @Override
    public String sendLog(String content) {
        try {
            sendSimpleEmail(content);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            onSendSuccess();
        }
        return "";
    }

    /**
     * 发送带附件的错误日志
     */
    @Override
    public void sendLogWithFile(String content,File file) {
        if(file==null){
            KLog.w("传入Email的日志文件为空");
        }
        try {
            sendFileEmail(content,file);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            onSendSuccess();
        }
    }

    /**
     * 发送完毕后的操作
     */
    @Override
    public void onSendSuccess() {
        SingleTaskPool.unInit();
    }
}
