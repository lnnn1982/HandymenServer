package com.ece651.handymenserver.Domain;

public class HandyMenUsrServiceInfo {
    private String usrName;
	private HandyMenSvrTypeEnum type;
	private String area;
	private String description;
	//private attachments;
	
	public HandyMenUsrServiceInfo(HandyMenSvrTypeEnum type, String usrName) {
		this.type = type;
		this.usrName = usrName;
	}
	
	public HandyMenSvrTypeEnum getType() {
		return type;
	}
	
	public String getUsrName() {
		return usrName;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getArea() {
    	return area;
    }
    
	public void setDescription(String description) {
    	this.description = description;
    }
    
	public String getDescription() {
    	return description;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
}
