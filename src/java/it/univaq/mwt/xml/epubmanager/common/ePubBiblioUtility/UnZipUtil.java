package it.univaq.mwt.xml.epubmanager.common.ePubBiblioUtility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class UnZipUtil {
    
   
    /**
     * Questo metodo serve per unzippare gli ePub e restituisce la lista dei file unzippati
     * @param zip indica la posizione in cui si trova il file ePub zippato
     * @param destDirectory indica dove andare a salvare i file unzippati
     */
    public static List<File> unzipArchive (String zip, String destDirectory)throws IOException {
        
        int BUFFER =2048;
        List lista = new ArrayList();
        File file = new File(zip);
        File unzipDestDir = new File(destDirectory);
        List<File> listaFile = new ArrayList<File>();
        //crea la cartella
        unzipDestDir.mkdir();
        
        ZipFile zipFile;
        //apre il file per leggerlo
        zipFile = new ZipFile(file, ZipFile.OPEN_READ );
        
        Enumeration zipFileEntries = zipFile.entries();
        
        //processiamo tutte le entrate
        while (zipFileEntries.hasMoreElements()){
            
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            
            File destFile = new File (unzipDestDir, currentEntry);
            listaFile.add(destFile);
            if (currentEntry.endsWith(".zip")) {
                lista.add(destFile.getAbsolutePath());
                               
            }
            
            File destParent = destFile.getParentFile();
            
            destParent.mkdirs();
            
            try {
                if (!entry.isDirectory()){
                    BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    
                    byte data[] = new byte[BUFFER];
                    
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                    
                    while ((currentByte= is.read(data, 0, BUFFER)) != -1){
                        dest.write(data,0, currentByte);
                        
                    }
                    dest.flush();;
                    dest.close();
                    is.close();
                           
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                
            }
        }
        
        zipFile.close();
        
        for(Iterator iter = lista.iterator(); iter.hasNext();){
          
            String zName = (String) iter.next();
            unzipArchive(zName, destDirectory + File.separatorChar + zName.substring(0, zName.lastIndexOf(".zip")));
        }
       return listaFile;
      
    }
}