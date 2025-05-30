package com.angidi.aski.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository repo;

	@PostMapping
	public User create(@RequestBody User user) {
		try {
			return repo.save(user);
		} catch (Exception e) {
			LOG.error("User.create({}) ", user, e);
			throw e;
		}
	}

	@GetMapping
	public List<User> get(@RequestBody(required = false) User user) {
		try {
			if(null != user)
				return repo.filter(user.getId(), user.getName(), user.getEmail(), user.getRoleId(), user.getCountry(), user.getCity());
			else
				return repo.findAll();
		} catch (Exception e) {
			LOG.error("User.get({}, {}, {}) ", user, e);
			throw e;
		}
	}

	@GetMapping("/login/okta")
	public User oktaLogin(@AuthenticationPrincipal User principal) throws Exception {
		try {
			return principal;
		} catch (Exception e) {
			LOG.error("User.get({}, {}, {}) ", principal, e);
			throw e;
		}
	}
}
