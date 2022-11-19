package cz.gattserver.grass.hw;

import cz.gattserver.grass.core.config.AbstractConfiguration;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;

import java.util.HashSet;
import java.util.Set;


/**
 * @author gattaka
 *
 */
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

	public String getDocumentsDir() {
		return documentsDir;
	}

	public void setDocumentsDir(String documentsDir) {
		this.documentsDir = documentsDir;
	}

	public String getPrint3dDir() {
		return print3dDir;
	}

	public void setPrint3dDir(String print3dDir) {
		this.print3dDir = print3dDir;
	}

}
