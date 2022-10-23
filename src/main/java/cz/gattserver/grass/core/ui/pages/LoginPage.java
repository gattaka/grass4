package cz.gattserver.grass.core.ui.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletService;

import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;

@Route(value = "loginpage")
@PageTitle("Login")
public class LoginPage extends OneColumnPage {

	private static final long serialVersionUID = 2568522523298977106L;

	@Autowired
	private SecurityService securityFacade;

	public LoginPage() {
		init();
	}

	private LoginResult login(String username, String password, boolean remember) {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		HttpServletResponse response = VaadinServletService.getCurrentResponse().getHttpServletResponse();
		LoginResult loginResult = securityFacade.login(username, password, remember, request, response);
		if (LoginResult.SUCCESS == loginResult) {
			// Reinitialize the session to protect against session fixation
			// attacks. This does not work with websocket communication.
			VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
		}
		return loginResult;
	}

	@Override
	protected void createColumnContent(Div layout) {
		layout.add(new H2("Přihlášení"));

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setPadding(false);
		layout.add(vl);

		TextField username = new TextField("Login");
		vl.add(username);

		PasswordField password = new PasswordField("Heslo");
		vl.add(password);

		Checkbox rememberMe = new Checkbox("Zapamatovat si přihlášení");
		vl.add(rememberMe);

		Button login = new Button("Přihlásit", evt -> {
			String pword = password.getValue();
			password.setValue("");
			LoginResult loginResult = login(username.getValue(), pword, rememberMe.getValue());
			switch (loginResult) {
			case FAILED_CREDENTIALS:
				UIUtils.showError("Špatné přihlašovací jméno nebo heslo");
				username.focus();
				break;
			case FAILED_DISABLED:
				UIUtils.showError("Účet je deaktivován");
				break;
			case FAILED_LOCKED:
				UIUtils.showError("Účet je zamčen");
				break;
			case SUCCESS:
				UIUtils.redirect(getPageURL(homePageFactory));
				break;
			}
		});
		login.addClickShortcut(Key.ENTER);
		vl.add(login);
	}
}
