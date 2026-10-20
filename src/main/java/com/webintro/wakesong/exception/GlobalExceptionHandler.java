package com.webintro.wakesong.exception;

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

/**
 * 오류 → 응답 변환표
 * 400 입력 오류 / 404 없는 주소 / 405 안 되는 메서드 / 415 JSON 아님 / 500 그 외 전부
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 400: 서비스에서 던진 입력 오류
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse badRequest(BadRequestException e) {
		return new ErrorResponse("VALIDATION_ERROR", e.getMessage(), e.getField());
	}

	// 400: ?date= / ?month= 가 없거나 형식이 틀림
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse badParam(Exception e) {
		String name = e instanceof MethodArgumentTypeMismatchException m ? m.getName()
				: ((MissingServletRequestParameterException) e).getParameterName();
		String message = name.equals("month") ? "월 형식이 올바르지 않아요. (YYYY-MM)" : "날짜 형식이 올바르지 않아요. (YYYY-MM-DD)";
		return new ErrorResponse("VALIDATION_ERROR", message, name);
	}

	// 400: JSON이 깨졌거나 본문이 없음
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse badJson() {
		return new ErrorResponse("VALIDATION_ERROR", "요청 형식이 올바르지 않아요.");
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse notFound() {
		return new ErrorResponse("NOT_FOUND", "요청한 주소를 찾을 수 없어요.");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse methodNotAllowed() {
		return new ErrorResponse("METHOD_NOT_ALLOWED", "지원하지 않는 요청 방식이에요.");
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public ErrorResponse notJson() {
		return new ErrorResponse("UNSUPPORTED_MEDIA_TYPE", "JSON(application/json)으로 보내 주세요.");
	}

	// 500: 원인은 서버 로그에만. 응답에는 SQL·스택트레이스를 싣지 않는다
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse serverError(Exception e) {
		log.error("처리하지 못한 오류", e);
		return new ErrorResponse("INTERNAL_SERVER_ERROR", "일시적인 오류로 요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.");
	}

}
