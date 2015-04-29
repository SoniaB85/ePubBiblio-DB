package it.univaq.mwt.xml.epubmanager.common.utility;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Zilfio
 */
public class EpubUtil {
    
    public static int generaInteri() {
        AtomicInteger ai = new AtomicInteger();
        return ai.incrementAndGet();
    }
    
    public static void addMimeTypeFile(ZipOutputStream zos) throws IOException{
        // creo una nuova ZipEntry con input il nome del file da creare
        ZipEntry zipEntry = new ZipEntry("mimetype");
        // aggiungo l'entry allo ZipOutputStream
        zos.putNextEntry(zipEntry);
        // decodifico la stringa come un array di byte e levo gli spazi bianchi (con trim)
        byte[] entryBytes = "application/epub+zip".trim().getBytes("US-ASCII");
        // setto la size dell'entry
        zipEntry.setSize(entryBytes.length);
        // il file mimetype non deve essere compresso!
        zipEntry.setMethod(ZipEntry.STORED);
        // checksum crc32
        Checksum checksum = new CRC32();
        checksum.update(entryBytes, 0, entryBytes.length);
        zipEntry.setCrc(checksum.getValue());
        // scrivo
        zos.write(entryBytes,0,entryBytes.length);
        zos.closeEntry();
    }
    
}
