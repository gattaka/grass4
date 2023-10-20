package cz.gattserver.grass.hw;

import java.util.Set;

import jakarta.annotation.Resource;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;


@Component("hwSection")
public class HWSection implements SectionService {

	@Resource(name = "hwPageFactory")
	private PageFactory hwPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return hwPageFactory;
	}

	public String getSectionCaption() {
		return "HW";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
