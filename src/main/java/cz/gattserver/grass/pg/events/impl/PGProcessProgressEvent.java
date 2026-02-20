package cz.gattserver.grass.pg.events.impl;


import cz.gattserver.grass.core.events.ProgressEvent;

public class PGProcessProgressEvent implements ProgressEvent {

	private String description;

	public PGProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}