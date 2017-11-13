package com.ece651.handymenserver.Domain;

public class HandyMenNotification {
    static public enum TypeEnum {
    	ReviewType;
    	
        public static String getTypeStr() {
        	String str = ReviewType.toString();
        	return str;
        }
        
        public static Boolean isTypeValid(String type) {
        	try {
    			Enum.valueOf(TypeEnum.class, type);
    			return true;
    		} catch (Exception e) {
    			return false;
    		}
        }
    }
	
    private String usrName;
    private String timeStamp;
    private TypeEnum notificationType;
    private String content;
    
    public HandyMenNotification(String usrName, String timeStamp,
    		TypeEnum type, String content) {
    	this.usrName = usrName;
    	this.timeStamp = timeStamp;
    	this.notificationType = type;
    	this.content = content;
    }
    
    public String getUsrName() {
    	return usrName;
    }
    
    public String getTimeStamp() {
    	return timeStamp;
    }
    
    public TypeEnum getNotificationType () {
    	return notificationType;
    }
    
    public String getContent() {
    	return content;
    }
    
	
}
