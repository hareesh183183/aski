package com.angidi.aski.auth;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class AuthConfig {
	
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthConfig.class);

	@Bean
	public SecurityFilterChain authFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository, AskiUserDetailsService userDetailsService) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.requestCache(cache -> cache.disable());
		http.formLogin(c -> c.disable());
		http.authorizeHttpRequests(r -> r.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers(
		                "/actuator/info", "/actuator/health", "/actuator/health/*", "/actuator/badge/info", "/actuator/badge/health",
		                "/api/login", "/api/login/service", "/api/login/options", "/login*",
		                //"/oauth2/google", "/oauth2/authorization/google",
		                "/index*", "/*.*",
		                "/api/docs", "/api/docs/swagger-config", "/docs", "/swagger-ui/*.*", "/swagger-ui/**"
		            ).permitAll()
				.anyRequest().authenticated());
				http.oauth2Client(Customizer.withDefaults());
				http.oauth2Login(lo -> {
					lo.userInfoEndpoint(info -> {
						final OidcUserService impl = new OidcUserService();
						info.oidcUserService(ser -> {
							OidcUser user = impl.loadUser(ser);
							OidcUser as = userDetailsService.loadUserByOidc(user);
							return as;
						});
						lo.loginPage("/login.html");
						lo.failureHandler((req, res, ex) -> {
							LOG.error("Auth failure debug: {}", req, ex);
			                res.sendRedirect("/login?reason=login_failure");
						});
						lo.permitAll();
					});
				});
				//http.oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()));
		
		return http.build();
	}
}
