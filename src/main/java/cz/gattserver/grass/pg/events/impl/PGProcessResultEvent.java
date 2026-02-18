package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.ResultEvent;

import java.util.UUID;

public class PGProcessResultEvent implements ResultEvent {

	private boolean success;
	private String resultDetails;
	private Long galleryId;
	private UUID operationId;

	public PGProcessResultEvent(UUID operationId) {
		this.operationId = operationId;
	}

	public PGProcessResultEvent(UUID operationId, Long galleryId) {
		this(operationId);
		this.success = true;
		this.galleryId = galleryId;
	}

	public PGProcessResultEvent(UUID operationId, boolean success, String resultDetails) {
		this(operationId);
		this.success = success;
		this.resultDetails = resultDetails;
	}

	public UUID getOperationId() {
		return operationId;
	}

	public Long getGalleryId() {
		return galleryId;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public String getResultDetails() {
		return resultDetails;
	}

}