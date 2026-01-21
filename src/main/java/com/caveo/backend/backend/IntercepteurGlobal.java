package com.caveo.backend.backend;

import com.caveo.backend.backend.exception.GestionException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class IntercepteurGlobal {


    @ExceptionHandler(GestionException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGestionException(GestionException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", ex.getStatus().value());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return Map.of(
                "message", "Erreur de validation",
                "errors", errors,
                "status", HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Erreur de contrainte de données";

        // Personnalisation du message selon le type d'erreur SQL
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Cette valeur existe déjà dans la base de données";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "Impossible de supprimer : des données dépendantes existent";
        } else if (ex.getMessage().contains("cannot be null")) {
            message = "Un champ obligatoire est manquant";
        }

        return Map.of(
                "message", message,
                "details", ex.getMostSpecificCause().getMessage(),
                "status", HttpStatus.CONFLICT.value()
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "message", "Accès refusé : vous n'avez pas les permissions nécessaires",
                "status", HttpStatus.FORBIDDEN.value()
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGenericException(Exception ex) {
        return Map.of(
                "message", "Une erreur inattendue s'est produite",
                "details", ex.getMessage(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
