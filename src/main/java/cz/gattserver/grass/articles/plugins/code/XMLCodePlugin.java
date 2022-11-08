package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class XMLCodePlugin extends AbstractCodePlugin {

	public XMLCodePlugin() {
		super("XML", "XML", "htmlxml_16.png", "xml", "application/xml");
	}

}
