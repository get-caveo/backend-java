package com.caveo.backend.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GestionException extends RuntimeException {

    private final HttpStatus status;

    public GestionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }


    public static GestionException emailExists(String email) {
        return new GestionException("Un utilisateur avec l'email '" + email + "' existe déjà", HttpStatus.CONFLICT);
    }

    public static GestionException notFound(String entity, Integer id) {
        return new GestionException(entity + " avec l'id " + id + " est introuvable", HttpStatus.NOT_FOUND);
    }

    public static GestionException notFound(String entity, String identifier) {
        return new GestionException(entity + " '" + identifier + "' est introuvable", HttpStatus.NOT_FOUND);
    }

    public static GestionException badRequest(String message) {
        return new GestionException(message, HttpStatus.BAD_REQUEST);
    }

    public static GestionException unauthorized(String message) {
        return new GestionException(message, HttpStatus.UNAUTHORIZED);
    }

    public static GestionException forbidden(String message) {
        return new GestionException(message, HttpStatus.FORBIDDEN);
    }

    public static GestionException conflict(String message) {
        return new GestionException(message, HttpStatus.CONFLICT);
    }
}

