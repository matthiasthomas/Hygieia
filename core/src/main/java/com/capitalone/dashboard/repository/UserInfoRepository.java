package com.capitalone.dashboard.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;

public interface UserInfoRepository extends PagingAndSortingRepository<UserInfo, ObjectId>,QueryDslPredicateExecutor<UserInfo>,UserInfoRepositoryCustom{

	UserInfo findByUsernameAndAuthType(String username, AuthType authType);
	
	List<UserInfo> findByUsername(String username);

	Page<UserInfo> findByUsernameLikeOrFirstNameLikeOrLastNameLikeOrderByUsernameAscAllIgnoreCase(String username, String firstName,String lastName,Pageable page);

    Collection<UserInfo> findByAuthoritiesIn(UserRole roleAdmin);

    Iterable<UserInfo> findByOrderByUsernameAsc();
    
	@Query(value="{'roles': ?0}")
	List<UserInfo> findByRole(ObjectId role);
}
