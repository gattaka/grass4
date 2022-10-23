package cz.gattserver.grass.core.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/core")
public class CoreResource {

	@Autowired
	private SecurityService securityFacade;

	@RequestMapping("/logged")
	public ResponseEntity<String> logged() {
		UserInfoTO user = securityFacade.getCurrentUser();
		if (user.getName() == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			return new ResponseEntity<>(user.getName(), HttpStatus.OK);
		}
	}

	// curl -i -X POST -d login=jmeno -d password=heslo
	// http://localhost:8180/web/ws/pg/login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestParam("login") String username,
			@RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
		LoginResult result = securityFacade.login(username, password, false, request, response);
		if (LoginResult.SUCCESS.equals(result)) {
			return new ResponseEntity<>(request.getSession().getId(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		securityFacade.logout(request, response);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
