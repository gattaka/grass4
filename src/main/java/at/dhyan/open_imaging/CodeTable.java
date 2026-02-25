package at.dhyan.open_imaging;

class CodeTable {
    final int[][] tbl; // Maps codes to lists of colors
    int initTableSize; // Number of colors +2 for CLEAR + EOI
    int initCodeSize; // Initial code size
    int initCodeLimit; // First code limit
    int codeSize; // Current code size, maximum is 12 bits
    int nextCode; // Next available code for a new entry
    int nextCodeLimit; // Increase codeSize when nextCode == limit
    BitReader br; // Notify when code sizes increases

    public CodeTable() {
        tbl = new int[4096][1];
    }

    void add(final int[] indices) {
        if (nextCode < 4096) {
            if (nextCode == nextCodeLimit && codeSize < 12) {
                codeSize++; // Max code size is 12
                br.setNumBits(codeSize);
                nextCodeLimit = (1 << codeSize) - 1; // 2^codeSize - 1
            }
            tbl[nextCode++] = indices;
        }
    }

    void clear() {
        codeSize = initCodeSize;
        br.setNumBits(codeSize);
        nextCodeLimit = initCodeLimit;
        nextCode = initTableSize; // Don't recreate table, reset pointer
    }

    void init(final GifFrame fr, final int[] activeColTbl, final BitReader br) {
        this.br = br;
        final int numColors = activeColTbl.length;
        initCodeSize = fr.firstCodeSize;
        initCodeLimit = (1 << initCodeSize) - 1; // 2^initCodeSize - 1
        initTableSize = fr.endOfInfoCode + 1;
        nextCode = initTableSize;
        for (int c = numColors - 1; c >= 0; c--) {
            tbl[c][0] = activeColTbl[c]; // Translated color
        } // A gap may follow with no colors assigned if numCols < CLEAR
        tbl[fr.clearCode] = new int[]{fr.clearCode}; // CLEAR
        tbl[fr.endOfInfoCode] = new int[]{fr.endOfInfoCode}; // EOI
        // Locate transparent color in code table and set to 0
        if (fr.transpColFlag && fr.transpColIndex < numColors) {
            tbl[fr.transpColIndex][0] = 0;
        }
    }
}