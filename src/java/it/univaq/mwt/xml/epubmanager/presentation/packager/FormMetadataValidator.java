package it.univaq.mwt.xml.epubmanager.presentation.packager;

import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import it.univaq.mwt.xml.epubmanager.common.spring.ValidationUtility;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class FormMetadataValidator implements Validator{
    
    @Override
    public boolean supports(Class<?> klass) {
        return Metadata.class.isAssignableFrom(klass);
    }
    
    @Override
    public void validate(Object target, Errors errors) {

        Metadata metadata = (Metadata) target;

        // controllo campi obbligatori
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "identifier", "errors.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "errors.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language", "errors.required");
        
        // controllo lunghezza campi
        ValidationUtility.rejectIfMinLength(errors, "identifier", "errors.minlength", metadata.getIdentifier(), 13);
        ValidationUtility.rejectIfMaxLength(errors, "identifier", "errors.maxlength", metadata.getIdentifier(), 25);
        ValidationUtility.rejectIfMaxLength(errors, "title", "errors.maxlength", metadata.getTitle(), 255);
        ValidationUtility.rejectIfMaxLength(errors, "language", "errors.maxlength", metadata.getLanguage(), 2);
        
        // controllo sulla lingua
        ValidationUtility.rejectIfNal(errors, "language", "errors.linguedisponibili", metadata.getLanguage());
    
    
    }
}
