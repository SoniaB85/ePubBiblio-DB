package it.univaq.mwt.xml.epubmanager.common.utility;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Zilfio
 */
public class ZipUtil {

    public static void createArchiveFile(String folder, String id) throws BusinessException {
        try {
            File directoryToZip = new File(folder);
            
            List<File> fileList = new ArrayList<File>();
            System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());
            getAllFiles(directoryToZip, fileList);
            System.out.println("---Creating zip file");
            writeZipFile(directoryToZip, fileList, id);
            System.out.println("---Done");
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        }
    }

    public static void getAllFiles(File dir, List<File> fileList) throws BusinessException {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            throw new BusinessException("IOException", e);
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, String id) throws BusinessException {

        try {
            FileOutputStream fos = new FileOutputStream(directoryToZip + File.separator + id + ".epub");
            ZipOutputStream zos = new ZipOutputStream(fos);

            // This sets the compression level to STORED, ie, uncompressed
            // zos.setLevel(ZipOutputStream.STORED);
            
            // mi assicuro che il mimetype lo metto nell'archivio per primo (lo genero in questo passo)
            EpubUtil.addMimeTypeFile(zos);
            
            // metto gli altri file nell'archivio
            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new BusinessException("FileNotFoundException", e);
        } catch (IOException e) {
            throw new BusinessException("IOException", e);
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws BusinessException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // we want the zipEntry's path to be a relative path that is relative
            // to the directory being zipped, so chop off the rest of the path
            String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                    file.getCanonicalPath().length());
            System.out.println("Writing '" + zipFilePath + "' to zip file");
            ZipEntry zipEntry = new ZipEntry(zipFilePath);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }   zos.closeEntry();
        fis.close();
        } catch (FileNotFoundException ex) {
            throw new BusinessException("FileNotFoundException", ex);
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                throw new BusinessException("IOException", ex);
            }
        }
    }

    

}
