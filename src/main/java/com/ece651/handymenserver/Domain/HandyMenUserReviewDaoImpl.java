package com.ece651.handymenserver.Domain;

import java.util.List;
import java.util.Map;
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
    	String insertSql = "INSERT INTO " + reviewTblName + 
    			"(usrName, reviewUsrName, svrType, reviewContent, rank) "
    			+ " VALUES (?, ?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			review.getUsrName(), review.getReviewUsrName(),
    			review.getSvrType().toString(),
    			review.getReviewContent(),
    			review.getRank()});
    	
    }
    
    public void updateUserReview(HandyMenUserReview review) throws Exception {
    	String updateSql = "update " + reviewTblName + " set reviewContent = ? and rank = ? " 
            +  " where usrName = ? and reviewUsrName = ? and svrType = ?";
    	int rowNum = jdbcTemplate.update(updateSql, new Object[]{review.getReviewContent(),
    			review.getRank(), review.getUsrName(), review.getReviewUsrName(),
    			review.getSvrType().toString()});
    	if(rowNum == 0) throw new Exception("updateUserReview not exist");
    }
    
    public HandyMenUserReview getUserReview(String usrName, 
    		String reviewUsrName, String svrType)throws Exception {
		String sql = "select * from " + reviewTblName + 
				" where usrName  = ? and reviewUsrName = ? "
				+ "svrType = ? ";
		
		return(HandyMenUserReview)jdbcTemplate.queryForObject(sql, 
		        new Object[] {usrName, reviewUsrName, svrType},
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
		    			Enum.valueOf(HandyMenSvrTypeEnum.class, rs.getString("svrType")),
		    			rs.getString("reviewContent"),
		    			rs.getInt("rank"));

		        return review;
	        }
        };
    }
    
    public void deleteUserReview(String userName, String reviewUsrName,
    		String svrType)throws Exception {
    	String deleteSql = "delete from " + reviewTblName + 
    			" where usrName = ? and reviewUsrName = ? "
    			+ "svrType = ? ";
    	jdbcTemplate.update(deleteSql, new Object[]{userName, reviewUsrName, svrType});
    }
	
    public List<HandyMenUserReview> listUsersReviewByName(String usrName) throws Exception {
		String sql = "select * from " + reviewTblName + 
				" where usrName  = ?";    	
    	
    	return jdbcTemplate.query(sql,
                new Object[]{usrName},   
                getRowMapper());
    }
    
    public Boolean isReviewExist(String userName, String reviewUsrName,
    		String svrType)throws Exception 
    {
    	String sql = "select 1 from " + reviewTblName + 
    			" where usrName = ? and reviewUsrName = ? and svrType = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{userName, reviewUsrName, svrType});
    	return !list.isEmpty();
    }
    
    
	
}
