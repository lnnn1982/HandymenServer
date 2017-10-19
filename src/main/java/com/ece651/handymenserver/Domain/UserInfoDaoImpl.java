package com.ece651.handymenserver.Domain;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserInfoDaoImpl implements UserInfoDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    final private String tblName = "HandyMenUserInfo";
	
    public void addUser(HandyMenUserInfo user) throws Exception {
    	String insertSql = "INSERT INTO " + tblName + "(usrName, type, emailAddr, "
    			+ "phoneNumList) VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			user.getUsrName(), user.getEmailAddr(),
    			user.getPhoneNumList()});
    }

    public void updateUser(HandyMenUserInfo user) throws Exception {
    
    
    }
    
    public List<HandyMenUserInfo> listUsers(HandyMenSvrTypeEnum type) {
    	String sql = "select * from " + tblName + " where svrType = ?";
    	return (List<HandyMenUserInfo>) jdbcTemplate.queryForList(
    			sql,
                new Object[]{type},   
                HandyMenUserInfo.class);
    }
    
    public List<HandyMenUserInfo> listServiceUsers(String usrName) {
    	return null;
    }
    
    public HandyMenUserInfo getUser(String usrName) throws Exception {
    	String sql = "select * from " + tblName + " where usrName  = ?";
    	return (HandyMenUserInfo) jdbcTemplate.queryForObject(
    			sql,
                new Object[]{usrName},   
                HandyMenUserInfo.class);
    }
	
	
	
	
	
	
}
