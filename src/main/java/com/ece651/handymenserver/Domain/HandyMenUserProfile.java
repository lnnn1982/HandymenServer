package com.ece651.handymenserver.Domain;

import java.util.*;

public class HandyMenUserProfile {
    private List<HandyMenUsrServiceInfo> serviceInfoList = new ArrayList<>();
    private HandyMenUserContactInfo contactInfo;
    private HandyMenUserAuth authInfo;
    
    public HandyMenUserProfile(HandyMenUserContactInfo contactInfo,
    		HandyMenUserAuth authInfo) {
    	this.contactInfo = contactInfo;
    	this.authInfo = authInfo;
    }
    
    public HandyMenUserContactInfo getContactInfo() {
    	return contactInfo;
    }	
    
    public void addServiceInfo(HandyMenUsrServiceInfo serviceInfo) {
    	this.serviceInfoList.add(serviceInfo);
    }
    
    public List<HandyMenUsrServiceInfo> getServiceInfoList() {
    	return serviceInfoList;
    }
    
    public void setServiceInfoList(List<HandyMenUsrServiceInfo> serviceInfoList) {
    	this.serviceInfoList = serviceInfoList;
    }
	
    public HandyMenUserAuth getAuthInfo() {
    	return authInfo;
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
