package it.univaq.mwt.xml.epubmanager.presentation;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {
	
    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException ex) {
        ModelAndView model = new ModelAndView("common.error");
        model.addObject("errorCause", ex.getCause());
        model.addObject("errorMessage", ex.getMessage());
        return model;
    }
 
}
