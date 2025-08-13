package com.angidi.aski.users;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.angidi.aski.auth.JWTService;
import com.angidi.aski.auth.Role;
import com.angidi.aski.auth.RoleRepository;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class);

	private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    UserController(UserRepository repo, AuthenticationManager authenticationManager, JWTService jwtService, PasswordEncoder encoder, RoleRepository roleRepository) {
        this.repo = repo;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

	@PostMapping("/create")
    @Transactional
	public User create(@RequestBody @Validated @Nonnull User user) {
		try {
            Optional.ofNullable(user.getPassword()).ifPresent(password -> user.setPassword(encoder.encode(password)));
            if(user.getRoleId() == null || !roleRepository.existsById(user.getRoleId()) ) {
                roleRepository.findByName("SUPPLIER").ifPresent(role -> user.setRoleId(role.getId()));
            }
            user.setEnabled(true);
            user.setCreatedOn(Instant.now());
            user.setUpdatedOn(Instant.now());
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
			LOG.error("User.get({}) ", user, e);
			throw e;
		}
	}

    @GetMapping("/@me")
    public User getMe(@AuthenticationPrincipal User principal) throws Exception {
        try {
            Optional<User> user = repo.findById(principal.getId());
            return user.orElse(null);
        } catch (Exception e) {
            LOG.error("User.getMe({}) ", principal, e);
            throw e;
        }
    }
}
