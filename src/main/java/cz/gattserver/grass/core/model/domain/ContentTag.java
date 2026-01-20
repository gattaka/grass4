package cz.gattserver.grass.core.model.domain;

import java.util.Set;

import jakarta.persistence.*;

@Entity(name = "CONTENT_TAG")
public class ContentTag {

	/**
	 * DB identifikátor
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Obsahy tagu
	 */
	@ManyToMany(mappedBy = "contentTags")
	private Set<ContentNode> contentNodes;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentTag))
			return false;
		return ((ContentTag) obj).getName().equals(getName());
	}

	@Override
	public int hashCode() {
		return getName() == null ? 0 : getName().hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ContentNode> getContentNodes() {
		return contentNodes;
	}

	public void setContentNodes(Set<ContentNode> contentNodes) {
		this.contentNodes = contentNodes;
	}

}
