package com.ourdax.coindocker.service.impl;

import com.google.common.base.Optional;
import com.ourdax.coindocker.domain.EmailObj;
import com.ourdax.coindocker.service.EmailService;
import java.util.Properties;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Created by zhangjinyang on 2018/8/21.
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService{

  @Value("${spring.mail.username}")
  private String user;
  @Value("${spring.mail.password}")
  private String passWord;
  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.port}")
  private String port;
  @Value("${spring.mail.properties.mail.smtp.auth}")
  private boolean auth;
  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private boolean starttlsEnable;
  @Value("${spring.mail.properties.mail.smtp.starttls.required}")
  private boolean starttlsRequired;
  @Value("${spring.mail.properties.mail.smtp.timeout}")
  private Integer timeOut;
  @Value("${spring.mail.properties.mail.smtp.socketFactory.class}")
  private String sslFactory;
  @Value("${spring.mail.properties.mail.smtp.socketFactory.fallback}")
  private boolean fallback;
  @Value("${mail.personal}")
  private String personal;

  @Override
  public void sendEmail(EmailObj emailObj) {

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.smtp.auth", Optional.fromNullable(auth).or(true));
    javaMailProperties.put("mail.smtp.starttls.enable", Optional.fromNullable(starttlsEnable).or(true));
    javaMailProperties.put("mail.smtp.starttls.required", Optional.fromNullable(starttlsRequired).or(true));
    javaMailProperties.put("mail.smtp.timeout", Optional.fromNullable(timeOut).or(15000));
    javaMailProperties.put("mail.smtp.port", Optional.fromNullable(port).or("465"));
    javaMailProperties.put("mail.smtp.socketFactory.class", Optional.fromNullable(sslFactory).or("javax.net.ssl.SSLSocketFactory"));
    javaMailProperties.put("mail.smtp.socketFactory.fallback", Optional.fromNullable(fallback).or(false));
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setJavaMailProperties(javaMailProperties);
    mailSender.setHost(host);
    mailSender.setUsername(user);
    mailSender.setPassword(passWord);
    mailSender.setDefaultEncoding("UTF-8");
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper;
    try {
      helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(user);
      helper.setTo(emailObj.getToUser().toArray(new String[emailObj.getToUser().size()]));
      if (emailObj.getCcUser() != null && emailObj.getCcUser().length > 0) {
        helper.setCc(emailObj.getCcUser());
      }
      helper.setSubject(emailObj.getSubject());
      helper.setText(emailObj.getText(),true);
      mailSender.send(mimeMessage);
    } catch (Exception e) {
      log.error("邮箱地址:{}发送给{}的邮件异常|",emailObj.getFromUser(), emailObj.getToUser(),e);
    }

  }
}
