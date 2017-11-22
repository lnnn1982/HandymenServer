package com.ece651.handymenserver.Domain;

public class HandyMenSvrTypeInfo {
    private HandyMenSvrTypeEnum svrType;
    private int svrTypeId;
    private String svrTypeUploadFileNames;
    private String svrTypePrice = "";
    private String occasion = "";
    
    HandyMenSvrTypeInfo() {
    	this.svrType = HandyMenSvrTypeEnum.OTHER_TYPE;
    	this.svrTypeId = HandyMenSvrTypeEnum.OTHER_TYPE.ordinal()+1;
    }
    
    public HandyMenSvrTypeInfo(HandyMenSvrTypeEnum svrType, int svrTypeId,
    		String uploadFileNames, String svrTypePrice,
    		String occasion) {
    	this.svrType = svrType;
    	this.svrTypeId = svrTypeId;
    	this.svrTypeUploadFileNames = uploadFileNames;
    	this.occasion = occasion;
    	this.svrTypePrice = svrTypePrice;
    }
    
    public String getSvrTypeUploadFileNames() {
    	return svrTypeUploadFileNames;
    }
    
    public int getSvrTypeId() {
    	return svrTypeId;
    }
    
    public HandyMenSvrTypeEnum getSvrType() {
    	return svrType;
    }
	
    public String getSvrTypePrice() {
    	return svrTypePrice;
    }
    
    public String getOccasion() {
    	return occasion;
    }
    
    
    
    
    
    
    
}
