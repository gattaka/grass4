package cz.gattserver.grass.core.model.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "USER_ACCOUNTS")
@Getter
@Setter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class User {

	/**
	 * DB identifikátor
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean hasRole(Role role) {
		return getRoles().contains(role.getAuthority());
	}

	public boolean isAdmin() {
		return hasRole(CoreRole.ADMIN);
	}

}
