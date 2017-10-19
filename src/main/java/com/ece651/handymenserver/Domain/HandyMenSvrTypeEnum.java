package com.ece651.handymenserver.Domain;

public enum HandyMenSvrTypeEnum {
	
	PLUMBER_TYPE("Plumber"),
	CARPENTER_TYPE("Carpenter"),
	ELECTRICIAN_TYPE("Electrician"),
	BABYSITER_TYPE("BabySitter"),
	OTHER_TYPE("Other"),
	NOSERVICE_TYPE("NoService");
	
	
	private String svrTypeName;
	
	private HandyMenSvrTypeEnum(String svrTypeName) {
		this.svrTypeName = svrTypeName;
	}

    public String getSvrTypeName() {
    	return svrTypeName;
    }
	
	
	
	
	
	
	
}
