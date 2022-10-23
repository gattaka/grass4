package cz.gattserver.grass.core.services.impl;

import cz.gattserver.grass.core.services.VersionInfoService;

public class VersionInfoServiceImpl implements VersionInfoService {

	private String version;

	public void setVersionProperties(String version) {
		this.version = version;
	}

	@Override
	public String getProjectVersion() {
		return version;
	}
}
