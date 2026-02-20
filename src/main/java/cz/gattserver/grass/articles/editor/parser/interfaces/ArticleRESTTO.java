package cz.gattserver.grass.articles.editor.parser.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ArticleRESTTO {

	private Long id;
	private String outputHTML;
	private ContentNodeTO contentNode;
	private Set<String> pluginCSSResources;
	private Set<String> pluginJSResources;

}