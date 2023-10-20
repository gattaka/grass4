package cz.gattserver.grass.language;

import java.util.Set;

import jakarta.annotation.Resource;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;


@Component("languageSection")
public class LanguageSection implements SectionService {

	@Resource(name = "languagePageFactory")
	private PageFactory languagePageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return languagePageFactory;
	}

	public String getSectionCaption() {
		return "Jazyky";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
