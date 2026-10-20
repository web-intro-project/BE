package com.webintro.wakesong.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

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

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError unreadableBody(HttpMessageNotReadableException e) {
		return ApiError.of(VALIDATION_ERROR, "요청 형식이 올바르지 않아요.");
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public ApiError unsupportedMediaType(HttpMediaTypeNotSupportedException e) {
		return ApiError.of("UNSUPPORTED_MEDIA_TYPE", "JSON(application/json)으로 보내 주세요.");
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError notFound(NoResourceFoundException e) {
		return ApiError.of("NOT_FOUND", "요청한 주소를 찾을 수 없어요.");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ApiError methodNotAllowed(HttpRequestMethodNotSupportedException e) {
		return ApiError.of("METHOD_NOT_ALLOWED", "지원하지 않는 요청 방식이에요.");
	}

	/** 예상하지 못한 오류. 원인은 서버 로그에만 남기고 응답에는 SQL·스택트레이스를 싣지 않는다. */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError unexpected(Exception e) {
		log.error("처리하지 못한 오류", e);
		return ApiError.of("INTERNAL_SERVER_ERROR", "일시적인 오류로 요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.");
	}

}
