package cz.gattserver.grass.core.config;

public class CoreConfiguration extends AbstractConfiguration {

	public CoreConfiguration() {
		super("cz.gattserver.grass4.core");
	}

	private Double sessionTimeout = 30.0;

	// mají mít uživatelé možnost se zaregistrovat ?
	private boolean registrations = true;

	public boolean isRegistrations() {
		return registrations;
	}

	public void setRegistrations(boolean registrations) {
		this.registrations = registrations;
	}

	public Double getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(Double sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}
