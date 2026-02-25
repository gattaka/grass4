package at.dhyan.open_imaging;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.arraycopy;

/*
 * Copyright 2014 Dhyan Blum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>
 * A decoder capable of processing a GIF data stream to render the graphics
 * contained in it. This implementation follows the official
 * <A HREF="http://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF
 * specification</A>.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <p>
 *
 * <pre>
 * final GifImage gifImage = GifDecoder.read(int[] data);
 * final int width = gifImage.getWidth();
 * final int height = gifImage.getHeight();
 * final int frameCount = gifImage.getFrameCount();
 * for (int i = 0; i < frameCount; i++) {
 * 	final BufferedImage image = gifImage.getFrame(i);
 * 	final int delay = gif.getDelay(i);
 * }
 * </pre>
 *
 * </p>
 *
 * @author Dhyan Blum
 * @version 1.09 November 2017
 *
 */
public final class GifDecoder {

    /**
     * @param in Raw image data as a byte[] array
     * @return A GifImage object exposing the properties of the GIF image.
     * @throws IOException If the image violates the GIF specification or is truncated.
     */
    public static GifImage read(final byte[] in) throws IOException {
        final GifImage img = new GifImage();
        GifFrame frame = null; // Currently open frame
        int pos = readHeader(in, img); // Read header, get next byte position
        pos = readLogicalScreenDescriptor(img, in, pos);
        if (img.hasGlobColTbl) {
            img.globalColTbl = new int[img.sizeOfGlobColTbl];
            pos = readColTbl(in, img.globalColTbl, pos);
        }
        while (pos < in.length) {
            final int block = in[pos] & 0xFF;
            switch (block) {
                case 0x21: // Extension introducer
                    if (pos + 1 >= in.length) {
                        throw new IOException("Unexpected end of file.");
                    }
                    pos = switch (in[pos + 1] & 0xFF) {
                        case 0xFE -> // Comment extension
                                readTextExtension(in, pos);
                        case 0xFF -> // Application extension
                                readAppExt(img, in, pos);
                        case 0x01 -> {
                            frame = null; // End of current frame
                            yield readTextExtension(in, pos);
                        }
                        case 0xF9 -> {
                            if (frame == null) {
                                frame = new GifFrame();
                                img.frames.add(frame);
                            }
                            yield readGraphicControlExt(frame, in, pos);
                        }
                        default -> throw new IOException("Unknown extension at " + pos);
                    };
                    break;
                case 0x2C: // Image descriptor
                    if (frame == null) {
                        frame = new GifFrame();
                        img.frames.add(frame);
                    }
                    pos = readImgDescr(frame, in, pos);
                    if (frame.hasLocColTbl) {
                        frame.localColTbl = new int[frame.sizeOfLocColTbl];
                        pos = readColTbl(in, frame.localColTbl, pos);
                    }
                    pos = readImgData(frame, in, pos);
                    frame = null; // End of current frame
                    break;
                case 0x3B: // GIF Trailer
                    return img; // Found trailer, finished reading.
                default:
                    // Unknown block. The image is corrupted. Strategies: a) Skip
                    // and wait for a valid block. Experience: It'll get worse. b)
                    // Throw exception. c) Return gracefully if we are almost done
                    // processing. The frames we have so far should be error-free.
                    final double progress = 1.0 * pos / in.length;
                    if (progress < 0.9) {
                        throw new IOException("Unknown block at: " + pos);
                    }
                    pos = in.length; // Exit loop
            }
        }
        return img;
    }

    /**
     * @param is Image data as input stream. This method will read from the
     *           input stream's current position. It will not reset the
     *           position before reading and won't reset or close the stream
     *           afterwards. Call these methods before and after calling this
     *           method as needed.
     * @return A GifImage object exposing the properties of the GIF image.
     * @throws IOException If an I/O error occurs, the image violates the GIF
     *                     specification or the GIF is truncated.
     */
    public static GifImage read(final InputStream is) throws IOException {
        final byte[] data = new byte[is.available()];
        is.read(data, 0, data.length);
        return read(data);
    }

    /**
     * @param in Raw data
     * @param i  Index of the first byte of the application extension
     * @return Index of the first byte after this extension
     */
    static int readAppExt(final GifImage img, final byte[] in, int i) {
        img.appId = new String(in, i + 3, 8); // should be "NETSCAPE"
        img.appAuthCode = new String(in, i + 11, 3); // should be "2.0"
        i += 14; // Go to sub-block size, it's value should be 3
        final int subBlockSize = in[i] & 0xFF;
        // The only app extension widely used is NETSCAPE, it's got 3 data bytes
        if (subBlockSize == 3) {
            // in[i+1] should have value 01, in[i+5] should be block terminator
            img.repetitions = in[i + 2] & 0xFF | in[i + 3] & 0xFF << 8; // Short
            return i + 5;
        } // Skip unknown application extensions
        while ((in[i] & 0xFF) != 0) { // While sub-block size != 0
            i += (in[i] & 0xFF) + 1; // Skip to next sub-block
        }
        return i + 1;
    }

    /**
     * @param in     Raw data
     * @param colors Pre-initialized target array to store ARGB colors
     * @param i      Index of the color table's first byte
     * @return Index of the first byte after the color table
     */
    static int readColTbl(final byte[] in, final int[] colors, int i) {
        final int numColors = colors.length;
        for (int c = 0; c < numColors; c++) {
            final int a = 0xFF; // Alpha 255 (opaque)
            final int r = in[i++] & 0xFF; // 1st byte is red
            final int g = in[i++] & 0xFF; // 2nd byte is green
            final int b = in[i++] & 0xFF; // 3rd byte is blue
            colors[c] = ((a << 8 | r) << 8 | g) << 8 | b;
        }
        return i;
    }

    /**
     * @param in Raw data
     * @param i  Index of the extension introducer
     * @return Index of the first byte after this block
     */
    static int readGraphicControlExt(final GifFrame fr, final byte[] in, final int i) {
        fr.disposalMethod = (in[i + 3] & 0b00011100) >>> 2; // Bits 4-2
        fr.transpColFlag = (in[i + 3] & 1) == 1; // Bit 0
        fr.delay = in[i + 4] & 0xFF | (in[i + 5] & 0xFF) << 8; // 16 bit LSB
        fr.transpColIndex = in[i + 6] & 0xFF; // Byte 6
        return i + 8; // Skipped byte 7 (blockTerminator), as it's always 0x00
    }

    /**
     * @param in  Raw data
     * @param img The GifImage object that is currently read
     * @return Index of the first byte after this block
     * @throws IOException If the GIF header/trailer is missing, incomplete or unknown
     */
    static int readHeader(final byte[] in, final GifImage img) throws IOException {
        if (in.length < 6) { // Check first 6 bytes
            throw new IOException("Image is truncated.");
        }
        img.header = new String(in, 0, 6);
        if (!img.header.equals("GIF87a") && !img.header.equals("GIF89a")) {
            throw new IOException("Invalid GIF header.");
        }
        return 6;
    }

    /**
     * @param fr The GIF frame to whom this image descriptor belongs
     * @param in Raw data
     * @param i  Index of the first byte of this block, i.e. the minCodeSize
     */
    static int readImgData(final GifFrame fr, final byte[] in, int i) {
        final int fileSize = in.length;
        final int minCodeSize = in[i++] & 0xFF; // Read code size, go to block
        final int clearCode = 1 << minCodeSize; // CLEAR = 2^minCodeSize
        fr.firstCodeSize = minCodeSize + 1; // Add 1 bit for CLEAR and EOI
        fr.clearCode = clearCode;
        fr.endOfInfoCode = clearCode + 1;
        final int imgDataSize = readImgDataSize(in, i);
        final byte[] imgData = new byte[imgDataSize + 2];
        int imgDataPos = 0;
        int subBlockSize = in[i] & 0xFF;
        while (subBlockSize > 0) { // While block has data
            try { // Next line may throw exception if sub-block size is fake
                final int nextSubBlockSizePos = i + subBlockSize + 1;
                final int nextSubBlockSize = in[nextSubBlockSizePos] & 0xFF;
                arraycopy(in, i + 1, imgData, imgDataPos, subBlockSize);
                imgDataPos += subBlockSize; // Move output data position
                i = nextSubBlockSizePos; // Move to next sub-block size
                subBlockSize = nextSubBlockSize;
            } catch (final Exception e) {
                // Sub-block exceeds file end, only use remaining bytes
                subBlockSize = fileSize - i - 1; // Remaining bytes
                arraycopy(in, i + 1, imgData, imgDataPos, subBlockSize);
                i += subBlockSize + 1; // Move to next sub-block size
                break;
            }
        }
        fr.data = imgData; // Holds LZW encoded data
        i++; // Skip last sub-block size, should be 0
        return i;
    }

    static int readImgDataSize(final byte[] in, int i) {
        final int fileSize = in.length;
        int imgDataPos = 0;
        int subBlockSize = in[i] & 0xFF;
        while (subBlockSize > 0) { // While block has data
            try { // Next line may throw exception if sub-block size is fake
                final int nextSubBlockSizePos = i + subBlockSize + 1;
                final int nextSubBlockSize = in[nextSubBlockSizePos] & 0xFF;
                imgDataPos += subBlockSize; // Move output data position
                i = nextSubBlockSizePos; // Move to next sub-block size
                subBlockSize = nextSubBlockSize;
            } catch (final Exception e) {
                // Sub-block exceeds file end, only use remaining bytes
                subBlockSize = fileSize - i - 1; // Remaining bytes
                imgDataPos += subBlockSize; // Move output data position
                break;
            }
        }
        return imgDataPos;
    }

    /**
     * @param fr The GIF frame to whom this image descriptor belongs
     * @param in Raw data
     * @param i  Index of the image separator, i.e. the first block byte
     * @return Index of the first byte after this block
     */
    static int readImgDescr(final GifFrame fr, final byte[] in, int i) {
        fr.x = in[++i] & 0xFF | (in[++i] & 0xFF) << 8; // Byte 1-2: left
        fr.y = in[++i] & 0xFF | (in[++i] & 0xFF) << 8; // Byte 3-4: top
        fr.w = in[++i] & 0xFF | (in[++i] & 0xFF) << 8; // Byte 5-6: width
        fr.h = in[++i] & 0xFF | (in[++i] & 0xFF) << 8; // Byte 7-8: height
        fr.wh = fr.w * fr.h;
        final byte b = in[++i]; // Byte 9 is a packed byte
        fr.hasLocColTbl = (b & 0b10000000) >>> 7 == 1; // Bit 7
        fr.interlaceFlag = (b & 0b01000000) >>> 6 == 1; // Bit 6
        fr.sortFlag = (b & 0b00100000) >>> 5 == 1; // Bit 5
        final int colTblSizePower = (b & 7) + 1; // Bits 2-0
        fr.sizeOfLocColTbl = 1 << colTblSizePower; // 2^(N+1), As per the spec
        return ++i;
    }

    /**
     * @param img image
     * @param i   Start index of this block.
     * @return Index of the first byte after this block.
     */
    static int readLogicalScreenDescriptor(final GifImage img, final byte[] in, final int i) {
        img.w = in[i] & 0xFF | (in[i + 1] & 0xFF) << 8; // 16 bit, LSB 1st
        img.h = in[i + 2] & 0xFF | (in[i + 3] & 0xFF) << 8; // 16 bit
        img.wh = img.w * img.h;
        final byte b = in[i + 4]; // Byte 4 is a packed byte
        img.hasGlobColTbl = (b & 0b10000000) >>> 7 == 1; // Bit 7
        final int colResPower = ((b & 0b01110000) >>> 4) + 1; // Bits 6-4
        img.colorResolution = 1 << colResPower; // 2^(N+1), As per the spec
        img.sortFlag = (b & 0b00001000) >>> 3 == 1; // Bit 3
        final int globColTblSizePower = (b & 7) + 1; // Bits 0-2
        img.sizeOfGlobColTbl = 1 << globColTblSizePower; // 2^(N+1), see spec
        img.bgColIndex = in[i + 5] & 0xFF; // 1 Byte
        img.pxAspectRatio = in[i + 6] & 0xFF; // 1 Byte
        return i + 7;
    }

    /**
     * @param in  Raw data
     * @param pos Index of the extension introducer
     * @return Index of the first byte after this block
     */
    static int readTextExtension(final byte[] in, final int pos) {
        int i = pos + 2; // Skip extension introducer and label
        int subBlockSize = in[i++] & 0xFF;
        while (subBlockSize != 0 && i < in.length) {
            i += subBlockSize;
            subBlockSize = in[i++] & 0xFF;
        }
        return i;
    }
}