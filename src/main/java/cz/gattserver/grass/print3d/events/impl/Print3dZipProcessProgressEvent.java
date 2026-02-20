package cz.gattserver.grass.print3d.events.impl;


import cz.gattserver.grass.core.events.ProgressEvent;

public class Print3dZipProcessProgressEvent implements ProgressEvent {

	private String description;

	public Print3dZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String description() {
		return description;
	}

}
