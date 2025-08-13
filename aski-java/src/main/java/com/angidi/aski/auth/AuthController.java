package com.angidi.aski.auth;

import com.angidi.aski.users.AskiUserDetailsService;
import com.angidi.aski.users.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthController.class);


    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AskiUserDetailsService userDetailsService;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    public AuthController(AuthenticationManager authenticationManager, JWTService jwtService, AskiUserDetailsService userDetailsService, SecurityContextHolderStrategy securityContextHolderStrategy, SecurityContextRepository securityContextRepository, SessionAuthenticationStrategy securityAuthenticationStrategy, SessionAuthenticationStrategy sessionAuthenticationStrategy){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.securityContextHolderStrategy = securityContextHolderStrategy;
        this.securityContextRepository = securityContextRepository;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    public record JWTRequest(@NotNull @NotBlank @Email String email, @NotNull @NotBlank String password) {
        @Override
        public String toString() {
            return "JWTRequest{" +
                    "email='" + email + '\'' +
                    '}';
        }
    }
    @PostMapping("/login/jwt")
    public JWTService.JWTResponse signInJwt(@RequestBody @Valid JWTRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try{
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email, request.password));
            if(authentication.isAuthenticated()){
                var token = jwtService.generateJwtToken(request.email);
                User user = userDetailsService.loadUserByUsername(request.email);
                LOG.debug("Api.signInJwt({}) => Authenticated", request);
                login(user, httpServletRequest, httpServletResponse);
                return token;
            } else{
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email and Password is not matching :"+request.email);
            }
        } catch (Exception e){
            LOG.error("Api.signInJwt({})", request, e);
            throw e;
        }
    }

    @GetMapping("/login/okta")
    public User oktaLogin(@AuthenticationPrincipal User principal, HttpServletRequest  httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            login(principal, httpServletRequest, httpServletResponse);
            return principal;
        } catch (Exception e) {
            LOG.error("User.get({}) ", principal, e);
            throw e;
        }
    }

    private SessionToken login (User user, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SessionToken token = new SessionToken(user);
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(token);
        this.securityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);
        this.sessionAuthenticationStrategy.onAuthentication (token, httpServletRequest, httpServletResponse);
        return token;
    }
}
