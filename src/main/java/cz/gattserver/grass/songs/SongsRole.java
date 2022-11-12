package cz.gattserver.grass.songs;


import cz.gattserver.grass.core.security.Role;

public enum SongsRole implements Role {

	SONGS_EDITOR("Songs editor");

	private String roleName;

	private SongsRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getAuthority() {
		return toString();
	}
}
