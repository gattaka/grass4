package cz.gattserver.grass.core.events;

public class MockProcessProgressEvent implements ProgressEvent {

	private String description;

	public MockProcessProgressEvent(String description) {
		this.description = description;
	}

	@Override
	public String getStepDescription() {
		return description;
	}

}
