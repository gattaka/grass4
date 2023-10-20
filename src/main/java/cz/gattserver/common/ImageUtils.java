package cz.gattserver.common;

import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

	private ImageUtils() {
	}

	private static String getExtension(String filename) {
		int dot = filename.lastIndexOf('.');
		if (dot <= 0 || filename.length() < 3)
			return "";
		return filename.substring(dot + 1);
	}

	public static boolean isSmallerThenMaxArea(File inputFile, int maxArea) throws IOException {
		BufferedImage image = ImageIO.read(inputFile);
		return image.getHeight() * image.getWidth() < maxArea;
	}

	public static void resizeImageFile(String filename, InputStream in, ByteArrayOutputStream bos, int maxWidth,
			int maxHeight) throws IOException {
		BufferedImage image = ImageIO.read(in);

		ResampleOp resampleOp = new ResampleOp(DimensionConstrain.createMaxDimension(maxWidth, maxHeight));
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		image = resampleOp.filter(image, null);

		ImageIO.write(image, getExtension(filename), bos);
	}

}
