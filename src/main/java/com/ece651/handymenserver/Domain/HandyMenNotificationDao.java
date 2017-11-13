package com.ece651.handymenserver.Domain;

import java.util.List;

public interface HandyMenNotificationDao {
    void addHandyMenNotification(HandyMenNotification notification) throws Exception;
	void deleteHandyMenNotification(String usrName, HandyMenNotification.TypeEnum notificationType,
			String timeStamp) throws Exception;
	
    List<HandyMenNotification> listUsersNotifications(String usrName) throws Exception;
	
}
