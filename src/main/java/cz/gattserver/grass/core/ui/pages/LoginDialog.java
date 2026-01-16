package cz.gattserver.grass.core.ui.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.interfaces.UserFieldsTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.services.impl.LoginResult;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinServletService;

import cz.gattserver.grass.core.ui.util.UIUtils;

public class LoginDialog extends WebDialog {

    public LoginDialog() {
        setHeaderTitle("Přihlášení");

        setWidth(300, Unit.PIXELS);

        setCloseOnOutsideClick(true);
        setCloseOnEsc(true);

        Binder<UserFieldsTO> binder = new Binder<>(UserFieldsTO.class);
        binder.setBean(new UserFieldsTO());

        TextField usernameField = new TextField("Login");
        binder.forField(usernameField).asRequired("Pole je povinné").bind(UserFieldsTO::getName, UserFieldsTO::setName);
        usernameField.setWidthFull();
        layout.add(usernameField);

        PasswordField passwordField = new PasswordField("Heslo");
        binder.forField(passwordField).asRequired("Pole je povinné")
                .bind(UserFieldsTO::getPassword, UserFieldsTO::setPassword);
        passwordField.setWidthFull();
        layout.add(passwordField);

        Checkbox rememberMeCheckbox = new Checkbox("Zapamatovat si přihlášení");
        layout.add(rememberMeCheckbox);

        Button loginBtn = new Button("Přihlásit", evt -> {
            if (!binder.validate().isOk())
                return;
            UserFieldsTO userFieldsTO = binder.getBean();
            LoginResult loginResult = login(userFieldsTO.getName(), userFieldsTO.getPassword(), rememberMeCheckbox.getValue());
            switch (loginResult) {
                case FAILED_CREDENTIALS:
                    UIUtils.showError("Špatné přihlašovací jméno nebo heslo");
                    usernameField.focus();
                    break;
                case FAILED_DISABLED:
                    UIUtils.showError("Účet je deaktivován");
                    break;
                case FAILED_LOCKED:
                    UIUtils.showError("Účet je zamčen");
                    break;
                case SUCCESS:
                    UI.getCurrent().getPage().reload();
                    break;
            }
        });
        loginBtn.addClickShortcut(Key.ENTER);
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button stornoBtn = new Button("Storno", evt -> close());

        getFooter().add(new HorizontalLayout(loginBtn, stornoBtn));
    }

    private LoginResult login(String username, String password, boolean remember) {
        HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
        HttpServletResponse response = VaadinServletService.getCurrentResponse().getHttpServletResponse();
        SecurityService securityService = SpringContextHelper.getBean(SecurityService.class);
        LoginResult loginResult = securityService.login(username, password, remember, request, response);
        if (LoginResult.SUCCESS == loginResult) {
            // Reinitialize the session to protect against session fixation
            // attacks. This does not work with websocket communication.
            //VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
        }
        return loginResult;
    }
}