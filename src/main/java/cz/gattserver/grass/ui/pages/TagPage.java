package cz.gattserver.grass.ui.pages;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.exception.GrassPageException;
import cz.gattserver.grass.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.services.ContentNodeService;
import cz.gattserver.grass.services.ContentTagService;
import cz.gattserver.grass.ui.components.ContentsLazyGrid;
import cz.gattserver.grass.ui.pages.template.OneColumnPage;
import cz.gattserver.web.common.server.URLIdentifierUtils;

@Route("tag")
public class TagPage extends OneColumnPage implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = -2716406706042922900L;

	@Autowired
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

	private String tagParameter;
	private ContentTagOverviewTO tag;

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		tagParameter = parameter;
		init();
	}

	@Override
	public String getPageTitle() {
		return tag.getName();
	}

	@Override
	protected void createColumnContent(Div layout) {

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(tagParameter);
		if (identifier == null)
			throw new GrassPageException(404);

		tag = contentTagFacade.getTagById(identifier.getId());

		if (tag == null)
			throw new GrassPageException(404);

		// Obsahy
		layout.add(new H2("Obsahy označené tagem: " + tag.getName()));

		ContentsLazyGrid tagContentsTable = new ContentsLazyGrid();
		tagContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getByTag(tag.getId(), q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCountByTag(tag.getId()));
		tagContentsTable.setWidthFull();
		layout.add(tagContentsTable);
	}

}
