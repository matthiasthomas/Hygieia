package com.capitalone.dashboard.auth.idm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.standard.StandardAuthenticationToken;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.AuthenticationService;

@Component
public class IDMAuthenticationProvider implements AuthenticationProvider {

	private final AuthenticationService authService;

	@Autowired
	public IDMAuthenticationProvider(AuthenticationService authService) {
		this.authService = authService;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		/*
		 * return authService.authenticate(authentication.getName(), (String)
		 * authentication.getCredentials());
		 */

		Collection<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ROLE_USER);

		return new UsernamePasswordAuthenticationToken(
				authentication.getName(), "", createAuthorities(roles));

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return StandardAuthenticationToken.class
				.isAssignableFrom(authentication);
	}

	private Collection<? extends GrantedAuthority> createAuthorities(
			Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities
				.forEach(authority -> {
					grantedAuthorities.add(new SimpleGrantedAuthority(authority
							.name()));
				});

		return grantedAuthorities;
	}
}
