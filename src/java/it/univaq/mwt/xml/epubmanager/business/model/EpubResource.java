package it.univaq.mwt.xml.epubmanager.business.model;

/**
 *
 * @author Zilfio
 */
public class EpubResource {
    private String id;
    private String name;
    private String path;
    private byte[] file;
    private String contentType;
    private String epub;

    public EpubResource() {
    }

    public EpubResource(String id, String name, String path, byte[] file, String contentType, String epub) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.file = file;
        this.contentType = contentType;
        this.epub = epub;
    }

    public EpubResource(String name, String path, byte[] file, String contentType, String epub) {
        this.name = name;
        this.path = path;
        this.file = file;
        this.contentType = contentType;
        this.epub = epub;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }  

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getEpub() {
        return epub;
    }

    public void setEpub(String epub) {
        this.epub = epub;
    }
    
}
