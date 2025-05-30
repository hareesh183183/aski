package com.angidi.aski.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RoleRepository repo;
	
	@GetMapping
	List<Role> getRoles(@AuthenticationPrincipal OidcUser principal){
		try {
			return repo.findAll();
		} catch (Exception e) {
			LOG.error("Api.getRoles() =>", e);
			throw e;
		}
		
	}
}
