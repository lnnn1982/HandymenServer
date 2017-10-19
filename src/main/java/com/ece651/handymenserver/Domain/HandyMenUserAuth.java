package com.ece651.handymenserver.Domain;

public class HandyMenUserAuth {
    private String usrName;
    private String passwd;
    
    HandyMenUserAuth(String usrName, String passwd) {
    	this.usrName = usrName;
    	this.passwd = passwd;
    }
    
    String getUsrName() {
    	return usrName;
    }
    
    String getPasswd() {
    	return passwd;
    }
	
	
	
	
	
	
	
	
	
}
