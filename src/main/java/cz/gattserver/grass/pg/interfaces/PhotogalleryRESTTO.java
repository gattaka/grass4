package cz.gattserver.grass.pg.interfaces;

import java.time.LocalDateTime;
import java.util.Set;

public class PhotogalleryRESTTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Kdy byl obsah vytvořen
	 */
	private LocalDateTime creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private LocalDateTime lastModificationDate;

	/**
	 * Jméno uživatele
	 */
	private String author;

	/**
	 * Jména souborů fotek
	 */
	private Set<String> files;

	public PhotogalleryRESTTO(Long id, String name, LocalDateTime creationDate, LocalDateTime lastModificationDate,
			String author, Set<String> files) {
		super();
		this.id = id;
		this.name = name;
		this.creationDate = creationDate;
		this.lastModificationDate = lastModificationDate;
		this.author = author;
		this.files = files;
	}

	public Set<String> getFiles() {
		return files;
	}

	public void setFiles(Set<String> files) {
		this.files = files;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(LocalDateTime lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
