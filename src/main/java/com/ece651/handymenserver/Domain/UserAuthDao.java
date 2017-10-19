package com.ece651.handymenserver.Domain;

public interface UserAuthDao {
    void addUsrAuth(HandyMenUserAuth auth);
    void updateUsrAuth(HandyMenUserAuth auth);
    HandyMenUserAuth getUsrAuth(String usrName);
}
