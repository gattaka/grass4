package cz.gattserver.grass.drinks;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component("drinksSection")
public class DrinksSection implements SectionService {

	@Resource(name = "drinksPageFactory")
	private PageFactory drinksPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return drinksPageFactory;
	}

	public String getSectionCaption() {
		return "Nápoje";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
