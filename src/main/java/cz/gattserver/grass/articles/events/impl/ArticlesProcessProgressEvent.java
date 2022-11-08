package cz.gattserver.grass.articles.events.impl;

import cz.gattserver.grass.core.events.ProgressEvent;

public class ArticlesProcessProgressEvent implements ProgressEvent {

	private String description;

	public ArticlesProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
