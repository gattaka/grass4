package cz.gattserver.grass.print3d.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * TO objekt pro přenos základních obsahových informací o 3D projektu
 * 
 * @author Hynek
 *
 */
@Setter
@Getter
public class Print3dCreateTO {

	private String name;
	private String projectDir;
	private Collection<String> tags;
	private boolean publicated;

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
	public Print3dCreateTO(String name, String projectDir, Collection<String> tags, boolean publicated) {
		this.name = name;
		this.projectDir = projectDir;
		this.tags = tags;
		this.publicated = publicated;
	}

}