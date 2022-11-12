package cz.gattserver.grass.articles.plugins.latex;

import cz.gattserver.grass.articles.plugins.PluginFamilyDescription;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class LatexFamilyDescription implements PluginFamilyDescription {

	@Override
	public String getFamily() {
		return "LaTeX";
	}

	@Override
	public String getDescription() {
		return "(<a target=\"_blank\" href=\"https://katex.org/docs/supported.html\">info</a>)";
	}

}
