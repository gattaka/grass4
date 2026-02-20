package cz.gattserver.grass.fm.events;


import cz.gattserver.grass.core.events.ProgressEvent;

public class FMZipProcessProgressEvent implements ProgressEvent {

	private String description;

	public FMZipProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String description() {
		return description;
	}

}
