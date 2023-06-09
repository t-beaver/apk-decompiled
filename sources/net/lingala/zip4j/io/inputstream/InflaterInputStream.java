package net.lingala.zip4j.io.inputstream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class InflaterInputStream extends DecompressedInputStream {
    private byte[] buff;
    private Inflater inflater = new Inflater(true);
    private int len;
    private byte[] singleByteBuffer = new byte[1];

    public InflaterInputStream(CipherInputStream cipherInputStream, int i) {
        super(cipherInputStream);
        this.buff = new byte[i];
    }

    private void fill() throws IOException {
        byte[] bArr = this.buff;
        int read = super.read(bArr, 0, bArr.length);
        this.len = read;
        if (read != -1) {
            this.inflater.setInput(this.buff, 0, read);
            return;
        }
        throw new EOFException("Unexpected end of input stream");
    }

    public void close() throws IOException {
        Inflater inflater2 = this.inflater;
        if (inflater2 != null) {
            inflater2.end();
        }
        super.close();
    }

    public void endOfEntryReached(InputStream inputStream) throws IOException {
        Inflater inflater2 = this.inflater;
        if (inflater2 != null) {
            inflater2.end();
            this.inflater = null;
        }
        super.endOfEntryReached(inputStream);
    }

    public void pushBackInputStreamIfNecessary(PushbackInputStream pushbackInputStream) throws IOException {
        int remaining = this.inflater.getRemaining();
        if (remaining > 0) {
            pushbackInputStream.unread(getLastReadRawDataCache(), this.len - remaining, remaining);
        }
    }

    public int read() throws IOException {
        if (read(this.singleByteBuffer) == -1) {
            return -1;
        }
        return this.singleByteBuffer[0];
    }

    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        while (true) {
            try {
                int inflate = this.inflater.inflate(bArr, i, i2);
                if (inflate != 0) {
                    return inflate;
                }
                if (this.inflater.finished()) {
                    return -1;
                }
                if (this.inflater.needsDictionary()) {
                    return -1;
                }
                if (this.inflater.needsInput()) {
                    fill();
                }
            } catch (DataFormatException e) {
                throw new IOException(e);
            }
        }
    }
}
