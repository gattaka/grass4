package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "NODE")
public class Node {

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	@ManyToOne(optional = true)
	private Node parent;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node))
			return false;
		return ((Node) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
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

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

}
