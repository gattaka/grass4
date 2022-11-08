package cz.gattserver.grass.articles.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;

public class ArticlesConfiguration extends AbstractConfiguration {

	public static final String PREFIX = "cz.gattserver.grass3.articles";

	public static final String ATTACHMENTS_PATH = "articles-attachments";

	public ArticlesConfiguration() {
		super(PREFIX);
	}

	/**
	 * Kolik je timeout pro zálohu (default 2 minuty)
	 */
	private int backupTimeout = 2;

	/**
	 * Kolik je délka tabulátoru ve znacích ?
	 */
	private int tabLength = 2;

	/**
	 * Adresář příloh
	 */
	private String attachmentsDir = "files";

	public int getTabLength() {
		return tabLength;
	}

	public void setTabLength(int tabLength) {
		this.tabLength = tabLength;
	}

	public int getBackupTimeout() {
		return backupTimeout;
	}

	public void setBackupTimeout(int backupTimeout) {
		this.backupTimeout = backupTimeout;
	}

	public String getAttachmentsDir() {
		return attachmentsDir;
	}

	public void setAttachmentsDir(String attachmentsDir) {
		this.attachmentsDir = attachmentsDir;
	}

}
