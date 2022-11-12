package cz.gattserver.grass.songs.model.interfaces;

public class SongTO extends SongOverviewTO {

	/**
	 * Text
	 */
	private String text;

	/**
	 * Embedded link
	 */
	private String embedded;

	public SongTO() {
	}

	public SongTO(String name, String author, Integer year, String text, Long id, Boolean publicated, String embedded) {
		super(name, author, year, id, publicated);
		this.text = text;
		this.embedded = embedded;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SongTO))
			return false;
		return ((SongTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEmbedded() {
		return embedded;
	}

	public void setEmbedded(String embedded) {
		this.embedded = embedded;
	}

}
