package com.dcloud.zxing2;

public final class WriterException extends Exception {
    public WriterException() {
    }

    public WriterException(String str) {
        super(str);
    }

    public WriterException(Throwable th) {
        super(th);
    }
}
