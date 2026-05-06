package com.sit.qb.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

		return new StanderedErrorResponse(400, "Validation Failed", errorMap);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public StanderedErrorResponse handleValidation(HandlerMethodValidationException ex) {
		List<String> errors = new ArrayList<>();
		for (var result : ex.getAllValidationResults()) {
			for (var error : result.getResolvableErrors()) {
				errors.add(error.getDefaultMessage());
			}
		}
		return new StanderedErrorResponse(400, "Validation Failed", errors);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public StanderedErrorResponse missingParam(MissingServletRequestParameterException ex) {
		return new StanderedErrorResponse(400, "Missing required parameter: " + ex.getParameterName(), null);
	}

	@ExceptionHandler(ArithmeticException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public StanderedErrorResponse arithmeticException(ArithmeticException ex) {
		return new StanderedErrorResponse(500, "Something went wrong", ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public StanderedErrorResponse resourceNotFound(ResourceNotFoundException ex) {
		return new StanderedErrorResponse(404, ex.getMessage(), null);
	}

	@ExceptionHandler(DuplicateEmailException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public StanderedErrorResponse duplicateEmail(DuplicateEmailException ex) {
		return new StanderedErrorResponse(409, ex.getMessage(), null);
	}

	@ExceptionHandler(IllegalStateTransitionException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public StanderedErrorResponse illegalTransition(IllegalStateTransitionException ex) {
		return new StanderedErrorResponse(400, ex.getMessage(), null);
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public StanderedErrorResponse conflict(ConflictException ex) {
		return new StanderedErrorResponse(409, ex.getMessage(), null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public StanderedErrorResponse dataIntegrityViolation(DataIntegrityViolationException ex) {
		return new StanderedErrorResponse(409, "Email already registered", null);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public StanderedErrorResponse invalidCredentials(InvalidCredentialsException ex) {
		return new StanderedErrorResponse(401, ex.getMessage(), null);
	}

}
