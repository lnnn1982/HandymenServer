package com.ece651.handymenserver.RestSvr;

import java.util.*;
import java.io.IOException;

import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.ece651.handymenserver.Domain.*;

@RestController
public class UsrService {

	@Autowired
    private UsrSvrInfoDao usrSvrInfoDao;
	@Autowired
    private UserInfoDao usrInfoDao;	
	
	
	@ExceptionHandler(IllegalArgumentException.class)
	void handleIllegalArgumentException(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value(), 
	    		"Please try again with valid parameter");
	}
	
	@RequestMapping("/addUser")
	HandyMenUserInfo addUser(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam("phoneNumList") String phoneNumList) {
		if(usrName == HandyMenUserInfo.GUEST_USR_NAME) {
			throw new IllegalArgumentException("The usrName is error");
		}
		
		try {
			HandyMenUserInfo user = new HandyMenUserInfo(usrName);
			//user.setPhoneNumList(Arrays.asList(phoneNumList.split("-")));
			user.setPhoneNumList(phoneNumList);
			user.setEmailAddr(emailAddr);
			usrInfoDao.addUser(user);
			
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("The usrName is error");
		}
	}
	
	@RequestMapping("/getUser")
	HandyMenUserInfo getUser(@RequestParam("usrName") String usrName ) {
		try {
			return usrInfoDao.getUser(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("The usrName is error");
		}
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}