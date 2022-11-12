package cz.gattserver.grass.songs.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

public class SongOverviewTO {

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
	 * DB id
	 */
	private Long id;

	/**
	 * Je písnička určena k publikování?
	 */
	private Boolean publicated = true;

	public SongOverviewTO() {
	}

	@QueryProjection
	public SongOverviewTO(String name, String author, Integer year, Long id, Boolean publicated) {
		super();
		this.name = name;
		this.author = author;
		this.year = year;
		this.id = id;
		this.publicated = publicated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongOverviewTO other = (SongOverviewTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public Boolean getPublicated() {
		return publicated;
	}

	public void setPublicated(Boolean publicated) {
		this.publicated = publicated;
	}

}
