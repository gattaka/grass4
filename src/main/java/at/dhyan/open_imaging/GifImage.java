package at.dhyan.open_imaging;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.arraycopy;

public class GifImage {
    public String header; // Bytes 0-5, GIF87a or GIF89a
    public boolean hasGlobColTbl; // 1 Bit
    public int colorResolution; // 3 Bits
    public boolean sortFlag; // True if global colors are sorted, 1 Bit
    public int sizeOfGlobColTbl; // 2^(val(3 Bits) + 1), see spec
    public int bgColIndex; // Background color index, 1 Byte
    public int pxAspectRatio; // Pixel aspect ratio, 1 Byte
    public int[] globalColTbl; // Global color table
    public String appId = ""; // 8 Bytes at in[i+3], usually "NETSCAPE"
    public String appAuthCode = ""; // 3 Bytes at in[i+11], usually "2.0"
    public int repetitions = 0; // 0: infinite loop, N: number of loops

    int w; // Unsigned 16 Bit, least significant byte first
    int h; // Unsigned 16 Bit, least significant byte first
    int wh; // Image width * image height
    final List<GifFrame> frames = new ArrayList<>(64);
    BufferedImage img = null; // Currently drawn frame
    int[] prevPx = null; // Previous frame's pixels
    final BitReader bits = new BitReader();
    final CodeTable codes = new CodeTable();
    Graphics2D g;

    private final int[] decode(final GifFrame fr, final int[] activeColTbl) {
        codes.init(fr, activeColTbl, bits);
        bits.init(fr.data); // Incoming codes
        final int clearCode = fr.clearCode, endCode = fr.endOfInfoCode;
        final int[] out = new int[wh]; // Target image pixel array
        final int[][] tbl = codes.tbl; // Code table
        int outPos = 0; // Next pixel position in the output image array
        codes.clear(); // Init code table
        bits.read(); // Skip leading clear code
        int code = bits.read(); // Read first code
        int[] pixels = tbl[code]; // Output pixel for first code
        arraycopy(pixels, 0, out, outPos, pixels.length);
        outPos += pixels.length;
        try {
            while (true) {
                final int prevCode = code;
                code = bits.read(); // Get next code in stream
                if (code == clearCode) { // After a CLEAR table, there is
                    codes.clear(); // no previous code, we need to read
                    code = bits.read(); // a new one
                    pixels = tbl[code]; // Output pixels
                    arraycopy(pixels, 0, out, outPos, pixels.length);
                    outPos += pixels.length;
                    continue; // Back to the loop with a valid previous code
                } else if (code == endCode) {
                    break;
                }
                final int[] prevVals = tbl[prevCode];
                final int[] prevValsAndK = new int[prevVals.length + 1];
                arraycopy(prevVals, 0, prevValsAndK, 0, prevVals.length);
                if (code < codes.nextCode) { // Code table contains code
                    pixels = tbl[code]; // Output pixels
                    arraycopy(pixels, 0, out, outPos, pixels.length);
                    outPos += pixels.length;
                    prevValsAndK[prevVals.length] = tbl[code][0]; // K
                } else {
                    prevValsAndK[prevVals.length] = prevVals[0]; // K
                    arraycopy(prevValsAndK, 0, out, outPos, prevValsAndK.length);
                    outPos += prevValsAndK.length;
                }
                codes.add(prevValsAndK); // Previous indices + K
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
        }
        return out;
    }

    private final int[] deinterlace(final int[] src, final GifFrame fr) {
        final int w = fr.w, h = fr.h, wh = fr.wh;
        final int[] dest = new int[src.length];
        // Interlaced images are organized in 4 sets of pixel lines
        final int set2Y = (h + 7) >>> 3; // Line no. = ceil(h/8.0)
        final int set3Y = set2Y + ((h + 3) >>> 3); // ceil(h-4/8.0)
        final int set4Y = set3Y + ((h + 1) >>> 2); // ceil(h-2/4.0)
        // Sets' start indices in source array
        final int set2 = w * set2Y, set3 = w * set3Y, set4 = w * set4Y;
        // Line skips in destination array
        final int w2 = w << 1, w4 = w2 << 1, w8 = w4 << 1;
        // Group 1 contains every 8th line starting from 0
        int from = 0, to = 0;
        for (; from < set2; from += w, to += w8) {
            arraycopy(src, from, dest, to, w);
        } // Group 2 contains every 8th line starting from 4
        for (to = w4; from < set3; from += w, to += w8) {
            arraycopy(src, from, dest, to, w);
        } // Group 3 contains every 4th line starting from 2
        for (to = w2; from < set4; from += w, to += w4) {
            arraycopy(src, from, dest, to, w);
        } // Group 4 contains every 2nd line starting from 1 (biggest group)
        for (to = w; from < wh; from += w, to += w2) {
            arraycopy(src, from, dest, to, w);
        }
        return dest; // All pixel lines have now been rearranged
    }

    private final void drawFrame(final GifFrame fr) {
        // Determine the color table that will be active for this frame
        final int[] activeColTbl = fr.hasLocColTbl ? fr.localColTbl : globalColTbl;
        // Get pixels from data stream
        int[] pixels = decode(fr, activeColTbl);
        if (fr.interlaceFlag) {
            pixels = deinterlace(pixels, fr); // Rearrange pixel lines
        }
        // Create image of type 2=ARGB for frame area
        final BufferedImage frame = new BufferedImage(fr.w, fr.h, 2);
        arraycopy(pixels, 0, ((DataBufferInt) frame.getRaster().getDataBuffer()).getData(), 0, fr.wh);
        // Draw frame area on top of working image
        g.drawImage(frame, fr.x, fr.y, null);

        // Visualize frame boundaries during testing
        // if (DEBUG_MODE) {
        // if (prev != null) {
        // g.setColor(Color.RED); // Previous frame color
        // g.drawRect(prev.x, prev.y, prev.w - 1, prev.h - 1);
        // }
        // g.setColor(Color.GREEN); // New frame color
        // g.drawRect(fr.x, fr.y, fr.w - 1, fr.h - 1);
        // }

        // Keep one copy as "previous frame" in case we need to restore it
        prevPx = new int[wh];
        arraycopy(((DataBufferInt) img.getRaster().getDataBuffer()).getData(), 0, prevPx, 0, wh);

        // Create another copy for the end user to not expose internal state
        fr.img = new BufferedImage(w, h, 2); // 2 = ARGB
        arraycopy(prevPx, 0, ((DataBufferInt) fr.img.getRaster().getDataBuffer()).getData(), 0, wh);

        // Handle disposal of current frame
        if (fr.disposalMethod == 2) {
            // Restore to background color (clear frame area only)
            g.clearRect(fr.x, fr.y, fr.w, fr.h);
        } else if (fr.disposalMethod == 3 && prevPx != null) {
            // Restore previous frame
            arraycopy(prevPx, 0, ((DataBufferInt) img.getRaster().getDataBuffer()).getData(), 0, wh);
        }
    }

    /**
     * Returns the background color of the first frame in this GIF image. If
     * the frame has a local color table, the returned color will be from
     * that table. If not, the color will be from the global color table.
     * Returns 0 if there is neither a local nor a global color table.
     *
     * @return 32 bit ARGB color in the form 0xAARRGGBB
     */
    public final int getBackgroundColor() {
        final GifFrame frame = frames.get(0);
        if (frame.hasLocColTbl) {
            return frame.localColTbl[bgColIndex];
        } else if (hasGlobColTbl) {
            return globalColTbl[bgColIndex];
        }
        return 0;
    }

    /**
     * If not 0, the delay specifies how many hundredths (1/100) of a second
     * to wait before displaying the frame <i>after</i> the current frame.
     *
     * @param index Index of the current frame, 0 to N-1
     * @return Delay as number of hundredths (1/100) of a second
     */
    public final int getDelay(final int index) {
        return frames.get(index).delay;
    }

    /**
     * @param index Index of the frame to return as image, starting from 0.
     *              For incremental calls such as [0, 1, 2, ...] the method's
     *              run time is O(1) as only one frame is drawn per call. For
     *              random access calls such as [7, 12, ...] the run time is
     *              O(N+1) with N being the number of previous frames that
     *              need to be drawn before N+1 can be drawn on top. Once a
     *              frame has been drawn it is being cached and the run time
     *              is more or less O(0) to retrieve it from the list.
     * @return A BufferedImage for the specified frame.
     */
    public final BufferedImage getFrame(final int index) {
        if (img == null) { // Init
            img = new BufferedImage(w, h, 2); // 2 = ARGB
            g = img.createGraphics();
            g.setBackground(new Color(0, true)); // Transparent color
        }
        GifFrame fr = frames.get(index);
        if (fr.img == null) {
            // Draw all frames until and including the requested frame
            for (int i = 0; i <= index; i++) {
                fr = frames.get(i);
                if (fr.img == null) {
                    drawFrame(fr);
                }
            }
        }
        return fr.img;
    }

    /**
     * @return The number of frames contained in this GIF image
     */
    public final int getFrameCount() {
        return frames.size();
    }

    /**
     * @return The height of the GIF image
     */
    public final int getHeight() {
        return h;
    }

    /**
     * @return The width of the GIF image
     */
    public final int getWidth() {
        return w;
    }
}