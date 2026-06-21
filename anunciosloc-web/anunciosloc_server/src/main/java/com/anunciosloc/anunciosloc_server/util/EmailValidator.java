package com.anunciosloc.anunciosloc_server.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class EmailValidator {
    
    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    
    
    private static final List<String> DOMINIOS_PERMITIDOS = Arrays.asList(
        "gmail.com",
        "hotmail.com",
        "outlook.com",
        "yahoo.com",
        "empresa.com",
        "universidade.ao",
        "anunciosloc.com"
    );
    
    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        
        if (!pattern.matcher(email).matches()) {
            return false;
        }
        
        
        String dominio = email.substring(email.indexOf('@') + 1);
        return DOMINIOS_PERMITIDOS.contains(dominio);
    }
    
    public static String getDominio(String email) {
        return email.substring(email.indexOf('@') + 1);
    }
}