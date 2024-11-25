package com.web.sso.exception;

import java.util.Date;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.sso.model.ExceptionResponse;

@ControllerAdvice
public class AuthException {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleAccessDeniedException(HttpClientErrorException exception, WebRequest request)
        throws JsonProcessingException {
        @SuppressWarnings("unchecked")
        HashMap<String, String> json = new ObjectMapper().readValue(exception.getResponseBodyAsString(), HashMap.class);

        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), json.get("message"), request.getDescription(false));
//        return new ResponseEntity(exceptionResponse, HttpStatus.FORBIDDEN);
        HttpStatus statusCode = exception.getStatusCode();
        return ResponseEntity.status(statusCode).body(exceptionResponse);
    }

}
