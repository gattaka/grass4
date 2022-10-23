package cz.gattserver.grass.books.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;

public class BookTO extends BookOverviewTO {

	/**
	 * Obrázek
	 */
	private byte[] image;

	/**
	 * Text
	 */
	private String description;

	public BookTO() {
	}

	@QueryProjection
	public BookTO(Long id, String name, String author, Double rating, String year, byte[] image, String description) {
		super(id, name, author, rating, year);
		this.image = image;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

}
