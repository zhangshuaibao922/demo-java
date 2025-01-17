package com.cardo.demojava.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.List;
import java.util.Properties;
@Component
@Slf4j
public class SendMailUtils {

    // 发件人的SMTP服务器地址（普通QQ邮箱）
    public static final String HOST = "smtp.qq.com";
    public static final String PORT = "465";
    // 发件人邮箱地址
    @Value("${email.user}")
    public String USERNAME;
    // 发件人邮箱授权码
    @Value("${email.code}")
    public String PASSWORD;
    // 邮件协议
    private static final String emailProtocol = "smtp";


    public boolean sendEmail(List<String> emailList, String title, String content) {
        try {
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", emailProtocol); // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", HOST); // 指定smtp服务器地址
            props.setProperty("mail.smtp.port", PORT); // 指定smtp端口号
            // 使用smtp身份验证
            props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
            props.put("mail.smtp.ssl.enable", "true"); // 开启SSL
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 指定SSL版本
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // 由于Properties默认不限制请求时间，可能会导致线程阻塞，所以指定请求时长
            props.setProperty("mail.smtp.connectiontimeout", "10000");// 与邮件服务器建立连接的时间限制
            props.setProperty("mail.smtp.timeout", "10000");// 邮件smtp读取的时间限制
            props.setProperty("mail.smtp.writetimeout", "10000");// 邮件内容上传的时间限制
            // 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getDefaultInstance(props);
            // 设置为debug模式, 可以查看详细的发送log
            session.setDebug(false);
            //  创建邮件
            MimeMessage message = new MimeMessage(session);
            //From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
            message.setFrom(new InternetAddress(USERNAME, "评审推送邮件", "UTF-8"));
            // To: 收件人（可以增加多个收件人、抄送、密送）
            int size = emailList.size();
            // 单个目标邮箱还是多个
            if (size == 1) {
                String email = emailList.iterator().next();
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(email, email, "UTF-8"));
            } else {
                InternetAddress[] addresses = new InternetAddress[emailList.size()];
                int i = 0;
                for (String email : emailList) {
                    addresses[i++] = new InternetAddress(email, email, "UTF-8");
                }
                message.setRecipients(MimeMessage.RecipientType.TO, addresses);
            }
            // Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
            message.setSubject(title, "UTF-8");
            // Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
            message.setContent(content, "text/html;charset=UTF-8");
            // 设置发件时间
            message.setSentDate(new Date());
            // 保存设置
            message.saveChanges();
            // 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            transport.connect(USERNAME, PASSWORD);
            // 发送邮件, 发到所有的收件地址
            //message.getAllRecipients()获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());
            // 关闭传输连接
            transport.close();
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败！", e);
            return false;
        }
    }
}
