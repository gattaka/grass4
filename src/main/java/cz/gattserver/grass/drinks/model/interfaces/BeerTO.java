package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.MaltType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class BeerTO extends DrinkTO {

    @Serial
    private static final long serialVersionUID = -5393479881376689604L;

    /**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Pivovar
	 */
	private String brewery;

	/**
	 * Hořkost -- International Bitterness Units scale (IBU)
	 */
	private Integer ibu;

	/**
	 * Stupňovitost
	 */
	private Double degrees;

	/**
	 * Kategorie -- IPA, APA, Ležák apod.
	 */
	private String category;

	/**
	 * Druh sladu
	 */
	private MaltType maltType;

	/**
	 * Použité slady
	 */
	private String malts;

	/**
	 * Použité chmely
	 */
	private String hops;

	public BeerTO() {
	}

	@QueryProjection
	public BeerTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description, Double alcohol,
			String country, Long infoId, String brewery, Integer ibu, Double degrees, String category,
			MaltType maltType, String malts, String hops) {
		super(id, name, type, rating, image, description, alcohol, country);
		this.infoId = infoId;
		this.brewery = brewery;
		this.ibu = ibu;
		this.degrees = degrees;
		this.category = category;
		this.maltType = maltType;
		this.malts = malts;
		this.hops = hops;
	}
}