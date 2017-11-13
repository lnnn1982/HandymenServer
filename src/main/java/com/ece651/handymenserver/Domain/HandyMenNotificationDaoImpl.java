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
public class HandyMenNotificationDaoImpl implements HandyMenNotificationDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    final private String notificationTblName = "HandyMenNotification";
    
    public void addHandyMenNotification(HandyMenNotification notification) throws Exception {
    	String insertSql = "INSERT INTO " + notificationTblName + 
    			"(usrName, timeStamp, notificationType, content) "
    			+ " VALUES (?, ?, ?, ?)";
    	jdbcTemplate.update(insertSql, new Object[]{
    			notification.getUsrName(),
    			notification.getTimeStamp(),
    			notification.getNotificationType().toString(),
    			notification.getContent()});
    }
    
	public void deleteHandyMenNotification(String usrName, String notificationType,
			String timeStamp) throws Exception {
    	String deleteSql = "delete from " + notificationTblName + 
    			" where usrName = ? and timeStamp = ? "
    			+ "and notificationType = ? ";
    	jdbcTemplate.update(deleteSql, new Object[]{usrName, timeStamp, notificationType});		
		
	}
	
	public Boolean isNotificationExist(String usrName, String notificationType,
			String timeStamp)throws Exception {
    	String sql = "select 1 from " + notificationTblName + 
    			" where usrName = ? and timeStamp = ? and notificationType = ? ";
    	List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql,
    			new Object[]{usrName, timeStamp, notificationType});
    	return !list.isEmpty();
	}
	
    public List<HandyMenNotification> listUsersNotifications(String usrName) throws Exception {
		String sql = "select * from " + notificationTblName + 
				" where usrName  = ?";    	
    	
    	return jdbcTemplate.query(sql,
                new Object[]{usrName},   
                getRowMapper());
    }
	
    private RowMapper getRowMapper() {
    	
    	return new RowMapper(){
			
		    @Override
	        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	HandyMenNotification notification = new HandyMenNotification(
		    			rs.getString("usrName"),
		    			rs.getString("timeStamp"),
		    			Enum.valueOf(HandyMenNotification.TypeEnum.class, rs.getString("notificationType")),
		    			rs.getString("content"));

		        return notification;
	        }
        };
    }
	
	
	
	
	
	
	
	
}
