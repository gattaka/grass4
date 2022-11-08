package cz.gattserver.grass.articles.editor.parser.interfaces;

import java.time.LocalDateTime;

public class AttachmentTO {

	private String name;
	private String size;
	private Long numericSize;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public AttachmentTO setSize(String size) {
		this.size = size;
		return this;
	}

	public String getName() {
		return name;
	}

	public AttachmentTO setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public AttachmentTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	public Long getNumericSize() {
		return numericSize;
	}

	public AttachmentTO setNumericSize(Long numericSize) {
		this.numericSize = numericSize;
		return this;
	}

}
