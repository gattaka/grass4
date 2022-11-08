package cz.gattserver.grass.articles.editor.parser.util;

public class Result {

	String prePart = "";
	String targetPart = "";
	String postPart = "";

	// testovací účely
	int checkSum;

	public String getPrePart() {
		return prePart;
	}

	public String getTargetPart() {
		return targetPart;
	}

	public String getPostPart() {
		return postPart;
	}

	public int getCheckSum() {
		return checkSum;
	}
}