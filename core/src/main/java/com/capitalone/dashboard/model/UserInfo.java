package com.capitalone.dashboard.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.google.common.collect.Sets;

@Document(collection = "user_info")
@CompoundIndexes({ @CompoundIndex(name = "username_authType", def = "{'username' : 1, 'authType': 1}") })
public class UserInfo {

	@Id
	private ObjectId id;
	private String username;
	private Collection<UserRole> authorities;
	private AuthType authType;
	private String firstName;
	private String middleName;
	private String lastName;
	private String displayName;
	private String emailAddress;
	private Collection<ObjectId> roles;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDate;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date updatedDate;

	public UserInfo() {
		authorities = Sets.newHashSet();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUsername() {
		return username == null ? username : username.toLowerCase(Locale.US);
	}

	public void setUsername(String username) {
		this.username = username == null ? username : username.toLowerCase(Locale.US);
	}

	public Collection<UserRole> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<UserRole> authorities) {
		this.authorities = authorities;
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
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Collection<ObjectId> getRoles() {
		return roles;
	}

	public void setRoles(Collection<ObjectId> roles) {
		this.roles = roles;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate == null ? Calendar.getInstance()
				.getTime() : createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = Calendar.getInstance().getTime();
	}
}
