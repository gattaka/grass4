package cz.gattserver.grass.pg.interfaces;

import java.util.Collection;

/**
 * TO objekt pro přenos základních obsahových informací o fotogalerii
 * 
 * @author Hynek
 *
 */
public class PhotogalleryPayloadTO {

	private String name;
	private String galleryDir;
	private Collection<String> tags;
	private boolean publicated;
	private boolean reprocess;

	/**
	 * Výchozí konstruktor
	 */
	public PhotogalleryPayloadTO() {
	}

	/**
	 * @param name
	 *            název galerie
	 * @param galleryDir
	 *            adresář se soubory fotogalerie
	 * @param tags
	 *            klíčová slova
	 * @param publicated
	 *            <code>true</code>, pokud má být galerie zveřejněna
	 * @param reprocess
	 *            <code>true</code>, pokud má si galerie přegenerovat náhledy,
	 *            miniatury apod.
	 */
	public PhotogalleryPayloadTO(String name, String galleryDir, Collection<String> tags, boolean publicated,
			boolean reprocess) {
		this.name = name;
		this.galleryDir = galleryDir;
		this.tags = tags;
		this.publicated = publicated;
		this.reprocess = reprocess;
	}

	/**
	 * @return název galerie
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            název galerie
	 * @return tento objekt pro řetězení
	 */
	public PhotogalleryPayloadTO setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return adresář se soubory fotogalerie
	 */
	public String getGalleryDir() {
		return galleryDir;
	}

	/**
	 * @param text
	 *            adresář se soubory fotogalerie
	 * @return tento objekt pro řetězení
	 */
	public PhotogalleryPayloadTO setGalleryDir(String galleryDir) {
		this.galleryDir = galleryDir;
		return this;
	}

	/**
	 * @return klíčová slova
	 */
	public Collection<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            klíčová slova
	 * @return tento objekt pro řetězení
	 */
	public PhotogalleryPayloadTO setTags(Collection<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * @return <code>true</code>, pokud má být galerie zveřejněna
	 */
	public boolean isPublicated() {
		return publicated;
	}

	/**
	 * @param publicated
	 *            <code>true</code>, pokud má být galerie zveřejněna
	 * @return tento objekt pro řetězení
	 */
	public PhotogalleryPayloadTO setPublicated(boolean publicated) {
		this.publicated = publicated;
		return this;
	}

	/**
	 * @return <code>true</code>, pokud se má galerii přegenerovat složka
	 *         miniatur a slideshow
	 */
	public boolean isReprocess() {
		return reprocess;
	}

	/**
	 * @param reprocess
	 *            <code>true</code>, pokud se má galerii přegenerovat složka
	 *            miniatur a slideshow
	 * @return tento objekt pro řetězení
	 */
	public PhotogalleryPayloadTO setReprocess(boolean reprocess) {
		this.reprocess = reprocess;
		return this;
	}

}
