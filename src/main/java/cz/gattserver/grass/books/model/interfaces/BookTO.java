package cz.gattserver.grass.books.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookTO {

    private Long id;
    private String name;
    private String author;
    private Double rating;
    private String year;
    private byte[] image;
    private String description;

    @QueryProjection
    public BookTO(Long id, String name, String author, Double rating, String year, byte[] image, String description) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.rating = rating;
        this.year = year;
        this.image = image;
        this.description = description;
    }
}