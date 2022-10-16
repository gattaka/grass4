package cz.gattserver.grass.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.services.UserService;
import cz.gattserver.grass.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.ui.util.UIUtils;

@Route(value = "registration")
@PageTitle("Registrace")
public class RegistrationPage extends OneColumnPage {

	private static final long serialVersionUID = 3104749805983602744L;

	@Autowired
	private UserService userFacade;

	private static final int MIN_USERNAME_LENGTH = 2;
	private static final int MAX_USERNAME_LENGTH = 20;

	public RegistrationPage() {
		init();
	}

	private static class RegistrationTO {
		private String username;
		private String password;
		private String password2;
		private String email;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getPassword2() {
			return password2;
		}

		public void setPassword2(String password2) {
			this.password2 = password2;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	@Override
	protected void createColumnContent(Div layout) {
		layout.add(new H2("Registrace nového uživatele"));

		VerticalLayout formLayout = new VerticalLayout();
		layout.add(formLayout);

		FormLayout formFieldsLayout = new FormLayout();
		formLayout.add(formFieldsLayout);

		Binder<RegistrationTO> binder = new Binder<>();
		binder.setBean(new RegistrationTO());

		// Username
		final TextField usernameField = new TextField("Uživatelské jméno");
		binder.forField(usernameField).asRequired("Jméno je povinné")
				.withValidator(new StringLengthValidator("Délka jména musí být mezi 2 až 20 znaky", MIN_USERNAME_LENGTH,
						MAX_USERNAME_LENGTH))
				.bind(RegistrationTO::getUsername, RegistrationTO::setUsername);
		formFieldsLayout.add(usernameField);

		// Email
		final TextField emailField = new TextField("Email");
		binder.forField(emailField).asRequired("Email je povinný")
				.withValidator(new EmailValidator("Email má špatný tvar"))
				.bind(RegistrationTO::getEmail, RegistrationTO::setEmail);
		formFieldsLayout.add(emailField);

		// Password
		final PasswordField passwordField = new PasswordField("Heslo");
		binder.forField(passwordField).asRequired("Heslo je povinné").bind(RegistrationTO::getPassword,
				RegistrationTO::setPassword);
		formFieldsLayout.add(passwordField);

		// Password 2
		final PasswordField passwordCopyField = new PasswordField("Heslo znovu");
		binder.forField(passwordCopyField).asRequired("Heslo je povinné").withValidator((value, context) -> {
			if (binder.getBean().getPassword() != null && binder.getBean().getPassword().equals(value))
				return ValidationResult.ok();
			return ValidationResult.error("Hesla se musí shodovat");
		}).bind(RegistrationTO::getPassword2, RegistrationTO::setPassword2);
		formFieldsLayout.add(passwordCopyField);

		VerticalLayout buttonLayout = new VerticalLayout();
		formLayout.add(buttonLayout);
		buttonLayout.setSpacing(true);
		buttonLayout.setPadding(false);

		// Login button
		Button submitButton = new Button("Registrovat", event -> {
			if (binder.isValid()) {
				RegistrationTO bean = binder.getBean();
				userFacade.registrateNewUser(bean.getEmail(), bean.getUsername(), bean.getPassword());
				UIUtils.showInfo("Registrace proběhla úspěšně");
				binder.setBean(new RegistrationTO());
			}
		});
		submitButton.setEnabled(false);
		buttonLayout.add(submitButton);

		binder.addStatusChangeListener(e -> submitButton.setEnabled(e.getBinder().isValid()));
	}
}
