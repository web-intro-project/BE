package com.webintro.wakesong.common;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final String VALIDATION_ERROR = "VALIDATION_ERROR";

	private static final Map<String, String> PARAM_MESSAGES = Map.of(
			"date", "날짜 형식이 올바르지 않아요. (YYYY-MM-DD)",
			"month", "월 형식이 올바르지 않아요. (YYYY-MM)");

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError validation(ValidationException e) {
		Map.Entry<String, String> first = e.getFieldErrors().entrySet().iterator().next();
		return new ApiError(VALIDATION_ERROR, first.getValue(), first.getKey(), e.getFieldErrors());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError typeMismatch(MethodArgumentTypeMismatchException e) {
		return invalidParam(e.getName());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError missingParam(MissingServletRequestParameterException e) {
		return invalidParam(e.getParameterName());
	}

	private static ApiError invalidParam(String name) {
		String message = PARAM_MESSAGES.getOrDefault(name, "요청 값이 올바르지 않아요.");
		return new ApiError(VALIDATION_ERROR, message, name, Map.of(name, message));
	}

}
