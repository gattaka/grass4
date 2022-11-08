package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class RubyCodePlugin extends AbstractCodePlugin {

	public RubyCodePlugin() {
		super("RUBY", "Ruby", "ruby.png", "ruby", "text/x-ruby");
	}

}
