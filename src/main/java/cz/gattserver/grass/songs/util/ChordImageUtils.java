package cz.gattserver.grass.songs.util;

import cz.gattserver.grass.songs.model.interfaces.ChordTO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ChordImageUtils {

	private ChordImageUtils() {
	}

	public static BufferedImage drawChord(ChordTO to, int size) {
		int cols = 7;
		int rows = 9;

		int dx = (int) (size * 0.6);
		int dy = size;
		int textOffset = 5;

		int w = cols * dx;
		int h = rows * size + textOffset;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics bg = image.createGraphics();
		bg.setColor(Color.WHITE);
		bg.fillRect(0, 0, w, h);

		int fontSize = (int) (size * 0.8);
		int fontYOffset = fontSize / 5;
		Font font = new Font(Font.MONOSPACED, Font.BOLD, fontSize);
		bg.setFont(font);
		bg.setColor(Color.DARK_GRAY);

		int pointD = (int) (dx * 0.55);
		int pointR = pointD / 2;
		int pointLineOffsetY = dy / 2 - pointR;
		int pointLineOffsetX = dx / 2 - pointR;

		String[] strings = new String[] { "", "E", "a", "d", "g", "h", "e" };
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				if (row == 0) {
					bg.setColor(Color.GRAY);
					bg.drawString(strings[col], col * dx + dx / 4, dy - fontYOffset);
					bg.setColor(Color.DARK_GRAY);
				} else if (col == 0) {
					bg.setColor(Color.GRAY);
					bg.drawString(String.valueOf(row), col * dx,
							row * dy + dy / 2 + size / 2 + textOffset - fontYOffset);
					bg.setColor(Color.DARK_GRAY);
				} else {
					int x = col * dx;
					int y = row * dy;
					bg.drawLine(x, y + textOffset, x + dx, y + textOffset);
					bg.setColor(Color.GRAY);
					bg.drawLine(x + dx / 2, y + textOffset, x + dx / 2, y + dy + textOffset);
					bg.setColor(Color.DARK_GRAY);
					long mask = 1L << (row - 1) * 6 + (col - 1);
					if ((to.getConfiguration().longValue() & mask) > 0)
						bg.fillArc(x + pointLineOffsetX, y + textOffset + pointLineOffsetY, pointD, pointD, 0, 360);
				}

		return image;

	}

}
