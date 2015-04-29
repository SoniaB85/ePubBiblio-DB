package it.univaq.mwt.xml.epubmanager.business.model;

import java.io.Serializable;

public class EpubCss extends EpubResource implements Serializable{

    public EpubCss() {
    }
    
    public EpubCss(String id, String name, String path, byte[] file, String contentType,String epub) {
        super(id, name, path, file, contentType, epub);
    }

    public EpubCss(String name, String path, byte[] file, String contentType, String epub) {
        super(name, path, file, contentType, epub);
    }

    @Override
    public String toString() {
        return "EpubCss{" + "id=" + super.getId() + ", name=" + super.getName() + ", path=" + super.getPath() + ", file=" + super.getFile() + '}';
    }
}
