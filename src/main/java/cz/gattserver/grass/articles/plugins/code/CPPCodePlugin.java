package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class CPPCodePlugin extends AbstractCodePlugin {

	public CPPCodePlugin() {
		super("CPP", "C++", "", "clike", "text/x-c++src");
	}
}
