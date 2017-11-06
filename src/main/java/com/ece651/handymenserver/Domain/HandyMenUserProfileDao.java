package com.ece651.handymenserver.Domain;

import java.util.List;

public interface HandyMenUserProfileDao {
    void addUser(HandyMenUserProfile user) throws Exception;
    void updateUser(HandyMenUserProfile user) throws Exception;
    HandyMenUserProfile getUser(String usrName)throws Exception;
    void deleteUser(String userName)throws Exception;
    
    List<HandyMenUserProfile> listUsersByServiceType(HandyMenSvrTypeEnum type, String usrName) 
    		throws Exception;
    List<HandyMenUserProfile> listAllServiceUsers(String usrName)throws Exception;
    
    void updatePasswd(String usr, String passwd) throws Exception;
    HandyMenUserAuth getUsrAuth(String usrName)throws Exception;
}
