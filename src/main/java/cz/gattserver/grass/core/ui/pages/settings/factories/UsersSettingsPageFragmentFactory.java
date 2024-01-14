package cz.gattserver.grass.core.ui.pages.settings.factories;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.modules.register.ModuleRegister;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.vaadin.ImageIcon;

public class UsersSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private UserService userFacade;

	@Lazy
	@Autowired
	protected ModuleRegister moduleRegister;

	private Grid<UserInfoTO> grid;

	@Override
	public void createFragment(Div layout) {
		grid = new Grid<>();

		layout.add(new H2("Správa uživatelů"));
		layout.add(grid);

		UIUtils.applyGrassDefaultStyle(grid);
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addColumn(UserInfoTO::getName).setHeader("Jméno");
		grid.addColumn(u -> u.getRoles().stream().map(Role::getRoleName).collect(Collectors.joining(", ")))
				.setHeader("Role");
		grid.addColumn(new LocalDateTimeRenderer<>(UserInfoTO::getRegistrationDate, "dd.MM.yyyy"))
				.setHeader("Registrován");
		grid.addColumn(new LocalDateTimeRenderer<>(UserInfoTO::getLastLoginDate, "dd.MM.yyyy"))
				.setHeader("Naposledy přihlášen");
		grid.addColumn(UserInfoTO::getEmail).setHeader("Email");
		grid.addColumn(u -> u.isConfirmed() ? "Ano" : "Ne").setHeader("Aktivní");

		List<UserInfoTO> users = userFacade.getUserInfoFromAllUsers();
		grid.setItems(users);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		buttonLayout.setSpacing(true);
		layout.add(buttonLayout);

		GridButton<UserInfoTO> activateBtn = new GridButton<>("Aktivovat",
				selectedUsers -> selectedUsers.forEach(u -> {
					u.setConfirmed(true);
					userFacade.activateUser(u.getId());
					grid.getDataProvider().refreshItem(u);
				}), grid).setEnableResolver(
				selectedUsers -> !selectedUsers.isEmpty() && !selectedUsers.iterator().next().isConfirmed());
		activateBtn.setIcon(new Image(ImageIcon.TICK_16_ICON.createResource(), "Aktivovat"));
		buttonLayout.add(activateBtn);

		GridButton<UserInfoTO> blockBtn = new GridButton<>("Zablokovat",
				selectedUsers -> selectedUsers.forEach(user -> {
					user.setConfirmed(false);
					userFacade.banUser(user.getId());
					grid.getDataProvider().refreshItem(user);
				}), grid).setEnableResolver(
				selectedUsers -> !selectedUsers.isEmpty() && selectedUsers.iterator().next().isConfirmed());
		blockBtn.setIcon(new Image(ImageIcon.BLOCK_16_ICON.createResource(), "Zablokovat"));
		buttonLayout.add(blockBtn);

		GridButton<UserInfoTO> editBtn = new GridButton<>("Upravit oprávnění", u -> {
			WebDialog w = new WebDialog("Uživatelské role");

			UserInfoTO user = users.iterator().next();
			w.setWidth("300px");

			for (final Role role : moduleRegister.getRoles()) {
				final Checkbox checkbox = new Checkbox(role.getRoleName());
				checkbox.setValue(user.getRoles().contains(role));
				checkbox.addValueChangeListener(event -> {
					if (checkbox.getValue()) {
						user.getRoles().add(role);
					} else {
						user.getRoles().remove(role);
					}
				});
				w.addComponent(checkbox);
			}

			HorizontalLayout btnLayout = new HorizontalLayout();
			btnLayout.setSizeFull();
			btnLayout.setPadding(true);
			btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

			w.getFooter().add(btnLayout);

			btnLayout.add(new Button("Upravit oprávnění", event -> {
				userFacade.changeUserRoles(user.getId(), user.getRoles());
				grid.getDataProvider().refreshItem(user);
				w.close();
			}));

			btnLayout.add(new Button("Storno", event -> w.close()));

			w.open();
		}, grid);
		editBtn.setIcon(new Image(ImageIcon.PENCIL_16_ICON.createResource(), "Upravit oprávnění"));
		buttonLayout.add(editBtn);
	}
}
