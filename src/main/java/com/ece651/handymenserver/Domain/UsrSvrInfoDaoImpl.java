package com.ece651.handymenserver.Domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
				new RowMapper(){
    			    @Override
    			    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    			    	HandyMenUsrServiceInfo user = 
    			        new HandyMenUsrServiceInfo(
    			        		HandyMenSvrTypeEnum.fromStr(rs.getString("type")),
    			        		rs.getString("usrName"));
    				    user.setDescription(rs.getString("description"));
    				    user.setArea(rs.getString("area"));
    				    return user;
    			    }
    		});
    }
	
	
	
	
	
	
}
