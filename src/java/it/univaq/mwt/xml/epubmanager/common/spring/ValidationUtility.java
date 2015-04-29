package it.univaq.mwt.xml.epubmanager.common.spring;

import org.springframework.validation.Errors;

public class ValidationUtility {
    
    public static void rejectIfMaxLength(Errors errors, String fieldName, String errorMessage, String fieldValue, int maxlength) {
        if (fieldValue != null && fieldValue.length() > maxlength) {
            Object[] args = {maxlength};
            errors.rejectValue(fieldName, errorMessage, args, "error.maxlength");
        }
    }
    
    public static void rejectIfMinLength(Errors errors, String fieldName, String errorMessage, String fieldValue, int minlength) {
        if (fieldValue != null && fieldValue.length() < minlength) {
            Object[] args = {minlength};
            errors.rejectValue(fieldName, errorMessage, args, "error.minlength");
        }
    }

    public static void rejectIfNal(Errors errors, String fieldName, String errorMessage, String fieldValue) {
        if (!(fieldValue.matches("it|en|es|fr"))) {
            errors.rejectValue(fieldName, errorMessage, "errors.linguedisponibili");
        }
    }

    public static void rejectIfNan(Errors errors, String fieldName,
            String errorMessage, String fieldValue) {

        try {
            long number = Long.parseLong(fieldValue);
        } catch (NumberFormatException e) {
            errors.rejectValue(fieldName, errorMessage, "error.integer");
        }

    }
}
