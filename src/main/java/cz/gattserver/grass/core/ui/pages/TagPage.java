package cz.gattserver.grass.core.ui.pages;

import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.common.server.URLIdentifierUtils;

@Route(value = "tag", layout = MainView.class)
public class TagPage extends Div implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = -2716406706042922900L;

	private ContentTagService contentTagFacade;
	private ContentNodeService contentNodeFacade;
    private SecurityService securityService;

	private String tagParameter;
	private ContentTagOverviewTO tag;

    public TagPage(ContentTagService contentTagFacade, ContentNodeService contentNodeFacade,
                   SecurityService securityService) {
        this.contentTagFacade = contentTagFacade;
        this.contentNodeFacade = contentNodeFacade;
        this.securityService = securityService;
    }

    @Override
	public void setParameter(BeforeEvent event, String parameter) {
		tagParameter = parameter;

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();
        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagParameter);
		if (identifier == null)
			throw new GrassPageException(404);

		tag = contentTagFacade.getTagById(identifier.getId());

		if (tag == null)
			throw new GrassPageException(404);

		// Obsahy
		layout.add(new H2("Obsahy označené tagem: " + tag.getName()));

		ContentsLazyGrid tagContentsTable = new ContentsLazyGrid();
		tagContentsTable.populate(securityService.getCurrentUser().getId() != null,
				q -> contentNodeFacade.getByTag(tag.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByTag(tag.getId()));
		tagContentsTable.setWidthFull();
		layout.add(tagContentsTable);
	}

    @Override
    public String getPageTitle() {
        return tag.getName();
    }
}