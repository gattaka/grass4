package cz.gattserver.grass.core.config;

public abstract class AbstractConfiguration {

	private String prefix;

	public AbstractConfiguration(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

}
