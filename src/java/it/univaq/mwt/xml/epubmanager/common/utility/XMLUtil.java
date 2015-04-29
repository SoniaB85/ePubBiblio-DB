package it.univaq.mwt.xml.epubmanager.common.utility;

import it.univaq.mwt.xml.epubmanager.business.EPubService;
import it.univaq.mwt.xml.epubmanager.business.MyErrorHandler;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;




public class XMLUtil {
    
   private static final String UPLOAD_DIR = Config.getSetting("uploadDirectory");
    @Autowired
    private EPubService service;

    /**
     * Carica un documento XML
     *
     * @param path percorso del file
     * @return Document che rappresenta il documento
     */
    public static Document loadDocument(String path) {
        // error handler
        MyErrorHandler h = new MyErrorHandler();
        // creo la factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // setX (che tipo di document builder ci deve produrre)

        dbf.setNamespaceAware(false);
        // se metti a true la valiazione il parser fa la validazione (prende la dtd associata e valida l'xml)
        dbf.setValidating(false);
        // in un documento xml gli spazi sono significativi!!!
        dbf.setIgnoringElementContentWhitespace(true);
        try {
            // dalla factory creo il DocumentBuilder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // setto error handler nel DocumentBuilder
            db.setErrorHandler(h);
            return db.parse(new File(path));
        } catch (ParserConfigurationException ex) {
            System.err.println("Impossibile creare il parser XML!");
        } catch (SAXException ex) {
            // viene gestita dall'error handler
        } catch (IOException ex) {
            System.err.println("Errore I/O: " + ex.getMessage());
        }

        return null;
    }

    public static Document createDocument() {
        // creo la factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // setX (che tipo di document builder ci deve produrre)

        dbf.setNamespaceAware(false);
        // se metti a true la valiazione il parser fa la validazione (prende la dtd associata e valida l'xml)
        dbf.setValidating(false);
        try {
            // dalla factory creo il DocumentBuilder
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException ex) {
            System.err.println("Impossibile creare il parser XML!");
        }

        return null;
    }

    /**
     * Metodo per salvare un documento
     *
     * @param d documento da salvare
     * @param w writer generico
     */
    public static void saveDocument(Document d, Writer w) {
        // prendo l'interfaccia dell'implementazione
        DOMImplementation i = d.getImplementation();
        // cast per prendere un'altra interfaccia
        DOMImplementationLS ils = (DOMImplementationLS) i;

        // LSOutput è lo stream di output che viene utilizzato per scrivere
        LSOutput lso = ils.createLSOutput();
        LSSerializer lss = ils.createLSSerializer();
        // configuro l'output
        lso.setCharacterStream(w);
        // configuro l'encoding
        lso.setEncoding("ISO-8859-1");
        // setto il pretty-printing (indentazioni)
        lss.getDomConfig().setParameter("format-pretty-print", true);

        lss.write(d, lso);
    }

    /**
     * Metodo che permette di validare un file XHTML
     *
     * @param file file da validare
     * @param bValidate booleano che indica se abilitare la validazione
     * @return ritorna false se non ho avuto errori, true altrimenti
     */
    public static boolean validateXhtml(File file, boolean bValidate) {
        // error handler
        MyErrorHandler h = new MyErrorHandler();
        // creo la factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // setX (che tipo di document builder ci deve produrre)
        dbf.setNamespaceAware(false);
        // se metti a true la validazione il parser fa la validazione (prende la dtd associata e valida l'xml)
        dbf.setValidating(bValidate);
        try {
            // configurazione features (features di xerces: http://xerces.apache.org/xerces-j/features.html)
            dbf.setFeature("http://xml.org/sax/features/namespaces", false);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            // caricamento della dtd esterna (metti a false)
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // dalla factory creo il DocumentBuilder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // setto error handler nel DocumentBuilder
            db.setErrorHandler(h);
            db.parse(file);
        } catch (ParserConfigurationException ex) {
            System.err.println("Impossibile creare il parser XML!");
        } catch (SAXException ex) {
            // viene gestita dall'error handler
        } catch (IOException ex) {
            System.err.println("Errore I/O: " + ex.getMessage());
        }

        if (bValidate) {
            if (h.hasErrors()) {
                System.err.println("* Il documento NON è valido!");
            } else {
                System.err.println("* Il documento è valido!");
            }
        } else {
            if (h.hasErrors()) {
                System.err.println("* Il documento NON è ben formato!");
            } else {
                System.err.println("* Il documento è ben formato!");
            }
        }

        // ritorno lo stato di errore del mio handler
        return h.hasErrors();
    }

 /**
  * Questo metodo cerca le ancore di un file xhtml per volta Una volta
  * trovate inserisce in un ArrayList il valore dell'attributo href
  * naturalmente solo se quest'attributo non contiene "*.html"
  * @param path
  * @param epub
  * @return 
  */
    public static List<String> trovaAncore(String path, String dir) {
       
        File xhtml = new File(UPLOAD_DIR + File.separator + dir + File.separator + path);
        System.out.println(UPLOAD_DIR + File.separator + dir + File.separator + path);
        List<String> ancore = new ArrayList<String>();
        /*DOM*/
        DocumentBuilderFactory docF = DocumentBuilderFactory.newInstance();
        docF.setNamespaceAware(false);
        docF.setValidating(false);
        DocumentBuilder docB = null;
        NodeList body = null;
        try {
            //Do not perform namespace processing.  
            docF.setFeature("http://xml.org/sax/features/namespaces", false);
            //Do not report validity errors.  
            docF.setFeature("http://xml.org/sax/features/validation", false);
            //	Build the grammar but do not use the default attributes and attribute types information it contains.   
            docF.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            //	Ignore the external DTD completely.  
            docF.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            docB = docF.newDocumentBuilder();

            //parsiamo direttamente il file che è sicuramente xhtml
            Document doc = docB.parse(xhtml);

            body = doc.getElementsByTagName("body");
            //Può avere al massimo 1 body
            Node x = body.item(0);
            //Se non c'è il body non ci sono parole da contare
            if (x != null) {
                NodeList listAncore = doc.getElementsByTagName("a");

                if (listAncore.getLength() != 0) {
                    System.out.println("Cerco le ancore giuste");
                    for (int i = 0; i < listAncore.getLength(); i++) {
                        //Su ciascun nodo trovato leggiamo il contenuto con namespace
                        Element element = (Element) listAncore.item(i);
                        String attribute = element.getAttribute("href");
                        //Se l'attributo contiene image si tratta di un'immagine ed aumentiamo il contatore d'immagini
                        if (!attribute.contains(".html") || !attribute.contains(".html")) {
                            if(!attribute.contains("http:"))
                            ancore.add(attribute);
                        }
                    }
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       return ancore;  
    }

  }  
    

