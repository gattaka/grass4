package cz.gattserver.grass.articles.events.impl;

import cz.gattserver.grass.core.events.StartEvent;

public class ArticlesProcessStartEvent implements StartEvent {

	private int steps;

	public ArticlesProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
