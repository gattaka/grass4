package cz.gattserver.grass.hw;

import cz.gattserver.grass.core.config.AbstractConfiguration;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


/**
 * @author gattaka
 *
 */
@Setter
@Getter
public class HWConfiguration extends AbstractConfiguration {

	/**
	 * HTTP cesta k souborům
	 */
	public static final String HW_PATH = "hw-files";

	/**
	 * Kořenový adresář FM
	 */
	private String rootDir = "files/hw";

	/**
	 * Adresář pro ukládání obrázků
	 */
	private String imagesDir = "images";

	/**
	 * Adresář pro ukládání miniatur obrázků
	 */
	private String imagesMiniDir = "mini";

	/**
	 * Adresář pro ukládání dokumentace
	 */
	private String documentsDir = "documents";

	/**
	 * Adresář pro ukládání 3d modelů
	 */
	private String print3dDir = "print3d";

	/**
	 * Kolik souborů zároveň se smí poslat na server
	 */
	private Integer maxSimUploads = 50;

	/**
	 * Maximální velikost upload souboru v KB
	 */
	private Long maxKBytesUploadSize = 100000L; // 100MB

	/**
	 * Jakým rolím má být modul přístupný - defaultně jenom adminovi
	 */
	private Set<Role> roles = new HashSet<>();

	public HWConfiguration() {
		super("cz.gattserver.grass3.hw");
		roles.add(CoreRole.ADMIN);
		roles.add(CoreRole.FRIEND);
	}
}