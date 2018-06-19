package com.capitalone.dashboard.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.util.PageUtil;

public class UserInfoRepositoryImpl implements UserInfoRepositoryCustom{
	private static final Log LOG = LogFactory.getLog(UserInfoRepositoryImpl.class);
	
    @Autowired
    private MongoOperations operations;
	
	@Override
	public Page<UserInfo> searchUser(String pattern, Pageable page) {
		BasicQuery query = new BasicQuery("{ \"$or\" : [ { \"$or\" : [ { \"username\" : { \"$regex\" : \""+pattern+"\" , \"$options\" : \"i\"}} , { \"firstName\" : { \"$regex\" : \""+pattern+"\" , \"$options\" : \"i\"}}]} , { \"lastName\" : { \"$regex\" : \""+pattern+"\" , \"$options\" : \"i\"}}]}");
		LOG.info("User Query >> " + query.toString());
		int total = operations.find(query, UserInfo.class).size();
		LOG.info("Total Users >>  "  + total);

		query.skip(page.getPageSize() * (page.getPageNumber() -1));
		query.limit(page.getPageSize());
		query.with(new Sort(Sort.Direction.ASC, "username"));
		Page<UserInfo> pages = new PageUtil<UserInfo>(operations.find(query, UserInfo.class),page,total);

		return pages;
	}
}
