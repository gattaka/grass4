package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.ProgressEvent;

public class PGZipProcessProgressEvent implements ProgressEvent {

	private String description;

	public PGZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String description() {
		return description;
	}

}
