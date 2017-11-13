package com.ece651.handymenserver.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ece651.handymenserver.Domain.*;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private EmailNotificationService emailService;

    public void sendVerificationCode(HandyMenUserContactInfo user, String code) {
    	System.out.println("send verification code to " + user.getEmailAddr());
    	
    	String content = "From Handymen Service, your verification code:" 
                + code + ". Please activate your user.";
    	String subject = "Verification from Handymen Service";
    	try {
    		emailService.sendTextMail(user.getEmailAddr(), subject, content);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void sendNotification(HandyMenUserContactInfo user, HandyMenNotification notification) {
    	String subject = "Notification from Handymen Service";
    	try {
    		emailService.sendTextMail(user.getEmailAddr(), subject, notification.getContent());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    }
	
    public void sendChatMessage(HandyMenUserContactInfo user, HandyMenChatMessage message) {
    	
    	
    	
    	
    	
    	
    }
	
	
	
	
	
	
	
}
