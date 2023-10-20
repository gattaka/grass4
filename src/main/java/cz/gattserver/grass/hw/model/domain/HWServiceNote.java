package cz.gattserver.grass.hw.model.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import cz.gattserver.grass.hw.interfaces.HWItemState;

/**
 * Údaj o opravě, změně součástí apod.
 */
@Entity(name = "SERVICE_NOTE")
public class HWServiceNote {

	/**
	 * Identifikátor změny
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Datum události
	 */
	private Date date;

	/**
	 * Popis změny
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Stav do kterého byl HW převeden v souvislosti s popisovanou událostí
	 */
	private HWItemState state;

	/**
	 * Součásti
	 */
	private String usage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public HWItemState getState() {
		return state;
	}

	public void setState(HWItemState state) {
		this.state = state;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

}
