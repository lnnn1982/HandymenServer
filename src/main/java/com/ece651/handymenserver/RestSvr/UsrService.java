package com.ece651.handymenserver.RestSvr;

import java.util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ece651.handymenserver.Domain.*;
import com.ece651.handymenserver.storage.*;
import com.ece651.handymenserver.notification.*;

@RestController
public class UsrService {

	@Autowired
    private HandyMenUserProfileDao usrProfileDao;
	@Autowired
    private HandyMenUserReviewDao usrReviewDao;
	
	@Autowired
    private NotificationService notificationService;
	
	@Autowired
    private StorageService storageService;
	
	@Autowired
	private HandyMenChatMessageDao chatMessageDao;
	
	@Autowired
	private HandyMenNotificationDao notificationDao;
	
	private Map<String, HandyMenUserProfile> waitUsers = new ConcurrentHashMap<>();
	private Map<String, String> usrNameVerifyCodeMap = new ConcurrentHashMap<>();
	
	
	@ExceptionHandler(Exception.class)
	public void handleException(HttpServletResponse response,
			Exception ex) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value(), 
	    		ex.getMessage());
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public ResponseMessage login(@RequestParam("usrName") String usrName,
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
	public ResponseMessage addUser(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam(value="phoneNumList", defaultValue="") String phoneNumList,
			@RequestParam(value="uploadFileNames", defaultValue="") String uploadFileNames,
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
		user.setUploadFileNames(uploadFileNames);
		
		HandyMenUserAuth auth = new HandyMenUserAuth(usrName, passwd);	
		HandyMenUserProfile profile = new HandyMenUserProfile(user, auth);
		waitUsers.put(usrName, profile);

		Random rand = new Random();
		String verificationCode = String.valueOf(Math.abs(rand.nextLong()));
		usrNameVerifyCodeMap.put(usrName, verificationCode);
		
		//send email to check
		notificationService.sendVerificationCode(user, verificationCode);
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"Already sent email, wait for verification code."
				+ " verification code:" + verificationCode);
	}
	
    @RequestMapping(value="/activateUser", method=RequestMethod.GET)
	public ResponseMessage activateUser(@RequestParam("usrName") String usrName, 
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
	public ResponseMessage updateUserContactInfo(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam("phoneNumList") String phoneNumList,
			@RequestParam("uploadFileNames") String uploadFileNames,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.checkUserAndEmail(usrName, emailAddr)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or email address is not valid");
		}
		
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		HandyMenUserContactInfo contact = new HandyMenUserContactInfo(usrName);
		contact.setEmailAddr(emailAddr);
		contact.setPhoneNumList(phoneNumList);
		contact.setUploadFileNames(uploadFileNames);
		
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
	public ResponseMessage updatePasswd(@RequestParam("usrName") String usrName, 
			@RequestParam("oldPasswd") String oldPasswd,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, oldPasswd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or oldPasswd not valid");
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
    public ResponseMessage listServiceTypes(@RequestParam("usrName") String usrName, 
			@RequestParam("passwd") String passwd) throws Exception
	{
    	if(usrName.equalsIgnoreCase(HandyMenUserContactInfo.GUEST_USR_NAME)) {
        	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
    				HandyMenSvrTypeEnum.getTypeStr());	
    	}

		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
    	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				HandyMenSvrTypeEnum.getTypeStr());
	}
    
    @RequestMapping(value="/addUserServiceInfo", method=RequestMethod.POST)
	public ResponseMessage addUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type,
			@RequestParam(value="area", defaultValue="") String area,
			@RequestParam(value="description", defaultValue="") String description,
			@RequestParam(value="priceRange", defaultValue="") String priceRange,
			@RequestParam(value="uploadFileNames", defaultValue="") String uploadFileNames,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
		
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
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
		serviceInfo.setUploadFileNames(uploadFileNames);
    	
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
	public ResponseMessage updateUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type,
			@RequestParam("area") String area,
			@RequestParam("description") String description,
			@RequestParam("priceRange") String priceRange,
			@RequestParam("uploadFileNames") String uploadFileNames,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
		
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
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
		serviceInfo.setUploadFileNames(uploadFileNames);
    	
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
	public ResponseMessage deleteUserServiceInfo(
			@RequestParam("usrName") String usrName, 
			@RequestParam("type") String type,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!HandyMenSvrTypeEnum.isTypeValid(type)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"type not valid");
		}
		
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
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
	public HandyMenUserProfile getUserProfile(@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			throw new Exception("user name or passwd not valid");
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
	public ResponseMessage deleteUser(@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
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
	public List<HandyMenUserProfile> listAllServiceUsers(
			@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
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
			if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
				throw new Exception("user name or passwd not valid");
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
	public List<HandyMenUserProfile> listServiceUsersByServiceType(
			@RequestParam("usrName") String usrName,
			@RequestParam("type") String type,
			@RequestParam("passwd") String passwd) throws Exception
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
			if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
				throw new Exception("user name or passwd not valid");
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
	public ResponseMessage addUserReview(@RequestParam("usrName") String usrName, 
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType,
			@RequestParam(value="reviewContent", defaultValue="") String reviewContent,
			@RequestParam("rank") int rank,
			@RequestParam("reviewUsrPasswd") String reviewUsrPasswd) throws Exception
	{
		if(usrName.equals(reviewUsrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review user the same as user");
		}
		
		if(!usrProfileDao.isUserPasswordValid(reviewUsrName, reviewUsrPasswd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review user name or passwd not valid");
		}
		
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
		
		try {
			HandyMenUserProfile profile = usrProfileDao.getUser(usrName);
			
			String content = "User " + reviewUsrName + " add a review for you.";	
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HandyMenNotification notification = new HandyMenNotification(usrName,
					df.format(new Date()),
					HandyMenNotification.TypeEnum.ReviewType, content);
			
			notificationService.sendNotification(profile.getContactInfo(), 
					notification);
			
			notificationDao.addHandyMenNotification(notification);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"add user review successfully");
	}
	
	@RequestMapping(value="/updateUserReview", method=RequestMethod.POST)
	public ResponseMessage updateUserReview(@RequestParam("usrName") String usrName, 
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType,
			@RequestParam("reviewContent") String reviewContent,
			@RequestParam("rank") int rank,
			@RequestParam("reviewUsrPasswd") String reviewUsrPasswd) throws Exception
	{
		if(!usrReviewDao.isReviewExist(usrName, reviewUsrName, svrType)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review not exist");
		}
		
		if(!usrProfileDao.isUserPasswordValid(reviewUsrName, reviewUsrPasswd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review user name or passwd not valid");
		}
		
		HandyMenUserReview review = new HandyMenUserReview(usrName, reviewUsrName, 
				Enum.valueOf(HandyMenSvrTypeEnum.class, svrType), reviewContent, rank);
		try {
			usrReviewDao.updateUserReview(review);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			HandyMenUserProfile profile = usrProfileDao.getUser(usrName);
			
			String content = "User " + reviewUsrName + " update a review for you.";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HandyMenNotification notification = new HandyMenNotification(usrName,
					df.format(new Date()), 
					HandyMenNotification.TypeEnum.ReviewType, content);
			
			notificationService.sendNotification(profile.getContactInfo(), 
					notification);
			notificationDao.addHandyMenNotification(notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"update user review successfully");
	}
	
	@RequestMapping(value="/listReviewByUserName", method=RequestMethod.GET)
	public List<HandyMenUserReview> listReviewByUserName(
			@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			throw new Exception("user name or passwd not valid");
		}

		try {
			return usrReviewDao.listUsersReviewByName(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@RequestMapping(value="/deleteUserReview", method=RequestMethod.GET)
	public ResponseMessage deleteUserReview(
			@RequestParam("usrName") String usrName,
			@RequestParam("reviewUsrName") String reviewUsrName,
			@RequestParam("svrType") String svrType,
			@RequestParam("reviewUsrPasswd") String reviewUsrPasswd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(reviewUsrName, reviewUsrPasswd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"review user name or passwd not valid");
		}
		
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
	
	
	@RequestMapping(value="/uploadFile", method=RequestMethod.POST)
	public ResponseMessage uploadFile(
			@RequestParam("usrName") String usrName,
			@RequestParam("file") MultipartFile file,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		try {
			storageService.store(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"upload file successfully");
	}
	
	@RequestMapping(value="/addUserChatMessage", method=RequestMethod.POST)
	public ResponseMessage addUserChatMessage(@RequestParam("usrName") String usrName, 
			@RequestParam("peerUsrName") String peerUsrName,
			@RequestParam("timeStamp") String timeStamp,
			@RequestParam("content") String content,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(usrName.equals(peerUsrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"usrName and peerUsrName are the same");
		}
		
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		if(chatMessageDao.isChatMessageExist(usrName, peerUsrName, timeStamp, usrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"chat message already exist");
		}
		
		if(chatMessageDao.isChatMessageExist(usrName, peerUsrName, timeStamp, peerUsrName)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"chat message already exist");
		}
		
		if(!(usrProfileDao.isUserExit(usrName) && 
				usrProfileDao.isUserExit(peerUsrName))) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or peer user name not valid");
		}
		
		HandyMenChatMessage message = new HandyMenChatMessage(usrName, peerUsrName, 
				timeStamp, content);
		try {
			chatMessageDao.addHandyMenChatMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			HandyMenUserProfile profile = usrProfileDao.getUser(peerUsrName);
			notificationService.sendChatMessage(profile.getContactInfo(), 
					message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"add user chat message successfully");
	}
	
	@RequestMapping(value="/deleteUserChatMessage", method=RequestMethod.POST)
	public ResponseMessage deleteUserChatMessage(
			@RequestParam("usrName") String usrName,
			@RequestParam("peerUsrName") String peerUsrName,
			@RequestParam("timeStamp") String timeStamp,
			@RequestParam("appUser") String appUser,
			@RequestParam("appUserPasswd") String appUserPasswd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(appUser, appUserPasswd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		if(!chatMessageDao.isChatMessageExist(usrName, peerUsrName, timeStamp, appUser)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"chat message not exist");
		}

		try {
			chatMessageDao.deleteHandyMenChatMessage(usrName, peerUsrName, timeStamp, appUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"delete user chat message successfully");
	}
	
	@RequestMapping(value="/listUserChatMessageByUserName", method=RequestMethod.GET)
	public List<HandyMenChatMessage> listUserChatMessageByUserName(
			@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			throw new Exception("user name or passwd not valid");
		}

		try {
			return chatMessageDao.listUsersChatMessages(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
    @RequestMapping(value="/listNotificationTypes", method=RequestMethod.GET)
    public ResponseMessage listNotificationTypes(
    		@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
    	if(usrName.equalsIgnoreCase(HandyMenUserContactInfo.GUEST_USR_NAME)) {
        	return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
        			HandyMenNotification.TypeEnum.getTypeStr());
    	}
    	
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
    			HandyMenNotification.TypeEnum.getTypeStr());
	}
	
	@RequestMapping(value="/deleteUserNotification", method=RequestMethod.POST)
	public ResponseMessage deleteUserNotification(
			@RequestParam("usrName") String usrName,
			@RequestParam("notificationType") String notificationType,
			@RequestParam("timeStamp") String timeStamp,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"user name or passwd not valid");
		}
		
		if(!notificationDao.isNotificationExist(usrName, notificationType, timeStamp)) {
			return new ResponseMessage(ResponseMessage.OpStatus.OP_FAIL, 
					"notification not exist");
		}

		try {
			notificationDao.deleteHandyMenNotification(usrName, notificationType, timeStamp);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return new ResponseMessage(ResponseMessage.OpStatus.OP_OK, 
				"delete user notification successfully");
	}
	
	@RequestMapping(value="/listUserNotificationByUserName", method=RequestMethod.GET)
	public List<HandyMenNotification> listUserNotificationByUserName(
			@RequestParam("usrName") String usrName,
			@RequestParam("passwd") String passwd) throws Exception
	{
		if(!usrProfileDao.isUserPasswordValid(usrName, passwd)) {
			throw new Exception("user name or passwd not valid");
		}

		try {
			return notificationDao.listUsersNotifications(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
}