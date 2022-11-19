package cz.gattserver.grass.language.model.dto;

import java.io.Serializable;

public class CrosswordHintTO implements CrosswordCell, Serializable {

	private static final long serialVersionUID = -9124113483077972310L;

	private int id;
	private int fromX;
	private int fromY;
	private int toX;
	private int toY;
	private int wordLength;
	private boolean horizontally;
	private String hint;

	public CrosswordHintTO(int id, int fromX, int fromY, int wordLength, boolean horizontally, String hint) {
		this.id = id;
		this.wordLength = wordLength;
		this.fromX = horizontally ? fromX + 1 : fromX;
		this.fromY = horizontally ? fromY : fromY + 1;
		this.toX = horizontally ? fromX + wordLength : fromX;
		this.toY = horizontally ? fromY : fromY + wordLength;
		this.horizontally = horizontally;
		this.hint = hint;
	}

	public int getWordLength() {
		return wordLength;
	}

	public int getFromX() {
		return fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public int getToX() {
		return toX;
	}

	public int getToY() {
		return toY;
	}

	public boolean isHorizontally() {
		return horizontally;
	}

	public String getHint() {
		return hint;
	}

	public int getId() {
		return id;
	}

	@Override
	public String getValue() {
		return String.valueOf(id);
	}

	@Override
	public boolean isWriteAllowed() {
		return false;
	}

}
