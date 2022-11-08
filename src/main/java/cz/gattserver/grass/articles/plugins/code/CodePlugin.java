package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CodePlugin extends AbstractCodePlugin {

	public CodePlugin() {
		super("CODE", "Code", "", null, null);
	}

}
