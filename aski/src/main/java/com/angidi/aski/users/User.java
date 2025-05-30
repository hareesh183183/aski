package com.angidi.aski.users;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

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
@JsonAutoDetect(
	    getterVisibility = JsonAutoDetect.Visibility.NONE,
	    setterVisibility = JsonAutoDetect.Visibility.NONE,
	    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
	    creatorVisibility = JsonAutoDetect.Visibility.NONE,
	    fieldVisibility = JsonAutoDetect.Visibility.ANY)
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Instant getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Instant createdOn) {
		this.createdOn = createdOn;
	}

	public Instant getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Instant updatedOn) {
		this.updatedOn = updatedOn;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Instant getLastLoginOn() {
		return lastLoginOn;
	}

	public void setLastLoginOn(Instant lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}

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
