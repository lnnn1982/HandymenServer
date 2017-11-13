package com.ece651.handymenserver.Domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class HandyMenChatMessageDaoImpl implements HandyMenChatMessageDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
    final private String chatMessageTblName = "HandyMenChatMessage";    
	
	public void addHandyMenChatMessage(HandyMenChatMessage message) throws Exception {
    	String insertSql = "INSERT INTO " + chatMessageTblName + 
    			"(usrName, peerUsrName, timeStamp, content) "
    			+ " VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			message.getUsrName(),
    			message.getPeerUsrName(),
    			message.getTimeStamp(),
    			message.getContent()});	
	}
	
	public void deleteHandyMenChatMessage(String usrName, String peerUsrName,
			String timeStamp) throws Exception {
    	String deleteSql = "delete from " + chatMessageTblName + 
    			" where usrName = ? and peerUsrName = ? "
    			+ "and timeStamp = ? ";
    	jdbcTemplate.update(deleteSql, new Object[]{usrName, peerUsrName, timeStamp});	
	}
	
    public Boolean isChatMessageExist(String usrName, String peerUsrName,
			String timeStamp)throws Exception {
    	String sql = "select 1 from " + chatMessageTblName + 
    			" where usrName = ? and peerUsrName = ? and timeStamp = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{usrName, peerUsrName, timeStamp});
    	return !list.isEmpty();
    }
	
    public List<HandyMenChatMessageDao> listUsersChatMessages(String usrName) throws Exception {
		String sql = "select * from " + chatMessageTblName + 
				" where usrName  = ?";    	
    	
    	return jdbcTemplate.query(sql,
                new Object[]{usrName},   
                getRowMapper());
    }
    
    private RowMapper getRowMapper() {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	HandyMenChatMessage message = new HandyMenChatMessage(
		    			rs.getString("usrName"),
		    			rs.getString("peerUsrName"),
		    			rs.getString("timeStamp"),
		    			rs.getString("content"));

		        return message;
	        }
        };
    }
}
