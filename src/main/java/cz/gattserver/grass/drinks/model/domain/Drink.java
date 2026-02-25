package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "DRINKS_DRINK")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Drink {

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
     * Typ
     */
    private DrinkType type;

    /**
     * Hodnocení
     */
    private Double rating;

    /**
     * Obrázek
     */
    @Lob
    private byte[] image;

    /**
     * Text
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * % alkoholu
     */
    private Double alcohol;

    /**
     * Země původu
     */
    private String country;

    /**
     * Další informace k nápoji, dle typu
     */
    private Long drinkInfo;
}