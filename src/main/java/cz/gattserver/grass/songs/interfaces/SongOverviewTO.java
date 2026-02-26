package cz.gattserver.grass.songs.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongOverviewTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1179767404749541186L;

    /**
     * DB id
     */
    @EqualsAndHashCode.Include
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
	 * Je písnička určena k publikování?
	 */
	private Boolean publicated = true;

	public SongOverviewTO() {
	}

	@QueryProjection
	public SongOverviewTO(Long id, String name, String author, Integer year, Boolean publicated) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.year = year;
		this.publicated = publicated;
	}
}