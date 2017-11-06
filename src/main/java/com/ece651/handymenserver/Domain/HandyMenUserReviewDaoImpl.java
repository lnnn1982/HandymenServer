package com.ece651.handymenserver.Domain;

import java.util.List;
import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.*;

@Component
public class HandyMenUserReviewDaoImpl implements HandyMenUserReviewDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
    final private String reviewTblName = "HandyMenUserReview";
	
	
    public void addUserReview(HandyMenUserReview review) throws Exception {
    	String insertSql = "INSERT INTO " + reviewTblName + "(usrName, reviewUsrName, reviewContent, rank) "
    			+ " VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			review.getUsrName(), review.getReviewUsrName(), review.getReviewContent(),
    			review.getRank()});
    	
    }
    
    public void updateUserReview(HandyMenUserReview review) throws Exception {
    	String updateSql = "update " + reviewTblName + " set reviewContent = ? and rank = ? " 
            +  " where usrName = ? and reviewUsrName = ?";
    	int rowNum = jdbcTemplate.update(updateSql, new Object[]{review.getReviewContent(),
    			review.getRank(), review.getUsrName(), review.getReviewUsrName()});
    	if(rowNum == 0) throw new Exception("updateUserReview not exist");
    }
    
    public HandyMenUserReview getUserReview(String usrName, 
    		String reviewUsrName)throws Exception {
		String sql = "select * from " + reviewTblName + 
				" where usrName  = ? and reviewUsrName = ?";
		
		return(HandyMenUserReview)jdbcTemplate.queryForObject(sql, 
		        new Object[] {usrName},
		        getRowMapper()
			);
    }
    
    private RowMapper getRowMapper() {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	HandyMenUserReview review = new HandyMenUserReview(
		    			rs.getString("usrName"),
		    			rs.getString("reviewUsrName"),
		    			rs.getString("reviewContent"),
		    			rs.getInt("rank"));

		        return review;
	        }
        };
    }
    
    public void deleteUserReview(String userName, String reviewUsrName)throws Exception {
    	String deleteSql = "delete from " + reviewTblName + " where usrName = ? and reviewUsrName = ?";
    	jdbcTemplate.update(deleteSql, new Object[]{userName, reviewUsrName});
    }
	
    public List<HandyMenUserReview> listUsersReviewByName(String usrName) throws Exception {
		String sql = "select * from " + reviewTblName + 
				" where usrName  = ?";    	
    	
    	return jdbcTemplate.query(sql,
                new Object[]{usrName},   
                getRowMapper());
    }
    
    
	
}
