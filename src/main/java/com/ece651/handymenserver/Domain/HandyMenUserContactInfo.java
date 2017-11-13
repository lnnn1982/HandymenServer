package com.ece651.handymenserver.Domain;

public class HandyMenUserContactInfo {
	public static final String GUEST_USR_NAME = "Guest";
	
    private String usrName = "";
    private String emailAddr = "";
    private String phoneNumList = "";
    private String uploadFileNames = "";
    
	public HandyMenUserContactInfo(String usrName) {
		this.usrName = usrName;
	}
	
	public String getUsrName() {
		return usrName;
	}
	
	public String getEmailAddr() {
		return emailAddr;
	}
	
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public void setPhoneNumList(String phNums) {
		this.phoneNumList = phNums;
	}
	
	public String getPhoneNumList() {
		return this.phoneNumList;
	}
	
	public void setUploadFileNames(String uploadFileNames) {
		this.uploadFileNames = uploadFileNames;
	}
	
	public String getUploadFileNames() {
		return uploadFileNames;
	}

	
	
//	public static List<HandyMenSvrTypeEnum> formSvrTypeEnumListFrmoStr(String str) {
//		str.replaceAll("[", "");
//		str.replaceAll("]", "");
//		
//		List<String> enumStrList = Arrays.asList(str.split(","));
//		List<HandyMenSvrTypeEnum> enumTypeList = new ArrayList<>();
//		for (String oneEnumStr : enumStrList) {
//			oneEnumStr.trim();
//			
//			HandyMenSvrTypeEnum svrTypeEnum = Enum.valueOf(HandyMenSvrTypeEnum.class, oneEnumStr);
//			enumTypeList.add(svrTypeEnum);
//		}
//		
//		return enumTypeList;
//	}


}
