package cz.gattserver.grass.ui.util;

public class GridUtils {

	public static final int ICON_COLUMN_WIDTH = 8 + 16 + 8;
	public static final int DATE_COLUMN_WIDTH = 90;
	public static final int AUTHOR_COLUMN_WIDTH = 90;
	public static final int NODE_COLUMN_WIDTH = 150;

	private GridUtils() {
	}

	public static int processHeight(int dataAmount) {
		int element = 31;
		int header = 31;
		int min = header + 3 * element;
		int max = 15 * element + header;

		int size = dataAmount * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		return size;
	}

}
