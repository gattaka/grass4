package cz.gattserver.grass.fm.interfaces;

import java.time.LocalDateTime;

public class FMItemTO {

	private String name;
	private String pathFromFMRoot;
	private String size;
	private Long numericSize;
	private boolean directory;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public FMItemTO setSize(String size) {
		this.size = size;
		return this;
	}

	public boolean isDirectory() {
		return directory;
	}

	public FMItemTO setDirectory(boolean directory) {
		this.directory = directory;
		return this;
	}

	public String getName() {
		return name;
	}

	public FMItemTO setName(String name) {
		this.name = name;
		return this;
	}

	public String getPathFromFMRoot() {
		return pathFromFMRoot;
	}

	public FMItemTO setPathFromFMRoot(String pathFromFMRoot) {
		this.pathFromFMRoot = pathFromFMRoot;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public FMItemTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	public Long getNumericSize() {
		return numericSize;
	}

	public FMItemTO setNumericSize(Long numericSize) {
		this.numericSize = numericSize;
		return this;
	}

}
