package cz.gattserver.grass.fm.test;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Service
@Primary
public class MockSecurityService implements SecurityService {

	private Set<Role> roles;

	@Override
	public LoginResult login(String username, String password, boolean remember, HttpServletRequest request,
							 HttpServletResponse response) {
		return null;
	}

	@Override
	public UserInfoTO getCurrentUser() {
		UserInfoTO mockTO = new UserInfoTO();
		mockTO.setName("mockUser");
		mockTO.setRoles(roles);
		return mockTO;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
	}

}
