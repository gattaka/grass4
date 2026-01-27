package cz.gattserver.common.slideshow;

import java.time.LocalDateTime;

public class ExifInfoTO {

	private LocalDateTime date;
	private Long dateMillis;
	private Integer orinetation;
	private String deviceMaker;
	private String deviceModel;
	private Double latitude;
	private Double longitude;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getDeviceMaker() {
		return deviceMaker;
	}

	public void setDeviceMaker(String deviceMaker) {
		this.deviceMaker = deviceMaker;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public Long getDateMillis() {
		return dateMillis;
	}

	public void setDateMillis(Long dateMillis) {
		this.dateMillis = dateMillis;
	}

	public Integer getOrinetation() {
		return orinetation;
	}

	public void setOrinetation(Integer orinetation) {
		this.orinetation = orinetation;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
