package cz.gattserver.grass.songs.interfaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
@NoArgsConstructor
public class SongTO extends SongOverviewTO {

    @Serial
    private static final long serialVersionUID = 2223110707299084497L;

    /**
	 * Text
	 */
	private String text;

	/**
	 * Embedded link
	 */
	private String embedded;

	public SongTO(String name, String author, Integer year, String text, Long id, Boolean publicated, String embedded) {
		super(name, author, year, id, publicated);
		this.text = text;
		this.embedded = embedded;
	}
}