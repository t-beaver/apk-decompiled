package com.dcloud.zxing2.datamatrix.detector;

import com.dcloud.zxing2.NotFoundException;
import com.dcloud.zxing2.ResultPoint;
import com.dcloud.zxing2.common.BitMatrix;
import com.dcloud.zxing2.common.DetectorResult;
import com.dcloud.zxing2.common.GridSampler;
import com.dcloud.zxing2.common.detector.MathUtils;
import com.dcloud.zxing2.common.detector.WhiteRectangleDetector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class Detector {
    private final BitMatrix image;
    private final WhiteRectangleDetector rectangleDetector;

    private static final class ResultPointsAndTransitions {
        private final ResultPoint from;
        private final ResultPoint to;
        private final int transitions;

        /* access modifiers changed from: package-private */
        public ResultPoint getFrom() {
            return this.from;
        }

        /* access modifiers changed from: package-private */
        public ResultPoint getTo() {
            return this.to;
        }

        public int getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return this.from + "/" + this.to + '/' + this.transitions;
        }

        private ResultPointsAndTransitions(ResultPoint resultPoint, ResultPoint resultPoint2, int i) {
            this.from = resultPoint;
            this.to = resultPoint2;
            this.transitions = i;
        }
    }

    private static final class ResultPointsAndTransitionsComparator implements Comparator<ResultPointsAndTransitions>, Serializable {
        private ResultPointsAndTransitionsComparator() {
        }

        public int compare(ResultPointsAndTransitions resultPointsAndTransitions, ResultPointsAndTransitions resultPointsAndTransitions2) {
            return resultPointsAndTransitions.getTransitions() - resultPointsAndTransitions2.getTransitions();
        }
    }

    public Detector(BitMatrix bitMatrix) throws NotFoundException {
        this.image = bitMatrix;
        this.rectangleDetector = new WhiteRectangleDetector(bitMatrix);
    }

    private ResultPoint correctTopRight(ResultPoint resultPoint, ResultPoint resultPoint2, ResultPoint resultPoint3, ResultPoint resultPoint4, int i) {
        float f = (float) i;
        float distance = ((float) distance(resultPoint, resultPoint2)) / f;
        float distance2 = (float) distance(resultPoint3, resultPoint4);
        ResultPoint resultPoint5 = new ResultPoint(resultPoint4.getX() + (((resultPoint4.getX() - resultPoint3.getX()) / distance2) * distance), resultPoint4.getY() + (distance * ((resultPoint4.getY() - resultPoint3.getY()) / distance2)));
        float distance3 = ((float) distance(resultPoint, resultPoint3)) / f;
        float distance4 = (float) distance(resultPoint2, resultPoint4);
        ResultPoint resultPoint6 = new ResultPoint(resultPoint4.getX() + (((resultPoint4.getX() - resultPoint2.getX()) / distance4) * distance3), resultPoint4.getY() + (distance3 * ((resultPoint4.getY() - resultPoint2.getY()) / distance4)));
        if (isValid(resultPoint5)) {
            return (isValid(resultPoint6) && Math.abs(transitionsBetween(resultPoint3, resultPoint5).getTransitions() - transitionsBetween(resultPoint2, resultPoint5).getTransitions()) > Math.abs(transitionsBetween(resultPoint3, resultPoint6).getTransitions() - transitionsBetween(resultPoint2, resultPoint6).getTransitions())) ? resultPoint6 : resultPoint5;
        }
        if (isValid(resultPoint6)) {
            return resultPoint6;
        }
        return null;
    }

    private ResultPoint correctTopRightRectangular(ResultPoint resultPoint, ResultPoint resultPoint2, ResultPoint resultPoint3, ResultPoint resultPoint4, int i, int i2) {
        float distance = ((float) distance(resultPoint, resultPoint2)) / ((float) i);
        float distance2 = (float) distance(resultPoint3, resultPoint4);
        ResultPoint resultPoint5 = new ResultPoint(resultPoint4.getX() + (((resultPoint4.getX() - resultPoint3.getX()) / distance2) * distance), resultPoint4.getY() + (distance * ((resultPoint4.getY() - resultPoint3.getY()) / distance2)));
        float distance3 = ((float) distance(resultPoint, resultPoint3)) / ((float) i2);
        float distance4 = (float) distance(resultPoint2, resultPoint4);
        ResultPoint resultPoint6 = new ResultPoint(resultPoint4.getX() + (((resultPoint4.getX() - resultPoint2.getX()) / distance4) * distance3), resultPoint4.getY() + (distance3 * ((resultPoint4.getY() - resultPoint2.getY()) / distance4)));
        if (isValid(resultPoint5)) {
            return (isValid(resultPoint6) && Math.abs(i - transitionsBetween(resultPoint3, resultPoint5).getTransitions()) + Math.abs(i2 - transitionsBetween(resultPoint2, resultPoint5).getTransitions()) > Math.abs(i - transitionsBetween(resultPoint3, resultPoint6).getTransitions()) + Math.abs(i2 - transitionsBetween(resultPoint2, resultPoint6).getTransitions())) ? resultPoint6 : resultPoint5;
        }
        if (isValid(resultPoint6)) {
            return resultPoint6;
        }
        return null;
    }

    private static int distance(ResultPoint resultPoint, ResultPoint resultPoint2) {
        return MathUtils.round(ResultPoint.distance(resultPoint, resultPoint2));
    }

    private static void increment(Map<ResultPoint, Integer> map, ResultPoint resultPoint) {
        Integer num = map.get(resultPoint);
        int i = 1;
        if (num != null) {
            i = 1 + num.intValue();
        }
        map.put(resultPoint, Integer.valueOf(i));
    }

    private boolean isValid(ResultPoint resultPoint) {
        return resultPoint.getX() >= 0.0f && resultPoint.getX() < ((float) this.image.getWidth()) && resultPoint.getY() > 0.0f && resultPoint.getY() < ((float) this.image.getHeight());
    }

    private static BitMatrix sampleGrid(BitMatrix bitMatrix, ResultPoint resultPoint, ResultPoint resultPoint2, ResultPoint resultPoint3, ResultPoint resultPoint4, int i, int i2) throws NotFoundException {
        float f = ((float) i) - 0.5f;
        float f2 = ((float) i2) - 0.5f;
        return GridSampler.getInstance().sampleGrid(bitMatrix, i, i2, 0.5f, 0.5f, f, 0.5f, f, f2, 0.5f, f2, resultPoint.getX(), resultPoint.getY(), resultPoint4.getX(), resultPoint4.getY(), resultPoint3.getX(), resultPoint3.getY(), resultPoint2.getX(), resultPoint2.getY());
    }

    private ResultPointsAndTransitions transitionsBetween(ResultPoint resultPoint, ResultPoint resultPoint2) {
        int x = (int) resultPoint.getX();
        int y = (int) resultPoint.getY();
        int x2 = (int) resultPoint2.getX();
        int y2 = (int) resultPoint2.getY();
        int i = 0;
        int i2 = 1;
        boolean z = Math.abs(y2 - y) > Math.abs(x2 - x);
        if (!z) {
            int i3 = y;
            y = x;
            x = i3;
            int i4 = y2;
            y2 = x2;
            x2 = i4;
        }
        int abs = Math.abs(y2 - y);
        int abs2 = Math.abs(x2 - x);
        int i5 = (-abs) / 2;
        int i6 = x < x2 ? 1 : -1;
        if (y >= y2) {
            i2 = -1;
        }
        boolean z2 = this.image.get(z ? x : y, z ? y : x);
        while (y != y2) {
            boolean z3 = this.image.get(z ? x : y, z ? y : x);
            if (z3 != z2) {
                i++;
                z2 = z3;
            }
            i5 += abs2;
            if (i5 > 0) {
                if (x == x2) {
                    break;
                }
                x += i6;
                i5 -= abs;
            }
            y += i2;
        }
        return new ResultPointsAndTransitions(resultPoint, resultPoint2, i);
    }

    public DetectorResult detect() throws NotFoundException {
        ResultPoint resultPoint;
        ResultPoint resultPoint2;
        BitMatrix bitMatrix;
        ResultPoint[] detect = this.rectangleDetector.detect();
        ResultPoint resultPoint3 = detect[0];
        ResultPoint resultPoint4 = detect[1];
        ResultPoint resultPoint5 = detect[2];
        ResultPoint resultPoint6 = detect[3];
        ArrayList arrayList = new ArrayList(4);
        arrayList.add(transitionsBetween(resultPoint3, resultPoint4));
        arrayList.add(transitionsBetween(resultPoint3, resultPoint5));
        arrayList.add(transitionsBetween(resultPoint4, resultPoint6));
        arrayList.add(transitionsBetween(resultPoint5, resultPoint6));
        ResultPoint resultPoint7 = null;
        Collections.sort(arrayList, new ResultPointsAndTransitionsComparator());
        ResultPointsAndTransitions resultPointsAndTransitions = (ResultPointsAndTransitions) arrayList.get(0);
        ResultPointsAndTransitions resultPointsAndTransitions2 = (ResultPointsAndTransitions) arrayList.get(1);
        HashMap hashMap = new HashMap();
        increment(hashMap, resultPointsAndTransitions.getFrom());
        increment(hashMap, resultPointsAndTransitions.getTo());
        increment(hashMap, resultPointsAndTransitions2.getFrom());
        increment(hashMap, resultPointsAndTransitions2.getTo());
        ResultPoint resultPoint8 = null;
        ResultPoint resultPoint9 = null;
        for (Map.Entry entry : hashMap.entrySet()) {
            ResultPoint resultPoint10 = (ResultPoint) entry.getKey();
            if (((Integer) entry.getValue()).intValue() == 2) {
                resultPoint8 = resultPoint10;
            } else if (resultPoint7 == null) {
                resultPoint7 = resultPoint10;
            } else {
                resultPoint9 = resultPoint10;
            }
        }
        if (resultPoint7 == null || resultPoint8 == null || resultPoint9 == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        ResultPoint[] resultPointArr = {resultPoint7, resultPoint8, resultPoint9};
        ResultPoint.orderBestPatterns(resultPointArr);
        ResultPoint resultPoint11 = resultPointArr[0];
        ResultPoint resultPoint12 = resultPointArr[1];
        ResultPoint resultPoint13 = resultPointArr[2];
        if (!hashMap.containsKey(resultPoint3)) {
            resultPoint = resultPoint3;
        } else if (!hashMap.containsKey(resultPoint4)) {
            resultPoint = resultPoint4;
        } else {
            resultPoint = !hashMap.containsKey(resultPoint5) ? resultPoint5 : resultPoint6;
        }
        int transitions = transitionsBetween(resultPoint13, resultPoint).getTransitions();
        int transitions2 = transitionsBetween(resultPoint11, resultPoint).getTransitions();
        if ((transitions & 1) == 1) {
            transitions++;
        }
        int i = transitions + 2;
        if ((transitions2 & 1) == 1) {
            transitions2++;
        }
        int i2 = transitions2 + 2;
        if (i * 4 >= i2 * 7 || i2 * 4 >= i * 7) {
            resultPoint2 = resultPoint13;
            ResultPoint correctTopRightRectangular = correctTopRightRectangular(resultPoint12, resultPoint11, resultPoint13, resultPoint, i, i2);
            if (correctTopRightRectangular != null) {
                resultPoint = correctTopRightRectangular;
            }
            int transitions3 = transitionsBetween(resultPoint2, resultPoint).getTransitions();
            int transitions4 = transitionsBetween(resultPoint11, resultPoint).getTransitions();
            if ((transitions3 & 1) == 1) {
                transitions3++;
            }
            int i3 = transitions3;
            if ((transitions4 & 1) == 1) {
                transitions4++;
            }
            bitMatrix = sampleGrid(this.image, resultPoint2, resultPoint12, resultPoint11, resultPoint, i3, transitions4);
        } else {
            ResultPoint correctTopRight = correctTopRight(resultPoint12, resultPoint11, resultPoint13, resultPoint, Math.min(i2, i));
            if (correctTopRight != null) {
                resultPoint = correctTopRight;
            }
            int max = Math.max(transitionsBetween(resultPoint13, resultPoint).getTransitions(), transitionsBetween(resultPoint11, resultPoint).getTransitions()) + 1;
            if ((max & 1) == 1) {
                max++;
            }
            int i4 = max;
            bitMatrix = sampleGrid(this.image, resultPoint13, resultPoint12, resultPoint11, resultPoint, i4, i4);
            resultPoint2 = resultPoint13;
        }
        return new DetectorResult(bitMatrix, new ResultPoint[]{resultPoint2, resultPoint12, resultPoint11, resultPoint});
    }
}
