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
 * 모든 컨트롤러에서 터진 예외를 한곳에서 잡아 {@link ErrorResponse} JSON으로 바꿔 주는 곳.
 *
 * <ul>
 *   <li>{@code @RestControllerAdvice}: 모든 @RestController에 공통으로 적용되는 예외 처리기.</li>
 *   <li>{@code @ExceptionHandler(X.class)}: X 예외가 터지면 이 메서드가 대신 응답을 만든다.</li>
 *   <li>{@code @ResponseStatus}: 그때 보낼 HTTP 상태 코드.</li>
 * </ul>
 *
 * <pre>
 * 예외                                       → 상태  code
 * BadRequestException (서비스 입력 검사)      → 400   VALIDATION_ERROR
 * ?date= / ?month= 형식 틀림 · 없음           → 400   VALIDATION_ERROR
 * JSON이 깨졌거나 본문 없음                   → 400   VALIDATION_ERROR
 * 없는 주소                                   → 404   NOT_FOUND
 * 지원하지 않는 메서드 (DELETE 등)            → 405   METHOD_NOT_ALLOWED
 * 본문이 JSON이 아님                          → 415   UNSUPPORTED_MEDIA_TYPE
 * 그 밖의 모든 예외 (DB 장애 등)              → 500   INTERNAL_SERVER_ERROR
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 응답의 code 값
	private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
	private static final String NOT_FOUND = "NOT_FOUND";
	private static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
	private static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
	private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

	// ===================== 400 Bad Request =====================

	/** 서비스의 입력 검사에서 던진 오류. 메시지와 틀린 칸 이름을 그대로 내보낸다. */
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse badRequest(BadRequestException e) {
		return new ErrorResponse(VALIDATION_ERROR, e.getMessage(), e.getField());
	}

	/** 쿼리 파라미터 형식이 틀림. 예) ?date=2026-13-01 → LocalDate로 바꿀 수 없음 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse wrongParam(MethodArgumentTypeMismatchException e) {
		return paramError(e.getName());
	}

	/** 필수 쿼리 파라미터가 없음. 예) GET /api/requests (date 없이) */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse missingParam(MissingServletRequestParameterException e) {
		return paramError(e.getParameterName());
	}

	/** POST 본문 JSON이 깨졌거나 비어 있음. 어느 칸이 틀렸는지 알 수 없으므로 field는 없다. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse badJson() {
		return new ErrorResponse(VALIDATION_ERROR, "요청 형식이 올바르지 않아요.");
	}

	// ===================== 404 / 405 / 415 =====================

	/** 없는 주소. 예) GET /api/nope  (빈 목록은 404가 아니라 200 + [] 이다) */
	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse notFound() {
		return new ErrorResponse(NOT_FOUND, "요청한 주소를 찾을 수 없어요.");
	}

	/** 주소는 있지만 그 메서드는 안 됨. 예) DELETE /api/requests */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorResponse methodNotAllowed() {
		return new ErrorResponse(METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식이에요.");
	}

	/** POST 본문이 JSON이 아님. 예) Content-Type: text/plain */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public ErrorResponse notJson() {
		return new ErrorResponse(UNSUPPORTED_MEDIA_TYPE, "JSON(application/json)으로 보내 주세요.");
	}

	// ===================== 500 Internal Server Error =====================

	/**
	 * 위에서 잡지 못한 나머지 전부 (DB 연결 끊김 등).
	 * 원인은 서버 로그에만 남기고, 응답에는 SQL·스택트레이스·비밀번호 같은 내부 정보를 싣지 않는다.
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse serverError(Exception e) {
		log.error("처리하지 못한 오류", e);
		return new ErrorResponse(INTERNAL_SERVER_ERROR, "일시적인 오류로 요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.");
	}

	// ===================== 도우미 =====================

	/** 쿼리 파라미터 오류 응답. 파라미터 이름(date / month)에 맞는 형식 안내를 붙인다. */
	private static ErrorResponse paramError(String name) {
		String message = name.equals("month")
				? "월 형식이 올바르지 않아요. (YYYY-MM)"
				: "날짜 형식이 올바르지 않아요. (YYYY-MM-DD)";
		return new ErrorResponse(VALIDATION_ERROR, message, name);
	}

}
