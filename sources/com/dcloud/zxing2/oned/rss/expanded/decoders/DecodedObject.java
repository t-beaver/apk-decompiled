package com.dcloud.zxing2.oned.rss.expanded.decoders;

abstract class DecodedObject {
    private final int newPosition;

    DecodedObject(int i) {
        this.newPosition = i;
    }

    /* access modifiers changed from: package-private */
    public final int getNewPosition() {
        return this.newPosition;
    }
}
