package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CSharpCodePlugin extends AbstractCodePlugin {

	public CSharpCodePlugin() {
		super("CSHARP", "C#", "", "clike", "text/x-csharp");
	}

}
