package com.capitalone.dashboard.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "error_log")
public class ErrorLog extends BaseModel {
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss z");
	private static final SimpleDateFormat FORMATTER_TRIM = new SimpleDateFormat("yyyy-MMM-dd");

	private String message;
	private String exception;
	private String data;
	private String module;
	private String functionality;
	private long timestamp;
	private String createDate;

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFunctionality() {
		return functionality;
	}

	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {		
		this.timestamp = timestamp;
		if(timestamp==0) {
			createDate = "";
		} else {
			synchronized (this) {
				createDate = FORMATTER_TRIM.format(new Date(timestamp));
			}
		}
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCreateDate() {
		synchronized (this) {
			return createDate = createDate == null ? FORMATTER_TRIM.format(new Date(
					timestamp)) : createDate;
		}
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String truncateException() {
		return (exception != null && exception.length() > 100) ? exception
				.substring(0, 100) : exception;
	}

	public String displayDate() {
		synchronized (this) {
			return FORMATTER.format(new Date(timestamp));
		}
	}
}
