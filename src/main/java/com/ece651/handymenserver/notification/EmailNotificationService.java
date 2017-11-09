package com.ece651.handymenserver.notification;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.mail.MailProperties;

@Service
public class EmailNotificationService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailProperties properties;
	
	
	public void sendTextMail(String toAddr, String subject, 
			String content) throws Exception 
	{
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(
        		mimeMessage, true);
        messageHelper.setFrom(properties.getUsername(), "Handymen Service of UW");
        messageHelper.setTo(toAddr);
        messageHelper.setSubject(subject);
        messageHelper.setText(content);
        mailSender.send(mimeMessage);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
