package cz.gattserver.grass.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class SQLCodePlugin extends AbstractCodePlugin {

	public SQLCodePlugin() {
		super("SQL", "SQL", "", "sql", "text/x-sql");
	}

}
