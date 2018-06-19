package com.capitalone.dashboard.request;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;

@SuppressWarnings({"CPD-START"})
public class CreateUserRequest {
	
	@NotNull
	private String username;
    
    @NotNull
	private UserRole role;
    
    @NotNull
	private AuthType authType;
    
    @NotNull
	private String firstName;
    
	private String middleName;
	
    @NotNull
	private String lastName;
    
    @NotNull 
    @Email
	private String emailAddress;
	
	public CreateUserRequest() {
	    
	}
	
	public String getUsername() {
		return username==null?username:username.toLowerCase(Locale.US);
	}
	
	public void setUsername(String username) {
		this.username = username==null?username:username.toLowerCase(Locale.US);
	}
	

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return lastName + " ," + firstName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@SuppressWarnings({"CPD-END"})
	public UserInfo getUserInfo()
	{
		UserInfo u = new UserInfo();
		return u;
	}
	
}

