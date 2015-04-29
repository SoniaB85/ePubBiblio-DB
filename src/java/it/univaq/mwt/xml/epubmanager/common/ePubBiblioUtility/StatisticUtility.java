package it.univaq.mwt.xml.epubmanager.common.ePubBiblioUtility;

import it.univaq.mwt.xml.epubmanager.business.model.ePubBiblio.EPub;
import it.univaq.mwt.xml.epubmanager.business.model.ePubBiblio.EPubBiblio;
import it.univaq.mwt.xml.epubmanager.business.model.ePubBiblio.EpubFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StatisticUtility {

    static int totParole;
    static int totImg;
    static int totCapitoli;

    /**
     * Questo metodo serve per assegnare l'ID di valore i all'i-esimo ePub
     * uplodato.
     * L'URL passata come parametro d'ingresso indica la posizione del file EPubBiblio.xml
     * Restituisce l'ID 
     * @param url 
     */
    public static int addID(String url) {

        File f = new File(url);
        int id = 0;
        //Controllo se EPubBiblio.xml esiste 
        // Se esiste allora vado a vedere qual'è l'ID dell'ultimo EPub e continuo la numerazione
        // Altrimenti vuol dire che è il primo inserimento e l'ID è sicuramente 1
        if (f.exists()) {
            
            /*DOM*/
            //creo un'istanza di DocumentBuilderFactory usando il metodo statico newInstance()
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docB;
            String exprEPub = null;
            String idT;
            try {
                //Genere il DocumentBuilder dalla DocumentBuilderFactory
                docB = factory.newDocumentBuilder();
                //Genereo un oggetto Document e lo carico ilsando il metodo parse di DocumentBuilder
                //Col processo di parsing devo gestire le eccezioni IOException e SAXException
                Document doc = docB.parse(url);
                //getDocumentElement permette di prelevare direttamente il nodo radice del documento XML
                // normalize serve a fondere nodi Text adiacenti nel sottoalbero controllato dall'elemento
                doc.getDocumentElement().normalize();
                /*Controllo se nel frattempo sono stati eliminati tutti gli ePub
                * se sono stati eliminati tutti la bibilioteca è vuota e l'id sarà 1
                */
                NodeList ePubList = doc.getElementsByTagName("EPub");
                System.out.println(ePubList.getLength());
                if (ePubList.getLength()==0){
                id= 1;
                }else{
                //Voglio la posizione dell'ultimo EPub inserito
                exprEPub = "//ePubList/EPub[position() = last ()]/@id";
                /*XPath*/
                //Creo un'istanza di XPathFactory tramite il metodo statico  e genero XPath
                //I passaggi li ho uniti
                XPath xPath = XPathFactory.newInstance().newXPath();
                // Chiamo il metodo evaluate dell'XPath passandogli l'espressione XPath exprEPub
                //la sorgente dati ovvero doc 
                // e una costante XPathConstants che indica il tipo di risultato atteso, in questo caso un oggetto di tipo Node che rappresenta il nodo dell'albero DOM.
                Node x = (Node) xPath.compile(exprEPub).evaluate(doc, XPathConstants.NODE);
                //getTextContent() legge il contenuto testuale del nodo
                idT = x.getTextContent();
                //Parso il contenuto testuale per trasformarlo in un'intero
                id = Integer.parseInt(idT);
                //Trovato l'ultimo id il nuovo id avrà valore +1
                id += 1;}
            } 
            catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException ex) {
                Logger.getLogger(StatisticUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            id = 1;
        }
        return id;
    }

    /**
     * Questo metodo serve per contare le immagini degli ePub 2.0 o 3.0
     * @param f
     */
    public static int contaImmagini(File f) {

        int risultato = 0;
        /*DOM*/
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docB;

        try {
            docB = factory.newDocumentBuilder();
            Document doc = docB.parse(new File(f.getPath()));
            /*
            * Con NodeList cerco tutti gli elementi figli della radice che si chiamano: 
            * -> ns0:item se si tratta di un ePub 3.0
            * -> item se si tratta di un ePub 2.0
            */           
            NodeList nodeL = doc.getElementsByTagName("ns0:item");
            if (nodeL.getLength() == 0) 
            {
                nodeL = doc.getElementsByTagName("item");
            }
            //Ciclo per la lunghezza della lista di nodi
            for (int i = 0; i < nodeL.getLength(); i++) {
                //Su ciascun nodo trovato leggiamo il contenuto con namespace
                Element element = (Element) nodeL.item(i);
                String attribute = element.getAttribute("media-type");
                //Se l'attributo contiene image si tratta di un'immagine ed aumentiamo il contatore d'immagini
                if (attribute.contains("image")) {
                    risultato++;
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {

            e.printStackTrace();
        }
        return risultato;

    }
    /**
     * Questo metodo serve per contare i capitoli degli ePub 2.0 o 3.0
     * @param f 
     */
    public static int contaCapitoli(File f) {

        int risultato = 0;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docB;

        try {
            docB = factory.newDocumentBuilder();
            Document doc = docB.parse(new File(f.getPath()));
            NodeList nodeL = doc.getElementsByTagName("ns0:item");
            if (nodeL.getLength() == 0) {
                nodeL = doc.getElementsByTagName("item");
            }
            for (int i = 0; i < nodeL.getLength(); i++) {
                Element element = (Element) nodeL.item(i);
                String attribute = element.getAttribute("media-type");
                if (attribute.contains("xhtml")) {
                    risultato++;
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {

            e.printStackTrace();
        }
        return risultato;

    }
    /**
     * Questo metodo serve per analizzare la lista di ePub uploadati e salvare i dati di quest'ultimi.
     * Qui vengono contate le immagini, parole, capitoli e si estraggono i dati dai file di ogni EPubFile 
     * per poi popolare gli oggetti EPub e l'oggetto EPubBiblio.
     * @param listaEPub
     * @param pathEPubBiblio
     */
    public static EPubBiblio analizza(List<EpubFile> listaEPub, String pathEPubBiblio) {
      
        Iterator<EpubFile> i = listaEPub.iterator();
        
        EPubBiblio ePubBiblio = new EPubBiblio();
        List<EPub> ePubs = new ArrayList<EPub>();
        //trovo l'id da assegrare all'EPub
        int x = addID(pathEPubBiblio + File.separator + "EPubBiblio.xml");
        int conta;
        String pathFile;
        String pathEPub;
        //Nel ciclo per ogni EPubFile creo un EPub che inizio a popolare
        while (i.hasNext()) {
            EpubFile temp = i.next();
            EPub ePub = new EPub();
            //setto l'attributo id di EPub
            ePub.setId(x);
            x++;
            //Conto le parole presenti nei capitoli (ovvero nei vari file .xhtml per ePub 3.0 e .html per ePub 2.0)
            //poi setto l'attributo Parole di EPub
            conta = contaParoleCapitoli(temp.getListaF());
            pathEPub = temp.getNomeEPubFile();
            ePub.setParole(conta);         
            
            //Qui cerco il path dove è salvato lo zip dell'ePub e salvo il percorso nell'attributo PathFile di EPub
            int lessT = pathEPub.lastIndexOf(".");
            String titoloLibro = pathEPub.substring(0, lessT);
            pathFile = pathEPubBiblio;
            pathEPubBiblio += File.separator + "ePubBiblioUploaded" + File.separator + titoloLibro;
            ePub.setPathFile(pathEPubBiblio);
            //Resetto pathEPubBiblio per il prossimo file altrimenti nel path ci sarebbero tutti i path della lista di file.
            pathEPubBiblio = pathFile;
            
            ePubs.add(ePub);
        }
        //Aggiungo ad ePubBiblio la lista di ePub parzialmente popolati (ovvero hanno ID, PathName e Parole
        ePubBiblio.setePubList(ePubs);
        //Ciclo per il numero di EPubfile presenti in listaEPub
        for (int j = 0; j < listaEPub.size(); j++) {
            // devo estrarre il j-esimo epub da entrambe le stutture dati
            //Cre un ePunFile e gli assegno il j-esimo ePub presente nella lista 
            EpubFile ePubFile = listaEPub.get(j);
            //devo trovare content.opf che serve per poter contare immagini e capitoli quindi scorro tutti i file
            for (int a = 0; a < ePubFile.getListaF().size(); a++) {

                File fileT = ePubFile.getListaF().get(a);
                if (fileT.getName().endsWith(".opf") || fileT.getName().contains(".opf")) {
                    int contaImg;
                    int contaCapitoli;
                    //Richiamo i metodi per contare 
                    //I metodi sono stati ottimizzati per contare immagini e capitoli sia di ePub 2.0 che di ePub 3.0
                    contaImg = contaImmagini(fileT);
                    contaCapitoli = contaCapitoli(fileT);
                    ePubBiblio.getePubList().get(j).setImmagini(contaImg);
                    ePubBiblio.getePubList().get(j).setCapitoli(contaCapitoli);

                    ePubBiblio.setMetadata(j, fileT);
                }
            }
        }
        return ePubBiblio;
    }

    /**
     * Conta le parole nei capitoli
     *
     * @return
     */
    public static int contaParoleCapitoli(List<File> lista) {

        List<File> lFile = lista;
        int ris = 0;
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

            for (Iterator iterator = lFile.iterator(); iterator.hasNext();) {
                File newFile = (File) iterator.next();
                File newnewFile = new File(newFile.getPath());
                if (newnewFile.getCanonicalFile().toString().contains(".xhtml") || newnewFile.getCanonicalFile().toString().contains(".html")) {
                    Document doc = docB.parse(newnewFile);
                    body = doc.getElementsByTagName("body");
                    //Può avere al massimo 1 body
                    Node x = body.item(0);
                    //Se non c'è il body non ci sono parole da contare
                    if (x != null) {
                        String testo = x.getTextContent();
                        testo = testo.replaceAll("\\W", ",");
                        testo = testo.replaceAll("[,]+", ",");
                        testo = testo.replaceAll("\\w", "");
                        ris = ris + testo.length();
                } 
              }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return ris;
    }
}
