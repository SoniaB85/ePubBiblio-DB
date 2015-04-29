package it.univaq.mwt.xml.epubmanager.common.utility;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import java.io.IOException;
import java.util.Properties;

public final class Config {

    private static final Properties properties = new Properties();

    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            properties.load(loader.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new BusinessException("Errore I/O", e);
        }
    }

    public static String getSetting(String key) {
        return properties.getProperty(key);
    }
}
