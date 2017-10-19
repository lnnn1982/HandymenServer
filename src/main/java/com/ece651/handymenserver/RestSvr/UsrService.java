package com.ece651.handymenserver.RestSvr;

import java.util.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.ece651.handymenserver.Domain.*;

@RestController
public class UsrService {

	@Autowired
    private UsrSvrInfoDao usrSvrInfoDao;
	@Autowired
    private UserInfoDao usrInfoDao;	
	
	
	
	
	@RequestMapping("/addUser")
	HandyMenUserInfo addUser(@RequestParam("usrName") String usrName, 
			@RequestParam("emailAddr") String emailAddr,
			@RequestParam("phoneNumList") String phoneNumList) {
		if(usrName == HandyMenUserInfo.GUEST_USR_NAME) {
			return null;
		}
		
		try {
			HandyMenUserInfo user = new HandyMenUserInfo(usrName);
			user.setPhoneNumList(Arrays.asList(phoneNumList.split("-")));
			user.setEmailAddr(emailAddr);
			usrInfoDao.addUser(user);
			
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getUser")
	HandyMenUserInfo getUser(@RequestParam("usrName") String usrName ) {
		try {
			return usrInfoDao.getUser(usrName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}