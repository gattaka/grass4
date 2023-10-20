package cz.gattserver.grass.test;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;

@Service
@Primary
public class MockSecurityService implements SecurityService {

	private UserInfoTO infoTO;

	public MockSecurityService() {
		infoTO = new UserInfoTO();
		infoTO.setName("mockUser");
		infoTO.setRoles(new HashSet<>());
		infoTO.setId(33333L);
	}

	@Override
	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
							 HttpServletResponse response) {
		return null;
	}

	@Override
	public UserInfoTO getCurrentUser() {
		return infoTO;
	}

	public UserInfoTO getInfoTO() {
		return infoTO;
	}

	public void setInfoTO(UserInfoTO infoTO) {
		this.infoTO = infoTO;
	}

	public void setRoles(HashSet<Role> hashSet) {
		infoTO.setRoles(hashSet);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
	}

}
