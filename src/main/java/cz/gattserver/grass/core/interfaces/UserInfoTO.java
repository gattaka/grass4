package cz.gattserver.grass.core.interfaces;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author gatt
 */
public class UserInfoTO extends UserFieldsTO implements UserDetails {

	@Serial
	private static final long serialVersionUID = 6412071100899777369L;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles();
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return isConfirmed();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public String getUsername() {
		return getName();
	}

}
