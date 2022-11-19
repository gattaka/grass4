package cz.gattserver.grass.core.services.impl;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GrassUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	public UserDetails loadUserByUsername(String username) {
		final UserInfoTO user = userService.getUser(username);
		if (user == null)
			throw new UsernameNotFoundException("Unable to find user");
		return user;
	}

}
