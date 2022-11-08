package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class TypeScriptCodePlugin extends AbstractCodePlugin {

	public TypeScriptCodePlugin() {
		super("TS", "TypeScript", "ts.png", "javascript", "text/typescript");
	}

}
