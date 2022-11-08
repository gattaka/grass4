package cz.gattserver.grass.core.ui.pages;

import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
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
