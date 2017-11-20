package com.ece651.handymenserver.Domain;

public class HandyMenSvrTypeInfo {
    private HandyMenSvrTypeEnum type;
    private int id;
    private String uploadFileNames;
    
    public HandyMenSvrTypeInfo(HandyMenSvrTypeEnum type, int id,
    		String uploadFileNames) {
    	this.type = type;
    	this.id = id;
    	this.uploadFileNames = uploadFileNames;
    }
    
    public String getUploadFileNames() {
    	return uploadFileNames;
    }
    
    public int getId() {
    	return id;
    }
    
    public HandyMenSvrTypeEnum getType() {
    	return type;
    }
	
}
