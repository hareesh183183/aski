package com.angidi.aski.auth;

import com.angidi.aski.users.AskiUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class AuthConfig {

    private final JWTAuthFilter jwtAuthFilter;
    private final AskiUserDetailsService userDetailsService;
    private AuthEntryPoint authEntryPoint;

    public AuthConfig(JWTAuthFilter jwtAuthFilter, AskiUserDetailsService userDetailsService, AuthEntryPoint authEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
    }
	
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthConfig.class);

    @Bean
    public CookieSerializer cookieSerializer() {
        var cookie = new DefaultCookieSerializer();
        cookie.setCookieName("ASKI");
        cookie.setCookiePath("/");
        cookie.setUseSecureCookie(true);
        cookie.setUseHttpOnlyCookie(true);
        return cookie;
    }

	@Bean
	public SecurityFilterChain authFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository, AskiUserDetailsService userDetailsService) throws Exception {
		http.csrf(CsrfConfigurer::disable);
        http.cors(cors ->cors.configurationSource(corsConfigurationSource()));
		http.requestCache(RequestCacheConfigurer::disable);
		http.formLogin(FormLoginConfigurer::disable);
		http.authorizeHttpRequests(r -> r
				.requestMatchers(
		                "/actuator/info", "/actuator/health", "/actuator/health/*", "/actuator/badge/info", "/actuator/badge/health",
		                "/oauth2/google", "/oauth2/authorization/google",
                        "/api/auth/login/*",
                        "/api/users/create",
		                "/index*", "/*.*",
		                "/api/docs", "/api/docs/swagger-config", "/docs", "/swagger-ui/*.*", "/swagger-ui/**"
		            ).permitAll()
				.anyRequest().authenticated());
                http.exceptionHandling(error -> error.authenticationEntryPoint(authEntryPoint));
				http.oauth2Client(Customizer.withDefaults());
				http.oauth2Login(lo -> {
					lo.userInfoEndpoint(info -> {
						final OidcUserService impl = new OidcUserService();
						info.oidcUserService(ser -> {
							OidcUser user = impl.loadUser(ser);
							OidcUser as = userDetailsService.loadUserByOidc(user);
							return as;
						});
						lo.failureHandler((req, res, ex) -> {
							LOG.error("Auth failure debug: {}", req, ex);
			                res.sendRedirect("/login?reason=login_failure");
						});
						lo.permitAll();
					});
				});
                http.sessionManagement(session -> {
                   session.maximumSessions(1)
                           .expiredUrl("/login?reason=login_failure")
                           .maxSessionsPreventsLogin(true);
                   session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

                });
                http.authenticationProvider(authenticationProvider());
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3005"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        final UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration);
        return configSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return SecurityContextHolder.getContextHolderStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
        ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        strategy.setMaximumSessions(1);

        return new CompositeSessionAuthenticationStrategy(List.of(
                new SessionFixationProtectionStrategy(),
                new RegisterSessionAuthenticationStrategy(sessionRegistry),
                strategy
        ));
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public <S extends Session>SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<S> sessionRepo) {
        return new SpringSessionBackedSessionRegistry<>(sessionRepo);

    }

}
