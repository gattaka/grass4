package cz.gattserver.grass.language.model.dto;

import java.util.ArrayList;
import java.util.List;

public class CrosswordTO {

	private int width;
	private int height;

	// Písmena obsahu
	private CrosswordCell[][] contentData;

	// Popisky řádků a sloupců
	private List<CrosswordHintTO> hints;

	public CrosswordTO(int width, int height) {
		this.width = width;
		this.height = height;

		contentData = new CrosswordCell[width][height];
		hints = new ArrayList<>();
	}

	public CrosswordCell getCell(int x, int y) {
		if (x > width - 1 || y > height - 1)
			return null;
		return contentData[x][y];
	}

	public void insertWord(int x, int y, String word, String hint, boolean horizontally) {
		CrosswordHintTO hintTO = new CrosswordHintTO(hints.size() + 1, x, y, word.length(), horizontally, hint);
		hints.add(hintTO);
		contentData[x][y] = hintTO;
		for (int i = 0; i < word.length(); i++) {
			if (horizontally)
				contentData[x + i + 1][y] = new CrosswordCharTO(String.valueOf(word.charAt(i)));
			else
				contentData[x][y + i + 1] = new CrosswordCharTO(String.valueOf(word.charAt(i)));
		}
	}

	public List<CrosswordHintTO> getHints() {
		return hints;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				CrosswordCell c = contentData[x][y];
				sb.append(c == null ? " " : c.getValue());
				sb.append("|");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
