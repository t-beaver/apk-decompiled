package com.nostra13.dcloudimageloader.core.assist;

public class FailReason {
    private final Throwable cause;
    private final FailType type;

    public enum FailType {
        IO_ERROR,
        DECODING_ERROR,
        NETWORK_DENIED,
        OUT_OF_MEMORY,
        UNKNOWN
    }

    public FailReason(FailType failType, Throwable th) {
        this.type = failType;
        this.cause = th;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public FailType getType() {
        return this.type;
    }
}
