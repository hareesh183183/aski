package com.angidi.aski.users;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
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
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "[^0-9]*", message = "Name cannot contain numbers")
	private String name;
    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Email
	private String email;
	@Column(name = "role_id")
	private Long roleId;
	private String country;
	private String city;
	private String state;
    private String password;
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
		return this.password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}



}
