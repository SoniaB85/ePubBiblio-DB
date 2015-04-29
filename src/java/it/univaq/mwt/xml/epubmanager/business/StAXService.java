package it.univaq.mwt.xml.epubmanager.business;

import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import java.util.List;

/**
 *
 * @author zilfio
 */
public interface StAXService {
    
    public void createTocNcxXMLFile (Metadata metadata, List<EpubXhtml> xhtmls, String path) throws BusinessException;
    
    public void createContentXMLFile(Metadata metadata, List<EpubXhtml> xhtmls, List<EpubCss> csses, List<EpubImage> images, String path) throws BusinessException;
    
    public void createContainerXMLFile (String path) throws BusinessException;
    
}
