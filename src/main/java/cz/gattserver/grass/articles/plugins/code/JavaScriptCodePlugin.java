package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class JavaScriptCodePlugin extends AbstractCodePlugin {

	public JavaScriptCodePlugin() {
		super("JS", "JavaScript", "js.ico", "javascript", "text/javascript");
	}

}
