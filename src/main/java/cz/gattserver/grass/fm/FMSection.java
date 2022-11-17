package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component("fmSection")
public class FMSection implements SectionService {

	@Resource(name = "fmPageFactory")
	private PageFactory fmPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		if (roles == null)
			return false;
		return roles.contains(CoreRole.ADMIN) || roles.contains(CoreRole.FRIEND);
	}

	public PageFactory getSectionPageFactory() {
		return fmPageFactory;
	}

	public String getSectionCaption() {
		return "Soubory";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
