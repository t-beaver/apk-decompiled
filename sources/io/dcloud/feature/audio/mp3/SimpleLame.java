package io.dcloud.feature.audio.mp3;

public class SimpleLame {
    static {
        System.loadLibrary("lamemp3");
    }

    public static native void close();

    public static native int encode(short[] sArr, short[] sArr2, int i, byte[] bArr);

    public static native int flush(byte[] bArr);

    public static native void init(int i, int i2, int i3, int i4, int i5);
}
