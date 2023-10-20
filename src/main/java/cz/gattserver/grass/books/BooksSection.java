package cz.gattserver.grass.books;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Set;

@Component("booksSection")
public class BooksSection implements SectionService {

	@Resource(name = "booksPageFactory")
	private PageFactory booksPageFactory;

	public boolean isVisibleForRoles(Set<Role> roles) {
		return true;
	}

	public PageFactory getSectionPageFactory() {
		return booksPageFactory;
	}

	public String getSectionCaption() {
		return "Knihy";
	}

	@Override
	public Role[] getSectionRoles() {
		return new Role[0];
	}

}
