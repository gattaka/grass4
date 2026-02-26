package cz.gattserver.grass.songs.model;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@Entity(name = "CHORD")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chord {

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
	 * Konfigurace
	 */
	private Long configuration;

}