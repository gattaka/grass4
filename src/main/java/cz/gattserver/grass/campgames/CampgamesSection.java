package cz.gattserver.grass.campgames;

import java.util.Set;

import jakarta.annotation.Resource;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

@Component("campgamesSection")
public class CampgamesSection implements SectionService {

	@Resource(name = "campgamesPageFactory")
	private PageFactory campgamesPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return campgamesPageFactory;
	}

	public String getSectionCaption() {
		return "Hry";
	}

	@Override
	public Role[] getSectionRoles() {
		return CampgamesRole.values();
	}

}
