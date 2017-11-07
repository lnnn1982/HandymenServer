package com.ece651.handymenserver.RestSvr;

public class ResponseMessage {
    public static enum OpStatus {
    	OP_OK,
    	OP_FAIL
    }
    
    private String message;
    private OpStatus status;
    
    public ResponseMessage(OpStatus status, String message) {
    	this.status = status;
    	this.message = message;
    }
    
    public String getMessage() {
    	return message;
    }
    
    public OpStatus getStatus() {
    	return status;
    }
    
	
	
	
	
	
}
