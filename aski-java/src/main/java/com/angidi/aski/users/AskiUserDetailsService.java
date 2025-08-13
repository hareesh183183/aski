package com.angidi.aski.users;

import java.time.Instant;
import java.util.List;

import com.angidi.aski.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AskiUserDetailsService implements UserDetailsService{


	private final UserRepository repo;
    private final RoleRepository roleRepo;

    public AskiUserDetailsService(UserRepository repo, RoleRepository roleRepo) {
        this.repo = repo;
        this.roleRepo = roleRepo;
    }
	
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
		List<User> users = repo.filter(null, null, user.getEmail(), null, null, null);
		if (users.isEmpty()) {
			user.setName(oidc.getFullName());
			user.setEnabled(true);
			user.setCreatedOn(Instant.now());
			user.setUpdatedOn(Instant.now());
			user.setRoleId(roleRepo.findByName("SUPPLIER").get().getId());
		} else {
			user = users.get(0);
		}
		user.setLastLoginOn(Instant.now());
		user = repo.save(user);
		return user;
	}

}
