package com.example.userverwaltung.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotValidException extends ResponseStatusException {
    public UserNotValidException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
