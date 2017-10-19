package com.ece651.handymenserver.Domain;

public interface UsrSvrInfoDao {
    void addUsrServiceInfo(HandyMenUsrServiceInfo svrInfo)throws Exception;
    void updateUsrServiceInfo(HandyMenUsrServiceInfo svrInfo)throws Exception;
    HandyMenUsrServiceInfo getUserSvrInfo(String usrName)throws Exception;
    
}
