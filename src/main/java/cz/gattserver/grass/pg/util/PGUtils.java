package cz.gattserver.grass.pg.util;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.imgscalr.Scalr;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class PGUtils {

    private static Logger logger = LoggerFactory.getLogger(PGUtils.class);

    public static final int MINIATURE_SIZE = 150;
    public static final int SLIDESHOW_WIDTH = 900;
    public static final int SLIDESHOW_HEIGHT = 800;

    private PGUtils() {
    }

    public static String getExtension(Path file) {
        String filename = file.getFileName().toString();
        int dot = filename.lastIndexOf('.');
        if (dot <= 0) return "";
        return filename.substring(dot + 1);
    }

    public static void resizeImage(Path inputFile, Path destinationFile) throws IOException {
        resizeImage(inputFile, destinationFile, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE);
    }

    public static void createErrorPreview(Path destinationFile) {
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
        resize(inputImage, destinationFile, PGUtils.MINIATURE_SIZE, PGUtils.MINIATURE_SIZE, null, "png");
    }

    public static ExifInfoTO readMetadata(Path inputFile) {
        ExifInfoTO infoTO = new ExifInfoTO();
        try (InputStream is = Files.newInputStream(inputFile)) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            // Date
            if (directory != null) {
                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                    infoTO.setDate(localDate);
                    infoTO.setDateMillis(date.getTime());
                }
            }

            // Orientation
            for (Directory d : metadata.getDirectories()) {
                if (infoTO.getOrinetation() == null)
                    infoTO.setOrinetation(d.getInteger(ExifIFD0Directory.TAG_ORIENTATION));
                if (infoTO.getDeviceMaker() == null) infoTO.setDeviceMaker(d.getString(ExifIFD0Directory.TAG_MAKE));
                if (infoTO.getDeviceModel() == null) infoTO.setDeviceModel(d.getString(ExifIFD0Directory.TAG_MODEL));
                d.getString(GpsDirectory.TAG_LATITUDE);
                if (d instanceof GpsDirectory && infoTO.getLatitude() == null && infoTO.getLongitude() == null) {
                    GeoLocation gps = ((GpsDirectory) d).getGeoLocation();
                    if (gps != null) {
                        infoTO.setLatitude(gps.getLatitude());
                        infoTO.setLongitude(gps.getLongitude());
                    }
                }
            }

        } catch (IOException | ImageProcessingException e) {
            // nezdařilo se, nevadí
            logger.error("Získání EXIF pro soubor {} se nezdařilo", inputFile.getFileName().toString(), e);
        }
        return infoTO;
    }

    public static void resizeImage(Path inputFile, Path destinationFile, int maxWidth, int maxHeight)
            throws IOException {
        String extension = getExtension(inputFile).toLowerCase();
        try (OutputStream os = Files.newOutputStream(destinationFile)) {
            if (extension.equals("svg")) {
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
                ExifInfoTO exifInfoTO = readMetadata(inputFile);

                Scalr.Rotation rotation = null;
                if (exifInfoTO.getOrinetation() != null) switch (exifInfoTO.getOrinetation()) {
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
                        rotation = Scalr.Rotation.CW_180;
                        break;
                    case 5:
                        // 90 degrees: image has been flipped back-to-front
                        // and is on its side.
                    case 6:
                        // 90 degrees, mirrored: image is on its side.
                        rotation = Scalr.Rotation.CW_90;
                        break;
                    case 7:
                        // 270 degrees: image has been flipped back-to-front and
                        // is on its far side.
                    case 8:
                        // 270 degrees, mirrored: image is on its far side.
                        rotation = Scalr.Rotation.CW_270;
                        break;
                }

                // Read image — TwelveMonkeys plugins register automatically via ServiceLoader,
                // so ImageIO transparently gains WebP/BMP/JPEG improvements.
                try (InputStream is = Files.newInputStream(inputFile)) {
                    BufferedImage image = ImageIO.read(is);
                    resize(image, destinationFile, maxWidth, maxHeight, rotation, extension);
                }
                if (exifInfoTO.getDateMillis() != null)
                    Files.setLastModifiedTime(destinationFile, FileTime.fromMillis(exifInfoTO.getDateMillis()));
            }
        }
    }

    private static void resize(BufferedImage image, Path destinationFile, int maxWidth, int maxHeight,
                               Scalr.Rotation rotation, String extension) throws IOException {
        image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, maxWidth, maxHeight);
        // If still taller than maxHeight after fitting width, re-fit to height
        if (image.getHeight() > maxHeight)
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, maxWidth, maxHeight);
        if (rotation != null) image = Scalr.rotate(image, rotation);

        // Normalize "jpg" → "jpeg" which ImageIO prefers
        String writerFormat = extension;
        if (extension.equals("jpg")) writerFormat = "jpeg";
        if (extension.equals("webp")) writerFormat = "png";

        try (OutputStream os = Files.newOutputStream(destinationFile)) {
            boolean success = ImageIO.write(image, writerFormat, os);
            if (!success) throw new IOException("Can't write image " + destinationFile.getFileName());
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
        return fileToExt.endsWith(".jpg") || fileToExt.endsWith(".jpeg") || fileToExt.endsWith(".gif") ||
                fileToExt.endsWith(".png") || fileToExt.endsWith(".bmp") || fileToExt.endsWith(".webp");
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
        return file.getFileName().toString().toLowerCase().endsWith(".svg");
    }

    /**
     * Zjistí dle přípony souboru, zda se jedná o WEBP obrázek
     *
     * @param file jméno souboru s příponou
     * @return <code>true</code>, pokud se dle přípony jedná o soubor
     * WEBP obrázku
     */
    public static boolean isWebpImage(Path file) {
        return file.getFileName().toString().toLowerCase().endsWith(".webp");
    }

    /**
     * Zjistí dle přípony souboru, zda se jedná o video
     *
     * @param file jméno souboru s příponou
     * @return <code>true</code>, pokud se dle přípony jedná o soubor videa
     */
    public static boolean isVideo(String file) {
        String fileToExt = file.toLowerCase();
        return fileToExt.endsWith(".mp4") || fileToExt.endsWith(".ogg") || fileToExt.endsWith(".webm") ||
                fileToExt.endsWith(".mov") || fileToExt.endsWith(".avi");
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

    public static String createPhotogalleryBaseURL(PhotogalleryTO photogallery) {
        return UIUtils.getContextPath() + "/" + PGConfiguration.PG_PATH + "/" + photogallery.photogalleryPath() +
                "/";
    }

}