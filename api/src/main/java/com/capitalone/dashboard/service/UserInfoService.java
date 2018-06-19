package com.capitalone.dashboard.service;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;

public interface UserInfoService {

	Collection<? extends GrantedAuthority> getAuthorities(String username,
			String firstName, String middleName, String lastName,
			String displayName, String emailAddress, AuthType authType);

	UserInfo getUserInfo(String username, String firstName, String middleName,
			String lastName, String displayName, String emailAddress,
			AuthType authType);

	Collection<UserInfo> getUsers();

	UserInfo promoteToAdmin(String username, AuthType authType);

	UserInfo demoteFromAdmin(String username, AuthType authType);

	boolean isUserValid(String userId, AuthType authType);

	Collection<UserInfo> getUsersByRole(ObjectId role);

	UserInfo createUser(UserInfo userInfo);

	boolean deleteUser(ObjectId id);

	Page<UserInfo> searchUsers(String search, int page, int size);

	UserInfo updateUser(UserInfo userInfo);
}
