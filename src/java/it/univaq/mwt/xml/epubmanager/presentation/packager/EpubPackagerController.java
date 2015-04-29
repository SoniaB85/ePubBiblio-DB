package it.univaq.mwt.xml.epubmanager.presentation.packager;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.EPubService;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/packager")
public class EpubPackagerController {
    
    @Autowired
    private EPubService service;

    @Autowired
    private FormMetadataValidator metadatavalidator;

    @Value("#{cfgproperties.uploadDirectory}")
    private String UPLOAD_DIR;
    
    @RequestMapping(value = "/create", method = {RequestMethod.GET})
    public String createMetadataStart(Model model) {
        
        model.addAttribute("metadata", new Metadata());
        return "metadata.createform";
    }

    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public String createMetadata(@ModelAttribute Metadata metadata, BindingResult bindingResult) throws BusinessException {
        metadatavalidator.validate(metadata, bindingResult);
        if (bindingResult.hasErrors()) {
            return "metadata.createform";
        }
        long id = service.startEpub(metadata);
        System.out.println("ID univoco per le altre chiamate: " + id);
        return "redirect:/packager/uploadresources?epub="+id;
    }

    @RequestMapping(value = "/uploadresources", method = {RequestMethod.GET})
    public String uploadResourcesStart(@RequestParam("epub") String epub, Model model) {
        model.addAttribute("epub", epub);
        return "uploadresources.createform";
    }

    @RequestMapping(value = "/uploadresources", method = {RequestMethod.POST})
    public String uploadResources(@RequestParam("epub") String epub,
            @RequestParam("xhtmlfiles") MultipartFile[] xhtmlfiles,
            @RequestParam("cssfiles") MultipartFile[] cssfiles,
            @RequestParam("imagefiles") MultipartFile[] imagefiles) throws BusinessException {
        
        // mantine l'ordine di inserimento dei file xhtml
        int order = 0;
        
        // gestione upload dei file XHTML
        for (MultipartFile xhtmlfile : xhtmlfiles) {
            if (!xhtmlfile.isEmpty()) {               
                try {
                    byte[] bytes = xhtmlfile.getBytes();
                    EpubXhtml epubXhtml = new EpubXhtml(null, FilenameUtils.getBaseName(xhtmlfile.getOriginalFilename()), xhtmlfile.getOriginalFilename(), bytes, ++order, "application/xhtml+xml", epub, null);
                    int result = service.addXHTML(Long.parseLong(epub), epubXhtml);
                    if (result != 0) {
                        // traccia del file appena inserito
                    } else {
                        // traccia del file non inserito
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        // gestione upload dei file CSS
        for (MultipartFile cssfile : cssfiles) {
            if (!cssfile.isEmpty()) {
                try {
                    byte[] bytes = cssfile.getBytes();
                    EpubCss epubCss = new EpubCss(null , FilenameUtils.getBaseName(cssfile.getOriginalFilename()), cssfile.getOriginalFilename(), bytes, cssfile.getContentType(), epub);
                    System.out.println(epubCss.toString());
                    int result = service.addStylesheet(Long.parseLong(epub), epubCss);
                    if (result != 0) {
                        
                    } else {
                        
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        // gestione upload delle immagini
        for (MultipartFile imagefile : imagefiles) {
            if (!imagefile.isEmpty()) {
                try {
                    byte[] bytes = imagefile.getBytes();
                    EpubImage epubImage = new EpubImage(null, FilenameUtils.getBaseName(imagefile.getOriginalFilename()), imagefile.getOriginalFilename(), bytes, imagefile.getContentType(), epub);
                    System.out.println(epubImage.toString());
                    int result = service.addImage(Long.parseLong(epub), epubImage);
                    if (result != 0) {
                        
                    } else {
                        
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        return "redirect:/packager/orderresources?epub="+epub;

    }

    @RequestMapping(value = "/orderresources", method = {RequestMethod.GET})
    public String orderResourcesStart(@RequestParam("epub") String epub, Model model) {

        model.addAttribute("sortingXhtmlFiles", service.findAllEpubXhtml(Long.parseLong(epub)));
        model.addAttribute("sortingCssFiles", service.findAllEpubCss(Long.parseLong(epub)));
        model.addAttribute("sortingImageFiles", service.findAllEpubImage(Long.parseLong(epub)));
        
        model.addAttribute("epub", epub);
        
        return "orderresorces.views";
    }

    @RequestMapping(value = "/orderresources/update", method = {RequestMethod.GET})
    public String updateOrderXhtmlStart(@RequestParam("id") String id, Model model) {
        
        EpubXhtml epubXhtml = service.getEpubXhtmlByPk(id);
        model.addAttribute("epubXhtml", epubXhtml);

        return "orderresorces.updateform";
    }

    @RequestMapping(value = "/orderresources/update", method = {RequestMethod.POST})
    public String updateOrderXhtml(@ModelAttribute EpubXhtml epubXhtml) {

        service.updateEpubXhtml(epubXhtml);

        return "redirect:/packager/orderresources?epub="+epubXhtml.getEpub();
    }

    @RequestMapping(value = "/orderresources/delete", method = {RequestMethod.GET})
    public String deleteOrderXhtmlStart(@RequestParam("id") String id, Model model) {
        
        EpubXhtml epubXhtml = service.getEpubXhtmlByPk(id);
        model.addAttribute("epubXhtml", epubXhtml);

        return "orderresorces.deleteform";
    }

    @RequestMapping(value = "/orderresources/delete", method = {RequestMethod.POST})
    public String deleteOrderXhtml(@ModelAttribute EpubXhtml epubXhtml) {
        
        service.deleteEpubXhtml(epubXhtml);
        
        return "redirect:/packager/orderresources?epub="+epubXhtml.getEpub();
    }

    @RequestMapping(value = "/orderok", method = {RequestMethod.GET})
    public String orderResources(@RequestParam("epub") String epub, Model model) {

        service.finalizeEpub(Long.parseLong(epub));
        
        model.addAttribute("epub", epub);
        
        return "download.epub";
    }
    
    /**
     * Gestione del download dell'Epub
     */
    @RequestMapping(value="/downloadEpub", method={RequestMethod.GET})
    public void dowloadEpub(@RequestParam("epub") String epub, HttpServletResponse response) throws BusinessException, FileNotFoundException, IOException {
        String downloadPath = UPLOAD_DIR + epub + File.separator + epub + ".epub";
        File epubToDownlod = new File(downloadPath);
        InputStream input = new FileInputStream(epubToDownlod);
        response.setContentType("application/force-download");
	response.setHeader("Content-Disposition", "attachment; filename=" + epubToDownlod.getName());
        IOUtils.copy(input, response.getOutputStream());
        response.flushBuffer();
    }
}
