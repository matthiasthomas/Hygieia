package com.capitalone.dashboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.capitalone.dashboard.model.UserInfo;

public interface UserInfoRepositoryCustom {
	Page<UserInfo> searchUser(String pattern,Pageable page);
}
