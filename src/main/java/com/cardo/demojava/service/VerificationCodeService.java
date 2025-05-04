package com.cardo.demojava.service;

import com.cardo.demojava.util.SendMailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {
    
    @Autowired
    private SendMailUtils sendMailUtils;
    
    // 使用ConcurrentHashMap存储验证码，key为邮箱，value为验证码和过期时间
    private final Map<String, CodeInfo> codeMap = new ConcurrentHashMap<>();
    
    // 验证码有效时间（5分钟）
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;
    
    public void sendVerificationCode(String email) {
        // 生成6位随机验证码
        String code = String.format("%06d", (int)(Math.random() * 1000000));


        // 创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("验证码");
        message.setText("您的验证码是：" + code + "，有效期为5分钟。");

        ArrayList<String> strings = new ArrayList<>();
        strings.add(email);
        sendMailUtils.sendEmail(strings,message.getSubject(),message.getText());
        
        // 存储验证码信息
        codeMap.put(email, new CodeInfo(code, System.currentTimeMillis()));
    }
    
    public boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = codeMap.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        // 检查验证码是否过期
        if (System.currentTimeMillis() - codeInfo.getTimestamp() > CODE_EXPIRE_TIME) {
            codeMap.remove(email);
            return false;
        }
        
        // 验证码正确则删除
        if (codeInfo.getCode().equals(code)) {
            codeMap.remove(email);
            return true;
        }
        
        return false;
    }
    
    // 内部类用于存储验证码信息
    private static class CodeInfo {
        private final String code;
        private final long timestamp;
        
        public CodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
        
        public String getCode() {
            return code;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
} 