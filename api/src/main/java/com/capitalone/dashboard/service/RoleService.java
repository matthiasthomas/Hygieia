package com.capitalone.dashboard.service;

import java.util.List;

import com.capitalone.dashboard.model.Role;

public interface RoleService {

	List<Role> getAllRoles();

	Role getRoleByName(String name);

}
