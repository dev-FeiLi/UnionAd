package io.union.log.common.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailSender {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final JavaMailSender sender;
    private final String sendFrom;
    private final String sendTo;

    public MailSender(@Autowired JavaMailSender sender, @Value("${spring.mail.send.from}") String sendFrom, @Value("${spring.mail.send.to}") String sendTo) {
        this.sender = sender;
        this.sendFrom = sendFrom;
        this.sendTo = sendTo;
    }

    public void send(String title, String msg) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sendFrom);//发送者.
            message.setTo(sendTo);//接收者.
            message.setSubject(title);//邮件主题.
            message.setText(msg);//邮件内容.
            sender.send(message);//发送邮件
        } catch (Exception e) {
            logger.error("send mail error: ", e);
        }
    }
}
