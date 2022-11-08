package cz.gattserver.grass.articles.plugins.basic.list;

import org.springframework.stereotype.Component;

/**
 * 
 * @author Hynek
 *
 */
@Component
public class OrderedListPlugin extends AbstractListPlugin {

	public OrderedListPlugin() {
		super("OL", "basic/img/ol_16.png", true);
	}

}
