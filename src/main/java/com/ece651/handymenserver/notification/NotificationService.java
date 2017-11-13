package com.ece651.handymenserver.notification;

import com.ece651.handymenserver.Domain.*;

public interface NotificationService {

    void sendVerificationCode(HandyMenUserContactInfo user, String code);
    void sendNotification(HandyMenUserContactInfo user, HandyMenNotification notification);
    void sendChatMessage(HandyMenUserContactInfo user, HandyMenChatMessage message);
}
