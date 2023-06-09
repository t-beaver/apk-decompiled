package net.lingala.zip4j.model;

import net.lingala.zip4j.headers.HeaderSignature;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.AesVersion;
import net.lingala.zip4j.model.enums.CompressionMethod;

public class AESExtraDataRecord extends ZipHeader {
    private AesKeyStrength aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256;
    private AesVersion aesVersion = AesVersion.TWO;
    private CompressionMethod compressionMethod = CompressionMethod.DEFLATE;
    private int dataSize = 7;
    private String vendorID = "AE";

    public AESExtraDataRecord() {
        setSignature(HeaderSignature.AES_EXTRA_DATA_RECORD);
    }

    public AesKeyStrength getAesKeyStrength() {
        return this.aesKeyStrength;
    }

    public AesVersion getAesVersion() {
        return this.aesVersion;
    }

    public CompressionMethod getCompressionMethod() {
        return this.compressionMethod;
    }

    public int getDataSize() {
        return this.dataSize;
    }

    public String getVendorID() {
        return this.vendorID;
    }

    public void setAesKeyStrength(AesKeyStrength aesKeyStrength2) {
        this.aesKeyStrength = aesKeyStrength2;
    }

    public void setAesVersion(AesVersion aesVersion2) {
        this.aesVersion = aesVersion2;
    }

    public void setCompressionMethod(CompressionMethod compressionMethod2) {
        this.compressionMethod = compressionMethod2;
    }

    public void setDataSize(int i) {
        this.dataSize = i;
    }

    public void setVendorID(String str) {
        this.vendorID = str;
    }
}
