package com.ece651.handymenserver.Domain;

import java.util.List;
import java.util.Map;
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
    			+ "area, description, priceRange) VALUES (?, ?, ?, ?, ?)";
    	jdbcTemplate.update(insertSvrTblSql, new Object[]{
    			user.getServiceInfo().getUsrName(), user.getServiceInfo().getType().toString(),
    			user.getServiceInfo().getArea(), 
    			user.getServiceInfo().getDescription(),
    			user.getServiceInfo().getPriceRange()});
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
		String sql = getFullSelectProfileFilds() + " where t1.usrName = ? ";
		
		return (HandyMenUserProfile)jdbcTemplate.queryForObject(sql,
                new Object[]{usrName},   
                usrProfileRowMap(true));
	}
	
	private String getFullSelectProfileFilds() {
		String sql = "select t1.usrName,t1.emailAddr, t1.phoneNumList, " + 
	               " t2.type, t2.area, t2.description, t2.priceRange from "+ 
	    			usrContactTblName + " t1 join " + usrServiceTblName + 
	    			" t2 on t1.usrName = t2.usrName join " + reviewTblName
	    			+ " t3 on t1.usrName = t3.usrName ";

		return sql;
	}
	
	private String getSimpleSelectProfileFilds() {
		String sql = "select t1.usrName, " + 
	               " t2.type, t2.area, t2.description, t2.priceRange from " + 
	    			usrContactTblName + " t1 join " + usrServiceTblName + 
	    			" t2 on t1.usrName = t2.usrName join " + reviewTblName
	    			+ " t3 on t1.usrName = t3.usrName ";

		return sql;
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
    
    public List<HandyMenUserProfile> listFullUserProfiles(
    		Map<String, String> searchFields,
    		Map<String, String> searchNotFields,
    		String orderField,
    		Boolean isDesc) throws Exception 
    {
    	String sql = getFullSelectProfileFilds() + genConditionSql(searchFields,
    			searchNotFields) + genOrderSql(orderField, isDesc);
    	return jdbcTemplate.query(sql,
    			getArgumentObjects(searchFields, searchNotFields, orderField),   
                usrProfileRowMap(true));
    }
    
    private Object[] getArgumentObjects(Map<String, String> searchFields,
    		Map<String, String> searchNotFields, String orderField)
    {
    	int arraySize = searchFields.size() + searchNotFields.size();
    	if(!orderField.isEmpty()) {
    		arraySize++;
    	}
    	
    	Object[] objArray = new Object[arraySize];
    	
    	int i = 0;
    	for (Map.Entry<String, String> item : searchFields.entrySet()) {
    		objArray[i] = item.getValue();
    		i++;
		}
    	
    	for (Map.Entry<String, String> item1 : searchNotFields.entrySet()) {
    		objArray[i] = item1.getValue();
    		i++;
		}
    	
    	if(!orderField.isEmpty()) {
    		objArray[i] = orderField;
    	}
    	
    	return objArray;
    }
    
    private String genConditionSql(Map<String, String> searchFields,
    		Map<String, String> searchNotFields) {
    	if(searchFields.isEmpty() && searchNotFields.isEmpty()) {
    		return " ";
    	}
    	
    	String whereSql = " where ";
    	for (Map.Entry<String, String> item : searchFields.entrySet()) {
    		String field = item.getKey();
    		if(item.getKey().equals("usrName")) {
    			field = "t1." + field;
    		}
    		
    		whereSql = whereSql + field + " = ? and ";
		}   	

    	for (Map.Entry<String, String> item1 : searchNotFields.entrySet()) {
    		String field = item1.getKey();
    		if(item1.getKey().equals("usrName")) {
    			field = "t1." + field;
    		}
    		
    		whereSql = whereSql + field + " != ? and ";
		}
    	
    	whereSql = whereSql.substring(0, whereSql.length()-5);
    	return whereSql;
    }
    
    private String genOrderSql(String orderField, Boolean isDesc) {
    	if(!orderField.isEmpty()) {
    		String sql = " order by ? ";
    		if(isDesc) {
    			sql += " desc ";
    		}
    		else {
    			sql += " asc ";
    		}
    		return sql;
    	}
    	else {
    		return " ";
    	}
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
			    service.setPriceRange(rs.getString("priceRange "));
   			 
   			    HandyMenUserContactInfo contactInfo = new HandyMenUserContactInfo(rs.getString("usrName"));
   			    if(isFull) {
       			    contactInfo.setPhoneNumList(rs.getString("phoneNumList"));
       			    contactInfo.setEmailAddr(rs.getString("emailAddr"));
   			    }
   			 
			    HandyMenUserProfile profile = new HandyMenUserProfile(contactInfo);
				profile.setServiceInfo(service);
				 
				return profile;
            }
        };
    }
    
    public List<HandyMenUserProfile> listSimpleUserProfiles(
    		Map<String, String> searchFields,
    		Map<String, String> searchNotFields,
    		String orderField,
    		Boolean isDesc) throws Exception 
    {
    	String sql = getSimpleSelectProfileFilds() + genConditionSql(searchFields,
    			searchNotFields) + genOrderSql(orderField, isDesc);
    	return jdbcTemplate.query(sql,
    			getArgumentObjects(searchFields, searchNotFields, orderField),   
                usrProfileRowMap(false));
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
