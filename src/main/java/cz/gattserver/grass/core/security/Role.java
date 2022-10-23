package cz.gattserver.grass.core.security;

import org.springframework.security.core.GrantedAuthority;

public interface Role extends GrantedAuthority {

	String getRoleName();

}
