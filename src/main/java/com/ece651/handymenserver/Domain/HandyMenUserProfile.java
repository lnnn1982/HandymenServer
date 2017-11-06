package com.ece651.handymenserver.Domain;

public class HandyMenUserProfile {
    private HandyMenUsrServiceInfo serviceInfo;
    private HandyMenUserContactInfo contactInfo;
    private HandyMenUserAuth authInfo;
    
    public HandyMenUserProfile(HandyMenUserContactInfo contactInfo,
    		HandyMenUserAuth authInfo) {
    	this.contactInfo = contactInfo;
    	this.serviceInfo = new HandyMenUsrServiceInfo(
    			HandyMenSvrTypeEnum.NOSERVICE_TYPE, contactInfo.getUsrName());
    	this.authInfo = authInfo;
    }
    
    public HandyMenUserProfile(HandyMenUserContactInfo contactInfo) {
    	this.contactInfo = contactInfo;
    	this.serviceInfo = new HandyMenUsrServiceInfo(
    			HandyMenSvrTypeEnum.NOSERVICE_TYPE, contactInfo.getUsrName());
    	this.authInfo = null;
    }
    
    HandyMenUserContactInfo getContactInfo() {
    	return contactInfo;
    }	
    
    void setServiceInfo(HandyMenUsrServiceInfo serviceInfo) {
    	this.serviceInfo = serviceInfo;
    }
    
    HandyMenUsrServiceInfo getServiceInfo() {
    	return serviceInfo;
    }
	
    HandyMenUserAuth getAuthInfo() {
    	return authInfo;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
