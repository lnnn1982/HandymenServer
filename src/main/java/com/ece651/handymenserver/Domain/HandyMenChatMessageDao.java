package com.ece651.handymenserver.Domain;

import java.util.List;

public interface HandyMenChatMessageDao {
    void addHandyMenChatMessage(HandyMenChatMessage message) throws Exception;
	void deleteHandyMenChatMessage(String usrName, String peerUsrName,
			String timeStamp) throws Exception;
    Boolean isChatMessageExist(String usrName, String peerUsrName,
			String timeStamp)throws Exception;
	
    List<HandyMenChatMessageDao> listUsersChatMessages(String usrName) throws Exception;
}
