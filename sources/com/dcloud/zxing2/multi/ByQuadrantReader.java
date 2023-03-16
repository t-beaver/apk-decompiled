package com.dcloud.zxing2.multi;

import com.dcloud.zxing2.BinaryBitmap;
import com.dcloud.zxing2.ChecksumException;
import com.dcloud.zxing2.DecodeHintType;
import com.dcloud.zxing2.FormatException;
import com.dcloud.zxing2.NotFoundException;
import com.dcloud.zxing2.Reader;
import com.dcloud.zxing2.Result;
import com.dcloud.zxing2.ResultPoint;
import java.util.Map;

public final class ByQuadrantReader implements Reader {
    private final Reader delegate;

    public ByQuadrantReader(Reader reader) {
        this.delegate = reader;
    }

    private static void makeAbsolute(ResultPoint[] resultPointArr, int i, int i2) {
        if (resultPointArr != null) {
            for (int i3 = 0; i3 < resultPointArr.length; i3++) {
                ResultPoint resultPoint = resultPointArr[i3];
                resultPointArr[i3] = new ResultPoint(resultPoint.getX() + ((float) i), resultPoint.getY() + ((float) i2));
            }
        }
    }

    public Result decode(BinaryBitmap binaryBitmap) throws NotFoundException, ChecksumException, FormatException {
        return decode(binaryBitmap, (Map<DecodeHintType, ?>) null);
    }

    public void reset() {
        this.delegate.reset();
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:10|11|12) */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:4|5|6) */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:7|8|9) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r2 = r5.delegate.decode(r6.crop(r0, r1, r0, r1), r7);
        makeAbsolute(r2.getResultPoints(), r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004d, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004e, code lost:
        r2 = r0 / 2;
        r3 = r1 / 2;
        r6 = r5.delegate.decode(r6.crop(r2, r3, r0, r1), r7);
        makeAbsolute(r6.getResultPoints(), r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0063, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:?, code lost:
        r3 = r5.delegate.decode(r6.crop(r0, 0, r0, r1), r7);
        makeAbsolute(r3.getResultPoints(), r0, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0029, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        r3 = r5.delegate.decode(r6.crop(0, r1, r0, r1), r7);
        makeAbsolute(r3.getResultPoints(), 0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x003b, code lost:
        return r3;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x003c */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0018 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.dcloud.zxing2.Result decode(com.dcloud.zxing2.BinaryBitmap r6, java.util.Map<com.dcloud.zxing2.DecodeHintType, ?> r7) throws com.dcloud.zxing2.NotFoundException, com.dcloud.zxing2.ChecksumException, com.dcloud.zxing2.FormatException {
        /*
            r5 = this;
            int r0 = r6.getWidth()
            int r1 = r6.getHeight()
            int r0 = r0 / 2
            int r1 = r1 / 2
            r2 = 0
            com.dcloud.zxing2.Reader r3 = r5.delegate     // Catch:{ NotFoundException -> 0x0018 }
            com.dcloud.zxing2.BinaryBitmap r4 = r6.crop(r2, r2, r0, r1)     // Catch:{ NotFoundException -> 0x0018 }
            com.dcloud.zxing2.Result r6 = r3.decode(r4, r7)     // Catch:{ NotFoundException -> 0x0018 }
            return r6
        L_0x0018:
            com.dcloud.zxing2.Reader r3 = r5.delegate     // Catch:{ NotFoundException -> 0x002a }
            com.dcloud.zxing2.BinaryBitmap r4 = r6.crop(r0, r2, r0, r1)     // Catch:{ NotFoundException -> 0x002a }
            com.dcloud.zxing2.Result r3 = r3.decode(r4, r7)     // Catch:{ NotFoundException -> 0x002a }
            com.dcloud.zxing2.ResultPoint[] r4 = r3.getResultPoints()     // Catch:{ NotFoundException -> 0x002a }
            makeAbsolute(r4, r0, r2)     // Catch:{ NotFoundException -> 0x002a }
            return r3
        L_0x002a:
            com.dcloud.zxing2.Reader r3 = r5.delegate     // Catch:{ NotFoundException -> 0x003c }
            com.dcloud.zxing2.BinaryBitmap r4 = r6.crop(r2, r1, r0, r1)     // Catch:{ NotFoundException -> 0x003c }
            com.dcloud.zxing2.Result r3 = r3.decode(r4, r7)     // Catch:{ NotFoundException -> 0x003c }
            com.dcloud.zxing2.ResultPoint[] r4 = r3.getResultPoints()     // Catch:{ NotFoundException -> 0x003c }
            makeAbsolute(r4, r2, r1)     // Catch:{ NotFoundException -> 0x003c }
            return r3
        L_0x003c:
            com.dcloud.zxing2.Reader r2 = r5.delegate     // Catch:{ NotFoundException -> 0x004e }
            com.dcloud.zxing2.BinaryBitmap r3 = r6.crop(r0, r1, r0, r1)     // Catch:{ NotFoundException -> 0x004e }
            com.dcloud.zxing2.Result r2 = r2.decode(r3, r7)     // Catch:{ NotFoundException -> 0x004e }
            com.dcloud.zxing2.ResultPoint[] r3 = r2.getResultPoints()     // Catch:{ NotFoundException -> 0x004e }
            makeAbsolute(r3, r0, r1)     // Catch:{ NotFoundException -> 0x004e }
            return r2
        L_0x004e:
            int r2 = r0 / 2
            int r3 = r1 / 2
            com.dcloud.zxing2.BinaryBitmap r6 = r6.crop(r2, r3, r0, r1)
            com.dcloud.zxing2.Reader r0 = r5.delegate
            com.dcloud.zxing2.Result r6 = r0.decode(r6, r7)
            com.dcloud.zxing2.ResultPoint[] r7 = r6.getResultPoints()
            makeAbsolute(r7, r2, r3)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.dcloud.zxing2.multi.ByQuadrantReader.decode(com.dcloud.zxing2.BinaryBitmap, java.util.Map):com.dcloud.zxing2.Result");
    }
}