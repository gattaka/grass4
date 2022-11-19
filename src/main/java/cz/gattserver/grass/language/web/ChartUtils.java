package cz.gattserver.grass.language.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import cz.gattserver.grass.language.model.dto.StatisticsTO;

public class ChartUtils {

	public static BufferedImage drawChart(List<StatisticsTO> stats) {

		int h = 200;
		int w = 948;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = image.createGraphics();
		bg.setColor(Color.WHITE);
		bg.fillRect(0, 0, w, h);

		int fontSize = 14;
		Font font = new Font(Font.SERIF, Font.PLAIN, fontSize);
		// AffineTransform affineTransform = new AffineTransform();
		// affineTransform.rotate(Math.toRadians(-45), 0, 0);
		// Font rotatedFont = font.deriveFont(affineTransform);
		bg.setFont(font);

		int max = stats.stream().map(StatisticsTO::getCount).collect(Collectors.maxBy((a, b) -> a - b)).orElse(0);
		int items = stats.size();
		float barw = ((float) w) / items;
		int yoffset = 30;
		for (int s = 0; s < items; s++) {
			int x = (int) (s * barw);
			int barh = (int) (((float) (h - yoffset)) / max * stats.get(s).getCount());
			int y = h - barh;
			bg.setColor(Color.DARK_GRAY);
			bg.drawString(String.valueOf((int) (stats.get(s).getSuccessRate() * 100)), x + barw / 3, y - 10);
			float hue = s * 1.0f / items;
			bg.setColor(Color.getHSBColor(hue, 1, 1));
			bg.fillRect(x, y, (int) barw, barh);
		}

		return image;
	}
}
