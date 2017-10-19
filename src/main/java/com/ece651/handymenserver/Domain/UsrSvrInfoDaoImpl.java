package com.ece651.handymenserver.Domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UsrSvrInfoDaoImpl implements UsrSvrInfoDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    final private String tblName = "HandyMenServiceInfo";    
	
	public void addUsrServiceInfo(HandyMenUsrServiceInfo svrInfo) throws Exception {
    	String insertSql = "INSERT INTO " + tblName + "(usrName, type, area, "
    			+ "description) VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			svrInfo.getUsrName(), svrInfo.getType(), svrInfo.getArea(),
    			svrInfo.getDescription()});
	}
	
    public void updateUsrServiceInfo(HandyMenUsrServiceInfo svrInfo) throws Exception {
    	
    }
    
    public HandyMenUsrServiceInfo getUserSvrInfo(String usrName) throws Exception {
    	String sql = "select * from " + tblName + " where usrName  = ?";
    	return (HandyMenUsrServiceInfo) jdbcTemplate.queryForObject(
    			sql,
                new Object[]{usrName},   
                HandyMenUsrServiceInfo.class);
    }
	
	
	
	
	
	
}
