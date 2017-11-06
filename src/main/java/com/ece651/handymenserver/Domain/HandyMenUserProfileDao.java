package com.ece651.handymenserver.Domain;

import java.util.List;
import java.util.Map;

public interface HandyMenUserProfileDao {
    void addUser(HandyMenUserProfile user) throws Exception;
    void updateUser(HandyMenUserProfile user) throws Exception;
    HandyMenUserProfile getUser(String usrName)throws Exception;
    void deleteUser(String userName)throws Exception;
    
    Boolean isUserExit(String usrName);
    Boolean isEmailExit(String emailAddr);
    
    List<HandyMenUserProfile> listFullUserProfiles(Map<String, String> searchFields,
    		Map<String, String> searchNotFiles,
    		String orderField,
    		Boolean isDesc) throws Exception;
    
    List<HandyMenUserProfile> listSimpleUserProfiles(Map<String, String> searchFields,
    		Map<String, String> searchNotFiles,
    		String orderField,
    		Boolean isDesc) throws Exception;    
    

    void updatePasswd(String usr, String passwd) throws Exception;
    HandyMenUserAuth getUsrAuth(String usrName)throws Exception;
}
