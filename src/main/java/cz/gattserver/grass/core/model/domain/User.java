package cz.gattserver.grass.core.model.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;

import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "USER_ACCOUNTS")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class User {

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Jméno uživatele
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * Heslo uživatele
	 */
	@Column(nullable = false)
	private String password;

	/**
	 * Role uživatele
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "USER_ACCOUNTS_ROLES", joinColumns = @JoinColumn(name = "USER_ACCOUNTS_ID"))
	private Set<String> roles = new HashSet<>();

	/**
	 * Datum registrace
	 */
	@Column(name = "REGISTRATION_DATE")
	private LocalDateTime registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	@Column(name = "LAST_LOGIN_DATE")
	private LocalDateTime lastLoginDate;

	/**
	 * Oblíbené obsahy
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<ContentNode> favourites;

	/**
	 * Email
	 */
	@Column(nullable = false)
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private Boolean confirmed = false;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
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

	public Set<ContentNode> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNode> favourites) {
		this.favourites = favourites;
	}

	public boolean hasRole(Role role) {
		return getRoles().contains(role.getAuthority());
	}

	public boolean isAdmin() {
		return hasRole(CoreRole.ADMIN);
	}

}
