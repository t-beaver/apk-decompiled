package com.dcloud.zxing2.oned;

import com.dcloud.zxing2.BarcodeFormat;
import com.dcloud.zxing2.EncodeHintType;
import com.dcloud.zxing2.WriterException;
import com.dcloud.zxing2.common.BitMatrix;
import java.util.Map;

public final class ITFWriter extends OneDimensionalCodeWriter {
    private static final int[] END_PATTERN = {3, 1, 1};
    private static final int[] START_PATTERN = {1, 1, 1, 1};

    public BitMatrix encode(String str, BarcodeFormat barcodeFormat, int i, int i2, Map<EncodeHintType, ?> map) throws WriterException {
        if (barcodeFormat == BarcodeFormat.ITF) {
            return super.encode(str, barcodeFormat, i, i2, map);
        }
        throw new IllegalArgumentException("Can only encode ITF, but got " + barcodeFormat);
    }

    public boolean[] encode(String str) {
        int length = str.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("The length of the input should be even");
        } else if (length <= 80) {
            boolean[] zArr = new boolean[((length * 9) + 9)];
            int appendPattern = OneDimensionalCodeWriter.appendPattern(zArr, 0, START_PATTERN, true);
            for (int i = 0; i < length; i += 2) {
                int digit = Character.digit(str.charAt(i), 10);
                int digit2 = Character.digit(str.charAt(i + 1), 10);
                int[] iArr = new int[18];
                for (int i2 = 0; i2 < 5; i2++) {
                    int i3 = i2 * 2;
                    int[][] iArr2 = ITFReader.PATTERNS;
                    iArr[i3] = iArr2[digit][i2];
                    iArr[i3 + 1] = iArr2[digit2][i2];
                }
                appendPattern += OneDimensionalCodeWriter.appendPattern(zArr, appendPattern, iArr, true);
            }
            OneDimensionalCodeWriter.appendPattern(zArr, appendPattern, END_PATTERN, true);
            return zArr;
        } else {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
        }
    }
}
