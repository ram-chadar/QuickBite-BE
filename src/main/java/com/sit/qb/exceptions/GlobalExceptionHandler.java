package com.sit.qb.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.sit.qb.response.StanderedErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public StanderedErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getFieldErrors();
		Map<String, String> errorMap = new HashMap<>();

		for (FieldError fieldError : fieldErrors) {
			String field = fieldError.getField();
			String message = fieldError.getDefaultMessage();
			errorMap.put(field, message);
		}

		StanderedErrorResponse errorResponse = new StanderedErrorResponse(400, "Validation Failed", errorMap);

		return errorResponse;
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public StanderedErrorResponse handleValidation(HandlerMethodValidationException ex) {

//		List<String> errors = ex.getAllValidationResults().stream()
//				.flatMap(result -> result.getResolvableErrors().stream()).map(error -> error.getDefaultMessage())
//				.toList();

		List<String> errors = new ArrayList<>();
		for (var result : ex.getAllValidationResults()) {
			for (var error : result.getResolvableErrors()) {
				errors.add(error.getDefaultMessage());
			}
		}
		StanderedErrorResponse errorResponse = new StanderedErrorResponse(400, "Validation Failed", errors);
		return errorResponse;
	}
	
	@ExceptionHandler(ArithmeticException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public StanderedErrorResponse arithmeticException(ArithmeticException ex) {
		StanderedErrorResponse errorResponse = new StanderedErrorResponse(500, "Something went wrong", ex.getMessage());
		return errorResponse;
	}

}
