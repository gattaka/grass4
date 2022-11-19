package cz.gattserver.grass.hw.interfaces;

import java.time.LocalDateTime;

public class HWItemFileTO {

	private String name;
	private String size;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public HWItemFileTO setSize(String size) {
		this.size = size;
		return this;
	}

	public String getName() {
		return name;
	}

	public HWItemFileTO setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public HWItemFileTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

}
