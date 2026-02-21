package cz.gattserver.grass.hw.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

import cz.gattserver.grass.hw.interfaces.HWItemState;
import lombok.Getter;
import lombok.Setter;

/**
 * HW Objekt
 */
@Setter
@Getter
@Entity(name = "HW_ITEM")
public class HWItem {

	/**
	 * Identifikátor hw
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Datum zakoupení (získání)
	 */
	private LocalDate purchaseDate;

	/**
	 * Cena
	 */
	private BigDecimal price;

	/**
	 * Stav hw - funkční, rozbitý, poruchový, bližší popis
	 */
	private HWItemState state;

	/**
	 * Počet let záruky
	 */
	private Integer warrantyYears;

	/**
	 * Součást celku
	 */
    @Column(name = "USEDIN_ID")
	private Long usedInId;

	/**
	 * Spravováno pro (spravuju tohle zařízení někomu?)
	 */
	private String supervizedFor;

	/**
	 * Popis
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Je položka veřejně viditelná?
	 */
	private Boolean publicItem;

}