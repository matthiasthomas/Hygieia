package com.capitalone.dashboard.auth.idm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.auth.standard.StandardAuthenticationToken;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.rest.DashboardController;

public class IDMLoginRequestFilter extends
		UsernamePasswordAuthenticationFilter {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DashboardController.class);

	public IDMLoginRequestFilter(String path,
			AuthenticationManager authManager,
			AuthenticationResultHandler authenticationResultHandler) {
		super();
		setAuthenticationManager(authManager);
		setAuthenticationSuccessHandler(authenticationResultHandler);
		setFilterProcessesUrl(path);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String username = null;
		String userId = ((HttpServletRequest) request).getHeader("sm_user");
		String fullName = ((HttpServletRequest) request).getHeader("HTTP_hygieia_sm_id");
		LOGGER.info("User ID:" + userId + " User Name:" + fullName);
		
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: "
							+ request.getMethod());
		}
		
		/*
    	Enumeration<String> headerNames = ((HttpServletRequest) request).getHeaderNames();
    	while(headerNames.hasMoreElements()) {
    	  String headerName = headerNames.nextElement();
    	  System.out.println("Header Name - " + headerName + ", Value - " + ((HttpServletRequest) request).getHeader(headerName));
    	}*/
    	
    	if(StringUtils.isEmpty(userId)) {
    		throw new AuthenticationServiceException("Not able to get IDM header");
    	} else {
    		username = userId;
    	}

		String password = obtainPassword(request);

/*		if (username == null) {
			username = "";
		}
*/
		if (password == null) {
			password = "";
		}

		username = username.trim();

		StandardAuthenticationToken authRequest = new StandardAuthenticationToken(
				username, password);

		authRequest.setDetails(AuthType.IDM);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

}
