package cz.gattserver.grass.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass.interfaces.UserInfoTO;
import cz.gattserver.grass.services.impl.LoginResult;

public interface SecurityService {

	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response);

	public UserInfoTO getCurrentUser();

	void logout(HttpServletRequest request, HttpServletResponse response);

}
