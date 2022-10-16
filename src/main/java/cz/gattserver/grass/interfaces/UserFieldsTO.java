package cz.gattserver.grass.interfaces;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import cz.gattserver.grass.model.domain.User;
import cz.gattserver.grass.security.CoreRole;
import cz.gattserver.grass.security.Role;

/**
 * Společný předek {@link User} TO tříd, který nemusí implementovat
 * {@link Serializable}
 * 
 * @author Hynek
 *
 */
public class UserFieldsTO {

	/**
	 * Jméno uživatele
	 */
	private String name;

	/**
	 * Heslo uživatele
	 */
	private String password;

	/**
	 * Role uživatele
	 */
	private Set<Role> roles = new HashSet<>();

	/**
	 * Datum registrace
	 */
	private LocalDateTime registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	private LocalDateTime lastLoginDate;

	/**
	 * Email
	 */
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private Boolean confirmed = false;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDateTime getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(LocalDateTime lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getPassword() {
		return password;
	}

	public boolean hasRole(CoreRole role) {
		return getRoles().contains(role);
	}

	public boolean isAdmin() {
		return hasRole(CoreRole.ADMIN);
	}

	@Override
	public String toString() {
		return "Name: " + (name == null ? "" : name) + " Roles: " + roles.toString();
	}

}
