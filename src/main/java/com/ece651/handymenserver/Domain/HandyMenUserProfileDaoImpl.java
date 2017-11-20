package com.ece651.handymenserver.Domain;

import java.util.*;
import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.*;

@Component
public class HandyMenUserProfileDaoImpl implements HandyMenUserProfileDao{
	
	static class UserServiceRank {
		private String usrName;
		private String svrType;
		private int avgRank;
		
		UserServiceRank(String usrName, String svrType, int avgRank) {
			this.usrName = usrName;
			this.svrType = svrType;
			this.avgRank = avgRank;
		}
		
		String getUsrName() {
			return usrName;
		}
		
		String getSvrType() {
			return svrType;
		}
		
		int getAvgRank() {
			return avgRank;
		}
	}
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    final private String usrContactTblName = "HandyMenUserContactInfo";
    final private String usrServiceTblName = "HandyMenServiceInfo";
    final private String authTblName = "HandyMenUserAuth";
    final private String reviewTblName = "HandyMenUserReview";
    final private String serviceTypeTblName = "HandyMenServiceTypeInfo";
	
    public void addUserBasicInfo(HandyMenUserContactInfo contactInfo,
    		HandyMenUserAuth auth) throws Exception {
    	addUserContactInfo(contactInfo);
    	addUserAuth(auth);
    }
    
    private void addUserContactInfo(HandyMenUserContactInfo contactInfo) throws Exception {
    	String insertContactSql = "INSERT INTO " + usrContactTblName + "(usrName, emailAddr, "
    			+ "phoneNumList, uploadFileNames) VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertContactSql, new Object[]{
    			contactInfo.getUsrName(), contactInfo.getEmailAddr(),
    			contactInfo.getPhoneNumList(),
    			contactInfo.getUploadFileNames()});
    }
    
    private void addUserAuth(HandyMenUserAuth auth) throws Exception {
    	String insertAuthTblSql = "INSERT INTO " + authTblName + "(usrName, passwd) "
    			+ " VALUES (?, ?)";
    	jdbcTemplate.update(insertAuthTblSql, new Object[]{
    			auth.getUsrName(), auth.getPasswd()});
    }
    
    public void updateUserContactInfo(HandyMenUserContactInfo contactInfo) throws Exception {
    	deleteContactInfo(contactInfo.getUsrName());
    	addUserContactInfo(contactInfo);
    }
    
    public void addUserServiceInfo(HandyMenUsrServiceInfo serviceInfo) throws Exception {
    	String insertSvrTblSql = "INSERT INTO " + usrServiceTblName + "(usrName, type, "
    			+ "area, description, priceRange, uploadFileNames) " + 
    			"VALUES (?, ?, ?, ?, ?, ?)";
    	jdbcTemplate.update(insertSvrTblSql, new Object[]{
    			serviceInfo.getUsrName(), serviceInfo.getType().toString(),
    			serviceInfo.getArea(), 
    			serviceInfo.getDescription(),
    			serviceInfo.getPriceRange(),
    			serviceInfo.getUploadFileNames()});
    }    

    public void updateUserServiceInfo(HandyMenUsrServiceInfo serviceInfo) throws Exception {
    	deleteServiceInfo(serviceInfo.getUsrName(), serviceInfo.getType().toString());
    	addUserServiceInfo(serviceInfo);
    }
    
    public void deleteUser(String userName) throws Exception {
    	deleteContactInfo(userName);
    	deleteServiceInfo(userName);
    	deleteAuth(userName);
    	deleteReviewInfo(userName);
    }
    
    private void deleteContactInfo(String userName) throws Exception {
    	String deleteContactSql = "delete from " + usrContactTblName + " where usrName = ?";
    	jdbcTemplate.update(deleteContactSql, new Object[]{userName});
    }
    
    public void deleteServiceInfo(String userName, String type) throws Exception {
    	String deleteServiceSql = "delete from " + usrServiceTblName 
    			+ " where usrName = ? and type = ? ";
    	jdbcTemplate.update(deleteServiceSql, new Object[]{userName, type});
    }
    
    private void deleteServiceInfo(String userName) throws Exception {
    	String deleteServiceSql = "delete from " + usrServiceTblName 
    			+ " where usrName = ? ";
    	jdbcTemplate.update(deleteServiceSql, new Object[]{userName});
    }
    
    private void deleteAuth(String userName) throws Exception {
    	String deleteAuthSql = "delete from " + authTblName + " where usrName = ?";
    	jdbcTemplate.update(deleteAuthSql, new Object[]{userName});
    }    
    
    private void deleteReviewInfo(String userName) throws Exception {
    	String deleteReviewSql = "delete from " + reviewTblName + " where usrName = ? or reviewUsrName = ?";
    	jdbcTemplate.update(deleteReviewSql, new Object[]{userName, userName});
    }   
	
    public List<HandyMenSvrTypeInfo> listServiceTypeInfos() throws Exception {
    	String sql = "select * from " + serviceTypeTblName;

    	return jdbcTemplate.query(sql,  
    			getServiceTypeRowMapper());
    }
    
    private RowMapper getServiceTypeRowMapper() {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	HandyMenSvrTypeInfo typeInfo = new HandyMenSvrTypeInfo(
		    			Enum.valueOf(HandyMenSvrTypeEnum.class, rs.getString("serviceType")),
		    			rs.getInt("id"),
		    			rs.getString("uploadFileNames"));

		        return typeInfo;
	        }
        };
    }
    
    public void setUploadFileNamesToOneServiceType(int id, String uploadFileNames) throws Exception {
    	String updateSql = "update " + serviceTypeTblName + " set uploadFileNames = ? " 
                +  " where id = ?";
    	jdbcTemplate.update(updateSql, new Object[]{uploadFileNames, id});
    }
    
    public HandyMenUserProfile getUser(String usrName) throws Exception {
    	return getUser(usrName, true);
    }

	private HandyMenUserProfile getUser(String usrName, Boolean isFull) throws Exception {
		String sql = "select * from " + usrContactTblName + 
				" where usrName  = ? ";
		
		HandyMenUserContactInfo contactInfo = (HandyMenUserContactInfo
				) jdbcTemplate.queryForObject(
				sql,
				new Object[] { usrName }, 
				getContactRowMapper(isFull));
		
		sql = "select * from " + usrServiceTblName + " where usrName = ? ";
    	List<HandyMenUsrServiceInfo> services = jdbcTemplate.query(
    			sql,
    			new Object[] { usrName},
    			getServiceInfoRowMapper());
    	
    	sql = "select usrName, svrType, cast(avg(rank) as SIGNED) avgRank from "
    			+ reviewTblName + " where usrName = ? "
    			+ "group by usrName, svrType ";
    	List<UserServiceRank> ranks = jdbcTemplate.query(
    			sql,
    			new Object[] { usrName},
    			getUserServiceRankMapper());
    	
    	addRankServiceInfo(services, ranks);
    	
    	HandyMenUserProfile profile = new HandyMenUserProfile(
    			contactInfo, 
    			new HandyMenUserAuth("", ""));
    	profile.setServiceInfoList(services);
    	
    	return profile;
	}
	
    private void addRankServiceInfo(List<HandyMenUsrServiceInfo> services,
    		List<UserServiceRank> ranks)
    {
    	Map<String, Integer> usrSvrRankMap = new HashMap<>();
    	for (UserServiceRank userServiceRank : ranks) {
			String key = userServiceRank.getUsrName() + "_"
					+ userServiceRank.getSvrType();
			usrSvrRankMap.put(key, userServiceRank.getAvgRank());
		}
    	
    	for (HandyMenUsrServiceInfo service : services) {
    		String key = service.getUsrName() + "_" + service.getType().toString();
    		Integer rank = usrSvrRankMap.get(key);
    		if(rank != null) {
    			service.setReivewRank(rank);
    		}
		}
    }

    public List<HandyMenUserProfile> listFullUserProfiles() throws Exception {
    	return listUserProfiles(true);
    }
    
    public List<HandyMenUserProfile> listSimpleUserProfiles() throws Exception {
    	return listUserProfiles(false);
    }    
    
    private List<HandyMenUserProfile> listUserProfiles(Boolean isFull) throws Exception {
    	List<HandyMenUserProfile> profiles = new ArrayList<>();
    	List<String> usrNames = getAllUserNames();
    	for (String usrName : usrNames) {
    		profiles.add(getUser(usrName, isFull));
		}

    	return profiles;
    }
    
    public List<HandyMenUserProfile> listFullUserProfilesBySvrType(
    		String serviceType) throws Exception {
    	return listUserProfilesBySvrType(serviceType, true); 
    }
    
    public List<HandyMenUserProfile> listSimpleUserProfilesBySvrType(
    		String serviceType) throws Exception {
        return listUserProfilesBySvrType(serviceType, false);    	
    }
    
    private List<HandyMenUserProfile> listUserProfilesBySvrType(
    		String serviceType, Boolean isFull) throws Exception 
    {
    	List<HandyMenUserProfile> profiles = new ArrayList<>();
    	List<String> usrNames = getUserNamesByServiceType(serviceType);
    	for (String usrName : usrNames) {
    		profiles.add(getUser(usrName, isFull));
		}

    	return profiles;    	
    }
     
    private List<String> getUserNamesByServiceType(String serviceType) throws Exception {
    	String sql = "select distinct usrName from " + usrServiceTblName + " where "
    			+ " type = ? ";
    	return (List<String>) jdbcTemplate.queryForList(
    			sql,   
                new Object[]{serviceType},  
                String.class);
    }
    
    private List<String> getAllUserNames() throws Exception {
    	String sql = "select distinct usrName from " + usrServiceTblName;
    	return (List<String>) jdbcTemplate.queryForList(
    			sql,
                String.class);
    }
	
    private RowMapper getContactRowMapper(Boolean isFull) {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
   			    HandyMenUserContactInfo contactInfo = new HandyMenUserContactInfo(
   			    		rs.getString("usrName"));
   			    if(isFull) {
       			    contactInfo.setPhoneNumList(rs.getString("phoneNumList"));
       			    contactInfo.setEmailAddr(rs.getString("emailAddr"));
       			    contactInfo.setUploadFileNames(rs.getString("uploadFileNames"));
   			    }

		        return contactInfo;
	        }
        };
    }
	
    private RowMapper getServiceInfoRowMapper() {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
   			    HandyMenUsrServiceInfo service = new HandyMenUsrServiceInfo(
			    			Enum.valueOf(HandyMenSvrTypeEnum.class, rs.getString("type")),
			    		    rs.getString("usrName"));
   			    service.setArea(rs.getString("area"));
			    service.setDescription(rs.getString("description"));
			    service.setPriceRange(rs.getString("priceRange"));
			    service.setUploadFileNames(rs.getString("uploadFileNames"));
			    
			    return service;
	        }
        };
    }
	
	private RowMapper getUserServiceRankMapper() {
		
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	UserServiceRank rankInfo = new UserServiceRank(
		    			    rs.getString("usrName"),
			    		    rs.getString("svrType"),
			    		    rs.getInt("avgRank"));
			    
			    return rankInfo;
	        }
        };
	}
	
    public Boolean isUserExit(String usrName){
    	String sql = "select 1 from " + usrContactTblName + " where usrName = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{usrName});
    	return !list.isEmpty();
    }
    
    public Boolean isEmailExit(String emailAddr){
    	String sql = "select 1 from " + usrContactTblName + " where emailAddr = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{emailAddr});
    	return !list.isEmpty();  	
    }
    
    public Boolean checkUserAndEmail(String usrName, String emailAddr) {
    	String sql = "select 1 from " + usrContactTblName + " where emailAddr = ? "
    			+ " and usrName = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{emailAddr, usrName});
    	return !list.isEmpty();    	
    }
    
    public Boolean isUsrServiceTypeExist(String usrName, String serviceType) {
    	String sql = "select 1 from " + usrServiceTblName + " where usrName = ? "
    			+ " and type = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{usrName, serviceType});
    	return !list.isEmpty();    	
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
    
    public Boolean isUserPasswordValid(String usrName, String passwd)throws Exception {
    	String sql = "select 1 from " + authTblName + " where usrName = ? and passwd = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{usrName, passwd});
    	return !list.isEmpty();
    }
	
}
