package it.univaq.mwt.xml.epubmanager.business.impl.ePubBiblio;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.MyErrorHandler;
import it.univaq.mwt.xml.epubmanager.business.RequestGrid;
import it.univaq.mwt.xml.epubmanager.business.ResponseGrid;
import it.univaq.mwt.xml.epubmanager.business.ePubBiblio.EPubBiblioService;
import it.univaq.mwt.xml.epubmanager.business.ePubBiblio.NamespaceContextHandler;
import it.univaq.mwt.xml.epubmanager.business.model.ePubBiblio.EPub;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class EPubBiblioServiceImpl implements EPubBiblioService {

    @Override
    public ResponseGrid<EPub> findAllEpubsPaginated(RequestGrid requestGrid, File f) throws BusinessException {
        List<EPub> epubList = new ArrayList<EPub>();

        XPathFactory xf = XPathFactory.newInstance();
        XPath x = xf.newXPath();
        NamespaceContextHandler nch = new NamespaceContextHandler();
        nch.addBinding("n", "http://it.univaq.mwt.xml/epubmanager");
        x.setNamespaceContext(nch);
        //carico il file
        InputSource file;
        try {
            file = new InputSource(new FileReader(f));
            if (file == null) {
                System.err.println("File inesistente!");
            } else {
                try {
                    Object children = x.evaluate("//n:ePubList/*", file, XPathConstants.NODESET);
                    NodeList epubs = (NodeList) children;
                    for (int j = 0; j < epubs.getLength(); j++) {
                        Node epub = epubs.item(j);
                        //Mi prendo i valori che mi interessano per ogni nodo <epub>
                        Object idEpub = x.evaluate("@id", epub, XPathConstants.STRING);
                        String iid = (String) idEpub;
                        int id = Integer.parseInt(iid);
                        System.out.println("ID: " + id);
                        Object t = x.evaluate("n:titolo/text()", epub, XPathConstants.STRING);
                        String titolo = (String) t;
                        System.out.println("TITOLO: " + t);
                        Object c = x.evaluate("n:autore/text()", epub, XPathConstants.STRING);
                        String autore = (String) c;
                        System.out.println("AUTORE: " + c);
                        Object l = x.evaluate("n:lingua/text()", epub, XPathConstants.STRING);
                        String lingua = (String) l;
                        System.out.println("LINGUA: " + l);
                        Object p = x.evaluate("n:editore/text()", epub, XPathConstants.STRING);
                        String editore = (String) p;
                        System.out.println("EDITORE: " + p);
                        EPub e = new EPub(id, titolo, autore, lingua, editore);
                        epubList.add(e);
                        System.out.println("Attributi Epub: " + e.getId() + "," + e.getTitolo() + "," + e.getAutore() + ","
                                + e.getLingua() + "," + e.getEditore());
                        //PER GESTIRE LA TABELLA:
                        if (requestGrid.getSortCol().equals(iid)) {
                            requestGrid.setSortCol(iid);
                            if (e.getTitolo().equals(requestGrid.getSortCol())) {
                                requestGrid.setSortCol(e.getTitolo());
                                if (e.getAutore().equals(requestGrid.getSortCol())) {
                                    requestGrid.setSortCol(e.getAutore());
                                    if (e.getLingua().equals(requestGrid.getSortCol())) {
                                        requestGrid.setSortCol(e.getLingua());
                                    }
                                }
                            } else {
                                requestGrid.setSortCol(e.getEditore());
                            }
                        }
                    }
                } catch (XPathExpressionException ex) {
                    System.err.println("Errore XPath: " + ex.getMessage());
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File non trovato: " + ex.getMessage());
        }
        long records = epubList.size();

        return new ResponseGrid<EPub>(requestGrid.getsEcho(), records, records, epubList);
    }

    /**
     * Metodo che permette di fare il download del file
     */
    @Override
    public String getEpubInBiblioPath(Long id, File f) throws FileNotFoundException, XPathExpressionException {
        XPathFactory xf = XPathFactory.newInstance();
        XPath x = xf.newXPath();
        NamespaceContextHandler nch = new NamespaceContextHandler();
        nch.addBinding("n", "http://it.univaq.mwt.xml/epubmanager");
        x.setNamespaceContext(nch);

        InputSource file = new InputSource(new FileReader(f));
        Object path = x.evaluate("//n:EPub[@id=" + (id.intValue()) + "]/n:percorsofile/text()", file, XPathConstants.STRING);
        String percorsoFile = (String) path;
        String filePath = percorsoFile + ".epub";
        System.out.println("Il percorso del file da scaricare è: " + percorsoFile);

        return filePath;
    }

    /**
     * Metodo che permette di eliminare un Epub dalla Biblio
     */
    @Override
    public void deleteEpubInBiblioFile(Long id, String biblio) throws FileNotFoundException, XPathExpressionException, IOException, TransformerException, ParserConfigurationException, SAXException {
        MyErrorHandler eh = new MyErrorHandler();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(eh);
        //uso il DOM per fare le operazioni di rimozione dei nodi dal documento XML
        String biblioXml = biblio + "EPubBiblio.xml";
        Document doc = db.parse(biblioXml);

        XPathFactory xf = XPathFactory.newInstance();
        XPath x = xf.newXPath();
        Object filePath = x.evaluate("//EPub[@id=" + (id.intValue()) + "]/percorsofile/text()", doc, XPathConstants.STRING);
        String path = (String) filePath;
        String pathToDelete = path + ".epub";
        System.out.println("Il file da eliminare è:" + pathToDelete);
        File fileToDelete = new File(pathToDelete);
        if(fileToDelete.exists()){
            fileToDelete.delete();
        }
        System.out.println("Il file" + fileToDelete.getName() + " è stato eliminato con successo!");
        String dirFileToDelete = biblio + File.separator + id.toString();
        File dirToDelete = new File(dirFileToDelete);
        delete(dirToDelete);

        //Vado a prendermi il valore dell'autore dell'EPub che cancello
        Object aut = x.evaluate("//EPub[@id=" + (id.intValue()) + "]/autore/text()", doc, XPathConstants.STRING);

        /*Quando cancello un EPub devo aggiornare la frequenza dell'autore ai fini statistici*/
        //Espressione per vedere se il nome del nodo author è uguale al nome dell'autore dell'EPub da eliminare
        String expression = "//lista-statistiche/StatisticheAutori/author[nome=\"" + aut + "\"]";

        NodeList authors;
        try {
            XPathExpression expr = x.compile(expression);
            authors = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            //prendo tutti i nodi con tag author
            NodeList temp = doc.getElementsByTagName("author");

            Element auth = (Element) authors.item(0);
            String nomeAutore = auth.getElementsByTagName("nome").item(0).getTextContent();

            boolean trovato = false;
            //Se la lunghezza dei nodi è 1 vuol dire che sarà sicuramente il nodo giusto su cui intervenire
            if (temp.getLength() == 1) {
                Element a = (Element) temp.item(0);
                int numfr = Integer.parseInt(a.getElementsByTagName("frequenza").item(0).getTextContent());
                numfr--;
                a.getElementsByTagName("frequenza").item(0).setTextContent(Integer.toString(numfr));
                //Se non ci sono altri EPub con quell'autore la frequenza è 0 quindi elimino tutto il nodo
                //Vuol dire anche che non ci sono più ePub nella biblioteca 
                //Quindi per non avere errori quando carico di nuovo un ePub devo creare un elemento author vuoto                  
                if (numfr == 0) {
                    Element nuovoAut = doc.createElement("author");
                    Element nome = doc.createElement("nome");
                    nome.setTextContent("");
                    nuovoAut.appendChild(nome);
                    Element frequenza = doc.createElement("frequenza");
                    frequenza.setTextContent(Integer.toString(0));
                    nuovoAut.appendChild(frequenza);
                    temp.item(0).getParentNode().appendChild(nuovoAut);
                    a.getParentNode().removeChild(a);
                    doc.normalize();
                    doc.getDocumentElement().normalize();
                    System.out.println("Nodo " + a.getNodeName() + " eliminato con successo!");

                }
            } else {
                //metto -1 perchè getLenght parte da 1 mentre i da 0    
                for (int i = 0; i < temp.getLength() - 1 || !trovato; i++) {

                    Element author = (Element) temp.item(i);
                    String newNomeAutore = author.getElementsByTagName("nome").item(0).getTextContent();
                    if (newNomeAutore == nomeAutore) {

                        int z = Integer.parseInt(author.getElementsByTagName("frequenza").item(0).getTextContent());
                        z--;
                        author.getElementsByTagName("frequenza").item(0).setTextContent(Integer.toString(z));
                        //Se non ci sono altri EPub con quell'autore la frequenza è 0 quindi elimino tutto il nodo
                        if (z == 0) {
                            author.getParentNode().removeChild(author);
                            doc.normalize();
                            doc.getDocumentElement().normalize();
                            System.out.println("Nodo " + author.getNodeName() + " eliminato con successo!");
                        }
                        trovato = true;
                        System.out.println("aggiornato meno statistica");

                    }
                }
            }

        } catch (Exception ex) {
        }
        /*Fine aggiornamento statistiche*/
        //Dopo aver aggiornato la frequenza, posso finalmente togliere il nodo dell'Epub
        Object epub = x.evaluate("//EPub[@id=" + (id.intValue()) + "]", doc, XPathConstants.NODE);
        Element e = (Element) epub;
        System.out.println("Eliminazione del nodo all'interno del file EpubBiblio.xml: " + e.getNodeName());
        e.getParentNode().removeChild(e);
        doc.normalize();
        doc.getDocumentElement().normalize();
        System.out.println("Nodo " + e.getNodeName() + " eliminato con successo!");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(biblioXml));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

}
