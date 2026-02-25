package at.dhyan.open_imaging;

class BitReader {
    private int bitPos; // Next bit to read
    private int numBits; // Number of bits to read
    private int bitMask; // Use to kill unwanted higher bits
    private byte[] in; // Data array

    // To avoid costly bounds checks, 'in' needs 2 more 0-bytes at the end
    void init(final byte[] in) {
        this.in = in;
        bitPos = 0;
    }

    int read() {
        // Byte indices: (bitPos / 8), (bitPos / 8) + 1, (bitPos / 8) + 2
        int i = bitPos >>> 3; // Byte = bit / 8
        // Bits we'll shift to the right, AND 7 is the same as MODULO 8
        final int rBits = bitPos & 7;
        // Byte 0 to 2, AND to get their unsigned values
        final int b0 = in[i++] & 0xFF, b1 = in[i++] & 0xFF, b2 = in[i] & 0xFF;
        // Glue the bytes together, don't do more shifting than necessary
        final int buf = ((b2 << 8 | b1) << 8 | b0) >>> rBits;
        bitPos += numBits;
        return buf & bitMask; // Kill the unwanted higher bits
    }

    void setNumBits(final int numBits) {
        this.numBits = numBits;
        bitMask = (1 << numBits) - 1;
    }
}