package it.univaq.mwt.xml.epubmanager.presentation.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    
    @RequestMapping(value={"/", "/index"})
    public String homeview () {
        return "common.index";
    }
    
}
