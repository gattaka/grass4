package cz.gattserver.grass.campgames;

import java.util.HashSet;
import java.util.Set;

import cz.gattserver.grass.core.config.AbstractConfiguration;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;

public class CampgamesConfiguration extends AbstractConfiguration {

	/**
	 * HTTP cesta k souborům
	 */
	public static final String CAMPGAMES_PATH = "campgames-files";

	/**
	 * Kořenový adresář
	 */
	private String rootDir = "files/campgames";

	/**
	 * Adresář pro ukládání obrázků
	 */
	private String imagesDir = "images";

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

	public CampgamesConfiguration() {
		super("cz.gattserver.grass3.campgames");
		roles.add(CoreRole.ADMIN);
		roles.add(CoreRole.FRIEND);
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public Integer getMaxSimUploads() {
		return maxSimUploads;
	}

	public void setMaxSimUploads(Integer maxSimUploads) {
		this.maxSimUploads = maxSimUploads;
	}

	public Long getMaxKBytesUploadSize() {
		return maxKBytesUploadSize;
	}

	public void setMaxKBytesUploadSize(Long maxKBytesUploadSize) {
		this.maxKBytesUploadSize = maxKBytesUploadSize;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getImagesDir() {
		return imagesDir;
	}

	public void setImagesDir(String imagesDir) {
		this.imagesDir = imagesDir;
	}

}
