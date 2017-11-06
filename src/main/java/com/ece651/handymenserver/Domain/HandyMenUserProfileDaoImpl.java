package com.ece651.handymenserver.Domain;

import java.util.List;
import java.sql.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.*;

@Component
public class HandyMenUserProfileDaoImpl implements HandyMenUserProfileDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    final private String usrContactTblName = "HandyMenUserContactInfo";
    final private String usrServiceTblName = "HandyMenServiceInfo";
    final private String authTblName = "HandyMenUserAuth";
    final private String reviewTblName = "HandyMenUserReview";
	
    public void addUser(HandyMenUserProfile user) throws Exception {
    	addUserNonAuthInfo(user);
    	
    	String insertAuthTblSql = "INSERT INTO " + authTblName + "(usrName, passwd) "
    			+ " VALUES (?, ?)";
    	jdbcTemplate.update(insertAuthTblSql, new Object[]{
    			user.getAuthInfo().getUsrName(), user.getAuthInfo().getPasswd()});	
    }
    
    private void addUserNonAuthInfo(HandyMenUserProfile user) throws Exception {
    	String insertContactSql = "INSERT INTO " + usrContactTblName + "(usrName, emailAddr, "
    			+ "phoneNumList) VALUES (?, ?, ?)";
    	jdbcTemplate.update(insertContactSql, new Object[]{
    			user.getContactInfo().getUsrName(), user.getContactInfo().getEmailAddr(),
    			user.getContactInfo().getPhoneNumList()});
    	
    	String insertSvrTblSql = "INSERT INTO " + usrServiceTblName + "(usrName, type, "
    			+ "area, description) VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSvrTblSql, new Object[]{
    			user.getServiceInfo().getUsrName(), user.getServiceInfo().getType().toString(),
    			user.getServiceInfo().getArea(), user.getServiceInfo().getDescription()});
    }

    public void updateUser(HandyMenUserProfile user) throws Exception {
    	String deleteContactSql = "delete from " + usrContactTblName + " where usrName = ?";
    	int rowNum = jdbcTemplate.update(deleteContactSql, new Object[]{
    			user.getContactInfo().getUsrName()});
    	if(rowNum == 0) {
    		throw new Exception("updateUser user not exist");
    	}
    	
    	String deleteServiceSql = "delete from " + usrServiceTblName + " where usrName = ?";
    	rowNum = jdbcTemplate.update(deleteServiceSql, new Object[]{
    			user.getContactInfo().getUsrName()});
    	if(rowNum == 0) {
    		throw new Exception("updateUser user not exist");
    	}
    	
    	addUserNonAuthInfo(user);
    }
    
    public void deleteUser(String userName) throws Exception {
    	String deleteContactSql = "delete from " + usrContactTblName + " where usrName = ?";
    	jdbcTemplate.update(deleteContactSql, new Object[]{userName});
    	
    	String deleteServiceSql = "delete from " + usrServiceTblName + " where usrName = ?";
    	jdbcTemplate.update(deleteServiceSql, new Object[]{userName});
    	
    	String deleteAuthSql = "delete from " + authTblName + " where usrName = ?";
    	jdbcTemplate.update(deleteAuthSql, new Object[]{userName});
    	
    	String deleteReviewSql = "delete from " + reviewTblName + " where usrName = ? or reviewUsrName = ?";
    	jdbcTemplate.update(deleteReviewSql, new Object[]{userName, userName});
    }
    
	public HandyMenUserProfile getUser(String usrName) throws Exception {
		String contactSql = "select * from " + usrContactTblName + " where usrName  = ?";
		HandyMenUserContactInfo contactInfo = (HandyMenUserContactInfo
			)jdbcTemplate.queryForObject(contactSql, 
		        new Object[] {usrName},
			    
		        new RowMapper(){
				
				    @Override
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				        HandyMenUserContactInfo user = new HandyMenUserContactInfo(rs.getString("usrName"));
				        user.setEmailAddr(rs.getString("emailAddr"));
				        user.setPhoneNumList(rs.getString("phoneNumList"));
				        return user;
			        }
		        }
			);
	
		String serviceSql = "select * from " + usrServiceTblName + " where usrName  = ?";
		HandyMenUsrServiceInfo serviceInfo = (HandyMenUsrServiceInfo
			)jdbcTemplate.queryForObject(serviceSql, 
		        new Object[] {usrName},
			    
		        new RowMapper(){
				
				    @Override
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				    	HandyMenUsrServiceInfo service = new HandyMenUsrServiceInfo(
				    			Enum.valueOf(HandyMenSvrTypeEnum.class, rs.getString("type")),
				    					rs.getString("usrName"));
				    	service.setArea(rs.getString("area"));
				    	service.setDescription(rs.getString("description"));
				        return service;
			        }
		        }
			);
		
		HandyMenUserProfile profile = new HandyMenUserProfile(contactInfo);
		profile.setServiceInfo(serviceInfo);
		return profile;
	}
	
    public List<HandyMenUserProfile> listUsersByServiceType(HandyMenSvrTypeEnum type, String usrName) throws Exception {
        if(usrName.equals(HandyMenUserContactInfo.GUEST_USR_NAME)) {
        	return listUserSimpleInfoByServiceType(type);
        }
        else {
        	return listUserFullInfoByServiceType(type);
        }
    }
    
    private List<HandyMenUserProfile> listUserFullInfoByServiceType(HandyMenSvrTypeEnum type) throws Exception {
    	String sql = "select t1.usrName,t1.emailAddr, t1.phoneNumList, t2.type, t2.area, t2.description from " + 
    			usrContactTblName + " t1 join " + usrServiceTblName + " t2 on t1.usrName = t2.usrName and t2.type = ?";

    	return jdbcTemplate.query(sql,
                new Object[]{type.toString()},   
                usrProfileRowMap(true));
    }
    
    private List<HandyMenUserProfile> listUserSimpleInfoByServiceType(HandyMenSvrTypeEnum type) throws Exception {
    	String sql = "select t2.usrName, t2.type, t2.area, t2.description from " + 
    			usrServiceTblName + " t2 where t2.type = ?";
    	
    	return jdbcTemplate.query(sql,
                new Object[]{type.toString()},   
                usrProfileRowMap(false));
    }    
    
    private RowMapper usrProfileRowMap(Boolean isFull) {
    	return new RowMapper()
    	{
    		
            @Override  
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException 
            {  
   			    HandyMenUsrServiceInfo service = new HandyMenUsrServiceInfo(
			    			Enum.valueOf(HandyMenSvrTypeEnum.class, rs.getString("type")),
			    		    rs.getString("usrName"));
   			    service.setArea(rs.getString("area"));
			    service.setDescription(rs.getString("description"));
   			 
   			    HandyMenUserContactInfo contactInfo = new HandyMenUserContactInfo(rs.getString("usrName"));
   			    if(isFull) {
       			    contactInfo.setEmailAddr(rs.getString("emailAddr"));
       			    contactInfo.setPhoneNumList(rs.getString("phoneNumList"));
   			    }
   			 
			    HandyMenUserProfile profile = new HandyMenUserProfile(contactInfo);
				profile.setServiceInfo(service);
				 
				return profile;
            }
        };
    }
    
    public List<HandyMenUserProfile> listAllServiceUsers(String usrName) throws Exception {
    	Boolean isFull = (!usrName.equals(HandyMenUserContactInfo.GUEST_USR_NAME));
    	String sql = "";
    	
    	if(isFull) {
    		sql = "select t1.usrName,t1.emailAddr, t1.phoneNumList, t2.type, t2.area, t2.description from " + 
        			usrContactTblName + " t1 join " + usrServiceTblName + " t2 on t1.usrName = t2.usrName and t2.type != ?";
    	}
    	else {
    		sql = "select t2.usrName, t2.type, t2.area, t2.description from " + 
        			usrServiceTblName + " t2 where t2.type != ?";
    	}

    	return jdbcTemplate.query(sql,
                new Object[]{HandyMenSvrTypeEnum.NOSERVICE_TYPE.toString()},   
                usrProfileRowMap(isFull));
    }
	
    
    public void updatePasswd(String usr, String passwd) throws Exception {
    	String updateSql = "update " + authTblName + " set passwd = ? where usrName = ?";
    	int rowNum = jdbcTemplate.update(updateSql, new Object[]{passwd, usr});
    	if(rowNum == 0) throw new Exception("updatePasswd usr not exist");
    }
    
    public HandyMenUserAuth getUsrAuth(String usrName)throws Exception {
		String sql = "select * from " + authTblName + " where usrName  = ?";
		return (HandyMenUserAuth)jdbcTemplate.queryForObject(sql, 
		        new Object[] {usrName},
			    
		        new RowMapper(){
				
				    @Override
			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				    	HandyMenUserAuth auth = new HandyMenUserAuth(rs.getString("usrName"),
				    			rs.getString("passwd"));
				        return auth;
			        }
		        }
			);
    	
    }
	
}
