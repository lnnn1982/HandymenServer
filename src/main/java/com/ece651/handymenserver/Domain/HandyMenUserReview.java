package com.ece651.handymenserver.Domain;

public class HandyMenUserReview {
    private String usrName;
    private String reviewUsrName;
    private String reviewContent;
    int rank;
    
    public HandyMenUserReview(String usrName, String reviewUsrName, String reviewContent, int rank) {
    	this.usrName = usrName;
    	this.reviewUsrName = reviewUsrName;
    	this.reviewContent = reviewContent;
    	this.rank = rank;
    }
    
    public String getUsrName() {
    	return usrName;
    }
    
    public String getReviewUsrName() {
    	return reviewUsrName;
    }
    
    public String getReviewContent() {
    	return reviewContent;
    }
    
    public int getRank() {
    	return rank;
    }
	
	
	
	
	
}
