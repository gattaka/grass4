package cz.gattserver.grass.articles.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("articlesEditorPageFactory")
public class ArticlesEditorPageFactory extends AbstractPageFactory {

	public ArticlesEditorPageFactory() {
		super("articles-editor");
	}

}
