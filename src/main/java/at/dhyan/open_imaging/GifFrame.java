package at.dhyan.open_imaging;

import java.awt.image.BufferedImage;

class GifFrame {
    // Graphic control extension (optional)
    // Disposal: 0=NO_ACTION, 1=NO_DISPOSAL, 2=RESTORE_BG, 3=RESTORE_PREV
    int disposalMethod; // 0-3 as above, 4-7 undefined
    boolean transpColFlag; // 1 Bit
    int delay; // Unsigned, LSByte first, n * 1/100 * s
    int transpColIndex; // 1 Byte
    // Image descriptor
    int x; // Position on the canvas from the left
    int y; // Position on the canvas from the top
    int w; // May be smaller than the base image
    int h; // May be smaller than the base image
    int wh; // width * height
    boolean hasLocColTbl; // Has local color table? 1 Bit
    boolean interlaceFlag; // Is an interlace image? 1 Bit
    @SuppressWarnings("unused")
    boolean sortFlag; // True if local colors are sorted, 1 Bit
    int sizeOfLocColTbl; // Size of the local color table, 3 Bits
    int[] localColTbl; // Local color table (optional)
    // Image data
    int firstCodeSize; // LZW minimum code size + 1 for CLEAR & EOI
    int clearCode;
    int endOfInfoCode;
    byte[] data; // Holds LZW encoded data
    BufferedImage img; // Full drawn image, not just the frame area
}