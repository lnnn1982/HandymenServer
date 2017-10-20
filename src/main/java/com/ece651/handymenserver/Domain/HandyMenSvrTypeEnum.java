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
	
	public static HandyMenSvrTypeEnum fromStr(String str) {
		if(str == "Plumber") return HandyMenSvrTypeEnum.PLUMBER_TYPE;
		if(str == "Carpenter") return HandyMenSvrTypeEnum.CARPENTER_TYPE;
		if(str == "Electrician") return HandyMenSvrTypeEnum.ELECTRICIAN_TYPE;
		if(str == "BabySitter") return HandyMenSvrTypeEnum.BABYSITER_TYPE;
		if(str == "Other") return HandyMenSvrTypeEnum.OTHER_TYPE;
		if(str == "NoService") return HandyMenSvrTypeEnum.NOSERVICE_TYPE;
		
		return NOSERVICE_TYPE;
	}
	
	
	
	
	
}
