package cz.gattserver.grass.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cz.gattserver.grass.interfaces.UserInfoTO;
import cz.gattserver.grass.services.UserService;

@Service
public class GrassUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;

	public GrassUserDetailServiceImpl() {
		System.out.println("DEBUG GrassUserDetailServiceImpl constructor");
	}

	public UserDetails loadUserByUsername(String username) {
		final UserInfoTO user = userService.getUser(username);
		if (user == null)
			throw new UsernameNotFoundException("Unable to find user");
		return user;
	}

}
