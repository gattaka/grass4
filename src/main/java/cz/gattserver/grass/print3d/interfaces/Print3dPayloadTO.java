package cz.gattserver.grass.print3d.interfaces;

import java.util.Collection;

/**
 * TO objekt pro přenos základních obsahových informací o 3D projektu
 * 
 * @author Hynek
 *
 */
public class Print3dPayloadTO {

	private String name;
	private String projectDir;
	private Collection<String> tags;
	private boolean publicated;

	/**
	 * Výchozí konstruktor
	 */
	public Print3dPayloadTO() {
	}

	/**
	 * @param name
	 *            název projektu
	 * @param projectDir
	 *            adresář se soubory
	 * @param tags
	 *            klíčová slova
	 * @param publicated
	 *            <code>true</code>, pokud má být obsah zveřejněn
	 */
	public Print3dPayloadTO(String name, String projectDir, Collection<String> tags, boolean publicated) {
		this.name = name;
		this.projectDir = projectDir;
		this.tags = tags;
		this.publicated = publicated;
	}

	/**
	 * @return název projektu
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            název projektu
	 * @return tento objekt pro řetězení
	 */
	public Print3dPayloadTO setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return adresář se soubory projektu
	 */
	public String getProjectDir() {
		return projectDir;
	}

	/**
	 * @param projectDir
	 *            adresář se soubory projektu
	 * @return tento objekt pro řetězení
	 */
	public Print3dPayloadTO setProjectDir(String projectDir) {
		this.projectDir = projectDir;
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
	public Print3dPayloadTO setTags(Collection<String> tags) {
		this.tags = tags;
		return this;
	}

	/**
	 * @return <code>true</code>, pokud má být obsah zveřejněn
	 */
	public boolean isPublicated() {
		return publicated;
	}

	/**
	 * @param publicated
	 *            <code>true</code>, pokud má být obsah zveřejněn
	 * @return tento objekt pro řetězení
	 */
	public Print3dPayloadTO setPublicated(boolean publicated) {
		this.publicated = publicated;
		return this;
	}

}
