package com.anunciosloc.anunciosloc_server.config;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", false);
        response.put("erro", "Erro de validação");
        response.put("campos", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Map<String, Object>> handleUnrecognizedProperty(
            UnrecognizedPropertyException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", false);
        response.put("erro", "Campo desconhecido: " + ex.getPropertyName());
        response.put("mensagem", "O campo '" + ex.getPropertyName() + "' não é reconhecido pelo servidor");
        response.put("dica", "Verifique os campos enviados na requisição");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @SuppressWarnings("null")
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", false);
        response.put("erro", "Tipo de dado inválido");
        response.put("campo", ex.getName());
        response.put("valor_recebido", ex.getValue() != null ? ex.getValue().toString() : "null");
        response.put("tipo_esperado", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", false);
        response.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

   
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", false);
        response.put("erro", "Erro interno do servidor");
        response.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}