package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.StartEvent;

public class PGProcessStartEvent implements StartEvent {

	private int steps;

	public PGProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int steps() {
		return steps;
	}

}
