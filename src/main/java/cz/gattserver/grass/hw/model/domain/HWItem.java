package cz.gattserver.grass.hw.model.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import org.hibernate.annotations.GenericGenerator;

import cz.gattserver.grass.hw.interfaces.HWItemState;

/**
 * HW Objekt
 */
@Entity(name = "HW_ITEM")
public class HWItem {

	/**
	 * Identifikátor hw
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ - klasifikace hw
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "HW_ITEM_HW_ITEM_TYPE")
	private Set<HWItemType> types;

	/**
	 * Datum zakoupení (získání)
	 */
	private Date purchaseDate;

	/**
	 * Cena
	 */
	private BigDecimal price;

	/**
	 * Stav hw - funkční, rozbitý, poruchový, bližší popis
	 */
	private HWItemState state;

	/**
	 * Poznámky ke stavu hw - opravy apod.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@OrderBy("date desc, id desc")
	private List<HWServiceNote> serviceNotes;

	/**
	 * Počet let záruky
	 */
	private Integer warrantyYears;

	/**
	 * Součást celku
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private HWItem usedIn;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSupervizedFor() {
		return supervizedFor;
	}

	public void setSupervizedFor(String supervizedFor) {
		this.supervizedFor = supervizedFor;
	}

	public HWItem getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(HWItem usedIn) {
		this.usedIn = usedIn;
	}

	public Integer getWarrantyYears() {
		return warrantyYears;
	}

	public void setWarrantyYears(Integer warrantyYears) {
		this.warrantyYears = warrantyYears;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<HWItemType> getTypes() {
		return types;
	}

	public void setTypes(Set<HWItemType> types) {
		this.types = types;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

	public List<HWServiceNote> getServiceNotes() {
		return serviceNotes;
	}

	public void setServiceNotes(List<HWServiceNote> serviceNotes) {
		this.serviceNotes = serviceNotes;
	}

	public Boolean getPublicItem() {
		return publicItem;
	}

	public void setPublicItem(Boolean publicItem) {
		this.publicItem = publicItem;
	}

}
