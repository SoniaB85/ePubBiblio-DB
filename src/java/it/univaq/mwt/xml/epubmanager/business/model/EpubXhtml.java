package it.univaq.mwt.xml.epubmanager.business.model;

import java.io.Serializable;

public class EpubXhtml extends EpubResource implements Serializable, Comparable<EpubXhtml> {
    
    private int index; // posizione (ordinamento)
    private String type; // stringa che indica il tipo di file (serve per la guide)
    
    
    
    public EpubXhtml() {
    }
    
    public EpubXhtml(String id, String name, String path, byte[] file, int index, String contentType, String epub, String type) {
        super(id, name, path, file, contentType, epub);
        this.index = index;
        this.type = type;
    }

    public EpubXhtml(String name, String path, byte[] file, int index, String contentType, String epub, String type) {
        super(name, path, file, contentType, epub);
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "EpubXhtml{" + "id=" + super.getId() + ", name=" + super.getName() + ", path=" + super.getPath() + ", file=" + super.getFile() + ",epub=" + super.getEpub()+'}';
    }

    @Override
    public int compareTo(EpubXhtml o) {
        return (this.index < o.index) ? -1: (this.index > o.index) ? 1:0 ;
        // return this.index - o.index;
    }
}
