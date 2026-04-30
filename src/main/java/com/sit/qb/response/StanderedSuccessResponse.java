package com.sit.qb.response;

import java.time.LocalDateTime;

public class StanderedSuccessResponse {
	
	private LocalDateTime timestamp=LocalDateTime.now();
    private int status;
    private String message;
    private Object data;
    
  
    
    public StanderedSuccessResponse() {
    	
	}

	public StanderedSuccessResponse( int status, String message, Object data) {
		super();
		
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    
    

}
