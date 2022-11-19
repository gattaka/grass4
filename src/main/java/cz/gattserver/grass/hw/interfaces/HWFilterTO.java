package cz.gattserver.grass.hw.interfaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class HWFilterTO implements Serializable {

	private static final long serialVersionUID = 7979321455877648798L;

	private String name;
	private HWItemState state;
	private String usedIn;
	private String supervizedFor;
	private BigDecimal price;
	private Date purchaseDateFrom;
	private Date purchaseDateTo;
	private Collection<String> types;
	private Boolean publicItem;
	private Long ignoreId;

	public String getSupervizedFor() {
		return supervizedFor;
	}

	public HWFilterTO setSupervizedFor(String supervizedFor) {
		this.supervizedFor = supervizedFor;
		return this;
	}

	public String getName() {
		return name;
	}

	public HWFilterTO setName(String name) {
		this.name = name;
		return this;
	}

	public Date getPurchaseDateFrom() {
		return purchaseDateFrom;
	}

	public HWFilterTO setPurchaseDateFrom(Date purchaseDateFrom) {
		this.purchaseDateFrom = purchaseDateFrom;
		return this;
	}

	public Date getPurchaseDateTo() {
		return purchaseDateTo;
	}

	public HWFilterTO setPurchaseDateTo(Date purchaseDateTo) {
		this.purchaseDateTo = purchaseDateTo;
		return this;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public HWFilterTO setPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public HWItemState getState() {
		return state;
	}

	public HWFilterTO setState(HWItemState state) {
		this.state = state;
		return this;
	}

	public String getUsedIn() {
		return usedIn;
	}

	public HWFilterTO setUsedIn(String usedIn) {
		this.usedIn = usedIn;
		return this;
	}

	public Collection<String> getTypes() {
		return types;
	}

	public HWFilterTO setTypes(Collection<String> types) {
		this.types = types;
		return this;
	}

	public Boolean getPublicItem() {
		return publicItem;
	}

	public void setPublicItem(Boolean publicItem) {
		this.publicItem = publicItem;
	}

	public Long getIgnoreId() {
		return ignoreId;
	}

	public void setIgnoreId(Long ignoreId) {
		this.ignoreId = ignoreId;
	}

}
