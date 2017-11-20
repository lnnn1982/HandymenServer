package com.ece651.handymenserver.Domain;

public enum HandyMenSvrTypeEnum {
	
	PLUMBER_TYPE,
	CARPENTER_TYPE,
	ELECTRICIAN_TYPE,
	BABYSITER_TYPE,
	INTERIORPAITING_TYPE,
	HOMECLEANING_TYPE,
	LANDSCAPING_TYPE,
	OTHER_TYPE;
    
    public static Boolean isTypeValid(String type) {
    	try {
			Enum.valueOf(HandyMenSvrTypeEnum.class, type);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public static Boolean isIdValid(int id) {
    	return (id >= 1) && (id <= 8);
    }

	
	
//	private String svrTypeName;
//	
//	private HandyMenSvrTypeEnum(String svrTypeName) {
//		this.svrTypeName = svrTypeName;
//	}
//
//    public String getSvrTypeName() {
//    	return svrTypeName;
//    }
//	
//	public static HandyMenSvrTypeEnum fromStr(String str) {
//		if(str == "Plumber") return HandyMenSvrTypeEnum.PLUMBER_TYPE;
//		if(str == "Carpenter") return HandyMenSvrTypeEnum.CARPENTER_TYPE;
//		if(str == "Electrician") return HandyMenSvrTypeEnum.ELECTRICIAN_TYPE;
//		if(str == "BabySitter") return HandyMenSvrTypeEnum.BABYSITER_TYPE;
//		if(str == "Other") return HandyMenSvrTypeEnum.OTHER_TYPE;
//		if(str == "NoService") return HandyMenSvrTypeEnum.NOSERVICE_TYPE;
//		
//		return NOSERVICE_TYPE;
//	}
//	
	
	
	
	
}
