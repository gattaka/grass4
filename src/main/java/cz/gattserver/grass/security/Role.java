package cz.gattserver.grass.security;

import org.springframework.security.core.GrantedAuthority;

public interface Role extends GrantedAuthority {

	String getRoleName();

}
