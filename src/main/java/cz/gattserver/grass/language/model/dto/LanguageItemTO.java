package cz.gattserver.grass.language.model.dto;

import java.time.LocalDateTime;

import cz.gattserver.grass.language.model.domain.ItemType;

public class LanguageItemTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Typ záznamu
	 */
	private ItemType type;

	/**
	 * Jazyk pod který záznam patří
	 */
	private Long language;

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

	public LanguageItemTO setTested(Integer tested) {
		this.tested = tested;
		return this;
	}

	public ItemType getType() {
		return type;
	}

	public LanguageItemTO setType(ItemType type) {
		this.type = type;
		return this;
	}

	public Long getLanguage() {
		return language;
	}

	public LanguageItemTO setLanguage(Long language) {
		this.language = language;
		return this;
	}

	public LocalDateTime getLastTested() {
		return lastTested;
	}

	public LanguageItemTO setLastTested(LocalDateTime lastTested) {
		this.lastTested = lastTested;
		return this;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public LanguageItemTO setSuccessRate(Double successRate) {
		this.successRate = successRate;
		return this;
	}

	public String getContent() {
		return content;
	}

	public LanguageItemTO setContent(String content) {
		this.content = content;
		return this;
	}

	public String getTranslation() {
		return translation;
	}

	public LanguageItemTO setTranslation(String translation) {
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
