package cz.gattserver.grass.songs.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity(name = "SONG")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    /**
     * DB id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}