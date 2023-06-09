package com.dcloud.zxing2.aztec.encoder;

import com.dcloud.zxing2.common.BitArray;

abstract class Token {
    static final Token EMPTY = new SimpleToken((Token) null, 0, 0);
    private final Token previous;

    Token(Token token) {
        this.previous = token;
    }

    /* access modifiers changed from: package-private */
    public final Token add(int i, int i2) {
        return new SimpleToken(this, i, i2);
    }

    /* access modifiers changed from: package-private */
    public final Token addBinaryShift(int i, int i2) {
        return new BinaryShiftToken(this, i, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void appendTo(BitArray bitArray, byte[] bArr);

    /* access modifiers changed from: package-private */
    public final Token getPrevious() {
        return this.previous;
    }
}
