package com.bank.exception;

import java.util.Date;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.directory.InvalidAttributesException;
import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	
	// Exceptions
	// IllegalArgumentException 
	// InstanceAlreadyExistsException
	// InvalidAttributesException 

	// handling specific exceptions
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception, 
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), 400, "Bad Request", exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InstanceAlreadyExistsException.class)
	public ResponseEntity<?> handleInstanceAlreadyExistsException(InstanceAlreadyExistsException exception,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), 409, "Already Exist", exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(InvalidAttributesException.class)		// 
	public ResponseEntity<?> handleInvalidAttributesException(InvalidAttributesException exception, 
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), 400, "Bad Request", exception.getMessage(), 
				request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<?> handleAuthenticationException(AuthenticationException exception, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), 401, "Not Authorized", exception.getMessage(), 
				request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED);
	}
	
	
	
	//handling global exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception exception, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), 500, "Internal Server Error", 
				exception.getMessage(), request.getDescription(false));
		return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
