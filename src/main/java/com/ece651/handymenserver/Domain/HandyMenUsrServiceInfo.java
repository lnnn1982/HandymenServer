package com.ece651.handymenserver.Domain;

public class HandyMenUsrServiceInfo {
    private String usrName = "";
	private HandyMenSvrTypeEnum type = HandyMenSvrTypeEnum.OTHER_TYPE;
	private String area = "";
	private String description = "";
	private String priceRange = "";
	private String uploadFileNames = "";
    private int reviewRank = 0;
	
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
	
	public void setPriceRange(String range) {
    	this.priceRange = range;
    }
    
	public String getPriceRange() {
    	return priceRange;
    }
    
    public void setReivewRank(int reviewRank) {
    	this.reviewRank = reviewRank;
    }
    
    public int getReviewRank() {
    	return reviewRank;
    }
	
	
	public void setUploadFileNames(String uploadFileNames) {
		this.uploadFileNames = uploadFileNames;
	}
	
	public String getUploadFileNames() {
		return uploadFileNames;
	}
	
	
	
	
	
	
	
	
}
