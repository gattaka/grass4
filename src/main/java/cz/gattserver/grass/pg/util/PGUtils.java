package cz.gattserver.grass.pg.util;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.interfaces.PhotogalleryItemType;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryViewItemTO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;

public class PGUtils {

	private static Logger logger = LoggerFactory.getLogger(PGUtils.class);

	public static final String EXIF_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";

	public static final int MINIATURE_SIZE = 150;
	public static final int SLIDESHOW_WIDTH = 900;
	public static final int SLIDESHOW_HEIGHT = 800;

	private PGUtils() {
	}

	public static String getExtension(Path file) {
		String filename = file.getFileName().toString();
		int dot = filename.lastIndexOf('.');
		if (dot <= 0)
			return "";
		return filename.substring(dot + 1);
	}

	public static void resizeImage(Path inputFile, Path destinationFile) throws IOException {
		resizeImage(inputFile, destinationFile, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE);
	}

	public static void createErrorPreview(String filename, Path destinationFile) {
		int h = MINIATURE_SIZE;
		int w = MINIATURE_SIZE;
		BufferedImage backgroundImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = backgroundImage.createGraphics();
		// https://stackoverflow.com/questions/4855847/problem-with-fillroundrect-seemingly-not-rendering-correctly
		bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bg.setColor(new Color(50, 50, 50));
		int wOffset = 10;
		int hOffset = 30;
		int corner = 25;
		bg.fillRoundRect(wOffset, hOffset, w - wOffset * 2, h - hOffset * 2, corner, corner);
		bg.setColor(new Color(150, 150, 150));
		int xc = w / 2 + 5;
		int yc = h / 2;
		int xr = 20;
		int yr = 20;
		bg.fillPolygon(new Polygon(new int[]{xc - xr, xc + xr, xc - xr}, new int[]{yc - yr, yc, yc + yr}, 3));
		try (OutputStream o = Files.newOutputStream(destinationFile)) {
			ImageIO.write(backgroundImage, "png", o);
		} catch (IOException e) {
			logger.error("Vytváření chybového náhledu videa {} se nezdařilo", destinationFile.getFileName().toString(),
					e);
		}
	}

	public static void resizeVideoPreviewImage(BufferedImage inputImage, Path destinationFile) throws IOException {
		try (OutputStream os = Files.newOutputStream(destinationFile)) {
			Thumbnails.of(inputImage).outputFormat("jpg").size(PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE)
					.toOutputStream(os);
		}
	}

	public static void resizeImage(Path inputFile, Path destinationFile, int maxWidth, int maxHeight)
			throws IOException {
		String filenameLow = inputFile.getFileName().toString().toLowerCase();
		try (OutputStream os = Files.newOutputStream(destinationFile)) {
			if (filenameLow.endsWith(".gif")) {
				try (InputStream is = Files.newInputStream(inputFile)) {
					GifImage gifImage = GifDecoder.read(is);
					BufferedImage image = gifImage.getFrame(0);
					Thumbnails.of(image).outputFormat("gif").size(maxWidth, maxHeight).toOutputStream(os);
				}
			} else if (filenameLow.endsWith(".svg")) {
				try (InputStream is = Files.newInputStream(inputFile)) {
					// https://xmlgraphics.apache.org/batik/using/transcoder.html
					// https://stackoverflow.com/questions/42340833/convert-svg-image-to-png-in-java-by-servlet
					// https://stackoverflow.com/questions/45239099/apache-batik-no-writeadapter-is-available
					TranscoderInput input = new TranscoderInput(is);
					TranscoderOutput output = new TranscoderOutput(os);
					JPEGTranscoder converter = new JPEGTranscoder();
					converter.addTranscodingHint(JPEGTranscoder.KEY_MAX_WIDTH, Float.valueOf(maxWidth));
					converter.addTranscodingHint(JPEGTranscoder.KEY_MAX_HEIGHT, Float.valueOf(maxHeight));
					converter.transcode(input, output);
				} catch (TranscoderException e) {
					throw new IOException("SVG to JPG failed", e);
				}
			} else {
				Date date = null;
				Integer orinetation = null;
				try (InputStream is = Files.newInputStream(inputFile)) {
					Metadata metadata = ImageMetadataReader.readMetadata(is);
					ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
					if (directory != null)
						date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
					for (Directory d : metadata.getDirectories()) {
						orinetation = d.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
						if (orinetation != null)
							break;
					}
				} catch (IOException | ImageProcessingException e) {
					// nezdařilo se, nevadí
					logger.error("Získání EXIF pro soubor {} se nezdařilo", inputFile.getFileName().toString(), e);
				}

				double angle = 0;
				if (orinetation != null)
					switch (orinetation) {
						case 1:
							// 0 degrees: the correct orientation, no adjustment
							// is required.
						case 2:
							// 0 degrees, mirrored: image has been flipped
							// back-to-front.
							break;
						case 3:
							// 180 degrees: image is upside down.
						case 4:
							// 180 degrees, mirrored: image has been flipped
							// back-to-front and is upside down.
							angle = 180;
							break;
						case 5:
							// 90 degrees: image has been flipped back-to-front
							// and is on its side.
						case 6:
							// 90 degrees, mirrored: image is on its side.
							angle = 90;
							break;
						case 7:
							// 270 degrees: image has been flipped back-to-front and
							// is on its far side.
						case 8:
							// 270 degrees, mirrored: image is on its far side.
							angle = 270;
							break;
					}

				try (InputStream is = Files.newInputStream(inputFile)) {
					Thumbnails.of(is).outputFormat("jpg").outputQuality(0.8)
							// .useExifOrientation(true) // není spolehlivé
							// aby se nepletlo s ručním otáčením
							.useExifOrientation(false).rotate(angle).size(maxWidth, maxHeight).toOutputStream(os);
				}
				if (date != null)
					Files.setLastModifiedTime(destinationFile, FileTime.fromMillis(date.getTime()));
			}
		}

	}

	public static BufferedImage getImageFromFile(Path inputFile) throws IOException {
		try (InputStream is = Files.newInputStream(inputFile)) {
			if (inputFile.toString().toLowerCase().endsWith(".gif")) {
				GifImage gifImage = GifDecoder.read(is);
				return gifImage.getFrame(0);
			} else {
				return ImageIO.read(is);
			}
		}
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o rasterový obrázek
	 *
	 * @param file jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 * rasterového obrázku
	 */
	public static boolean isRasterImage(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".jpg") || fileToExt.endsWith(".jpeg") || fileToExt.endsWith(".gif")
				|| fileToExt.endsWith(".png") || fileToExt.endsWith(".bmp");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o rasterový obrázek
	 *
	 * @param file jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 * rasterového obrázku
	 */
	public static boolean isRasterImage(Path file) {
		return PGUtils.isRasterImage(file.getFileName().toString());
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o vektorový obrázek
	 *
	 * @param file jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor
	 * vektorového obrázku
	 */
	public static boolean isVectorImage(Path file) {
		return file.getFileName().toString().endsWith(".svg");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o video
	 *
	 * @param file jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor videa
	 */
	public static boolean isVideo(String file) {
		String fileToExt = file.toLowerCase();
		return fileToExt.endsWith(".mp4") || fileToExt.endsWith(".ogg") || fileToExt.endsWith(".webm")
				|| fileToExt.endsWith(".mov") || fileToExt.endsWith(".avi");
	}

	/**
	 * Zjistí dle přípony souboru, zda se jedná o video
	 *
	 * @param file jméno souboru s příponou
	 * @return <code>true</code>, pokud se dle přípony jedná o soubor videa
	 */
	public static boolean isVideo(Path file) {
		return PGUtils.isVideo(file.getFileName().toString());
	}

	public static String createItemURL(String file, PhotogalleryTO photogallery) {
		return UIUtils.getContextPath() + "/" + PGConfiguration.PG_PATH + "/" + photogallery.getPhotogalleryPath() + "/"
				+ file;
	}

	public static String createDetailURL(PhotogalleryViewItemTO item, PhotogalleryTO photogallery) {
		String file = item.getFile().getFileName().toString();
		String url = createItemURL(file, photogallery);
		boolean video = PhotogalleryItemType.VIDEO.equals(item.getType());
		if (video) {
			url = url.substring(0, url.length() - 4);
		} else if (url.endsWith(".svg.png")) {
			// U vektorů je potřeba uříznout .png příponu, protože
			// originál je vektor, který se na slideshow dá rovnou
			// použít
			url = url.substring(0, url.length() - 4);
		}
		return url;
	}
}