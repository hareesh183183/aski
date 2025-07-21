package com.angidi.aski.auth;

import java.time.Instant;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "roles")
@Getter
@Setter
public class Role {

	@Id
	private Long id;
	@Nonnull
	private String name;
	@Nonnull
	private Instant createdOn;
	@Nonnull
	private Instant updatedOn;
	
	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + "]";
	}
	
}
