package cz.gattserver.grass.articles.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("articlesViewerPageFactory")
public class ArticlesViewerPageFactory extends AbstractPageFactory {

	public ArticlesViewerPageFactory() {
		super("articles");
	}

}
