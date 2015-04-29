package it.univaq.mwt.xml.epubmanager.business.model;

import java.io.Serializable;

/**
 * I metadata sono tutte le informazioni che ci permettono di descrivere uno specifico eBook 
 * e sono racchiusi all’interno del file con estensione .opf (Open Packaging Format).
 * obbligatori: identifier, title, language
 * opzionali: creator, subject, description, publisher, contributor, date, type, format, source
 * relation, coverage, rights
 * @author Zilfio
 */
public class Metadata implements Serializable{
    private String identifier; // generalmente è il codice ISBN
    private String title;
    private String language;

    public Metadata() {
        
    }
    
    public Metadata(String identifier, String title, String language) {
        this.identifier = identifier;
        this.title = title;
        this.language = language;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}