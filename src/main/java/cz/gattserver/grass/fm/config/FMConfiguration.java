package cz.gattserver.grass.fm.config;

import cz.gattserver.grass.core.config.AbstractConfiguration;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import org.apache.commons.lang3.Validate;

import java.util.HashSet;
import java.util.Set;

public class FMConfiguration extends AbstractConfiguration {

	/**
	 * HTTP cesta k souborům
	 */
	public static final String FM_PATH = "fm-files";

	/**
	 * Kořenový adresář FM
	 */
	private String rootDir = "files";

	/**
	 * Kolik souborů zároveň se smí poslat na server
	 */
	private int maxSimUploads = 50;

	/**
	 * Maximální velikost upload souboru v KB
	 */
	private long maxKBytesUploadSize = 100000L; // 100MB

	/**
	 * Jakým rolím má být modul přístupný - defaultně jenom adminovi
	 */
	private Set<Role> roles = new HashSet<>();

	public FMConfiguration() {
		super("cz.gattserver.grass3.fm");
		roles.add(CoreRole.ADMIN);
		roles.add(CoreRole.FRIEND);
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		Validate.notBlank(rootDir, "RootDir nesmí být prázdný");
		this.rootDir = rootDir;
	}

	public int getMaxSimUploads() {
		return maxSimUploads;
	}

	public void setMaxSimUploads(int maxSimUploads) {
		this.maxSimUploads = maxSimUploads;
	}

	public long getMaxKBytesUploadSize() {
		return maxKBytesUploadSize;
	}

	public void setMaxKBytesUploadSize(long maxKBytesUploadSize) {
		this.maxKBytesUploadSize = maxKBytesUploadSize;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}
