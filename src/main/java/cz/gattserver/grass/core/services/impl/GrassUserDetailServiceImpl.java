package cz.gattserver.grass.core.services.impl;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;

@Service
public class GrassUserDetailServiceImpl implements UserDetailsService, Serializable {

	@Serial
	private static final long serialVersionUID = 5745870051782226283L;

	@Autowired
	private UserService userService;

	public UserDetails loadUserByUsername(String username) {
		final UserInfoTO user = userService.getUser(username);
		if (user == null)
			throw new UsernameNotFoundException("Unable to find user");
		return user;
	}
}