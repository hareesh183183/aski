package com.angidi.aski.auth;

import com.angidi.aski.users.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SessionToken extends AbstractAuthenticationToken {
    private final User user;
    private final String credentials;
    public SessionToken(User user) {
        super(null);
        this.user = user;
        this.credentials = user.getEmail();
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
