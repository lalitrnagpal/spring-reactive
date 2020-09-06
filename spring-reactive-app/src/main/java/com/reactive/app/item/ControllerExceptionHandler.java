package com.reactive.app.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException rtException) {
		log.error("Exception caught in Controller Advice: ", rtException);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("From Controller Advice: " + rtException.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception exception) {
		log.error("Exception caught in Controller Advice: ", exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("From Controller Advice: " + exception.getMessage());
	}
	
}
