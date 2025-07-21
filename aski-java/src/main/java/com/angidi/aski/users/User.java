package com.angidi.aski.users;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity(name = "users")
@Getter
@Setter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class User implements OidcUser, UserDetails, Principal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	@Column(name = "role_id")
	private Long roleId;
	private String country;
	private String city;
	private String state;
	@Column(name = "created_on")
	private Instant createdOn;
	@Column(name = "updated_on")
	private Instant updatedOn;
	@Column(name = "last_login_on")
	private Instant lastLoginOn;

	@Transient
	@JsonIgnore
	private OidcUser oidc;

	private boolean enabled;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", roleId=" + roleId + ", country=" + country
				+ ", city=" + city + "]";
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return oidc == null ? null : oidc.getAuthorities();
	}

	@Override
	public Map<String, Object> getClaims() {
		// TODO Auto-generated method stub
		return Map.of("test", "test");
	}

	@Override
	public OidcUserInfo getUserInfo() {
		// TODO Auto-generated method stub
		return oidc == null ? null : oidc.getUserInfo();
	}

	@Override
	public OidcIdToken getIdToken() {
		// TODO Auto-generated method stub
		return oidc == null ? null : oidc.getIdToken();
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return "0";
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

}
