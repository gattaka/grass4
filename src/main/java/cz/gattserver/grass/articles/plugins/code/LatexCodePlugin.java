package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class LatexCodePlugin extends AbstractCodePlugin {

	public LatexCodePlugin() {
		super("LATEX", "LaTeX", "", "stex", "text/x-stex");
	}

}
