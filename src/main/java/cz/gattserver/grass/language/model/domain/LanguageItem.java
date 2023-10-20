package cz.gattserver.grass.language.model.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "LANGUAGEITEM")
public class LanguageItem {

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Typ záznamu
	 */
	private ItemType type;

	/**
	 * Jazyk pod který záznam patří
	 */
	@ManyToOne
	private Language language;

	/**
	 * Obsah
	 */
	private String content;

	/**
	 * Překlad
	 */
	private String translation;

	/**
	 * Poslední datum zkoušení
	 */
	private LocalDateTime lastTested;

	/**
	 * Kolikrát již byl záznam zkoušen
	 */
	private Integer tested = 0;

	/**
	 * Úspěšnost při zkoušení
	 */
	private Double successRate = 0.0;

	public Integer getTested() {
		return tested;
	}

	public LanguageItem setTested(Integer tested) {
		this.tested = tested;
		return this;
	}

	public ItemType getType() {
		return type;
	}

	public LanguageItem setType(ItemType type) {
		this.type = type;
		return this;
	}

	public Language getLanguage() {
		return language;
	}

	public LanguageItem setLanguage(Language language) {
		this.language = language;
		return this;
	}

	public LocalDateTime getLastTested() {
		return lastTested;
	}

	public LanguageItem setLastTested(LocalDateTime lastTested) {
		this.lastTested = lastTested;
		return this;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public LanguageItem setSuccessRate(Double successRate) {
		this.successRate = successRate;
		return this;
	}

	public String getContent() {
		return content;
	}

	public LanguageItem setContent(String content) {
		this.content = content;
		return this;
	}

	public String getTranslation() {
		return translation;
	}

	public LanguageItem setTranslation(String translation) {
		this.translation = translation;
		return this;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
