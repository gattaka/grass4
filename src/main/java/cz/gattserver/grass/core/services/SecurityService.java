package cz.gattserver.grass.core.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.impl.LoginResult;

public interface SecurityService {

	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
			HttpServletResponse response);

	public UserInfoTO getCurrentUser();

	void logout(HttpServletRequest request, HttpServletResponse response);

}
