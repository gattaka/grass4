package cz.gattserver.grass.core.events;

public class MockProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;
	private Long galleryId;

	public MockProcessResultEvent(boolean success, String resultDetails) {
		this.success = success;
		this.resultDetails = resultDetails;
	}

	public Long getGalleryId() {
		return galleryId;
	}

	@Override
	public boolean success() {
		return success;
	}

	@Override
	public String resultDetails() {
		return resultDetails;
	}

}
