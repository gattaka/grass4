package cz.gattserver.grass.books.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BookOverviewTO {

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
     * Hodnocení
     */
    private Double rating;

    /**
     * Kdy byla kniha vydána
     */
    private String year;

    @QueryProjection
    public BookOverviewTO(Long id, String name, String author, Double rating, String year) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.rating = rating;
        this.year = year;
    }

    public BookOverviewTO(Long id) {
        this.id = id;
    }
}