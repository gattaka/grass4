package cz.gattserver.grass.mock;


import cz.gattserver.grass.config.AbstractConfiguration;
import cz.gattserver.grass.NonConfigValue;

public class MockConfiguration extends AbstractConfiguration {

	private String configValue;

	private String configValue2;

	@NonConfigValue
	private String nonConfigValue;

	public MockConfiguration(String prefix) {
		super(prefix);
	}

	public String getConfigValue2() {
		return configValue2;
	}

	public void setConfigValue2(String configValue2) {
		this.configValue2 = configValue2;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getNonConfigValue() {
		return nonConfigValue;
	}

	public void setNonConfigValue(String nonConfigValue) {
		this.nonConfigValue = nonConfigValue;
	}

}
