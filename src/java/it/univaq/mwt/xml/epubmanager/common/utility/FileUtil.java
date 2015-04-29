package it.univaq.mwt.xml.epubmanager.common.utility;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Zilfio
 */
public class FileUtil {
    
    /**
     * 
     * @param inputStream
     * @return 
     * @throws BusinessException 
     */
    public static String fileToString(InputStream inputStream) throws BusinessException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }     
            reader.close();
            return out.toString();
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        }
    }
    
    /**
     * Metodo che salva una risorsa da URL a file
     * @param url indirizzo dove è situata la risorsa
     */
    public static void saveResourceURLToFile(URL url) {
        try {
            url = new URL("http://blog-assets.newrelic.com/wp-content/uploads/javalogo.png");
            FileUtils.copyURLToFile(url, new File("D:/" + url.getPath()));
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }
    }
    
}
