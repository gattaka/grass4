package cz.gattserver.grass.songs.model.domain;

import jakarta.persistence.*;

@Entity(name = "SONG")
public class Song {

    /**
     * DB id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Autor
	 */
	private String author;

	/**
	 * Rok
	 */
	private Integer year;

	/**
	 * Text
	 */
	@Column(columnDefinition = "TEXT")
	private String text;

	/**
	 * Je písnička určena k publikování?
	 */
	private Boolean publicated = true;

	/**
	 * Embedded link
	 */
	private String embedded;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Song))
			return false;
		return ((Song) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getPublicated() {
		return publicated;
	}

	public void setPublicated(Boolean publicated) {
		this.publicated = publicated;
	}

	public String getEmbedded() {
		return embedded;
	}

	public void setEmbedded(String embedded) {
		this.embedded = embedded;
	}

}
