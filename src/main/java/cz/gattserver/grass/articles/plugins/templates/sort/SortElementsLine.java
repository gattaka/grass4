package cz.gattserver.grass.articles.plugins.templates.sort;

import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.ArrayList;
import java.util.List;

class SortElementsLine {
	
	private String comparable;
	private List<Element> elements = new ArrayList<>();

	public String getComparable() {
		return comparable;
	}

	public void setComparable(String comparable) {
		this.comparable = comparable;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

}