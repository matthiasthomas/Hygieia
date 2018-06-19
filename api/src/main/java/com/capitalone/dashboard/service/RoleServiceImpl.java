package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	/**
	 * Default autowired constructor for repositories
	 * 
	 * @param roleRepository
	 *            Repository containing all roles
	 */
	@Autowired
	public RoleServiceImpl(RoleRepository roleRepository) {		
		this.roleRepository =roleRepository;
	}

	@Override
	public Role getRoleByName(String name) {
		return roleRepository.findByName(name); 
	}
	
	@Override
	public List<Role> getAllRoles() {
		return (List<Role>)roleRepository.findAll();
	}
}
