package cz.gattserver.grass.core.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.impl.LoginResult;

import java.security.NoSuchAlgorithmException;

public interface SecurityService {

    LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
                      HttpServletResponse response);

    UserInfoTO getCurrentUser();

    String computeAccessHash(String value);

    void logout(HttpServletRequest request, HttpServletResponse response);

}