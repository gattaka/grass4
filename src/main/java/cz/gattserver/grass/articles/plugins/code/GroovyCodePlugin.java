package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class GroovyCodePlugin extends AbstractCodePlugin {

	public GroovyCodePlugin() {
		super("GROOVY", "Groovy", "groovy.jpeg", "groovy", "text/x-groovy");
	}

}
