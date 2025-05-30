package com.angidi.aski.auth;

import java.time.Instant;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "roles")
public class Role {

	@Id
	private Long id;
	@Nonnull
	private String name;
	@Nonnull
	private Instant createdOn;
	@Nonnull
	private Instant updatedOn;
	
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
	
	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + "]";
	}
	
}
