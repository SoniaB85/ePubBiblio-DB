package it.univaq.mwt.xml.epubmanager.business.impl;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.StAXService;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import it.univaq.mwt.xml.epubmanager.common.utility.XMLUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.stereotype.Service;

@Service
public class StAXServiceImpl implements StAXService {

    @Override
    public void createTocNcxXMLFile(Metadata metadata, List<EpubXhtml> xhtmls, String path) throws BusinessException {
        Writer output = null;
        XMLStreamWriter xmlwriter = null;
        try {
            //è necessario che l'encoding dello stream usato per l'output
            //coincida con quello specificato all'apertura del documento
            //con il metodo writeStartdocument!
            output = new OutputStreamWriter(new FileOutputStream(path + "toc.ncx"), "UTF-8");
            XMLOutputFactory f = XMLOutputFactory.newInstance();
            xmlwriter = f.createXMLStreamWriter(output);

            //apriamo il documento, dichiarandone la versione di XML e l'encoding
            xmlwriter.writeStartDocument("UTF-8", "1.0");
            //la dichiarazione doctype viene trascritta direttamente nell'output
            xmlwriter.writeDTD("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">");

            xmlwriter.writeStartElement("ncx");
            xmlwriter.writeAttribute("xmlns", "http://www.daisy.org/z3986/2005/ncx/");
            xmlwriter.writeAttribute("version", "2005-1");

            xmlwriter.writeStartElement("head");
            xmlwriter.writeStartElement("meta");
            xmlwriter.writeAttribute("name", "dtb:uid");
            xmlwriter.writeAttribute("content", metadata.getIdentifier());
            xmlwriter.writeEndElement();

            xmlwriter.writeStartElement("meta");
            xmlwriter.writeAttribute("name", "dtb:depth");
            xmlwriter.writeAttribute("content", "1");
            xmlwriter.writeEndElement();

            xmlwriter.writeStartElement("meta");
            xmlwriter.writeAttribute("name", "dtb:totalPageCount");
            xmlwriter.writeAttribute("content", "0");
            xmlwriter.writeEndElement();

            xmlwriter.writeStartElement("meta");
            xmlwriter.writeAttribute("name", "dtb:maxPageNumber");
            xmlwriter.writeAttribute("content", "0");
            xmlwriter.writeEndElement();

            xmlwriter.writeEndElement();

            xmlwriter.writeStartElement("docTitle");
            xmlwriter.writeStartElement("text");
            xmlwriter.writeCharacters(metadata.getTitle());
            xmlwriter.writeEndElement();
            xmlwriter.writeEndElement();

            xmlwriter.writeStartElement("navMap");

            List<EpubXhtml> epubXhtmls = xhtmls;
            List<EpubXhtml> epubXhtmlsOrd = epubXhtmls;

            // Collections.sort(epubXhtmlsOrd, Collections.reverseOrder());
            Collections.sort(epubXhtmlsOrd);
            int playOrder = 1;
            for (EpubXhtml epubXhtml : epubXhtmlsOrd) {
                xmlwriter.writeStartElement("navPoint");
                xmlwriter.writeAttribute("id", "navPoint-" + epubXhtml.getId());
                
                xmlwriter.writeAttribute("playOrder", Integer.toString(playOrder));
                playOrder++;
                xmlwriter.writeStartElement("navLabel");
                xmlwriter.writeStartElement("text");
                xmlwriter.writeCharacters(epubXhtml.getName());
                xmlwriter.writeEndElement();
                xmlwriter.writeEndElement();
                xmlwriter.writeStartElement("content");
                xmlwriter.writeAttribute("src", epubXhtml.getPath());
                xmlwriter.writeEndElement();
               
                //Controllo se nel file ci sono ancore per definire i navPoint delle ancore
                List<String> ancore = XMLUtil.trovaAncore(epubXhtml.getPath(),String.valueOf(metadata.getIdentifier()));
                Iterator<String> i = ancore.iterator();
                while (i.hasNext()) {
                    String ancora = i.next();
                    xmlwriter.writeStartElement("navPoint");
                    xmlwriter.writeAttribute("id", "navPoint-" + playOrder);
                    
                    xmlwriter.writeAttribute("playOrder", Integer.toString(playOrder));
                    playOrder++;
                    xmlwriter.writeStartElement("navLabel");
                    xmlwriter.writeStartElement("text");
                    xmlwriter.writeCharacters(ancora);
                    xmlwriter.writeEndElement();
                    xmlwriter.writeEndElement();
                    xmlwriter.writeStartElement("content");
                    xmlwriter.writeAttribute("src", epubXhtml.getPath());
                    xmlwriter.writeEndElement();
                    xmlwriter.writeEndElement();
                }

                xmlwriter.writeEndElement();
            }

            xmlwriter.writeEndElement();

            xmlwriter.writeEndElement();

            //chiudiamo il documento
            xmlwriter.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new BusinessException("XMLStreamException", ex);
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        } finally {
            try {
                if (xmlwriter != null) {
                    xmlwriter.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                throw new BusinessException("IOException", ex);
            } catch (XMLStreamException ex) {
                throw new BusinessException("XMLStreamException", ex);
            }
        }
    }

    @Override
    public void createContentXMLFile(Metadata metadata, List<EpubXhtml> xhtmls, List<EpubCss> csses, List<EpubImage> images, String path) throws BusinessException {
        Writer output = null;
        XMLStreamWriter xmlwriter = null;
        try {
            //è necessario che l'encoding dello stream usato per l'output
            //coincida con quello specificato all'apertura del documento
            //con il metodo writeStartdocument!
            output = new OutputStreamWriter(new FileOutputStream(path + "content.opf"), "UTF-8");
            XMLOutputFactory f = XMLOutputFactory.newInstance();
            xmlwriter = f.createXMLStreamWriter(output);

            //apriamo il documento, dichiarandone la versione di XML e l'encoding
            xmlwriter.writeStartDocument("UTF-8", "1.0");

            xmlwriter.writeStartElement("package");
            xmlwriter.writeAttribute("xmlns", "http://www.idpf.org/2007/opf");
            xmlwriter.writeAttribute("unique-identifier", "isbn_" + metadata.getIdentifier());
            xmlwriter.writeAttribute("version", "2.0");

            // metadata
            xmlwriter.writeStartElement("metadata");
            xmlwriter.writeAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
            xmlwriter.writeAttribute("xmlns:dcterms", "http://purl.org/dc/terms/");
            xmlwriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            xmlwriter.writeAttribute("xmlns:opf", "http://www.idpf.org/2007/opf");
            // metadata: title
            xmlwriter.writeStartElement("dc:title");
            xmlwriter.writeCharacters(metadata.getTitle());
            xmlwriter.writeEndElement();
            // metadata: language
            xmlwriter.writeStartElement("dc:language");
            xmlwriter.writeAttribute("xsi:type", "dcterms:RFC3066");
            xmlwriter.writeCharacters(metadata.getLanguage());
            xmlwriter.writeEndElement();
            // metadata: identifier
            xmlwriter.writeStartElement("dc:identifier");
            xmlwriter.writeAttribute("id", "isbn_" + metadata.getIdentifier());
            xmlwriter.writeAttribute("opf:scheme", "ISBN");
            xmlwriter.writeCharacters(metadata.getIdentifier());
            xmlwriter.writeEndElement();

            xmlwriter.writeEndElement();

            // manifest
            xmlwriter.writeStartElement("manifest");

            // manifest: file toc.ncx
            xmlwriter.writeStartElement("item");
            xmlwriter.writeAttribute("id", "ncx");
            xmlwriter.writeAttribute("href", "toc.ncx");
            xmlwriter.writeAttribute("media-type", "application/x-dtbncx+xml");
            xmlwriter.writeEndElement();

            // manifest: files css
            List<EpubCss> epubCsss = csses;
            for (EpubCss epubCss : epubCsss) {
                xmlwriter.writeStartElement("item");
                xmlwriter.writeAttribute("id", "css_" + epubCss.getId());
                xmlwriter.writeAttribute("href", epubCss.getPath());
                xmlwriter.writeAttribute("media-type", epubCss.getContentType());
                xmlwriter.writeEndElement();
            }
            // manifest: files image
            List<EpubImage> epubImages = images;
            for (EpubImage epubImage : epubImages) {
                xmlwriter.writeStartElement("item");
                xmlwriter.writeAttribute("id", "img_" + epubImage.getId());
                xmlwriter.writeAttribute("href", epubImage.getPath());
                xmlwriter.writeAttribute("media-type", epubImage.getContentType());
                xmlwriter.writeEndElement();
            }
            // manifest: files xhtml
            List<EpubXhtml> epubXhtmls = xhtmls;
            for (EpubXhtml epubXhtml : epubXhtmls) {
                xmlwriter.writeStartElement("item");
                xmlwriter.writeAttribute("id", "xhtml_" + epubXhtml.getId());
                xmlwriter.writeAttribute("href", epubXhtml.getPath());
                xmlwriter.writeAttribute("media-type", epubXhtml.getContentType());
                xmlwriter.writeEndElement();
            }

            xmlwriter.writeEndElement();

            // spine
            xmlwriter.writeStartElement("spine");
            xmlwriter.writeAttribute("toc", "ncx");

            for (EpubXhtml epubXhtml : epubXhtmls) {
                xmlwriter.writeStartElement("itemref");
                xmlwriter.writeAttribute("idref", "xhtml_" + epubXhtml.getId());
                xmlwriter.writeEndElement();
            }

            xmlwriter.writeEndElement();

            // guide
            // mi scorro la lista per vedere se ho qualche oggetto con type non vuoto
            List<EpubXhtml> guide = new ArrayList<EpubXhtml>();
            for (EpubXhtml epubXhtml : epubXhtmls) {
                if (epubXhtml.getType() != null) {
                    guide.add(epubXhtml);
                }
            }

            System.out.println("lunghezza guide: " + guide.size());

            if (!guide.isEmpty()) {
                xmlwriter.writeStartElement("guide");
                for (EpubXhtml ex : guide) {
                    xmlwriter.writeStartElement("reference");
                    xmlwriter.writeAttribute("type", ex.getType());
                    xmlwriter.writeAttribute("title", ex.getName());
                    xmlwriter.writeAttribute("href", ex.getPath());
                    xmlwriter.writeEndElement();
                }
                xmlwriter.writeEndElement();
            }

            xmlwriter.writeEndElement();

            //chiudiamo il documento
            xmlwriter.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new BusinessException("XMLStreamException", ex);
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        } finally {
            try {
                if (xmlwriter != null) {
                    xmlwriter.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                throw new BusinessException("IOException", ex);
            } catch (XMLStreamException ex) {
                throw new BusinessException("XMLStreamException", ex);
            }
        }
    }

    @Override
    public void createContainerXMLFile(String path) throws BusinessException {
        Writer output = null;
        XMLStreamWriter xmlwriter = null;
        try {
            //è necessario che l'encoding dello stream usato per l'output
            //coincida con quello specificato all'apertura del documento
            //con il metodo writeStartdocument!
            output = new OutputStreamWriter(new FileOutputStream(path + "container.xml"), "UTF-8");
            XMLOutputFactory f = XMLOutputFactory.newInstance();
            xmlwriter = f.createXMLStreamWriter(output);

            //apriamo il documento, dichiarandone la versione di XML e l'encoding
            xmlwriter.writeStartDocument("UTF-8", "1.0");

            xmlwriter.writeStartElement("container");
            xmlwriter.writeAttribute("version", "1.0");
            xmlwriter.writeAttribute("xmlns", "urn:oasis:names:tc:opendocument:xmlns:container");
            xmlwriter.writeStartElement("rootfiles");
            xmlwriter.writeStartElement("rootfile");
            xmlwriter.writeAttribute("full-path", "content.opf");
            xmlwriter.writeAttribute("media-type", "application/oebps-package+xml");

            xmlwriter.writeEndElement();
            xmlwriter.writeEndElement();
            xmlwriter.writeEndElement();

            //chiudiamo il documento
            xmlwriter.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new BusinessException("XMLStreamException", ex);
        } catch (IOException ex) {
            throw new BusinessException("IOException", ex);
        } finally {
            try {
                if (xmlwriter != null) {
                    xmlwriter.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException ex) {
                throw new BusinessException("IOException", ex);
            } catch (XMLStreamException ex) {
                throw new BusinessException("XMLStreamException", ex);
            }
        }
    }
}
