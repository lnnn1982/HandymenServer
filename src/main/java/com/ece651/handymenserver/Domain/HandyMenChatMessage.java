package com.ece651.handymenserver.Domain;

public class HandyMenChatMessage {
    private String usrName;
    private String peerUsrName;
    private String timeStamp;
    private String content;
    
    public HandyMenChatMessage(String usrName, String peerUsrName,
    		String timeStamp, String content) {
    	this.usrName = usrName;
    	this.peerUsrName = peerUsrName;
    	this.timeStamp = timeStamp;
    	this.content = content;
    }
    
    public String getUsrName() {
    	return usrName;
    }
    
    public String getPeerUsrName() {
    	return peerUsrName;
    }
    
    public String getTimeStamp() {
    	return timeStamp;
    }
    
    public String getContent() {
    	return content;
    }
    
	
}
