package net.lingala.zip4j.headers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.util.InternalZipConstants;
import net.lingala.zip4j.util.Zip4jUtil;

public class HeaderUtil {
    public static String decodeStringWithCharset(byte[] bArr, boolean z, Charset charset) {
        if (charset != null) {
            return new String(bArr, charset);
        }
        if (z) {
            return new String(bArr, InternalZipConstants.CHARSET_UTF_8);
        }
        try {
            return new String(bArr, InternalZipConstants.ZIP_STANDARD_CHARSET_NAME);
        } catch (UnsupportedEncodingException unused) {
            return new String(bArr);
        }
    }

    public static byte[] getBytesFromString(String str, Charset charset) {
        if (charset == null) {
            return str.getBytes(InternalZipConstants.ZIP4J_DEFAULT_CHARSET);
        }
        return str.getBytes(charset);
    }

    public static FileHeader getFileHeader(ZipModel zipModel, String str) throws ZipException {
        FileHeader fileHeaderWithExactMatch = getFileHeaderWithExactMatch(zipModel, str);
        if (fileHeaderWithExactMatch != null) {
            return fileHeaderWithExactMatch;
        }
        String replaceAll = str.replaceAll("\\\\", "/");
        FileHeader fileHeaderWithExactMatch2 = getFileHeaderWithExactMatch(zipModel, replaceAll);
        return fileHeaderWithExactMatch2 == null ? getFileHeaderWithExactMatch(zipModel, replaceAll.replaceAll("/", "\\\\")) : fileHeaderWithExactMatch2;
    }

    private static FileHeader getFileHeaderWithExactMatch(ZipModel zipModel, String str) throws ZipException {
        if (zipModel == null) {
            throw new ZipException("zip model is null, cannot determine file header with exact match for fileName: " + str);
        } else if (!Zip4jUtil.isStringNotNullAndNotEmpty(str)) {
            throw new ZipException("file name is null, cannot determine file header with exact match for fileName: " + str);
        } else if (zipModel.getCentralDirectory() == null) {
            throw new ZipException("central directory is null, cannot determine file header with exact match for fileName: " + str);
        } else if (zipModel.getCentralDirectory().getFileHeaders() == null) {
            throw new ZipException("file Headers are null, cannot determine file header with exact match for fileName: " + str);
        } else if (zipModel.getCentralDirectory().getFileHeaders().size() == 0) {
            return null;
        } else {
            for (FileHeader next : zipModel.getCentralDirectory().getFileHeaders()) {
                String fileName = next.getFileName();
                if (Zip4jUtil.isStringNotNullAndNotEmpty(fileName) && str.equalsIgnoreCase(fileName)) {
                    return next;
                }
            }
            return null;
        }
    }

    public static List<FileHeader> getFileHeadersUnderDirectory(List<FileHeader> list, FileHeader fileHeader) {
        if (!fileHeader.isDirectory()) {
            return Collections.emptyList();
        }
        return (List) list.stream().filter(new Predicate() {
            public final boolean test(Object obj) {
                return ((FileHeader) obj).getFileName().startsWith(FileHeader.this.getFileName());
            }
        }).collect(Collectors.toList());
    }

    public static long getOffsetStartOfCentralDirectory(ZipModel zipModel) {
        if (zipModel.isZip64Format()) {
            return zipModel.getZip64EndOfCentralDirectoryRecord().getOffsetStartCentralDirectoryWRTStartDiskNumber();
        }
        return zipModel.getEndOfCentralDirectoryRecord().getOffsetOfStartOfCentralDirectory();
    }

    public static long getTotalUncompressedSizeOfAllFileHeaders(List<FileHeader> list) {
        long j;
        long j2 = 0;
        for (FileHeader next : list) {
            if (next.getZip64ExtendedInfo() == null || next.getZip64ExtendedInfo().getUncompressedSize() <= 0) {
                j = next.getUncompressedSize();
            } else {
                j = next.getZip64ExtendedInfo().getUncompressedSize();
            }
            j2 += j;
        }
        return j2;
    }
}
