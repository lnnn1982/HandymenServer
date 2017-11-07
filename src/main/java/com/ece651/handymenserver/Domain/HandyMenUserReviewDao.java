package com.ece651.handymenserver.Domain;

import java.util.List;

public interface HandyMenUserReviewDao {
    void addUserReview(HandyMenUserReview review) throws Exception;
    void updateUserReview(HandyMenUserReview review) throws Exception;
    void deleteUserReview(String userName, String reviewUsrName,
    		String svrType)throws Exception;
    Boolean isReviewExist(String userName, String reviewUsrName,
    		String svrType)throws Exception;
	
    List<HandyMenUserReview> listUsersReviewByName(String usrName) throws Exception;
}
