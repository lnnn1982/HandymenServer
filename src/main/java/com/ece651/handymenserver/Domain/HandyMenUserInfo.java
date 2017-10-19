package com.ece651.handymenserver.Domain;

import java.util.Collections;
import java.util.List;

public class HandyMenUserInfo {
	public static final String GUEST_USR_NAME = "Guest";
	
    private String usrName;
    private String emailAddr;
    private List<String> phoneNumList;
    private HandyMenSvrTypeEnum svrType;
    
	public HandyMenUserInfo(String usrName) {
		this.usrName = usrName;
		this.svrType = HandyMenSvrTypeEnum.NOSERVICE_TYPE;
	}
	
	public String getUsrName() {
		return usrName;
	}

	public void setEmailAddr(String emailAddr) {
	    this.emailAddr = emailAddr;	
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setPhoneNumList(List<String> phNums) {
		this.phoneNumList = phNums;
	}
	
	public List<String> getPhoneNumList() {
		return Collections.unmodifiableList(this.phoneNumList);
	}
	
	public void setSvrType(HandyMenSvrTypeEnum svrType) {
		this.svrType = svrType;
	}
	
	public HandyMenSvrTypeEnum getSvrType() {
		return svrType;
	}


}
