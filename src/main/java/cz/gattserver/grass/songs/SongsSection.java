package cz.gattserver.grass.songs;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Set;

@Component("songsSection")
public class SongsSection implements SectionService {

	@Resource(name = "songsPageFactory")
	private PageFactory songsPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return songsPageFactory;
	}

	public String getSectionCaption() {
		return "Zpěvník";
	}

	@Override
	public Role[] getSectionRoles() {
		return SongsRole.values();
	}

}
