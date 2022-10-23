package cz.gattserver.grass.core.security;

public enum CoreRole implements Role {

	ADMIN("Admin"), USER("Uživatel"), FRIEND("Host"), AUTHOR("Autor");

	private String roleName;

	private CoreRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getAuthority() {
		return toString();
	}
}
