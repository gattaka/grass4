package cz.gattserver.grass.songs.interfaces;

import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public SongTO(Long id, String name, String author, Integer year, String text, Boolean publicated, String embedded) {
        super(id, name, author, year, publicated);
        this.text = text;
        this.embedded = embedded;
    }
}