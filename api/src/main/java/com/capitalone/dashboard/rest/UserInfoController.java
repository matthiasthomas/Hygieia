package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.service.UserInfoService;

@RestController
@RequestMapping("/users")
public class UserInfoController {
    
	private UserInfoService userInfoService;
	
	@Autowired
	public UserInfoController(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}
	
	@RequestMapping(method = GET)
    public Collection<UserInfo> getUsers() {
        return userInfoService.getUsers();
    }
	
	@RequestMapping(value = "/search/{page}/{size}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Page<UserInfo> searchUsers(@PathVariable int page,@PathVariable int size) {
        return userInfoService.searchUsers("",page,size);
    }

    @RequestMapping(value = "/search/{page}/{size}/{query}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Page<UserInfo> searchUsers(@PathVariable int page,@PathVariable int size,@PathVariable String query) {
        return userInfoService.searchUsers(query,page,size);
    }
}
