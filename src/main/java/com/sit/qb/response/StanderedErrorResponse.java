package com.sit.qb.response;

import java.time.LocalDateTime;
import java.util.List;

public class StanderedErrorResponse {

	private LocalDateTime timestamp = LocalDateTime.now();
	private int status;
	private boolean success = false;
	private String message;

	private Object errors;

	public StanderedErrorResponse() {
		// TODO Auto-generated constructor stub
	}

	public StanderedErrorResponse(int status, String message, Object errors) {
		super();
		this.status = status;
		this.message = message;

		this.errors = errors;
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

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getErrors() {
		return errors;
	}

	public void setErrors(Object errors) {
		this.errors = errors;
	}

}
