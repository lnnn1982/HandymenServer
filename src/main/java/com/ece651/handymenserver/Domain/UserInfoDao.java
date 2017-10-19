package com.ece651.handymenserver.Domain;

import java.util.List;

public interface UserInfoDao {
    void addUser(HandyMenUserInfo user) throws Exception;
    void updateUser(HandyMenUserInfo user) throws Exception;
    List<HandyMenUserInfo> listUsers(HandyMenSvrTypeEnum type);
    List<HandyMenUserInfo> listServiceUsers(String usrName);
    HandyMenUserInfo getUser(String usrName)throws Exception;

}
