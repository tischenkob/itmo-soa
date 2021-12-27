package ru.ifmo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class})
	protected ResponseEntity<Object> handleIllegalInput(RuntimeException ex, WebRequest request) {
		System.out.printf("ERROR: Exception '%s' for request %s", ex.getMessage(), request);
		return handleExceptionInternal(ex, null, new HttpHeaders(), BAD_REQUEST, request);
	}

	@ExceptionHandler({NoSuchElementException.class})
	protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
		System.out.printf("ERROR: Exception '%s' for request %s", ex.getMessage(), request);
		return handleExceptionInternal(ex, null, new HttpHeaders(), NOT_FOUND, request);
	}

}