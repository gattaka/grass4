package cz.gattserver.grass.recipes;

import java.util.Set;

import jakarta.annotation.Resource;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;


@Component("recipesSection")
public class RecipesSection implements SectionService {

	@Resource(name = "recipesPageFactory")
	private PageFactory recipesPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return recipesPageFactory;
	}

	public String getSectionCaption() {
		return "Recepty";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
