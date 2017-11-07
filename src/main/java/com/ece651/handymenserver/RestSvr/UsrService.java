package com.ece651.handymenserver.RestSvr;

import java.util.*;
import java.io.IOException;
import java.util.concurrent.*;

import javax.servlet.http.*;

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
	
    @RequestMapping(value="/updateUserContactInfo", method=RequestMethod.GET)
	ResponseMessage updateUserContactInfo(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam("phoneNumList") String phoneNumList) throws Exception
	{
		if(!usrProfileDao.checkUserAndEmail(usrName, emailAddr)) {
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
    
    @RequestMapping(value="/updatePasswd", method=RequestMethod.GET)
	ResponseMessage updatePasswd(@RequestParam("usrName") String usrName, 
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserExit(usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user not exist");
		}

    	try {
    		usrProfileDao.updatePasswd(usrName, passwd);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"update password successfully");
	}
    
    @RequestMapping(value="/listServiceTypes", method=RequestMethod.GET)
    ResponseMessage listServiceTypes()
	{
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				HandyMenSvrTypeEnum.getTypeStr());
	}
    
    @RequestMapping(value="/addUserServiceInfo", method=RequestMethod.POST)
	ResponseMessage addUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type,
			@RequestParam(value="area", defaultValue="") String area,
			@RequestParam(value="description", defaultValue="") String description,
			@RequestParam(value="priceRange", defaultValue="") String priceRange) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
		
		if(!usrProfileDao.isUserExit(usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user not exist");
		}
		
		if(usrProfileDao.isUsrServiceTypeExist(usrName, type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user and service type already exist");
		}
		
		HandyMenUsrServiceInfo serviceInfo = new HandyMenUsrServiceInfo(
				Enum.valueOf(HandyMenSvrTypeEnum.class, type), usrName);
		serviceInfo.setArea(area);
		serviceInfo.setDescription(description);
		serviceInfo.setPriceRange(priceRange);
    	
    	try {
    		usrProfileDao.addUserServiceInfo(serviceInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"add user service info successfully");
	}
    
    @RequestMapping(value="/updateUserServiceInfo", method=RequestMethod.POST)
	ResponseMessage updateUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type,
			@RequestParam(value="area", defaultValue="") String area,
			@RequestParam(value="description", defaultValue="") String description,
			@RequestParam(value="priceRange", defaultValue="") String priceRange) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
    	
		if(!usrProfileDao.isUsrServiceTypeExist(usrName, type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user and service type not exist");
		}
		
		HandyMenUsrServiceInfo serviceInfo = new HandyMenUsrServiceInfo(
				Enum.valueOf(HandyMenSvrTypeEnum.class, type), usrName);
		serviceInfo.setArea(area);
		serviceInfo.setDescription(description);
		serviceInfo.setPriceRange(priceRange);
    	
    	try {
    		usrProfileDao.updateUserServiceInfo(serviceInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"update user service info successfully");
	}    
    
    @RequestMapping(value="/deleteUserServiceInfo", method=RequestMethod.GET)
	ResponseMessage deleteUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
    	
		if(!usrProfileDao.isUsrServiceTypeExist(usrName, type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user and service type not exist");
		}
    	
    	try {
    		usrProfileDao.deleteServiceInfo(usrName, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    	
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"delete user service info successfully");
	}
	
	@RequestMapping("/getUserProfile")
	HandyMenUserProfile getUserProfile(@RequestParam("usrName") String usrName) throws Exception
	{
		if(!usrProfileDao.isUserExit(usrName)) {
			throw new Exception("user not exist");
		}
		
		try {
			HandyMenUserProfile profile = usrProfileDao.getUser(usrName);
			return profile;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping("/deleteUser")
	ResponseMessage deleteUser(@RequestParam("usrName") String usrName) throws Exception
	{
		if(!usrProfileDao.isUserExit(usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user not exist");
		}
		
		try {
			usrProfileDao.deleteUser(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"delete user successfully");
	}	
	
	@RequestMapping("/listAllServiceUsers")
	List<HandyMenUserProfile> listAllServiceUsers(@RequestParam("usrName") String usrName) throws Exception
	{
		if(usrName.equalsIgnoreCase(HandyMenUserContactInfo.GUEST_USR_NAME)) {
			try {
				return usrProfileDao.listSimpleUserProfiles();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		else {
			if(!usrProfileDao.isUserExit(usrName)) {
				throw new Exception("user not exist");
			}
			
			try {
				return usrProfileDao.listFullUserProfiles();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	@RequestMapping("/listServiceUsersByServiceType")
	List<HandyMenUserProfile> listServiceUsersByServiceType(
			@RequestParam("usrName") String usrName,
			@RequestParam("type") String type) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			throw new Exception("service type not valid");
		}
		
		if(usrName.equalsIgnoreCase(HandyMenUserContactInfo.GUEST_USR_NAME)) {
			try {
				return usrProfileDao.listSimpleUserProfilesBySvrType(type);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		else {
			if(!usrProfileDao.isUserExit(usrName)) {
				throw new Exception("user not exist");
			}
			
			try {
				return usrProfileDao.listFullUserProfilesBySvrType(type);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	@RequestMapping(value="/addUserReview", method=RequestMethod.POST)
	ResponseMessage addUserReview(@RequestParam("usrName") String usrName, 
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType,
			@RequestParam(value="reviewContent", defaultValue="") String reviewContent,
			@RequestParam("rank") int rank) throws Exception
	{
		if(usrReviewDao.isReviewExist(usrName, reviewUsrName, svrType)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review already exist");
		}
		
		if(!(usrProfileDao.isUserExit(usrName) && 
				usrProfileDao.isUserExit(reviewUsrName))) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or review user name not valid");
		}
		
		if(!HandyMenSvrTypeEnum.isTypeValid(svrType)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"service type not valid");
		}
		
		HandyMenUserReview review = new HandyMenUserReview(usrName, reviewUsrName, 
				Enum.valueOf(HandyMenSvrTypeEnum.class, svrType), reviewContent, rank);
		try {
			usrReviewDao.addUserReview(review);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"add user review successfully");
	}
	
	@RequestMapping(value="/updateUserReview", method=RequestMethod.POST)
	ResponseMessage updateUserReview(@RequestParam("usrName") String usrName, 
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType,
			@RequestParam(value="reviewContent", defaultValue="") String reviewContent,
			@RequestParam("rank") int rank) throws Exception
	{
		if(!usrReviewDao.isReviewExist(usrName, reviewUsrName, svrType)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review not exist");
		}
		
		HandyMenUserReview review = new HandyMenUserReview(usrName, reviewUsrName, 
				Enum.valueOf(HandyMenSvrTypeEnum.class, svrType), reviewContent, rank);
		try {
			usrReviewDao.updateUserReview(review);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"update user review successfully");
	}
	
	@RequestMapping(value="/listReviewByUserName", method=RequestMethod.GET)
	List<HandyMenUserReview> listReviewByUserName(
			@RequestParam("usrName") String usrName) throws Exception
	{
		if(!usrProfileDao.isUserExit(usrName)) {
			throw new Exception("user not exist");
		}

		try {
			return usrReviewDao.listUsersReviewByName(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping(value="/deleteUserReview", method=RequestMethod.GET)
	ResponseMessage deleteUserReview(
			@RequestParam("usrName") String usrName,
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType) throws Exception
	{
		if(!usrReviewDao.isReviewExist(usrName, reviewUsrName, svrType)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review not exist");
		}

		try {
			usrReviewDao.deleteUserReview(usrName, reviewUsrName, svrType);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"delete review successfully");
	}
	
	
	
}