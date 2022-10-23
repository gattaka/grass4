package cz.gattserver.grass.campgames.interfaces;

import java.time.LocalDateTime;

public class CampgameFileTO {

	private String name;
	private String size;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public CampgameFileTO setSize(String size) {
		this.size = size;
		return this;
	}

	public String getName() {
		return name;
	}

	public CampgameFileTO setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public CampgameFileTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampgameFileTO other = (CampgameFileTO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
