package cz.gattserver.grass.articles.plugins.basic.list;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Hynek
 *
 */
@Component
public class UnorderedListPlugin extends AbstractListPlugin {

	public UnorderedListPlugin() {
		super("UL", "basic/img/ul_16.png", false);
	}

}
