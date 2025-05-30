package com.angidi.aski.auth;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angidi.aski.users.User;
import com.angidi.aski.users.UserRepository;

@Service
public class AskiUserDetailsService implements UserDetailsService{

	@Autowired
	UserRepository repo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Override
	@Transactional
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = new User();
		user.setEmail(username);
		List<User> users = repo.filter(null, null, user.getEmail(), null, null, null);
		if (users.isEmpty()) {
			user.setRoleId(roleRepo.findByName("SUPPLIER").get().getId());
			return repo.save(user);
		} else {
			return users.get(0);
		}
	}
	
	@Transactional
	public User loadUserByOidc(StandardClaimAccessor oidc) throws UsernameNotFoundException {
		User user = new User();
		user.setEmail(oidc.getEmail());
		user.setLastLoginOn(Instant.now());
		List<User> users = repo.filter(null, null, user.getEmail(), null, null, null);
		if (users.isEmpty()) {
			user.setName(oidc.getFullName());
			user.setRoleId(roleRepo.findByName("SUPPLIER").get().getId());
		} else {
			user = users.get(0);
		}
		user = repo.save(user);
		return user;
	}

}
