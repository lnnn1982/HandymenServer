package com.ece651.handymenserver.RestSvr;

import java.util.*;
import java.io.IOException;
import java.util.concurrent.*;

import javax.servlet.http.*;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.ece651.handymenserver.Domain.*;

@RestController
public class UsrService {

	@Autowired
    private HandyMenUserProfileDao usrProfileDao;
	@Autowired
    private HandyMenUserReviewDao usrReviewDao;	
	
	Map<String, HandyMenUserProfile> waitUsers = new ConcurrentHashMap<>();
	Map<String, String> usrNameVerifyCodeMap = new ConcurrentHashMap<>();
	
	
	@ExceptionHandler(Exception.class)
	void handleException(HttpServletResponse response,
			Exception ex) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value(), 
	    		ex.getMessage());
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	ResponseMessage login(@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserExit(usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user not exist");
		}
		
		HandyMenUserAuth auth = null;
		
		try {
			auth = usrProfileDao.getUsrAuth(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		if(auth.getPasswd().equals(passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
					"Login successfully");
		}
		else {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"Password is wrong");
		}
	}
	
	@RequestMapping(value="/addUser", method=RequestMethod.GET)
	ResponseMessage addUser(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam(value="phoneNumList", defaultValue="") String phoneNumList,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(usrName.equalsIgnoreCase(HandyMenUserContactInfo.GUEST_USR_NAME)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name can not be " + usrName);
		}
		
		if(usrProfileDao.isUserExit(usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name already in use");
		}
		
		if(usrProfileDao.isEmailExit(emailAddr)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"email address already in use");
		}

		HandyMenUserContactInfo user = new HandyMenUserContactInfo(usrName);
		user.setPhoneNumList(phoneNumList);
		user.setEmailAddr(emailAddr);
		
		HandyMenUserAuth auth = new HandyMenUserAuth(usrName, passwd);	
		HandyMenUserProfile profile = new HandyMenUserProfile(user, auth);
		waitUsers.put(usrName, profile);

		Random rand = new Random();
		String verificationCode = String.valueOf(Math.abs(rand.nextLong()));
		usrNameVerifyCodeMap.put(usrName, verificationCode);
		
		//send email to check
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"Already sent email, wait for verification code."
				+ " verification code:" + verificationCode);
	}
	
    @RequestMapping(value="/activateUser", method=RequestMethod.GET)
	ResponseMessage activateUser(@RequestParam("usrName") String usrName, 
			@RequestParam("verificationCode") String code) throws Exception
	{
    	String storeVeriCode = usrNameVerifyCodeMap.get(usrName);
    	HandyMenUserProfile profile = waitUsers.get(usrName);
    	
    	if(storeVeriCode == null || profile == null) {
    		return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user not exist");
    	}
    	
    	if(!storeVeriCode.equals(code)) {
    		return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"verification code is wrong");    		
    	}
    	
    	usrNameVerifyCodeMap.remove(usrName);
    	
    	try {
    		usrProfileDao.addUserBasicInfo(profile.getContactInfo(), profile.getAuthInfo());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	waitUsers.remove(usrName);
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"activate user successfully");
	}
	
    @RequestMapping(value="/updateUserContanctInfo", method=RequestMethod.GET)
	ResponseMessage updateUserContanctInfo(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam("phoneNumList") String phoneNumList) throws Exception
	{
		if(usrProfileDao.checkUserAndEmail(usrName, emailAddr)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or email address is not valid");
		}
		
		HandyMenUserContactInfo contact = new HandyMenUserContactInfo(usrName);
		contact.setEmailAddr(emailAddr);
		contact.setPhoneNumList(phoneNumList);
		
    	try {
    		usrProfileDao.updateUserContactInfo(contact);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"update contact info successfully");
	}
    
    @RequestMapping(value="/listServiceTypes", method=RequestMethod.GET)
    ResponseMessage listServiceTypes()
	{
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				HandyMenSvrTypeEnum.BABYSITER_TYPE.toString() + ","
				+ "");
	}
    
    
	
//	@RequestMapping("/getUserProfile")
//	HandyMenUserContactInfo getUser(@RequestParam("usrName") String usrName ) {
//		try {
//
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new IllegalArgumentException("The usrName is error");
//		}
//	}	
//	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}