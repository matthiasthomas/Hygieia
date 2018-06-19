package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.Set;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.auth.exceptions.CreateUserException;
import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.request.ApiTokenRequest;
import com.capitalone.dashboard.request.CreateUserRequest;
import com.capitalone.dashboard.request.UpdateUserRequest;
import com.capitalone.dashboard.service.ApiTokenService;
import com.capitalone.dashboard.service.UserInfoService;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.HygieiaConstants;
import com.google.common.collect.Sets;

@RestController
@RequestMapping("/admin")
@Admin
public class AdminController {

	private final UserInfoService userInfoService;

	private final ApiTokenService apiTokenService;

	@Autowired
	public AdminController(UserInfoService userInfoService,
			ApiTokenService apiTokenService) {
		this.userInfoService = userInfoService;
		this.apiTokenService = apiTokenService;
	}

	@RequestMapping(path = "/users/addAdmin", method = RequestMethod.POST)
	public ResponseEntity<UserInfo> addAdmin(@RequestBody UserInfo user) {
		UserInfo savedUser = userInfoService.promoteToAdmin(user.getUsername(),
				user.getAuthType());

		return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/updateUser", method = RequestMethod.PUT)
	public ResponseEntity<UserInfo> updateUser(
			@RequestBody UpdateUserRequest user) {

		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(user.getUsername());
		userInfo.setFirstName(user.getFirstName());
		userInfo.setMiddleName(user.getMiddleName());
		userInfo.setLastName(user.getLastName());
		userInfo.setDisplayName(user.getDisplayName());
		userInfo.setEmailAddress(user.getEmailAddress());
		userInfo.setId(user.getId());
		userInfo.setAuthType(user.getAuthType());
		userInfo.setUpdatedDate(null);
		Set<UserRole> authorities = Sets.newHashSet();
		authorities.add(user.getRole());
		userInfo.setAuthorities(authorities);

		UserInfo savedUser = userInfoService.updateUser(userInfo);
		return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
	}

	@RequestMapping(path = "/users/deleteUser/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteUser(@PathVariable ObjectId id) {
		return new ResponseEntity<Boolean>(userInfoService.deleteUser(id),
				HttpStatus.OK);
	}

	@RequestMapping(path = "/users/createUser", method = RequestMethod.PUT)
	public ResponseEntity<DataResponse<UserInfo>> createUser(
			@RequestBody CreateUserRequest user) {
		try {
			UserInfo userInfo = new UserInfo();
			userInfo.setUsername(user.getUsername());
			userInfo.setFirstName(user.getFirstName());
			userInfo.setMiddleName(user.getMiddleName());
			userInfo.setLastName(user.getLastName());
			userInfo.setDisplayName(user.getDisplayName());
			userInfo.setEmailAddress(user.getEmailAddress());
			userInfo.setAuthType(user.getAuthType());
			userInfo.setCreatedDate(null);
			userInfo.setUpdatedDate(null);
			Set<UserRole> authorities = Sets.newHashSet();
			authorities.add(user.getRole());
			userInfo.setAuthorities(authorities);

			UserInfo savedUser = userInfoService.createUser(userInfo);
			return new ResponseEntity<DataResponse<UserInfo>>(
					new DataResponse<UserInfo>(savedUser,
							"User created successfully", 1,
							System.currentTimeMillis()), HttpStatus.OK);
		} catch (CreateUserException e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AdminController.CREATEUSER", e.getMessage(), e);
			return new ResponseEntity<DataResponse<UserInfo>>(
					new DataResponse<UserInfo>(null, e.getMessage(), -1,
							System.currentTimeMillis()), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ge) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AdminController.CREATEUSER", ge.getMessage(), ge);
			return new ResponseEntity<DataResponse<UserInfo>>(
					new DataResponse<UserInfo>(
							null,
							"Unknown error while user creation, please try again after some time",
							-2, System.currentTimeMillis()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/users/removeAdmin", method = RequestMethod.POST)
	public ResponseEntity<UserInfo> removeAuthorityFromUser(
			@RequestBody UserInfo user) {
		UserInfo savedUser = userInfoService.demoteFromAdmin(
				user.getUsername(), user.getAuthType());

		return new ResponseEntity<UserInfo>(savedUser, HttpStatus.OK);
	}

	@RequestMapping(value = "/createToken", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createToken(
			@Valid @RequestBody ApiTokenRequest apiTokenRequest) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(
					apiTokenService.getApiToken(apiTokenRequest.getApiUser(),
							apiTokenRequest.getExpirationDt()));
		} catch (EncryptionException e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AdminController.CREATETOKEN", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					e.getMessage());
		} catch (HygieiaException e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AdminController.CREATETOKEN", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					e.getMessage());
		}
	}

	@RequestMapping(value = "/updateToken/{id}", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateToken(
			@Valid @RequestBody ApiTokenRequest apiTokenRequest,
			@PathVariable ObjectId id) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(
					apiTokenService.updateToken(
							apiTokenRequest.getExpirationDt(), id));
		} catch (HygieiaException e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AdminController.updateToken", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					e.getMessage());
		}
	}

	@RequestMapping(value = "/deleteToken/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteToken(@PathVariable ObjectId id) {
		apiTokenService.deleteToken(id);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/apitokens", method = RequestMethod.GET)
	public Collection<ApiToken> getApiTokens() {
		Collection<ApiToken> tokens = apiTokenService.getApiTokens();
		return tokens;
	}
}
