package com.ece651.handymenserver.notification;

import com.ece651.handymenserver.Domain.*;

public interface NotificationService {

    void sendVerificationCode(HandyMenUserContactInfo user, String code);
    void sendReviewNotification(HandyMenUserContactInfo user, HandyMenUserReview review);
    void sendUpdateReviewNotification(HandyMenUserContactInfo user, HandyMenUserReview review);
}
